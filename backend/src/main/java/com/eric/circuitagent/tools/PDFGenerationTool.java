package com.eric.circuitagent.tools;

import cn.hutool.core.io.FileUtil;
import com.eric.circuitagent.constant.FileConstant;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * PDF generation tool (fixed version)
 */
public class PDFGenerationTool {

    @Tool(description = "Generate a PDF file with given content", returnDirect = false)
    public String generatePDF(
            @ToolParam(description = "Name of the file to save the generated PDF") String fileName,
            @ToolParam(description = "Content to be included in the PDF") String content) {

        String fileDir = FileConstant.FILE_SAVE_DIR + "/pdf";
        String filePath = fileDir + "/" + (fileName.endsWith(".pdf") ? fileName : fileName + ".pdf");

        try {
            // Create directory
            FileUtil.mkdir(fileDir);

            // Get or create Chinese font
            PdfFont chineseFont = getChineseFont();

            // Create PDF document
            try (PdfWriter writer = new PdfWriter(filePath);
                    PdfDocument pdf = new PdfDocument(writer);
                    Document document = new Document(pdf, PageSize.A4)) {

                // Set document properties
                document.setMargins(50, 50, 50, 50);

                // Create paragraph and set font
                Paragraph paragraph = new Paragraph(content)
                        .setFont(chineseFont)
                        .setFontSize(12)
                        .setTextAlignment(TextAlignment.LEFT)
                        .setMultipliedLeading(1.2f); // Line spacing

                // Add paragraph
                document.add(paragraph);
            }

            return "PDF generated successfully to: " + filePath;

        } catch (Exception e) {
            return "Error generating PDF: " + e.getMessage() + ". Please ensure Chinese font files are available.";
        }
    }

    /**
     * Get Chinese font - supports multiple solutions
     */
    private PdfFont getChineseFont() throws IOException {
        // Solution 1: Use system fonts (if available)
        String[] systemFontPaths = {
                "C:/Windows/Fonts/simhei.ttf", // Windows SimHei
                "C:/Windows/Fonts/simsun.ttf", // Windows SimSun
                "/System/Library/Fonts/PingFang.ttc", // macOS
                "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf" // Linux
        };

        // Solution 2: Use font files in the resources directory
        String[] resourceFontPaths = {
                "static/fonts/simsun.ttf",
                "fonts/simsun.ttf",
                "static/fonts/msyh.ttf", // Microsoft YaHei
                "fonts/msyh.ttf"
        };

        // Try system fonts first
        for (String fontPath : systemFontPaths) {
            if (Files.exists(Paths.get(fontPath))) {
                return PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H);
            }
        }

        // Then try resource font files
        for (String fontPath : resourceFontPaths) {
            try {
                Resource resource = new ClassPathResource(fontPath);
                if (resource.exists()) {
                    return PdfFontFactory.createFont();
                }
            } catch (Exception e) {
                // Continue to try the next font
                continue;
            }
        }

        // Finally, try built-in font as fallback
        try {
            return PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H");
        } catch (Exception e) {
            throw new IOException(
                    "No suitable Chinese font found. Please install Chinese fonts or provide font files in resources/fonts/ directory.");
        }
    }
}