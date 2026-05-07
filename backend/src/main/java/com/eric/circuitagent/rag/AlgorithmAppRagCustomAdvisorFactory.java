package com.eric.circuitagent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;

@Slf4j
public class AlgorithmAppRagCustomAdvisorFactory {
        public static Advisor createAlgorithmAppRagCustomAdvisor(VectorStore vectorStore, String status) {
                Filter.Expression expression = new FilterExpressionBuilder()
                                .eq("status", status)
                                .build();
                DocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                                .vectorStore(vectorStore)
                                .filterExpression(expression) // Filter condition
                                .similarityThreshold(0.5) // Similarity threshold
                                .topK(3) // Number of documents to return
                                .build();
                return RetrievalAugmentationAdvisor.builder()
                                .documentRetriever(documentRetriever)
                                .queryAugmenter(AlgorithmAppContextualQueryAugmenterFactory.createInstance())
                                .build();
        }
}
