import { apiRequest } from './apiClient';
import { loadAuth } from '../utils/authStorage';

const LAST_REQUEST_ID_KEY = 'resq_last_request_id';

function requireToken() {
  const auth = loadAuth();
  const token = auth?.token;
  if (!token) {
    const err = new Error('Vui lòng đăng nhập để tạo/xem yêu cầu cứu hộ.');
    err.status = 401;
    throw err;
  }
  return token;
}

export function getLastRescueRequestId() {
  const raw = localStorage.getItem(LAST_REQUEST_ID_KEY);
  if (!raw) return '';
  return raw;
}

export function setLastRescueRequestId(id) {
  if (id === undefined || id === null) return;
  localStorage.setItem(LAST_REQUEST_ID_KEY, String(id));
}

/**
 * UC201 - Create rescue request
 * Backend: POST /api/rescue-requests
 * Payload: docs-aligned snake_case (backend supports aliases)
 */
export async function createRescueRequest({
  company_id,
  incident_desc,
  location_address,
  latitude,
  longitude,
  images_base64,
  service_type,
} = {}) {
  const token = requireToken();

  const lat = latitude !== '' && latitude !== null && latitude !== undefined ? Number(latitude) : null;
  const lng = longitude !== '' && longitude !== null && longitude !== undefined ? Number(longitude) : null;

  if (company_id === undefined || company_id === null || String(company_id).trim() === '') {
    throw new Error('Vui lòng chọn công ty cứu hộ.');
  }

  const res = await apiRequest('/api/rescue-requests', {
    method: 'POST',
    token,
    body: {
      // docs-aligned payload (backend supports aliases)
      company_id: Number(company_id),
      location_address: String(location_address || '').trim(),
      latitude: lat,
      longitude: lng,
      incident_desc: String(incident_desc || '').trim() || null,
      service_type: service_type ?? null,
      images_base64: Array.isArray(images_base64) ? images_base64 : [],
    },
  });

  const created = res.data;
  if (created?.id != null) setLastRescueRequestId(created.id);
  return created;
}

/**
 * UC Track - REAL: Fetch rescue request detail from backend.
 * GET /api/rescue-requests/{id}
 */
export async function getRescueRequestDetail(id) {
  const token = requireToken();

  const res = await apiRequest(`/api/rescue-requests/${id}`, {
    method: 'GET',
    token,
  });

  return res.data;
}

/**
 * List requests for current user (backend).
 * GET /api/rescue-requests/user/my-requests
 */
export async function getMyRescueRequests() {
  const token = requireToken();

  const res = await apiRequest('/api/rescue-requests/user/my-requests', {
    method: 'GET',
    token,
  });

  return res.data;
}

/**
 * List requests assigned to current company (backend).
 * GET /api/rescue-requests/company/assigned
 */
export async function getCompanyAssignedRescueRequests() {
  const token = requireToken();

  const res = await apiRequest('/api/rescue-requests/company/assigned', {
    method: 'GET',
    token,
  });

  return res.data;
}

/**
 * ADMIN: List all rescue requests in system.
 * GET /api/rescue-requests/admin/all
 */
export async function getAllRescueRequestsAdmin() {
  const token = requireToken();

  const res = await apiRequest('/api/rescue-requests/admin/all', {
    method: 'GET',
    token,
  });

  return res.data;
}

/**
 * UC203 - Cancel rescue request
 * POST /api/rescue-requests/{id}/cancel
 */
export async function cancelRescueRequest(id) {
  const auth = loadAuth();
  const token = auth?.token;

  const res = await apiRequest(`/api/rescue-requests/${id}/cancel`, {
    method: 'POST',
    token,
  });

  return res.data;
}

/**
 * UC205 - Accept rescue request (company)
 * POST /api/rescue-requests/{id}/accept
 */
export async function acceptRescueRequest(id) {
  const token = requireToken();

  const res = await apiRequest(`/api/rescue-requests/${id}/accept`, {
    method: 'POST',
    token,
  });

  return res.data;
}

/**
 * UC205 - Update rescue request status (company)
 * PATCH /api/rescue-requests/{id}/status
 */
export async function updateRescueRequestStatus(id, { status, note } = {}) {
  const token = requireToken();

  const res = await apiRequest(`/api/rescue-requests/${id}/status`, {
    method: 'PATCH',
    token,
    body: {
      status,
      note: note ?? null,
    },
  });

  return res.data;
}

/**
 * UC205 - Reject rescue request (company)
 * POST /api/rescue-requests/{id}/reject
 */
export async function rejectRescueRequest(id, rejectionReason) {
  const token = requireToken();

  const res = await apiRequest(`/api/rescue-requests/${id}/reject`, {
    method: 'POST',
    token,
    body: {
      rejectionReason: String(rejectionReason || '').trim(),
    },
  });

  return res.data;
}
