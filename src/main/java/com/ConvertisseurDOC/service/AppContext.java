package com.ConvertisseurDOC.service;

public class AppContext {

    private static final StorageService STORAGE_SERVICE = new StorageService();
    private static final LibreOfficeService LIBRE_OFFICE_SERVICE = new LibreOfficeService();
    private static final PreviewService PREVIEW_SERVICE = new PreviewService(LIBRE_OFFICE_SERVICE);
    private static final ConversionService CONVERSION_SERVICE = new ConversionService(STORAGE_SERVICE);
    
    // Mets ici l’URL publique de ton appli en production
    // En local: http://localhost:8080/NOM_DU_PROJET
    private static final ShareLinkService SHARE_LINK_SERVICE =
            new ShareLinkService("http://localhost:8080/PdfConverterWeb");

    public static StorageService storage() { return STORAGE_SERVICE; }
    public static LibreOfficeService libreOffice() { return LIBRE_OFFICE_SERVICE; }
    public static PreviewService preview() { return PREVIEW_SERVICE; }
    public static ConversionService conversion() { return CONVERSION_SERVICE; }
    public static ShareLinkService share() { return SHARE_LINK_SERVICE; }

    public static Object download() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}