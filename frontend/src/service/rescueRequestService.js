import { apiRequest } from './apiClient';
import { loadAuth } from '../utils/authStorage';

const LAST_REQUEST_ID_KEY = 'resq_last_request_id';
const MOCK_NEXT_ID_KEY = 'resq_mock_next_request_id';
const MOCK_REQUESTS_KEY = 'resq_mock_requests';

// While backend UC201 + data are not ready, keep rescue-request APIs mocked.
// Switch by setting VITE_USE_MOCK_RESCUE_REQUESTS_API=false in frontend env.
const USE_MOCK_RESCUE_REQUESTS_API = (import.meta.env.VITE_USE_MOCK_RESCUE_REQUESTS_API ?? 'true') !== 'false';

function loadMockRequests() {
  const raw = localStorage.getItem(MOCK_REQUESTS_KEY);
  if (!raw) return [];
  try {
    const parsed = JSON.parse(raw);
    return Array.isArray(parsed) ? parsed : [];
  } catch {
    return [];
  }
}

function saveMockRequests(requests) {
  localStorage.setItem(MOCK_REQUESTS_KEY, JSON.stringify(requests));
}

function sleep(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
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
 * UC201 - MOCK: Create rescue request (backend not finished yet).
 * Simulates POST /api/rescue-requests success and returns a shape similar to backend ApiResponse.data.
 */
export async function createRescueRequestMock({
  company_id,
  incident_desc,
  location_address,
  latitude,
  longitude,
  images_base64,
}) {
  await sleep(500);

  const nextRaw = localStorage.getItem(MOCK_NEXT_ID_KEY);
  const nextId = Number.isFinite(Number(nextRaw)) ? Number(nextRaw) : 1000;
  const id = nextId + 1;
  localStorage.setItem(MOCK_NEXT_ID_KEY, String(id));
  setLastRescueRequestId(id);

  const createdAt = new Date().toISOString();
  const request = {
    id,
    status: 'PENDING_CONFIRMATION',
    createdAt,
    updatedAt: createdAt,
    companyId: company_id ? Number(company_id) : null,
    companyName: company_id ? `Công ty cứu hộ #${company_id}` : 'Công ty cứu hộ (mock)',
    companyPhoneNumber: '',
    location: location_address,
    latitude: latitude !== '' && latitude !== null && latitude !== undefined ? Number(latitude) : null,
    longitude: longitude !== '' && longitude !== null && longitude !== undefined ? Number(longitude) : null,
    description: incident_desc,
    imagesBase64: Array.isArray(images_base64) ? images_base64 : [],
  };

  const list = loadMockRequests();
  // Keep newest first
  saveMockRequests([request, ...list].slice(0, 50));

  return request;
}

export function getMyRescueRequestsMock() {
  return loadMockRequests();
}

/**
 * UC Track - REAL: Fetch rescue request detail from backend.
 * GET /api/rescue-requests/{id}
 */
export async function getRescueRequestDetail(id) {
  if (USE_MOCK_RESCUE_REQUESTS_API) {
    const list = loadMockRequests();
    const found = list.find((r) => String(r.id) === String(id));
    if (!found) {
      const err = new Error('Không tìm thấy yêu cầu (mock). Hãy tạo yêu cầu mới trước để xem giao diện.');
      err.status = 404;
      throw err;
    }
    return found;
  }

  const auth = loadAuth();
  const token = auth?.token;

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
  if (USE_MOCK_RESCUE_REQUESTS_API) {
    return loadMockRequests();
  }

  const auth = loadAuth();
  const token = auth?.token;

  const res = await apiRequest('/api/rescue-requests/user/my-requests', {
    method: 'GET',
    token,
  });

  return res.data;
}
