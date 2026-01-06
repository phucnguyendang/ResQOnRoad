import { apiRequest } from './apiClient';
import { loadAuth } from '../utils/authStorage';

function requireToken() {
  const auth = loadAuth();
  const token = auth?.token;
  if (!token) {
    const err = new Error('Vui lòng đăng nhập với quyền ADMIN.');
    err.status = 401;
    throw err;
  }
  return token;
}

export async function adminListCompanies({ page = 0, size = 50, sortBy = 'createdAt', sortDirection = 'desc' } = {}) {
  const token = requireToken();
  const res = await apiRequest(
    `/api/admin/companies?page=${encodeURIComponent(String(page))}` +
      `&size=${encodeURIComponent(String(size))}` +
      `&sortBy=${encodeURIComponent(String(sortBy))}` +
      `&sortDirection=${encodeURIComponent(String(sortDirection))}`,
    { method: 'GET', token },
  );
  return res.data; // Spring Page<CompanyAdminResponse>
}

export async function adminGetCompanyDetail(companyId) {
  const token = requireToken();
  const res = await apiRequest(`/api/admin/companies/${encodeURIComponent(String(companyId))}`, { method: 'GET', token });
  return res.data;
}
