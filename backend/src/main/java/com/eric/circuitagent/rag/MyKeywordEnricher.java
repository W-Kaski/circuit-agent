package com.eric.circuitagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;

import org.springframework.ai.model.transformer.KeywordMetadataEnricher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
class MyKeywordEnricher {
    @Resource
    private ChatModel dashscopeChatModel;

    List<Document> enrichDocuments(List<Document> documents) {
        if (documents == null || documents.isEmpty()) {
            return documents;
        }

        try {
            KeywordMetadataEnricher enricher = new KeywordMetadataEnricher(this.dashscopeChatModel, 5);
            return enricher.apply(documents);
        } catch (Exception e) {
            log.warn("Skip keyword enrichment and fall back to raw documents", e);
            return documents;
        }
    }
}
