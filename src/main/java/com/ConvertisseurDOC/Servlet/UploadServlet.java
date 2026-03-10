package com.ConvertisseurDOC.Servlet;

import com.ConvertisseurDOC.model.ConversionJob;
import com.ConvertisseurDOC.model.ConversionType;
import com.ConvertisseurDOC.service.AppContext;
import com.ConvertisseurDOC.util.FileValidator;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import java.io.IOException;
import java.nio.file.Path;

@WebServlet(name = "UploadServlet", urlPatterns = {"/upload"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1,  // 1MB
        maxFileSize = 1024 * 1024 * 50,       // 50MB
        maxRequestSize = 1024 * 1024 * 60     // 60MB
)
public class UploadServlet extends HttpServlet {

    private FileValidator fileValidator;

    @Override
    public void init() throws ServletException {
        this.fileValidator = new FileValidator();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/upload.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            String typeParam = req.getParameter("type");
            if (typeParam == null || typeParam.trim().isEmpty()) {
                forwardError(req, resp, "Veuillez choisir un type de conversion.");
                return;
            }

            ConversionType type;
            try {
                type = ConversionType.valueOf(typeParam);
            } catch (IllegalArgumentException ex) {
                forwardError(req, resp, "Type de conversion invalide.");
                return;
            }

            Part filePart = req.getPart("file");
            if (filePart == null || filePart.getSize() == 0) {
                forwardError(req, resp, "Veuillez sélectionner un fichier.");
                return;
            }

            String fileName = getSubmittedFileNameSafe(filePart);
            if (fileName == null || fileName.trim().isEmpty()) {
                forwardError(req, resp, "Nom de fichier invalide.");
                return;
            }

            if (!fileValidator.isAllowed(fileName, type)) {
                forwardError(req, resp, "Extension non autorisée pour ce type de conversion.");
                return;
            }

            // 1) créer le job
            ConversionJob job = AppContext.storage().createJob(type, fileName);

            // 2) enregistrer le fichier uploadé
            Path inputPath = AppContext.storage().saveUploadedFile(job.getId(), fileName, filePart.getInputStream());

            // 3) convertir
            AppContext.conversion().convert(job.getId(), inputPath, type);

            // 4) rediriger vers résultat
            resp.sendRedirect(req.getContextPath() + "/result?id=" + job.getId());

        } catch (Exception e) {
            e.printStackTrace();
            forwardError(req, resp, "Erreur : " + e.getMessage());
        }
    }

    private void forwardError(HttpServletRequest req, HttpServletResponse resp, String message)
            throws ServletException, IOException {
        req.setAttribute("error", message);
        req.getRequestDispatcher("/error.jsp").forward(req, resp);
    }

    private String getSubmittedFileNameSafe(Part part) {
        String name = part.getSubmittedFileName();
        if (name == null) return null;

        name = name.replace("\\", "/");
        int slash = name.lastIndexOf('/');
        if (slash >= 0) name = name.substring(slash + 1);

        return name.trim();
    }
}