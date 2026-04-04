const HOP_BY_HOP_HEADERS = new Set([
  'connection',
  'keep-alive',
  'proxy-authenticate',
  'proxy-authorization',
  'te',
  'trailers',
  'transfer-encoding',
  'upgrade',
  'host',
  'content-length',
  'content-encoding'
]);

function buildTargetUrl(req, backendBaseUrl) {
  const segments = Array.isArray(req.query.path)
    ? req.query.path
    : req.query.path
      ? [req.query.path]
      : [];

  const path = segments.join('/');
  const target = new URL(`/api/${path}`, backendBaseUrl);

  for (const [key, value] of Object.entries(req.query)) {
    if (key === 'path') continue;
    if (Array.isArray(value)) {
      value.forEach((item) => target.searchParams.append(key, item));
    } else if (value !== undefined) {
      target.searchParams.append(key, value);
    }
  }

  return target;
}

function buildRequestHeaders(req) {
  const headers = {};

  for (const [key, value] of Object.entries(req.headers || {})) {
    const normalized = key.toLowerCase();
    if (HOP_BY_HOP_HEADERS.has(normalized)) continue;
    headers[normalized] = value;
  }

  return headers;
}

function normalizeRequestBody(req, headers) {
  if (req.method === 'GET' || req.method === 'HEAD') {
    return undefined;
  }

  if (!req.body) return undefined;

  if (Buffer.isBuffer(req.body) || typeof req.body === 'string') {
    return req.body;
  }

  headers['content-type'] = headers['content-type'] || 'application/json';
  return JSON.stringify(req.body);
}

function copyResponseHeaders(upstream, res) {
  upstream.headers.forEach((value, key) => {
    if (HOP_BY_HOP_HEADERS.has(key.toLowerCase())) return;
    res.setHeader(key, value);
  });
}

async function handler(req, res) {
  if (req.method === 'OPTIONS') {
    res.status(204).send('');
    return;
  }

  const backendBaseUrl = process.env.BACKEND_URL || 'https://online-judge-backend-mrca.onrender.com';
  if (!backendBaseUrl) {
    res.status(500).json({
      error: 'BACKEND_URL is not configured',
      message: 'Set BACKEND_URL in Vercel Project Settings (Environment Variables).'
    });
    return;
  }

  try {
    const target = buildTargetUrl(req, backendBaseUrl);
    const headers = buildRequestHeaders(req);
    const body = normalizeRequestBody(req, headers);

    const upstream = await fetch(target, {
      method: req.method,
      headers,
      body
    });

    copyResponseHeaders(upstream, res);
    const data = Buffer.from(await upstream.arrayBuffer());
    res.status(upstream.status).send(data);
  } catch (error) {
    res.status(502).json({
      error: 'Bad gateway',
      message: error?.message || 'Failed to reach upstream backend'
    });
  }
}

module.exports = handler;
