import { apiRequest } from './apiClient';

export async function searchNearbyCompanies({
  latitude,
  longitude,
  maxDistance = 50,
  page = 0,
  size = 20,
  token,
}) {
  const res = await apiRequest(`/companies/search`, {
    method: 'POST',
    body: {
      latitude,
      longitude,
      maxDistance,
      page,
      size,
    },
    token,
  });

  // Backend returns ApiResponse<Page<CompanySearchResponse>>: { status, message, data }
  return res.data;
}

export async function getCompanyDetails(companyId, token) {
  const res = await apiRequest(`/companies/${companyId}`, {
    method: 'GET',
    token,
  });

  return res.data;
}
