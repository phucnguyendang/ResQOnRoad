const REVIEWS_KEY = 'resq_company_reviews';

function loadAll() {
  const raw = localStorage.getItem(REVIEWS_KEY);
  if (!raw) return [];
  try {
    const parsed = JSON.parse(raw);
    return Array.isArray(parsed) ? parsed : [];
  } catch {
    return [];
  }
}

function saveAll(list) {
  localStorage.setItem(REVIEWS_KEY, JSON.stringify(list));
}

/**
 * Get review for a specific rescue request (one review per request).
 */
export function getReviewByRequestId(requestId) {
  if (requestId == null) return null;
  const id = String(requestId);
  const all = loadAll();
  return all.find((r) => String(r.requestId) === id) || null;
}

/**
 * Create or update review for a request.
 * This is a frontend mock storage while backend review API is not available.
 */
export async function upsertReview({ requestId, companyId, rating, comment }) {
  if (requestId == null) throw new Error('requestId là bắt buộc');
  if (companyId == null) throw new Error('companyId là bắt buộc');
  const safeRating = Number(rating);
  if (!Number.isFinite(safeRating) || safeRating < 1 || safeRating > 5) {
    throw new Error('rating phải từ 1 đến 5');
  }

  const now = new Date().toISOString();
  const all = loadAll();
  const idx = all.findIndex((r) => String(r.requestId) === String(requestId));

  const payload = {
    requestId: Number(requestId),
    companyId: Number(companyId),
    rating: safeRating,
    comment: String(comment || '').trim(),
    updatedAt: now,
    createdAt: idx >= 0 ? all[idx].createdAt : now,
  };

  if (idx >= 0) {
    all[idx] = payload;
  } else {
    all.unshift(payload);
  }
  saveAll(all.slice(0, 200));

  return payload;
}

/**
 * List reviews for a company from localStorage (frontend mock).
 */
export function getReviewsByCompanyId(companyId) {
  if (companyId == null) return [];
  const id = String(companyId);
  const all = loadAll();
  return all
    .filter((r) => String(r.companyId) === id)
    .sort((a, b) => String(b.updatedAt || '').localeCompare(String(a.updatedAt || '')));
}
