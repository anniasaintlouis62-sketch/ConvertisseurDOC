package com.ConvertisseurDOC.Servlet;

import com.ConvertisseurDOC.model.ConversionJob;
import com.ConvertisseurDOC.model.ConversionType;
import com.ConvertisseurDOC.service.AppContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ResultServlet", urlPatterns = {"/result"})
public class ResultServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String jobId = req.getParameter("id");
        if (jobId == null || jobId.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/upload");
            return;
        }

        ConversionJob job = AppContext.storage().getJob(jobId);
        if (job == null) {
            req.setAttribute("error", "Job introuvable.");
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
            return;
        }

        // Télécharger = toujours le fichier final (XLSX, DOCX, PDF...)
        String downloadUrl = req.getContextPath() + "/download?id=" + jobId;

        // ===== APERÇU =====
        String previewUrl = null;

        // 1) Si un preview a été généré (pdf), on l’utilise
        if (job.getPreviewFileName() != null && !job.getPreviewFileName().trim().isEmpty()) {
            previewUrl = req.getContextPath() + "/preview?id=" + jobId;
        } else {
            // 2) Sinon : pour PDF->WORD et PDF->EXCEL on affiche le PDF original uploadé
            ConversionType type = job.getType();

            if (type == ConversionType.PDF_TO_WORD || type == ConversionType.PDF_TO_EXCEL) {
                previewUrl = req.getContextPath() + "/preview?id=" + jobId + "&mode=input";
            }
        }

        // URLs de partage
        String whatsappUrl = AppContext.share().buildWhatsappShareUrl(jobId);
        String telegramUrl = AppContext.share().buildTelegramShareUrl(jobId);

        req.setAttribute("job", job);
        req.setAttribute("previewUrl", previewUrl);
        req.setAttribute("downloadUrl", downloadUrl);
        req.setAttribute("whatsappUrl", whatsappUrl);
        req.setAttribute("telegramUrl", telegramUrl);

        req.getRequestDispatcher("/result.jsp").forward(req, resp);
    }
}