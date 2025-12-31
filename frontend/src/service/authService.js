import { apiRequest } from './apiClient';

export async function login({ username, password }) {
  const res = await apiRequest('/api/auth/login', {
    method: 'POST',
    body: { username, password },
  });
  // { message, data: { token, account_id, role, profile } }
  return res.data;
}

export async function register({
  username,
  password,
  fullName,
  phoneNumber,
  email,
  avatarBase64,
  role,
}) {
  const res = await apiRequest('/api/auth/register', {
    method: 'POST',
    body: {
      username,
      password,
      fullName,
      phoneNumber,
      email,
      avatarBase64,
      role,
    },
  });
  return res.data;
}
