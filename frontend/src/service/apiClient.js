// Backend is configured with server.servlet.context-path=/v1
// (see backend/src/main/resources/application.properties)
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/v1';

function buildUrl(path) {
  if (!path.startsWith('/')) return `${API_BASE_URL}/${path}`;
  return `${API_BASE_URL}${path}`;
}

async function parseJsonSafe(res) {
  const text = await res.text();
  if (!text) return null;
  try {
    return JSON.parse(text);
  } catch {
    return null;
  }
}

export async function apiRequest(path, { method = 'GET', body, token, headers } = {}) {
  const url = buildUrl(path);
  console.log(`🌐 API ${method} ${url}`, { token: token ? '✓' : '✗', body }); // DEBUG
  const upperMethod = String(method || 'GET').toUpperCase();
  const requestBody =
    body === undefined || upperMethod === 'GET' || upperMethod === 'HEAD'
      ? undefined
      : (typeof body === 'string' ? body : JSON.stringify(body));
  
  const res = await fetch(url, {
    method,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(headers || {}),
    },
    body: requestBody,
  });

  const payload = await parseJsonSafe(res);
  console.log(`📨 API Response ${res.status}:`, payload); // DEBUG

  if (!res.ok) {
    // Backend error format: { error: { code, message, details: [] } }
    const error = payload?.error;
    const message = error?.message || `HTTP ${res.status}`;
    const details = Array.isArray(error?.details) ? error.details : [];
    const err = new Error(message);
    err.status = res.status;
    err.details = details;
    err.payload = payload;
    console.error(`❌ API Error ${res.status}:`, message, details); // DEBUG
    throw err;
  }

  return payload;
}
