import { apiRequest } from './apiClient';
import { loadAuth } from '../utils/authStorage';

/**
 * UC304 - Get all workflow logs (admin only)
 * @param {number} page - Page number (0-indexed)
 * @param {number} size - Page size
 * @returns {Promise} Paginated list of workflow logs
 */
export async function getWorkflowLogs({ page = 0, size = 20, sortBy = 'timestamp', order = 'DESC' } = {}) {
  const auth = loadAuth();
  const token = auth?.token;

  const res = await apiRequest(`/api/admin/workflows?page=${page}&size=${size}&sortBy=${sortBy}&order=${order}`, {
    method: 'GET',
    token,
  });
  // Expected response format: { data: { content: [...], totalElements, totalPages, currentPage, pageSize } }
  return res.data || { content: [], totalElements: 0, totalPages: 0, currentPage: 0, pageSize: 0 };
}

/**
 * UC304 - Get workflow logs with filters
 * @param {number} page - Page number
 * @param {number} size - Page size
 * @param {string} requestId - Filter by rescue request ID (optional)
 * @param {string} userId - Filter by user ID (optional)
 * @param {string} companyId - Filter by company ID (optional)
 * @param {string} status - Filter by workflow status (optional)
 * @returns {Promise} Filtered paginated list of workflow logs
 */
export async function getWorkflowLogsFiltered({
  page = 0,
  size = 20,
  requestId = null,
  userId = null,
  companyId = null,
  status = null,
  sortBy = 'timestamp',
  order = 'DESC'
} = {}) {
  const auth = loadAuth();
  const token = auth?.token;

  const params = new URLSearchParams({
    page,
    size,
    sortBy,
    order,
  });

  if (requestId) params.append('requestId', requestId);
  if (userId) params.append('userId', userId);
  if (companyId) params.append('companyId', companyId);
  if (status) params.append('status', status);

  const res = await apiRequest(`/api/admin/workflows?${params.toString()}`, {
    method: 'GET',
    token,
  });

  return res.data || { content: [], totalElements: 0, totalPages: 0, currentPage: 0, pageSize: 0 };
}
