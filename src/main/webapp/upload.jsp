
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ConvertisseurDOC</title>
    <link href="https://fonts.googleapis.com/css2?family=Syne:wght@400;700;800&family=DM+Sans:wght@300;400;500&display=swap" rel="stylesheet">
    <style>
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

        :root {
            --ink: #0a0a0f;
            --paper: #f5f2eb;
            --accent: #e8471d;
            --accent2: #1d6fe8;
            --muted: #8a8880;
            --border: #d8d4cb;
        }

        body {
            font-family: 'DM Sans', sans-serif;
            background: var(--paper);
            color: var(--ink);
            min-height: 100vh;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
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

        .bg-number {
            position: fixed;
            top: -60px;
            right: -40px;
            font-family: 'Syne', sans-serif;
            font-size: 400px;
            font-weight: 800;
            color: rgba(0,0,0,0.03);
            line-height: 1;
            pointer-events: none;
            user-select: none;
            z-index: 0;
        }

        .wrapper {
            position: relative;
            z-index: 1;
            width: 100%;
            max-width: 560px;
        }

        .masthead { margin-bottom: 48px; }

        .masthead-tag {
            display: inline-block;
            font-size: 11px;
            font-weight: 500;
            letter-spacing: 0.15em;
            text-transform: uppercase;
            color: var(--accent);
            border: 1px solid var(--accent);
            padding: 4px 10px;
            border-radius: 2px;
            margin-bottom: 16px;
        }

        .masthead h1 {
            font-family: 'Syne', sans-serif;
            font-size: clamp(36px, 6vw, 56px);
            font-weight: 800;
            line-height: 1;
            letter-spacing: -0.03em;
        }

        .masthead h1 span { color: var(--accent); }

        .masthead p {
            margin-top: 12px;
            font-size: 15px;
            color: var(--muted);
            font-weight: 300;
        }

        .card {
            background: #fff;
            border: 1px solid var(--border);
            border-radius: 4px;
            padding: 40px;
            box-shadow: 6px 6px 0 var(--ink);
            transition: box-shadow 0.2s ease, transform 0.2s ease;
        }

        .card:hover {
            box-shadow: 8px 8px 0 var(--ink);
            transform: translate(-2px, -2px);
        }

        .drop-zone {
            border: 2px dashed var(--border);
            border-radius: 4px;
            padding: 48px 20px;
            text-align: center;
            cursor: pointer;
            transition: all 0.25s ease;
            position: relative;
            background: var(--paper);
        }

        .drop-zone:hover, .drop-zone.drag-over {
            border-color: var(--accent);
            background: #fff8f6;
        }

        .drop-zone input[type="file"] {
            position: absolute;
            inset: 0;
            opacity: 0;
            cursor: pointer;
            width: 100%;
            height: 100%;
        }

        .drop-icon {
            width: 52px;
            height: 52px;
            margin: 0 auto 16px;
            background: var(--ink);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            transition: transform 0.3s ease;
        }

        .drop-zone:hover .drop-icon { transform: translateY(-4px); }

        .drop-icon svg {
            width: 24px; height: 24px;
            fill: none; stroke: #fff;
            stroke-width: 2;
            stroke-linecap: round;
            stroke-linejoin: round;
        }

        .drop-title {
            font-family: 'Syne', sans-serif;
            font-size: 17px;
            font-weight: 700;
        }

        .drop-sub {
            margin-top: 6px;
            font-size: 13px;
            color: var(--muted);
        }

        .file-selected {
            margin-top: 10px;
            font-size: 13px;
            font-weight: 500;
            color: var(--accent);
            display: none;
        }

        .divider {
            display: flex;
            align-items: center;
            gap: 12px;
            margin: 28px 0;
            color: var(--muted);
            font-size: 12px;
            letter-spacing: 0.08em;
            text-transform: uppercase;
        }

        .divider::before, .divider::after {
            content: '';
            flex: 1;
            height: 1px;
            background: var(--border);
        }

        .field-label {
            font-size: 11px;
            font-weight: 500;
            letter-spacing: 0.12em;
            text-transform: uppercase;
            color: var(--muted);
            margin-bottom: 8px;
            display: block;
        }

        .select-wrapper { position: relative; }

        .select-wrapper::after {
            content: '';
            position: absolute;
            right: 16px;
            top: 50%;
            transform: translateY(-50%);
            width: 0; height: 0;
            border-left: 5px solid transparent;
            border-right: 5px solid transparent;
            border-top: 6px solid var(--ink);
            pointer-events: none;
        }

        select {
            width: 100%;
            padding: 14px 40px 14px 16px;
            border: 1px solid var(--border);
            border-radius: 4px;
            font-family: 'DM Sans', sans-serif;
            font-size: 14px;
            font-weight: 500;
            background: var(--paper);
            color: var(--ink);
            appearance: none;
            cursor: pointer;
            transition: border-color 0.2s;
        }

        select:focus { outline: none; border-color: var(--ink); }

        .btn-submit {
            margin-top: 32px;
            width: 100%;
            padding: 18px;
            border: 2px solid var(--ink);
            border-radius: 4px;
            font-family: 'Syne', sans-serif;
            font-weight: 700;
            font-size: 15px;
            letter-spacing: 0.04em;
            text-transform: uppercase;
            cursor: pointer;
            background: var(--ink);
            color: var(--paper);
            box-shadow: 4px 4px 0 var(--accent);
            transition: all 0.2s ease;
            position: relative;
            overflow: hidden;
        }

        .btn-submit::after {
            content: '';
            position: absolute;
            inset: 0;
            background: var(--accent);
            transform: translateX(-101%);
            transition: transform 0.35s cubic-bezier(0.4,0,0.2,1);
            z-index: 0;
        }

        .btn-submit:hover::after { transform: translateX(0); }

        .btn-submit span { position: relative; z-index: 1; }

        .btn-submit:hover {
            color: #fff;
            box-shadow: 6px 6px 0 var(--ink);
            transform: translate(-2px, -2px);
        }

        .footer-note {
            margin-top: 32px;
            text-align: center;
            font-size: 12px;
            color: var(--muted);
        }

        .btn-submit.loading span::before {
            content: '';
            display: inline-block;
            width: 14px; height: 14px;
            border: 2px solid rgba(255,255,255,0.4);
            border-top-color: #fff;
            border-radius: 50%;
            animation: spin 0.7s linear infinite;
            margin-right: 10px;
            vertical-align: middle;
        }

        @keyframes spin { to { transform: rotate(360deg); } }

        @keyframes slideUp {
            from { opacity: 0; transform: translateY(40px); }
            to { opacity: 1; transform: translateY(0); }
        }

        .wrapper { animation: slideUp 0.6s cubic-bezier(0.16,1,0.3,1) both; }
        .masthead { animation: slideUp 0.6s 0.05s cubic-bezier(0.16,1,0.3,1) both; }
        .card { animation: slideUp 0.6s 0.15s cubic-bezier(0.16,1,0.3,1) both; }
    </style>
</head>
<body>

<div class="bg-number">∞</div>

<div class="wrapper">
    <div class="masthead">
        <div class="masthead-tag">Outil de conversion</div>
        <h1>Convertis<br><span>tes docs.</span></h1>
        <p>PDF · Word · Excel — rapide, propre, sans prise de tête.</p>
    </div>

    <div class="card">
        <form action="<%= request.getContextPath() %>/upload"
              method="post"
              enctype="multipart/form-data"
              id="uploadForm">

            <div class="drop-zone" id="dropZone">
                <input type="file" name="file" id="fileInput" required accept=".pdf,.doc,.docx,.xls,.xlsx">
                <div class="drop-icon">
                    <svg viewBox="0 0 24 24">
                        <path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4"/>
                        <polyline points="17 8 12 3 7 8"/>
                        <line x1="12" y1="3" x2="12" y2="15"/>
                    </svg>
                </div>
                <div class="drop-title">Glisse ton fichier ici</div>
                <div class="drop-sub">ou clique pour parcourir · max 50 MB</div>
                <div class="file-selected" id="fileName">—</div>
            </div>

            <div class="divider">Format de conversion</div>

            <label class="field-label">Choisir le type</label>
            <div class="select-wrapper">
                <select name="type" required>
                    <option value="WORD_TO_PDF">Word → PDF</option>
                    <option value="PDF_TO_WORD">PDF → Word</option>
                    <option value="PDF_TO_EXCEL">PDF → Excel</option>
                </select>
            </div>

            <button class="btn-submit" type="submit" id="submitBtn">
                <span>Lancer la conversion →</span>
            </button>
        </form>
    </div>

    <div class="footer-note">© 2026 ConvertisseurDOC &nbsp;·&nbsp; Java &amp; Tomcat</div>
</div>

<script>
    const fileInput = document.getElementById('fileInput');
    const dropZone  = document.getElementById('dropZone');
    const fileName  = document.getElementById('fileName');
    const submitBtn = document.getElementById('submitBtn');
    const form      = document.getElementById('uploadForm');

    fileInput.addEventListener('change', () => {
        if (fileInput.files[0]) {
            fileName.textContent = '📎 ' + fileInput.files[0].name;
            fileName.style.display = 'block';
            dropZone.style.borderColor = 'var(--accent)';
        }
    });

    ['dragover','dragenter'].forEach(e => dropZone.addEventListener(e, ev => {
        ev.preventDefault(); dropZone.classList.add('drag-over');
    }));
    ['dragleave','drop'].forEach(e => dropZone.addEventListener(e, ev => {
        ev.preventDefault(); dropZone.classList.remove('drag-over');
    }));

    form.addEventListener('submit', () => {
        submitBtn.classList.add('loading');
        submitBtn.disabled = true;
        submitBtn.querySelector('span').textContent = 'Conversion en cours…';
    });
</script>
</body>
</html>