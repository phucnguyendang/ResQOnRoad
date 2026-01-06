import { apiRequest } from './apiClient';
import { loadAuth } from '../utils/authStorage';

export async function getUserProfile() {
  const auth = loadAuth();
  const res = await apiRequest('/users/profile', {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${auth?.token}`,
    },
  });
  return res.data;
}

export async function updateUserProfile({
  fullName,
  phoneNumber,
  avatarBase64,
}) {
  const auth = loadAuth();
  const payload = {};
  
  if (fullName !== undefined) payload.full_name = fullName;
  if (phoneNumber !== undefined) payload.phone_number = phoneNumber;
  if (avatarBase64 !== undefined) payload.avatar_base64 = avatarBase64;

  const res = await apiRequest('/users/profile', {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${auth?.token}`,
    },
    body: payload,
  });
  return res.data;
}
