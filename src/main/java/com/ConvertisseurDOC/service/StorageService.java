package com.ConvertisseurDOC.service;

import com.ConvertisseurDOC.model.ConversionJob;
import com.ConvertisseurDOC.model.ConversionType;
import com.ConvertisseurDOC.repository.InMemoryJobRepository;
import com.ConvertisseurDOC.repository.JobRepository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.stream.Stream;

public class StorageService {

    // ✅ On cherche d'abord dans les variables d'environnement, sinon on utilise "uploads" par défaut
    private static final String BASE_DIR = 
            System.getenv("STORAGE_BASE_DIR") != null ? System.getenv("STORAGE_BASE_DIR") : "uploads";

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
        // ✅ Nettoyage automatique des vieux fichiers avant chaque nouveau job
        cleanupOldJobs();

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

    /**
     * ✅ Nettoie les fichiers vieux de plus de 30 minutes
     * Essentiel pour le plan gratuit de Render (512MB RAM/Disk)
     */
    private void cleanupOldJobs() {
        Path root = Paths.get(BASE_DIR);
        if (!Files.exists(root)) return;

        Instant threshold = Instant.now().minus(30, ChronoUnit.MINUTES);

        try (Stream<Path> paths = Files.list(root)) {
            paths.filter(Files::isDirectory).forEach(p -> {
                try {
                    Instant lastModified = Files.getLastModifiedTime(p).toInstant();
                    if (lastModified.isBefore(threshold)) {
                        deleteDir(p);
                        System.out.println("CLEANUP: deleted old job dir " + p);
                    }
                } catch (IOException e) {
                    System.err.println("CLEANUP: failed to check/delete " + p + ": " + e.getMessage());
                }
            });
        } catch (IOException e) {
            System.err.println("CLEANUP: failed to list root " + root + ": " + e.getMessage());
        }
    }

    private void deleteDir(Path path) throws IOException {
        try (Stream<Path> walk = Files.walk(path)) {
            walk.sorted(java.util.Comparator.reverseOrder())
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException e) {
                        System.err.println("CLEANUP: failed to delete file " + p);
                    }
                });
        }
    }
}