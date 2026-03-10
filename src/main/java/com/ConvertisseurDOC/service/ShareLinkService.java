package com.ConvertisseurDOC.service;

import java.net.URLEncoder;

public class ShareLinkService {

    // Exemple: "https://ton-site.com/PdfConverterWeb"
    // En local tu peux mettre: "http://localhost:8080/PdfConverterWeb"
    private final String publicBaseUrl;

    public ShareLinkService(String publicBaseUrl) {
        this.publicBaseUrl = removeTrailingSlash(publicBaseUrl);
    }

    /**
     * Lien public direct vers ton DownloadServlet
     * Exemple: https://ton-site.com/app/download?id=xxx
     */
    public String buildDownloadUrl(String jobId) {
        return publicBaseUrl + "/download?id=" + urlEncode(jobId);
    }

    public String buildWhatsappShareUrl(String jobId) {
        String link = buildDownloadUrl(jobId);
        return "https://wa.me/?text=" + urlEncode(link);
    }

    public String buildTelegramShareUrl(String jobId) {
        String link = buildDownloadUrl(jobId);
        return "https://t.me/share/url?url=" + urlEncode(link) + "&text=" + urlEncode("Document converti");
    }

    private String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            return value;
        }
    }

    private String removeTrailingSlash(String s) {
        if (s == null) return "";
        while (s.endsWith("/")) s = s.substring(0, s.length() - 1);
        return s;
    }
}