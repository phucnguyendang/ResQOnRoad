import { apiRequest } from './apiClient';
import { loadAuth } from '../utils/authStorage';

/**
 * UC401 - Get list of all users (admin only)
 */
export async function getAllUsers() {
  const auth = loadAuth();
  const token = auth?.token;

  const res = await apiRequest('/api/admin/users', {
    method: 'GET',
    token,
  });
  // Expected response format: { data: [ { id, username, full_name, phone_number, email, role, status }, ... ] }
  return res.data || [];
}

/**
 * UC401 - Lock/Unlock a user account (admin only)
 * @param {number} userId - The user ID to lock/unlock
 * @param {boolean} isLocked - true to lock, false to unlock
 */
export async function toggleUserLock(userId, isLocked) {
  const auth = loadAuth();
  const token = auth?.token;

  const res = await apiRequest(`/api/admin/users/${userId}/lock?locked=${isLocked}`, {
    method: 'PUT',
    token,
    body: {
      is_locked: isLocked,
    },
  });
  // Expected response format: { data: { id, username, status, message } }
  return res.data;
}

/**
 * Admin stats (users/posts/comments/locked)
 * GET /api/admin/stats
 */
export async function getAdminStats() {
  const auth = loadAuth();
  const token = auth?.token;

  const res = await apiRequest('/api/admin/stats', {
    method: 'GET',
    token,
  });

  return res.data;
}

/**
 * Admin post management (community posts)
 */

export async function adminListDeletedCommunityPosts() {
  const auth = loadAuth();
  const token = auth?.token;

  const res = await apiRequest('/api/admin/community-posts/deleted', {
    method: 'GET',
    token,
  });

  return res.data || [];
}

export async function adminListClosedCommunityPosts() {
  const auth = loadAuth();
  const token = auth?.token;

  const res = await apiRequest('/api/admin/community-posts/closed', {
    method: 'GET',
    token,
  });

  return res.data || [];
}

export async function adminGetCommunityPostDetail(postId) {
  const auth = loadAuth();
  const token = auth?.token;

  const res = await apiRequest(`/api/admin/community-posts/${encodeURIComponent(String(postId))}`,
    {
      method: 'GET',
      token,
    },
  );

  return res.data;
}

export async function adminRestoreCommunityPost(postId) {
  const auth = loadAuth();
  const token = auth?.token;

  const res = await apiRequest(
    `/api/admin/community-posts/${encodeURIComponent(String(postId))}/restore`,
    {
      method: 'PATCH',
      token,
    },
  );

  return res.data;
}

export async function adminOpenCommunityPostComments(postId) {
  const auth = loadAuth();
  const token = auth?.token;

  const res = await apiRequest(
    `/api/admin/community-posts/${encodeURIComponent(String(postId))}/comments/open`,
    {
      method: 'PATCH',
      token,
    },
  );

  return res.data;
}
