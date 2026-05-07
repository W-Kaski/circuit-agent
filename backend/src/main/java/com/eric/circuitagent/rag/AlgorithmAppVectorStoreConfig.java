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

    @Bean
    VectorStore algorithmAppVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel)
                .build();
        if (dashscopeApiKey == null || dashscopeApiKey.isBlank() || dashscopeApiKey.contains("placeholder")) {
            log.warn("Skip vector store initialization because DashScope API key is not configured");
            return simpleVectorStore;
        }

        // 加载文档
        List<Document> documents = algorithmAppDocumentLoader.loadMarkdowns();
        // 自主切分
//        List<Document> splitDocuments = myTokenTextSplitter.splitCustomized(documents);

        // 自动补充关键词元信息
        List<Document> enrichedDocuments = myKeywordEnricher.enrichDocuments(documents);

        try {
            simpleVectorStore.add(enrichedDocuments);
        } catch (Exception e) {
            log.warn("Skip vector store document ingestion because embeddings are unavailable", e);
        }
        return simpleVectorStore;
    }
}

