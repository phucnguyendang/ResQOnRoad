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
