package com.ConvertisseurDOC.util;

import com.ConvertisseurDOC.model.ConversionType;

public class FileValidator {

    /**
     * Vérifie si le fichier est autorisé selon le type de conversion
     */
    public boolean isAllowed(String fileName, ConversionType type) {
        if (fileName == null || type == null) {
            return false;
        }

        String ext = getExtension(fileName);

        if (ext == null) {
            return false;
        }

        switch (type) {

            case WORD_TO_PDF:
                return isWord(ext);

            case PDF_TO_WORD:
            case PDF_TO_EXCEL:
                return ext.equals("pdf");

            default:
                return false;
        }
    }

    /**
     * Récupère l'extension du fichier
     */
    private String getExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index < 0) {
            return null;
        }
        return fileName.substring(index + 1).toLowerCase();
    }

    /**
     * Extensions Word autorisées
     */
    private boolean isWord(String ext) {
        return ext.equals("doc") || ext.equals("docx");
    }
}