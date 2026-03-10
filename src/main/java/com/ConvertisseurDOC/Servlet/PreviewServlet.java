package com.ConvertisseurDOC.Servlet;

import com.ConvertisseurDOC.model.ConversionJob;
import com.ConvertisseurDOC.service.AppContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@WebServlet(name = "PreviewServlet", urlPatterns = {"/preview"})
public class PreviewServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String jobId = req.getParameter("id");
        if (jobId == null || jobId.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètre id manquant.");
            return;
        }

        String mode = req.getParameter("mode"); // "input" ou null

        ConversionJob job = AppContext.storage().getJob(jobId);
        if (job == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Job introuvable.");
            return;
        }

        Path jobDir = AppContext.storage().getJobDir(jobId);

        Path file;
        String filename;

        // ====== 1) Aperçu du PDF original (upload) ======
        if ("input".equalsIgnoreCase(mode)) {
            // IMPORTANT: adapter si ton modèle n'a pas getInputFileName()
            filename = job.getInputFileName();

            if (filename == null || filename.trim().isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Fichier upload introuvable.");
                return;
            }

            file = jobDir.resolve(filename);

            // Normalement c'est un PDF si tu fais PDF->Word
            resp.setContentType("application/pdf");
            resp.setHeader("Content-Disposition", "inline; filename=\"" + filename + "\"");
        }
        // ====== 2) Aperçu généré (previewFileName) ======
        else {
            filename = job.getPreviewFileName();

            if (filename == null || filename.trim().isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Aperçu introuvable.");
                return;
            }

            file = jobDir.resolve(filename);

            
            resp.setContentType("application/pdf");
            resp.setHeader("Content-Disposition", "inline; filename=\"" + filename + "\"");
        }

        if (!Files.exists(file)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Fichier introuvable sur disque.");
            return;
        }

        resp.setContentLengthLong(Files.size(file));
        Files.copy(file, resp.getOutputStream());
    }
}