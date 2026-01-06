import { apiRequest } from './apiClient';
import { loadAuth } from '../utils/authStorage';

export async function getMyCompanyProfile() {
  const auth = loadAuth();
  const res = await apiRequest('/api/companies/profile', {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${auth?.token}`,
    },
  });
  return res.data;
}

export async function updateMyCompanyProfile(payload) {
  const auth = loadAuth();
  const res = await apiRequest('/api/companies/profile', {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${auth?.token}`,
    },
    body: payload,
  });
  return res.data;
}
