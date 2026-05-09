package com.eric.circuitagent.rag;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.beans.factory.ObjectProvider;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;

import java.util.List;


@Configuration
@Slf4j
public class AlgorithmAppVectorStoreConfig {

    @Value("${spring.ai.dashscope.api-key:}")
    private String dashscopeApiKey;

    @Resource
    private AlgorithmAppDocumentLoader algorithmAppDocumentLoader;
    @Resource
    private MyTokenTextSplitter myTokenTextSplitter;

    @Resource
    private MyKeywordEnricher myKeywordEnricher;

    @Value("${USE_LOCAL_EMBEDDING:false}")
    private boolean useLocalEmbedding;

    @Value("${OLLAMA_BASE_URL:http://localhost:11434}")
    private String ollamaBaseUrl;

    @Value("${OLLAMA_EMBEDDING_MODEL:nomic-embed-text}")
    private String ollamaEmbeddingModel;

    @Bean
    public EmbeddingModel customEmbeddingModel(
            ObjectProvider<DashScopeEmbeddingModel> dashscopeModelProvider,
            ObjectProvider<OllamaEmbeddingModel> ollamaModelProvider) {
        if (useLocalEmbedding) {
            log.info("Switching to local Ollama embedding model");
            OllamaEmbeddingModel ollamaModel = ollamaModelProvider.getIfAvailable();
            if (ollamaModel != null) return ollamaModel;
            log.warn("OllamaEmbeddingModel not found, falling back to DashScope");
        }
        return dashscopeModelProvider.getIfAvailable();
    }

    @Bean
    VectorStore algorithmAppVectorStore(@org.springframework.beans.factory.annotation.Qualifier("customEmbeddingModel") EmbeddingModel embeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(embeddingModel)
                .build();
        if (dashscopeApiKey == null || dashscopeApiKey.isBlank() || dashscopeApiKey.contains("placeholder")) {
            log.warn("Skip vector store initialization because DashScope API key is not configured");
            return simpleVectorStore;
        }

        // 加载文档
        List<Document> documents = algorithmAppDocumentLoader.loadMarkdowns();
        // 自主切分
        List<Document> splitDocuments = myTokenTextSplitter.splitCustomized(documents);

        // 自动补充关键词元信息
        List<Document> enrichedDocuments = myKeywordEnricher.enrichDocuments(splitDocuments);

        try {
            simpleVectorStore.add(enrichedDocuments);
        } catch (Exception e) {
            log.warn("Skip vector store document ingestion because embeddings are unavailable", e);
        }
        return simpleVectorStore;
    }
}

