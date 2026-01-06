const LAST_USER_ID_KEY = 'resq_last_user_id';

export function setLastUserId(userId) {
  if (userId == null || userId === '') return;
  localStorage.setItem(LAST_USER_ID_KEY, String(userId));
}

export function getLastUserId() {
  const raw = localStorage.getItem(LAST_USER_ID_KEY);
  if (!raw) return null;
  const n = Number(raw);
  return Number.isFinite(n) ? n : null;
}

export function clearLastUserId() {
  localStorage.removeItem(LAST_USER_ID_KEY);
}
