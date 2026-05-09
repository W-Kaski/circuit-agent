package com.eric.circuitagent.controller;

import com.eric.circuitagent.rag.KnowledgeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/knowledge")

public class KnowledgeController {

    @Resource
    private KnowledgeService knowledgeService;

    @GetMapping("/files")
    public List<KnowledgeService.KnowledgeFile> listFiles() {
        return knowledgeService.listFiles();
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        knowledgeService.uploadFile(file);
        return "File uploaded and indexed: " + file.getOriginalFilename();
    }

    @DeleteMapping("/files/{name}")
    public String deleteFile(@PathVariable String name) throws IOException {
        knowledgeService.deleteFile(name);
        return "File deleted: " + name;
    }

    @GetMapping(value = "/files/{name}/content", produces = "text/plain;charset=UTF-8")
    public String getFileContent(@PathVariable String name) throws IOException {
        return knowledgeService.getFileContent(name);
    }

    @PostMapping("/reindex")
    public String reindex() {
        knowledgeService.reindex();
        return "Re-indexing triggered";
    }
}
