import { apiRequest } from './apiClient';
import { loadAuth } from '../utils/authStorage';

const REVIEW_CACHE_KEY = 'resq_review_cache_by_request';

function requireToken() {
  const auth = loadAuth();
  const token = auth?.token;
  if (!token) {
    const err = new Error('Vui lòng đăng nhập để gửi/xem đánh giá.');
    err.status = 401;
    throw err;
  }
  return token;
}

function loadCache() {
  const raw = localStorage.getItem(REVIEW_CACHE_KEY);
  if (!raw) return {};
  try {
    const parsed = JSON.parse(raw);
    return parsed && typeof parsed === 'object' ? parsed : {};
  } catch {
    return {};
  }
}

function saveCache(cache) {
  localStorage.setItem(REVIEW_CACHE_KEY, JSON.stringify(cache));
}

/**
 * Get cached review for a specific rescue request (UI convenience).
 * Backend response does not include requestId, so we keep a local mapping.
 */
export function getReviewByRequestId(requestId) {
  if (requestId == null) return null;
  const cache = loadCache();
  return cache[String(requestId)] || null;
}

/**
 * Get locally cached reviews for a company (UI convenience).
 * These are created when the current user submits a review.
 */
export function getCachedReviewsByCompanyId(companyId) {
  if (companyId == null) return [];
  const target = Number(companyId);
  if (!Number.isFinite(target)) return [];

  const cache = loadCache();
  const values = Object.values(cache);
  return values
    .filter((r) => r && typeof r === 'object')
    .filter((r) => Number(r.companyId) === target)
    .sort((a, b) => {
      const ta = new Date(a.updatedAt || a.createdAt || 0).getTime();
      const tb = new Date(b.updatedAt || b.createdAt || 0).getTime();
      return tb - ta;
    });
}

/**
 * UC102: Create or update review (backend)
 * POST /api/reviews
 */
export async function upsertReview({ requestId, companyId, rating, comment }) {
  if (requestId == null) throw new Error('requestId là bắt buộc');

  const safeRating = Number(rating);
  if (!Number.isFinite(safeRating) || safeRating < 1 || safeRating > 5) {
    throw new Error('rating phải từ 1 đến 5');
  }

  const token = requireToken();
  const res = await apiRequest('/api/reviews', {
    method: 'POST',
    token,
    body: {
      requestId: Number(requestId),
      rating: safeRating,
      comment: String(comment || '').trim(),
    },
  });

  // Cache minimal info for request-based UI.
  const now = new Date().toISOString();
  const cache = loadCache();
  cache[String(requestId)] = {
    requestId: Number(requestId),
    companyId: companyId != null ? Number(companyId) : null,
    rating: safeRating,
    comment: String(comment || '').trim(),
    updatedAt: now,
    createdAt: cache[String(requestId)]?.createdAt || now,
    server: res?.data ?? null,
  };
  saveCache(cache);

  return res.data;
}

/**
 * List reviews for a company (backend)
 * GET /api/reviews/companies/{companyId}/reviews?page=1&limit=10
 */
export async function getReviewsByCompanyId(companyId, { page = 1, limit = 10 } = {}) {
  if (companyId == null) return [];
  const res = await apiRequest(
    `/api/reviews/companies/${encodeURIComponent(String(companyId))}/reviews?page=${encodeURIComponent(String(page))}&limit=${encodeURIComponent(String(limit))}`,
    { method: 'GET' }
  );
  const items = res?.data?.items;
  return Array.isArray(items) ? items : [];
}

/**
 * Get average rating for a company (backend)
 * GET /api/reviews/companies/{companyId}/rating
 */
export async function getCompanyRating(companyId) {
  if (companyId == null) throw new Error('companyId là bắt buộc');
  const res = await apiRequest(`/api/reviews/companies/${encodeURIComponent(String(companyId))}/rating`, { method: 'GET' });
  return res.data;
}

/**
 * Check if current user has reviewed a company (backend)
 * GET /api/reviews/check?companyId=...
 */
export async function hasReviewedCompany(companyId) {
  if (companyId == null) throw new Error('companyId là bắt buộc');
  const token = requireToken();
  const res = await apiRequest(`/api/reviews/check?companyId=${encodeURIComponent(String(companyId))}`, {
    method: 'GET',
    token,
  });
  return res.data;
}

// Backward-compatible default export (some components import default).
export default {
  createOrUpdateReview: async (requestId, rating, comment) => {
    const data = await upsertReview({ requestId, rating, comment });
    return { data };
  },
};
