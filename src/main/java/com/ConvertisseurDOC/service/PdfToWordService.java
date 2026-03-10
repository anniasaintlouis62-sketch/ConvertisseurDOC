package com.ConvertisseurDOC.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class PdfToWordService {

    /**
     * Convertit un PDF (texte) en DOCX.
     * ⚠️ Si le PDF est scanné (image), le texte sera vide (OCR requis).
     */
    public Path convertPdfToDocx(Path pdfPath, Path outDir) throws Exception {
        Files.createDirectories(outDir);

        String baseName = stripExtension(pdfPath.getFileName().toString());
        Path docxOut = outDir.resolve(baseName + ".docx");

        String text = extractText(pdfPath);

        try (XWPFDocument doc = new XWPFDocument();
             OutputStream os = Files.newOutputStream(docxOut)) {

            // Simple: un paragraphe par ligne
            String[] lines = text.split("\\r?\\n");
            for (String line : lines) {
                XWPFParagraph p = doc.createParagraph();
                p.createRun().setText(line);
            }

            doc.write(os);
        }

        return docxOut;
    }

    private String extractText(Path pdfPath) throws Exception {
        try (PDDocument document = PDDocument.load(pdfPath.toFile())) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            return stripper.getText(document);
        }
    }

    private String stripExtension(String name) {
        int i = name.lastIndexOf('.');
        return (i > 0) ? name.substring(0, i) : name;
    }
}