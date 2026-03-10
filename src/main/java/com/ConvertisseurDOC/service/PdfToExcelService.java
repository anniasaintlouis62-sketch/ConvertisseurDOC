package com.ConvertisseurDOC.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PdfToExcelService {

    /**
     * PDF -> XLSX
     * - Essaie d'abord de détecter des tableaux via l'alignement des colonnes
     * - Si aucun tableau détecté, fallback: 1 ligne PDF = 1 ligne Excel
     */
    public Path convertPdfToXlsx(Path pdfPath, String xlsx, Path outDir) throws Exception {
        Files.createDirectories(outDir);

        String baseName = stripExtension(pdfPath.getFileName().toString());
        Path xlsxOut = outDir.resolve(baseName + ".xlsx");

        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            String text = extractText(pdfPath);
            boolean wroteTable = tryWriteAsTable(text, wb);

            if (!wroteTable) {
                writeTextFallback(text, wb);
            }

            try (OutputStream os = Files.newOutputStream(xlsxOut)) {
                wb.write(os);
            }
        }

        return xlsxOut;
    }

    // =======================
    // 1) DÉTECTION DE TABLEAU
    // =======================

    /**
     * Détecte si le texte ressemble à un tableau (plusieurs colonnes séparées par
     * des espaces multiples ou des tabulations) et l'écrit dans Excel.
     */
    private boolean tryWriteAsTable(String text, XSSFWorkbook wb) {
        String[] lines = text.split("\\r?\\n");
        List<String[]> tableRows = new ArrayList<>();
        int maxCols = 0;

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;

            // Découpe sur 2+ espaces consécutifs ou tabulations (séparateurs de colonnes)
            String[] cells = trimmed.split("\\t|  +");
            if (cells.length > 1) {
                tableRows.add(cells);
                if (cells.length > maxCols) maxCols = cells.length;
            }
        }

        // On considère que c'est un tableau si au moins 50% des lignes ont 2+ colonnes
        long multiColLines = tableRows.size();
        long totalLines = 0;
        for (String line : lines) {
            if (!line.trim().isEmpty()) totalLines++;
        }

        if (multiColLines == 0 || totalLines == 0) return false;
        if ((double) multiColLines / totalLines < 0.5) return false;

        // Écriture dans Excel
        Sheet sheet = wb.createSheet("Tableau");
        int rIndex = 0;
        for (String[] cells : tableRows) {
            Row row = sheet.createRow(rIndex++);
            for (int c = 0; c < cells.length; c++) {
                row.createCell(c).setCellValue(cells[c] == null ? "" : cells[c].trim());
            }
        }

        // Auto-size des colonnes
        for (int c = 0; c < maxCols; c++) {
            sheet.autoSizeColumn(c);
        }

        return true;
    }

    // =======================
    // 2) FALLBACK TEXTE BRUT
    // =======================

    private void writeTextFallback(String text, XSSFWorkbook wb) {
        Sheet sheet = wb.createSheet("Texte");
        String[] lines = text.split("\\r?\\n");
        int rowIndex = 0;

        for (String line : lines) {
            String cleaned = (line == null) ? "" : line.trim();
            if (cleaned.isEmpty()) continue;

            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(cleaned);
        }

        sheet.autoSizeColumn(0);
    }

    // =======================
    // UTILITAIRES
    // =======================

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