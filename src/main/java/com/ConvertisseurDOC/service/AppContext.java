package com.ConvertisseurDOC.service;

public class AppContext {

    private static final StorageService STORAGE_SERVICE = new StorageService();
    private static final LibreOfficeService LIBRE_OFFICE_SERVICE = new LibreOfficeService();
    private static final PreviewService PREVIEW_SERVICE = new PreviewService(LIBRE_OFFICE_SERVICE);
    private static final ConversionService CONVERSION_SERVICE = new ConversionService(STORAGE_SERVICE);
    
    // ✅ Utilise l'URL de production si disponible, sinon localhost
    private static final String APP_URL = 
            System.getenv("APP_URL") != null ? System.getenv("APP_URL") : "http://localhost:8080/ConvertisseurDOC";

    private static final ShareLinkService SHARE_LINK_SERVICE =
            new ShareLinkService(APP_URL);

    public static StorageService storage() { return STORAGE_SERVICE; }
    public static LibreOfficeService libreOffice() { return LIBRE_OFFICE_SERVICE; }
    public static PreviewService preview() { return PREVIEW_SERVICE; }
    public static ConversionService conversion() { return CONVERSION_SERVICE; }
    public static ShareLinkService share() { return SHARE_LINK_SERVICE; }

    public static Object download() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}