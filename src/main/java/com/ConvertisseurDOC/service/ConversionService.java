package com.ConvertisseurDOC.service;

import com.ConvertisseurDOC.model.ConversionJob;
import com.ConvertisseurDOC.model.ConversionType;
import com.ConvertisseurDOC.model.JobStatus;

import java.nio.file.Files;
import java.nio.file.Path;

public class ConversionService {

    private final StorageService storageService;
    private final LibreOfficeService libreOfficeService;
    private final PdfToWordService pdfToWordService;
    private final PdfToExcelService pdfToExcelService;
    public ConversionService(StorageService storageService) {
        this.storageService = storageService;
        this.libreOfficeService = new LibreOfficeService();   // Word->PDF ok
        this.pdfToWordService = new PdfToWordService();       // PDF->Word en Java
        this.pdfToExcelService = new PdfToExcelService();
    }

    public void convert(String jobId, Path inputPath, ConversionType type) throws Exception {

        ConversionJob job = storageService.getJob(jobId);
        if (job == null) {
            throw new IllegalArgumentException("Job introuvable: " + jobId);
        }

        try {
            job.setStatus(JobStatus.PROCESSING);

            Path jobDir = storageService.getJobDir(jobId);
            Files.createDirectories(jobDir);

            Path output = null;
            Path preview = null;

            switch (type) {
                case WORD_TO_PDF:
                    output = libreOfficeService.convert(inputPath, "pdf", jobDir);
                    preview = output; // preview = pdf
                    break;

                case PDF_TO_WORD:
                    // ✅ Conversion Java (PDFBox -> DOCX)
                    output = pdfToWordService.convertPdfToDocx(inputPath, jobDir);

                    // preview = PDF original (pas besoin de générer un pdf)
                    preview = null;
                    break;

                case PDF_TO_EXCEL:
                    // Option simple (à améliorer plus tard avec Tabula)
                    output = pdfToExcelService.convertPdfToXlsx(inputPath, "xlsx", jobDir);
                    preview = null;
                    break;

                default:
                    throw new IllegalArgumentException("Type non supporté: " + type);
            }

            // ✅ LOGS + vérification obligatoire
            System.out.println("=== CONVERSION RESULT ===");
            System.out.println("jobId=" + jobId);
            System.out.println("type=" + type);
            System.out.println("inputPath=" + inputPath.toAbsolutePath());
            System.out.println("outputPath=" + (output == null ? "null" : output.toAbsolutePath()));
            System.out.println("outputExists=" + (output != null && Files.exists(output)));

            if (output == null || !Files.exists(output)) {
                // IMPORTANT: ne pas enregistrer un output qui n'existe pas
                throw new RuntimeException("Conversion terminée mais fichier output introuvable sur disque.");
            }

            storageService.setOutputFile(jobId, output.getFileName().toString());

            if (preview != null && Files.exists(preview)) {
                storageService.setPreviewFile(jobId, preview.getFileName().toString());
            }

            job.setStatus(JobStatus.DONE);

        } catch (Exception e) {
            job.setStatus(JobStatus.FAILED);
            job.setErrorMessage(e.getMessage());
            throw e;
        }
    }
}