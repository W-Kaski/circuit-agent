package com.eric.ekaiagent.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PDFGenerationToolTest {

    @Test
    void generatePDF() {
        PDFGenerationTool tool = new PDFGenerationTool();
        String fileName = "hello.pdf";
        String content = "hello你好，fiejfoaijfojgeoivaewg" +
                "faeijfpoaijefoaijef" +
                "fjeiga;opwijgpaoiwejg";
        String result = tool.generatePDF(fileName, content);
        assertNotNull(result);
    }

    @Test
    void generateAdvancedPDF() {
    }
}