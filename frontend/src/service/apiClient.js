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
  const res = await fetch(buildUrl(path), {
    method,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(headers || {}),
    },
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });

  const payload = await parseJsonSafe(res);

  if (!res.ok) {
    // Backend error format: { error: { code, message, details: [] } }
    const error = payload?.error;
    const message = error?.message || `HTTP ${res.status}`;
    const details = Array.isArray(error?.details) ? error.details : [];
    const err = new Error(message);
    err.status = res.status;
    err.details = details;
    err.payload = payload;
    throw err;
  }

  return payload;
}
