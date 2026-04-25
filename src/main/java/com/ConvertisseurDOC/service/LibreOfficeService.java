package com.ConvertisseurDOC.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;          // ✅ Java 8
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LibreOfficeService {

    // ✅ On cherche d'abord dans les variables d'environnement, sinon on utilise le chemin Windows par défaut
    private static final String DEFAULT_SOFFICE =
            System.getenv("SOFFICE_PATH") != null ? System.getenv("SOFFICE_PATH") : "C:\\Program Files\\LibreOffice\\program\\soffice.exe";

    private final String sofficeCmd;

    public LibreOfficeService() {
        this.sofficeCmd = DEFAULT_SOFFICE;
    }

    public LibreOfficeService(String sofficeCmd) {
        this.sofficeCmd = sofficeCmd;
    }

    public Path convert(Path inputFile, String targetExt, Path outDir) throws Exception {
        Files.createDirectories(outDir);

        // ✅ Java 8: Paths.get(...)
        Path sofficePath = Paths.get(sofficeCmd);
        if (!Files.exists(sofficePath)) {
            throw new RuntimeException("Impossible de lancer LibreOffice. Vérifie sofficeCmd = "
                    + sofficeCmd + " (fichier introuvable)");
        }

        List<String> cmd = new ArrayList<>();
        cmd.add(sofficeCmd);
        cmd.add("--headless");
        cmd.add("--nologo");
        cmd.add("--nolockcheck");
        cmd.add("--nodefault");
        cmd.add("--nofirststartwizard");
        cmd.add("--convert-to");
        cmd.add(targetExt);
        cmd.add("--outdir");
        cmd.add(outDir.toAbsolutePath().toString());
        cmd.add(inputFile.toAbsolutePath().toString());

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);

        Process p;
        try {
            p = pb.start();
        } catch (Exception ex) {
            throw new RuntimeException("Impossible de lancer LibreOffice. Vérifie sofficeCmd = "
                    + sofficeCmd + "\nErreur: " + ex.getMessage(), ex);
        }

        StringBuilder logs = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                logs.append(line).append('\n');
            }
        }

        int code = p.waitFor();
        if (code != 0) {
            throw new RuntimeException("LibreOffice a échoué (code=" + code + ")\n" + logs);
        }

        String baseName = stripExtension(inputFile.getFileName().toString());
        Path expected = outDir.resolve(baseName + "." + targetExt);

        if (Files.exists(expected)) {
            return expected;
        }

        // Fallback : prendre le fichier le plus récent avec la bonne extension
        return Files.list(outDir)
                .filter(f -> f.getFileName().toString().toLowerCase().endsWith("." + targetExt.toLowerCase()))
                .max(Comparator.comparingLong(f -> f.toFile().lastModified()))
                .orElseThrow(() -> new RuntimeException(
                        "Fichier converti introuvable dans " + outDir + "\nLogs:\n" + logs));
    }

    private String stripExtension(String name) {
        int i = name.lastIndexOf('.');
        return (i > 0) ? name.substring(0, i) : name;
    }
}