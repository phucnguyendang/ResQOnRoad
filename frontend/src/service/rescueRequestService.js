import { apiRequest } from './apiClient';

export async function getCompanyAssignedRequests(token) {
  const res = await apiRequest('/rescue-requests/company/assigned', {
    method: 'GET',
    token,
  });

  // Expected response: { data: [...] }
  return res.data || [];
}

export async function getRescueRequestById(id, token) {
  const res = await apiRequest(`/rescue-requests/${id}`, {
    method: 'GET',
    token,
  });

  return res.data;
}

export async function acceptRescueRequest(id, token) {
  const res = await apiRequest(`/rescue-requests/${id}/accept`, {
    method: 'POST',
    token,
  });

  return res.data;
}

export async function updateRescueRequestStatus(id, status, reason = null, token) {
  const body = { status };
  if (reason) {
    body.reason = reason;
  }

  const res = await apiRequest(`/rescue-requests/${id}/status`, {
    method: 'PATCH',
    body,
    token,
  });

  return res.data;
}

export async function rejectRescueRequest(id, reason, token) {
  const res = await apiRequest(`/rescue-requests/${id}/reject`, {
    method: 'POST',
    body: { reason },
    token,
  });

  return res.data;
}
