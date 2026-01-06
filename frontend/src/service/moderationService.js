import { apiRequest } from './apiClient';
import { loadAuth } from '../utils/authStorage';

function requireToken() {
  const auth = loadAuth();
  const token = auth?.token;
  if (!token) {
    const err = new Error('Vui lòng đăng nhập để truy cập chức năng kiểm duyệt.');
    err.status = 401;
    throw err;
  }
  return token;
}

/**
 * UC405 - Get moderation list
 * GET /api/admin/moderation?page=0&size=10&status=PENDING&type=POST
 */
export async function getModerationList({
  page = 0,
  size = 10,
  status = null,
  type = null,
  keyword = null,
} = {}) {
  const token = requireToken();

  let url = `/api/admin/moderation?page=${encodeURIComponent(String(page))}&size=${encodeURIComponent(String(size))}`;

  if (status) {
    url += `&status=${encodeURIComponent(String(status))}`;
  }
  if (type) {
    url += `&type=${encodeURIComponent(String(type))}`;
  }
  if (keyword) {
    url += `&keyword=${encodeURIComponent(String(keyword))}`;
  }

  const res = await apiRequest(url, {
    method: 'GET',
    token,
  });

  return res.data;
}

/**
 * UC405 - Get pending content
 * GET /api/admin/moderation/pending
 */
export async function getPendingContent({ page = 0, size = 10 } = {}) {
  const token = requireToken();

  const url = `/api/admin/moderation/pending?page=${encodeURIComponent(String(page))}&size=${encodeURIComponent(String(size))}`;

  const res = await apiRequest(url, {
    method: 'GET',
    token,
  });

  return res.data;
}

/**
 * UC405 - Get moderation statistics
 * GET /api/admin/moderation/statistics
 */
export async function getModerationStatistics() {
  const token = requireToken();

  const res = await apiRequest('/api/admin/moderation/statistics', {
    method: 'GET',
    token,
  });

  return res.data;
}

/**
 * UC405 - Search content
 * GET /api/admin/moderation/search?keyword=...
 */
export async function searchContent(keyword) {
  const token = requireToken();

  const url = `/api/admin/moderation/search?keyword=${encodeURIComponent(String(keyword))}`;

  const res = await apiRequest(url, {
    method: 'GET',
    token,
  });

  return res.data;
}

/**
 * UC405 - Get content detail
 * GET /api/admin/moderation/{id}
 */
export async function getContentDetail(id) {
  const token = requireToken();

  const res = await apiRequest(`/api/admin/moderation/${encodeURIComponent(String(id))}`, {
    method: 'GET',
    token,
  });

  return res.data;
}

/**
 * UC405 - Approve content
 * POST /api/admin/moderation/{id}/approve
 */
export async function approveContent(id) {
  const token = requireToken();

  const res = await apiRequest(
    `/api/admin/moderation/${encodeURIComponent(String(id))}/approve`,
    {
      method: 'POST',
      token,
    },
  );

  return res.data;
}

/**
 * UC405 - Reject content
 * POST /api/admin/moderation/{id}/reject
 */
export async function rejectContent(id, { reason = '' } = {}) {
  const token = requireToken();

  const body = {
    reason: String(reason || '').trim(),
  };

  const res = await apiRequest(
    `/api/admin/moderation/${encodeURIComponent(String(id))}/reject`,
    {
      method: 'POST',
      token,
      body,
    },
  );

  return res.data;
}

/**
 * UC405 - Remove content
 * POST /api/admin/moderation/{id}/remove
 */
export async function removeContent(id, { reason = '' } = {}) {
  const token = requireToken();

  const body = {
    reason: String(reason || '').trim(),
  };

  const res = await apiRequest(
    `/api/admin/moderation/${encodeURIComponent(String(id))}/remove`,
    {
      method: 'POST',
      token,
      body,
    },
  );

  return res.data;
}

/**
 * UC405 - Delete content permanently
 * DELETE /api/admin/moderation/{id}
 */
export async function deleteContent(id) {
  const token = requireToken();

  const res = await apiRequest(`/api/admin/moderation/${encodeURIComponent(String(id))}`, {
    method: 'DELETE',
    token,
  });

  return res.data;
}

export default {
  getModerationList,
  getPendingContent,
  getModerationStatistics,
  searchContent,
  getContentDetail,
  approveContent,
  rejectContent,
  removeContent,
  deleteContent,
};
