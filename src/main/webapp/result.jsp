<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.ConvertisseurDOC.model.ConversionJob" %>
<%
    ConversionJob job = (ConversionJob) request.getAttribute("job");
    String previewUrl = (String) request.getAttribute("previewUrl");
    String downloadUrl = (String) request.getAttribute("downloadUrl");
    String whatsappUrl = (String) request.getAttribute("whatsappUrl");
    String telegramUrl = (String) request.getAttribute("telegramUrl");
    boolean hasError = job != null && job.getErrorMessage() != null;
    boolean success = !hasError;
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Résultat — ConvertisseurDOC</title>
    <link href="https://fonts.googleapis.com/css2?family=Syne:wght@400;700;800&family=DM+Sans:wght@300;400;500&display=swap" rel="stylesheet">
    <style>
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

        :root {
            --ink: #0a0a0f;
            --paper: #f5f2eb;
            --accent: #e8471d;
            --green: #1a9e5c;
            --red: #d63b2f;
            --muted: #8a8880;
            --border: #d8d4cb;
        }

        body {
            font-family: 'DM Sans', sans-serif;
            background: var(--paper);
            color: var(--ink);
            min-height: 100vh;
            padding: 40px 20px;
            position: relative;
            overflow-x: hidden;
        }

        body::before {
            content: '';
            position: fixed;
            inset: 0;
            background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 256 256' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noise'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noise)' opacity='0.04'/%3E%3C/svg%3E");
            pointer-events: none;
            z-index: 0;
        }

        .bg-word {
            position: fixed;
            bottom: -80px;
            left: -20px;
            font-family: 'Syne', sans-serif;
            font-size: 300px;
            font-weight: 800;
            color: rgba(0,0,0,0.03);
            line-height: 1;
            pointer-events: none;
            user-select: none;
            z-index: 0;
        }

        .page {
            position: relative;
            z-index: 1;
            max-width: 860px;
            margin: 0 auto;
        }

        .topbar {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 40px;
            animation: slideDown 0.5s cubic-bezier(0.16,1,0.3,1) both;
        }

        .logo {
            font-family: 'Syne', sans-serif;
            font-weight: 800;
            font-size: 18px;
            letter-spacing: -0.02em;
        }

        .logo span { color: var(--accent); }

        .back-link {
            font-size: 13px;
            font-weight: 500;
            color: var(--muted);
            text-decoration: none;
            display: flex;
            align-items: center;
            gap: 6px;
            transition: color 0.2s;
        }

        .back-link:hover { color: var(--ink); }

        .status-banner {
            padding: 20px 28px;
            border-radius: 4px;
            border: 1px solid;
            display: flex;
            align-items: center;
            gap: 16px;
            margin-bottom: 32px;
            animation: slideUp 0.5s 0.1s cubic-bezier(0.16,1,0.3,1) both;
        }

        .status-banner.ok { background: #f0faf5; border-color: var(--green); color: var(--green); }
        .status-banner.err { background: #fff5f5; border-color: var(--red); color: var(--red); }

        .status-icon {
            width: 36px; height: 36px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            flex-shrink: 0;
        }

        .ok .status-icon { background: var(--green); }
        .err .status-icon { background: var(--red); }

        .status-icon svg {
            width: 18px; height: 18px;
            fill: none; stroke: #fff;
            stroke-width: 2.5;
            stroke-linecap: round;
            stroke-linejoin: round;
        }

        .status-text strong {
            display: block;
            font-family: 'Syne', sans-serif;
            font-weight: 700;
            font-size: 16px;
        }

        .status-text span { font-size: 13px; opacity: 0.8; }

        .main-grid {
            display: grid;
            grid-template-columns: 1fr 280px;
            gap: 24px;
            align-items: start;
        }

        @media (max-width: 700px) { .main-grid { grid-template-columns: 1fr; } }

        .preview-card {
            background: #fff;
            border: 1px solid var(--border);
            border-radius: 4px;
            box-shadow: 6px 6px 0 var(--ink);
            overflow: hidden;
            animation: slideUp 0.5s 0.2s cubic-bezier(0.16,1,0.3,1) both;
        }

        .preview-header {
            padding: 16px 20px;
            border-bottom: 1px solid var(--border);
            display: flex;
            align-items: center;
            justify-content: space-between;
        }

        .preview-header h3 {
            font-family: 'Syne', sans-serif;
            font-size: 14px;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0.08em;
        }

        .preview-dot {
            width: 8px; height: 8px;
            border-radius: 50%;
            background: var(--green);
            animation: pulse 2s ease infinite;
        }

        @keyframes pulse {
            0%, 100% { opacity: 1; transform: scale(1); }
            50% { opacity: 0.5; transform: scale(1.3); }
        }

        .preview-body { background: #f0ede6; }

        iframe { width: 100%; height: 520px; border: none; display: block; }

        .no-preview {
            height: 200px;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            gap: 12px;
            color: var(--muted);
            font-size: 14px;
        }

        .no-preview svg {
            width: 40px; height: 40px;
            stroke: var(--border); fill: none;
            stroke-width: 1.5;
            stroke-linecap: round;
            stroke-linejoin: round;
        }

        .sidebar {
            display: flex;
            flex-direction: column;
            gap: 16px;
            animation: slideUp 0.5s 0.3s cubic-bezier(0.16,1,0.3,1) both;
        }

        .side-card {
            background: #fff;
            border: 1px solid var(--border);
            border-radius: 4px;
            padding: 20px;
            box-shadow: 4px 4px 0 var(--ink);
        }

        .side-card h4 {
            font-family: 'Syne', sans-serif;
            font-size: 11px;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0.12em;
            color: var(--muted);
            margin-bottom: 14px;
        }

        .btn {
            display: flex;
            align-items: center;
            gap: 10px;
            width: 100%;
            padding: 13px 16px;
            border-radius: 4px;
            font-family: 'DM Sans', sans-serif;
            font-size: 14px;
            font-weight: 500;
            text-decoration: none;
            border: 1px solid var(--border);
            cursor: pointer;
            transition: all 0.2s ease;
            margin-bottom: 8px;
            background: var(--paper);
            color: var(--ink);
        }

        .btn:last-child { margin-bottom: 0; }

        .btn:hover {
            background: var(--ink);
            color: #fff;
            border-color: var(--ink);
            transform: translateX(4px);
        }

        .btn-primary {
            background: var(--ink);
            color: #fff;
            border-color: var(--ink);
            font-family: 'Syne', sans-serif;
            font-weight: 700;
            letter-spacing: 0.04em;
        }

        .btn-primary:hover {
            background: var(--accent);
            border-color: var(--accent);
            transform: translateX(4px);
        }

        .btn-icon {
            width: 28px; height: 28px;
            border-radius: 4px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 16px;
            flex-shrink: 0;
        }

        .btn-text { flex: 1; }
        .btn-arrow { font-size: 16px; opacity: 0.4; }

        .info-list { list-style: none; }

        .info-list li {
            display: flex;
            justify-content: space-between;
            padding: 8px 0;
            border-bottom: 1px solid var(--border);
            font-size: 13px;
        }

        .info-list li:last-child { border-bottom: none; }
        .info-list .key { color: var(--muted); }
        .info-list .val { font-weight: 500; }

        @keyframes slideUp {
            from { opacity: 0; transform: translateY(30px); }
            to { opacity: 1; transform: translateY(0); }
        }

        @keyframes slideDown {
            from { opacity: 0; transform: translateY(-20px); }
            to { opacity: 1; transform: translateY(0); }
        }
    </style>
</head>
<body>

<div class="bg-word">OK</div>

<div class="page">

    <div class="topbar">
        <div class="logo">Convertisseur<span>DOC</span></div>
        <a class="back-link" href="<%= request.getContextPath() %>/upload">← Nouvelle conversion</a>
    </div>

    <div class="status-banner <%= success ? "ok" : "err" %>">
        <div class="status-icon">
            <% if (success) { %>
                <svg viewBox="0 0 24 24"><polyline points="20 6 9 17 4 12"/></svg>
            <% } else { %>
                <svg viewBox="0 0 24 24"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            <% } %>
        </div>
        <div class="status-text">
            <strong><%= success ? "Conversion réussie !" : "Échec de la conversion" %></strong>
            <span>
                <% if (success) { %>Ton document est prêt.
                <% } else if (job != null) { %><%= job.getErrorMessage() %>
                <% } %>
            </span>
        </div>
    </div>

    <div class="main-grid">

        <div class="preview-card">
            <div class="preview-header">
                <h3>Aperçu du document</h3>
                <% if (previewUrl != null) { %><div class="preview-dot"></div><% } %>
            </div>
            <div class="preview-body">
                <% if (previewUrl != null) { %>
                    <iframe src="<%= previewUrl %>"></iframe>
                <% } else { %>
                    <div class="no-preview">
                        <svg viewBox="0 0 24 24">
                            <path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/>
                            <polyline points="14 2 14 8 20 8"/>
                        </svg>
                        Aperçu non disponible pour ce format
                    </div>
                <% } %>
            </div>
        </div>

        <div class="sidebar">

            <div class="side-card">
                <h4>Actions</h4>
                <% if (downloadUrl != null) { %>
                <a class="btn btn-primary" href="<%= downloadUrl %>">
                    <span class="btn-icon">⬇</span>
                    <span class="btn-text">Télécharger</span>
                    <span class="btn-arrow">→</span>
                </a>
                <% } %>
                <button class="btn" onclick="shareHybrid('<%= downloadUrl != null ? downloadUrl : "" %>', '<%= whatsappUrl != null ? whatsappUrl : "" %>')">
                    <span class="btn-icon">💬</span>
                    <span class="btn-text">Partager via WhatsApp</span>
                    <span class="btn-arrow">→</span>
                </button>
                <% if (telegramUrl != null) { %>
                <a class="btn" href="<%= telegramUrl %>" target="_blank">
                    <span class="btn-icon">✈</span>
                    <span class="btn-text">Partager via Telegram</span>
                    <span class="btn-arrow">→</span>
                </a>
                <% } %>
            </div>

            <% if (job != null) { %>
            <div class="side-card">
                <h4>Détails</h4>
                <ul class="info-list">
                    <li><span class="key">Statut</span><span class="val"><%= job.getStatus() %></span></li>
                    <% if (job.getErrorMessage() == null) { %>
                    <li><span class="key">Résultat</span><span class="val" style="color:var(--green)">✓ Succès</span></li>
                    <% } %>
                </ul>
            </div>
            <% } %>

            <div class="side-card">
                <h4>Autre fichier ?</h4>
                <a class="btn btn-primary" href="<%= request.getContextPath() %>/upload">
                    <span class="btn-icon">🔄</span>
                    <span class="btn-text">Nouvelle conversion</span>
                    <span class="btn-arrow">→</span>
                </a>
            </div>

        </div>
    </div>
</div>

<script>
    function shareHybrid(downloadUrl, whatsappUrl) {
        if (downloadUrl) {
            const a = document.createElement("a");
            a.href = downloadUrl; a.download = "";
            document.body.appendChild(a); a.click();
            document.body.removeChild(a);
        }
        if (whatsappUrl) setTimeout(() => window.open(whatsappUrl, "_blank"), 1200);
    }
</script>
</body>
</html>