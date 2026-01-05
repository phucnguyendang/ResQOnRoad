import { apiRequest } from './apiClient';

function toQuery(params) {
  const usp = new URLSearchParams();
  Object.entries(params).forEach(([k, v]) => {
    if (v === undefined || v === null || v === '') return;
    usp.set(k, String(v));
  });
  const s = usp.toString();
  return s ? `?${s}` : '';
}

export async function searchNearbyCompanies({ lat, lng, maxDistance = 50.0, page = 0, size = 20 } = {}) {
  const query = toQuery({ lat, lng, maxDistance, page, size });
  const res = await apiRequest(`/api/companies/search${query}`, { method: 'GET' });
  return res.data; // Spring Page<CompanySearchResponse>
}

export async function getCompanyDetail(companyId) {
  if (companyId == null || companyId === '') throw new Error('companyId là bắt buộc');
  const res = await apiRequest(`/api/companies/${encodeURIComponent(String(companyId))}`, { method: 'GET' });
  return res.data; // CompanyDetailResponse
}
