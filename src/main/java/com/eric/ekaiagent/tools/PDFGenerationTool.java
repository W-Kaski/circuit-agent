package com.eric.ekaiagent.tools;

import cn.hutool.core.io.FileUtil;
import com.eric.ekaiagent.constant.FileConstant;
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
 * PDF 生成工具（修复版）
 */
public class PDFGenerationTool {

    @Tool(description = "Generate a PDF file with given content", returnDirect = false)
    public String generatePDF(
            @ToolParam(description = "Name of the file to save the generated PDF") String fileName,
            @ToolParam(description = "Content to be included in the PDF") String content) {
        
        String fileDir = FileConstant.FILE_SAVE_DIR + "/pdf";
        String filePath = fileDir + "/" + (fileName.endsWith(".pdf") ? fileName : fileName + ".pdf");
        
        try {
            // 创建目录
            FileUtil.mkdir(fileDir);
            
            // 获取或创建中文字体
            PdfFont chineseFont = getChineseFont();
            
            // 创建PDF文档
            try (PdfWriter writer = new PdfWriter(filePath);
                 PdfDocument pdf = new PdfDocument(writer);
                 Document document = new Document(pdf, PageSize.A4)) {
                
                // 设置文档属性
                document.setMargins(50, 50, 50, 50);
                
                // 创建段落并设置字体
                Paragraph paragraph = new Paragraph(content)
                        .setFont(chineseFont)
                        .setFontSize(12)
                        .setTextAlignment(TextAlignment.LEFT)
                        .setMultipliedLeading(1.2f); // 行间距
                
                // 添加段落
                document.add(paragraph);
            }
            
            return "PDF generated successfully to: " + filePath;
            
        } catch (Exception e) {
            return "Error generating PDF: " + e.getMessage() + ". Please ensure Chinese font files are available.";
        }
    }
    
    /**
     * 获取中文字体 - 支持多种方案
     */
    private PdfFont getChineseFont() throws IOException {
        // 方案1: 使用系统字体（如果可用）
        String[] systemFontPaths = {
            "C:/Windows/Fonts/simhei.ttf",    // Windows 黑体
            "C:/Windows/Fonts/simsun.ttf",    // Windows 宋体
            "/System/Library/Fonts/PingFang.ttc", // macOS
            "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf" // Linux
        };
        
        // 方案2: 使用资源目录下的字体文件
        String[] resourceFontPaths = {
            "static/fonts/simsun.ttf",
            "fonts/simsun.ttf",
            "static/fonts/msyh.ttf",    // 微软雅黑
            "fonts/msyh.ttf"
        };
        
        // 首先尝试系统字体
        for (String fontPath : systemFontPaths) {
            if (Files.exists(Paths.get(fontPath))) {
                return PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H);
            }
        }
        
        // 然后尝试资源文件中的字体
        for (String fontPath : resourceFontPaths) {
            try {
                Resource resource = new ClassPathResource(fontPath);
                if (resource.exists()) {
                    return PdfFontFactory.createFont();
                }
            } catch (Exception e) {
                // 继续尝试下一个字体
                continue;
            }
        }
        
        // 最后尝试内置字体（作为fallback）
        try {
            return PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H");
        } catch (Exception e) {
            throw new IOException("No suitable Chinese font found. Please install Chinese fonts or provide font files in resources/fonts/ directory.");
        }
    }
}