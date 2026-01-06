const LAST_POST_ID_KEY = 'resq_last_post_id';
const LAST_POST_BACK_VIEW_KEY = 'resq_last_post_back_view';

export function setLastPostId(postId) {
  if (postId == null || postId === '') return;
  localStorage.setItem(LAST_POST_ID_KEY, String(postId));
}

export function getLastPostId() {
  const raw = localStorage.getItem(LAST_POST_ID_KEY);
  if (!raw) return null;
  const n = Number(raw);
  return Number.isFinite(n) ? n : null;
}

export function clearLastPostId() {
  localStorage.removeItem(LAST_POST_ID_KEY);
}

export function setLastPostBackView(backView) {
  if (!backView) return;
  localStorage.setItem(LAST_POST_BACK_VIEW_KEY, String(backView));
}

export function getLastPostBackView() {
  return localStorage.getItem(LAST_POST_BACK_VIEW_KEY) || null;
}

export function clearLastPostBackView() {
  localStorage.removeItem(LAST_POST_BACK_VIEW_KEY);
}
