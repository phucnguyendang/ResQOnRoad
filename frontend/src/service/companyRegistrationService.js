import { apiRequest } from './apiClient';
import { loadAuth } from '../utils/authStorage';

function getToken() {
  const auth = loadAuth();
  return auth?.token;
}

export async function createCompanyRegistration(payload) {
  const token = getToken();
  const res = await apiRequest('/api/company-registrations', {
    method: 'POST',
    token,
    body: payload,
  });
  return res.data;
}

export async function getMyCompanyRegistration() {
  const token = getToken();
  const res = await apiRequest('/api/company-registrations/my', {
    method: 'GET',
    token,
  });
  return res.data;
}

export async function adminListCompanyRegistrations({ status = 'PENDING', page = 0, size = 20 } = {}) {
  const token = getToken();
  const qs = new URLSearchParams({ status, page: String(page), size: String(size) }).toString();
  const res = await apiRequest(`/api/admin/company-registrations?${qs}`, {
    method: 'GET',
    token,
  });
  return res.data;
}

export async function adminApproveCompanyRegistration(id) {
  const token = getToken();
  const res = await apiRequest(`/api/admin/company-registrations/${encodeURIComponent(String(id))}/approve`, {
    method: 'POST',
    token,
  });
  return res.data;
}

export async function adminRejectCompanyRegistration(id, reason) {
  const token = getToken();
  const res = await apiRequest(`/api/admin/company-registrations/${encodeURIComponent(String(id))}/reject`, {
    method: 'POST',
    token,
    body: { reason },
  });
  return res.data;
}
