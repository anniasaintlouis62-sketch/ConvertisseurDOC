package com.ConvertisseurDOC.Servlet;

import com.ConvertisseurDOC.model.ConversionJob;
import com.ConvertisseurDOC.service.AppContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@WebServlet(name = "DownloadServlet", urlPatterns = {"/download"})
public class DownloadServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String jobId = req.getParameter("id");
        if (jobId == null || jobId.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètre id manquant.");
            return;
        }

        ConversionJob job = AppContext.storage().getJob(jobId);
        if (job == null || job.getOutputFileName() == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Fichier converti introuvable.");
            return;
        }

      Path file = AppContext.storage().getOutputFile(jobId);

System.out.println("JOB ID = " + jobId);
System.out.println("OUTPUT NAME = " + job.getOutputFileName());
System.out.println("PATH CHECK = " + (file == null ? "null" : file.toAbsolutePath()));

if (file == null || !Files.exists(file)) {
    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Fichier converti introuvable sur disque.");
    return;
}

        String fileName = job.getOutputFileName();

        // Content-Type (mieux que deviner à la main si possible)
        String mime = Files.probeContentType(file);
        if (mime == null) mime = guessContentType(fileName);

        resp.setContentType(mime);
        resp.setHeader("X-Content-Type-Options", "nosniff");

        // ✅ Content-Disposition robuste (support accents + espaces)
        String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()).replace("+", "%20");
        resp.setHeader("Content-Disposition",
                "attachment; filename=\"" + fileName.replace("\"", "") + "\"; filename*=UTF-8''" + encoded);

        resp.setContentLengthLong(Files.size(file));

        // ✅ Stream + flush
        try (OutputStream os = resp.getOutputStream()) {
            Files.copy(file, os);
            os.flush();
        }
    }

    private String guessContentType(String name) {
        String n = name.toLowerCase();
        if (n.endsWith(".pdf")) return "application/pdf";
        if (n.endsWith(".docx")) return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        if (n.endsWith(".doc")) return "application/msword";
        if (n.endsWith(".xlsx")) return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        return "application/octet-stream";
    }
}