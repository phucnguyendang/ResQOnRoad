import { apiRequest } from './apiClient';
import { loadAuth } from '../utils/authStorage';

function requireToken() {
  const auth = loadAuth();
  const token = auth?.token;
  if (!token) {
    const err = new Error('Vui lòng đăng nhập để đăng bài hoặc bình luận.');
    err.status = 401;
    throw err;
  }
  return token;
}

/**
 * UC103 - Community feed
 * GET /api/community/posts?page=0&size=10
 */
export async function getCommunityPosts({ page = 0, size = 10 } = {}) {
  const res = await apiRequest(
    `/api/community/posts?page=${encodeURIComponent(String(page))}&size=${encodeURIComponent(String(size))}`,
    { method: 'GET' },
  );
  return res.data;
}

/**
 * Get current user's posts (requires login)
 * GET /api/community/posts/my-posts?page=0&size=10
 */
export async function getMyCommunityPosts({ page = 0, size = 10 } = {}) {
  const token = requireToken();
  const res = await apiRequest(
    `/api/community/posts/my-posts?page=${encodeURIComponent(String(page))}&size=${encodeURIComponent(String(size))}`,
    { method: 'GET', token },
  );
  return res.data;
}

/**
 * Get a company's posts by companyId
 * GET /api/community/posts/by-company/{companyId}?page=0&size=10
 */
export async function getCompanyCommunityPosts(companyId, { page = 0, size = 10 } = {}) {
  if (companyId == null || companyId === '') throw new Error('companyId là bắt buộc');
  const res = await apiRequest(
    `/api/community/posts/by-company/${encodeURIComponent(String(companyId))}?page=${encodeURIComponent(String(page))}&size=${encodeURIComponent(String(size))}`,
    { method: 'GET' },
  );
  return res.data;
}

/**
 * UC103 - Create post
 * POST /api/community/posts
 */
export async function createCommunityPost({
  title,
  content,
  incidentType,
  location,
  latitude,
  longitude,
  imageBase64,
} = {}) {
  const token = requireToken();

  const body = {
    title: String(title || '').trim(),
    content: String(content || '').trim(),
    incidentType: incidentType ? String(incidentType).trim() : null,
    location: location ? String(location).trim() : null,
    latitude:
      latitude !== '' && latitude !== null && latitude !== undefined ? Number(latitude) : null,
    longitude:
      longitude !== '' && longitude !== null && longitude !== undefined ? Number(longitude) : null,
    imageBase64: imageBase64 ? String(imageBase64) : null,
  };

  const res = await apiRequest('/api/community/posts', {
    method: 'POST',
    token,
    body,
  });

  return res.data;
}

/**
 * UC103 - Get comments for a post
 * GET /api/community/posts/{postId}/comments
 */
export async function getCommunityComments(postId) {
  const res = await apiRequest(`/api/community/posts/${encodeURIComponent(String(postId))}/comments`, {
    method: 'GET',
  });
  return res.data;
}

/**
 * UC103 - Add comment
 * POST /api/community/posts/{postId}/comments
 */
export async function addCommunityComment(postId, { content, parentCommentId } = {}) {
  const token = requireToken();

  const body = {
    content: String(content || '').trim(),
    parentCommentId: parentCommentId ?? null,
  };

  const res = await apiRequest(`/api/community/posts/${encodeURIComponent(String(postId))}/comments`, {
    method: 'POST',
    token,
    body,
  });

  return res.data;
}

/**
 * Delete a community post
 * DELETE /api/community/posts/{postId}
 */
export async function deleteCommunityPost(postId) {
  const token = requireToken();
  const res = await apiRequest(`/api/community/posts/${encodeURIComponent(String(postId))}`, {
    method: 'DELETE',
    token,
  });
  return res.data;
}

/**
 * Close comments for a post (mark as resolved)
 * PATCH /api/community/posts/{postId}/resolve
 */
export async function resolveCommunityPost(postId) {
  const token = requireToken();
  const res = await apiRequest(`/api/community/posts/${encodeURIComponent(String(postId))}/resolve`, {
    method: 'PATCH',
    token,
  });
  return res.data;
}

/**
 * Close comments for a post
 * PATCH /api/community/posts/{postId}/comments/close
 */
export async function closeCommunityPostComments(postId) {
  const token = requireToken();
  const res = await apiRequest(
    `/api/community/posts/${encodeURIComponent(String(postId))}/comments/close`,
    {
      method: 'PATCH',
      token,
    },
  );
  return res.data;
}

/**
 * Open comments for a post
 * PATCH /api/community/posts/{postId}/comments/open
 */
export async function openCommunityPostComments(postId) {
  const token = requireToken();
  const res = await apiRequest(
    `/api/community/posts/${encodeURIComponent(String(postId))}/comments/open`,
    {
      method: 'PATCH',
      token,
    },
  );
  return res.data;
}

/**
 * Delete a community comment
 * DELETE /api/community/comments/{commentId}
 */
export async function deleteCommunityComment(commentId) {
  const token = requireToken();
  const res = await apiRequest(`/api/community/comments/${encodeURIComponent(String(commentId))}`, {
    method: 'DELETE',
    token,
  });
  return res.data;
}

/**
 * Close a community comment
 * PATCH /api/community/comments/{commentId}/close
 */
export async function closeCommunityComment(commentId) {
  const token = requireToken();
  const res = await apiRequest(`/api/community/comments/${encodeURIComponent(String(commentId))}/close`, {
    method: 'PATCH',
    token,
  });
  return res.data;
}
