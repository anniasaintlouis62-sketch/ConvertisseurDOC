/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ConvertisseurDOC.service;

import java.nio.file.Path;

public class PreviewService {

    private final LibreOfficeService libreOfficeService;

    public PreviewService(LibreOfficeService libreOfficeService) {
        this.libreOfficeService = libreOfficeService;
    }

    /**
     * Génère un fichier de prévisualisation (souvent PDF) pour affichage dans le navigateur.
     * @param outputFile fichier converti (pdf/docx/xlsx)
     * @param jobDir dossier du job (uploads/{jobId})
     * @return Path du fichier preview (souvent .pdf) ou null si pas possible
     */
    public Path buildPreview(Path outputFile, Path jobDir) throws Exception {
        String name = outputFile.getFileName().toString().toLowerCase();

        if (name.endsWith(".pdf")) {
            return outputFile; // preview direct
        }

        if (name.endsWith(".docx") || name.endsWith(".doc")) {
            // doc/docx -> pdf pour aperçu
            return libreOfficeService.convert(outputFile, "pdf", jobDir);
        }

        
        return null;
    }
}