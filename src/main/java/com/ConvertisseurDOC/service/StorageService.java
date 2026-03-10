package com.ConvertisseurDOC.service;

import com.ConvertisseurDOC.model.ConversionJob;
import com.ConvertisseurDOC.model.ConversionType;
import com.ConvertisseurDOC.repository.InMemoryJobRepository;
import com.ConvertisseurDOC.repository.JobRepository;

import java.io.InputStream;
import java.nio.file.*;
import java.util.UUID;

public class StorageService {

    // ⚠️ volontairement relatif pour voir EXACTEMENT où Tomcat écrit
    private static final String BASE_DIR = "uploads";

    private final JobRepository jobRepository;

    public StorageService() {
        this.jobRepository = new InMemoryJobRepository();
        System.out.println("=== StorageService INITIALIZED ===");
        System.out.println("Working directory = " + Paths.get("").toAbsolutePath());
    }

    /**
     * Crée un nouveau job
     */
    public ConversionJob createJob(ConversionType type, String inputFileName) {
        String id = UUID.randomUUID().toString();
        ConversionJob job = new ConversionJob(id, type);
        job.setInputFileName(inputFileName);

        jobRepository.save(job);

        System.out.println("JOB CREATED:");
        System.out.println("  jobId = " + id);
        System.out.println("  type = " + type);
        System.out.println("  inputFileName = " + inputFileName);

        return job;
    }

    /**
     * Dossier du job
     */
    public Path getJobDir(String jobId) {
        Path dir = Paths.get(BASE_DIR, jobId).toAbsolutePath().normalize();
        System.out.println("JOB DIR = " + dir);
        return dir;
    }

    /**
     * Sauvegarde le fichier uploadé
     */
    public Path saveUploadedFile(String jobId, String fileName, InputStream inputStream) throws Exception {
        Path jobDir = getJobDir(jobId);
        Files.createDirectories(jobDir);

        Path target = jobDir.resolve(fileName).toAbsolutePath().normalize();

        Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);

        System.out.println("UPLOAD SAVED:");
        System.out.println("  fileName = " + fileName);
        System.out.println("  path = " + target);
        System.out.println("  exists = " + Files.exists(target));

        return target;
    }

    /**
     * Enregistre le fichier de sortie
     */
    public void setOutputFile(String jobId, String outputFileName) {
        ConversionJob job = jobRepository.findById(jobId);
        if (job != null) {
            job.setOutputFileName(outputFileName);
            jobRepository.save(job);

            System.out.println("OUTPUT FILE SET:");
            System.out.println("  jobId = " + jobId);
            System.out.println("  outputFileName = " + outputFileName);
            System.out.println("  expectedPath = "
                    + getJobDir(jobId).resolve(outputFileName).toAbsolutePath());
        }
    }

    /**
     * Enregistre le fichier preview
     */
    public void setPreviewFile(String jobId, String previewFileName) {
        ConversionJob job = jobRepository.findById(jobId);
        if (job != null) {
            job.setPreviewFileName(previewFileName);
            jobRepository.save(job);

            System.out.println("PREVIEW FILE SET:");
            System.out.println("  jobId = " + jobId);
            System.out.println("  previewFileName = " + previewFileName);
        }
    }

    /**
     * Récupère un job
     */
    public ConversionJob getJob(String jobId) {
        return jobRepository.findById(jobId);
    }

    /**
     * Récupère le fichier de sortie
     */
    public Path getOutputFile(String jobId) {
        ConversionJob job = jobRepository.findById(jobId);
        if (job == null || job.getOutputFileName() == null) {
            System.out.println("GET OUTPUT FILE: job or filename NULL");
            return null;
        }

        Path file = getJobDir(jobId)
                .resolve(job.getOutputFileName())
                .toAbsolutePath()
                .normalize();

        System.out.println("GET OUTPUT FILE:");
        System.out.println("  jobId = " + jobId);
        System.out.println("  file = " + file);
        System.out.println("  exists = " + Files.exists(file));

        return file;
    }
}