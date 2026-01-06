import { apiRequest } from './apiClient';
import { loadAuth } from '../utils/authStorage';

export async function adminListRescueCompanies({ page = 0, size = 50, sortBy = 'createdAt', sortDirection = 'desc' } = {}) {
  const auth = loadAuth();
  const token = auth?.token;

  const qs = new URLSearchParams({
    page: String(page),
    size: String(size),
    sortBy: String(sortBy),
    sortDirection: String(sortDirection),
  }).toString();

  const res = await apiRequest(`/api/admin/companies?${qs}`, {
    method: 'GET',
    token,
  });

  return res.data; // Spring Page<CompanyAdminResponse>
}
