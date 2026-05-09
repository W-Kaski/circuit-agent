package com.eric.circuitagent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
// read all the markdown file and convert to document list
public class AlgorithmAppDocumentLoader {

    private final ResourcePatternResolver resourcePatternResolver;

    public AlgorithmAppDocumentLoader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    /**
     * Load multiple Markdown documents from the resource directory and parse them
     * into Document objects.
     * This method is typically used for ingesting algorithm-related knowledge base
     * files for downstream RAG (Retrieval-Augmented Generation) tasks.
     * 
     * @return List of parsed Document objects
     */
    private static final String STORAGE_PATH = "file:storage/documents/";

    /**
     * Load multiple Markdown documents from both classpath and external storage.
     * 
     * @return List of parsed Document objects
     */
    public List<Document> loadMarkdowns() {
        List<Document> allDocuments = new ArrayList<>();
        try {
            // 1. Load from classpath (baked-in docs)
            Resource[] classpathResources = resourcePatternResolver.getResources("classpath:documents/*.md");
            loadFromResources(classpathResources, allDocuments);

            // 2. Load from external storage (user uploaded docs)
            Resource[] externalResources = resourcePatternResolver.getResources(STORAGE_PATH + "*.md");
            loadFromResources(externalResources, allDocuments);
            
        } catch (IOException e) {
            log.error("Failed to load Markdown documents", e);
        }
        return allDocuments;
    }

    private void loadFromResources(Resource[] resources, List<Document> allDocuments) {
        if (resources == null) return;
        for (Resource resource : resources) {
            try {
                String filename = resource.getFilename();
                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true)
                        .withIncludeCodeBlock(true) // Enabled for better technical doc support
                        .withIncludeBlockquote(true)
                        .withAdditionalMetadata("filename", filename)
                        .build();
                MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
                allDocuments.addAll(reader.get());
            } catch (Exception e) {
                log.warn("Failed to parse document: {}", resource.getFilename());
            }
        }
    }
}
