const LAST_COMPANY_ID_KEY = 'resq_last_company_id';

export function setLastCompanyId(companyId) {
  if (companyId == null || companyId === '') return;
  localStorage.setItem(LAST_COMPANY_ID_KEY, String(companyId));
}

export function getLastCompanyId() {
  const raw = localStorage.getItem(LAST_COMPANY_ID_KEY);
  if (!raw) return null;
  const n = Number(raw);
  return Number.isFinite(n) ? n : null;
}

export function clearLastCompanyId() {
  localStorage.removeItem(LAST_COMPANY_ID_KEY);
}
