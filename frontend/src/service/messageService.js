import { apiRequest } from './apiClient';
import { loadAuth } from '../utils/authStorage';

function requireToken() {
  const auth = loadAuth();
  const token = auth?.token;
  if (!token) {
    const err = new Error('Vui lòng đăng nhập để sử dụng tin nhắn.');
    err.status = 401;
    throw err;
  }
  return token;
}

/**
 * UC101 - Step 1: Access messaging interface (create or get conversation)
 * POST /api/messages/conversation/{requestId}
 */
export async function getOrCreateConversation(requestId) {
  const token = requireToken();

  const res = await apiRequest(`/api/messages/conversation/${requestId}`, {
    method: 'POST',
    token,
  });

  return res.data;
}

/**
 * UC101 - Step 2: Get message history
 * GET /api/messages/{requestId}
 */
export async function getConversationMessages(requestId) {
  const token = requireToken();

  const res = await apiRequest(`/api/messages/${requestId}`, {
    method: 'GET',
    token,
  });

  return res.data;
}

/**
 * UC101 - Step 3: Send a message
 * POST /api/messages
 */
export async function sendMessage({ requestId, content, attachmentUrl, attachmentType }) {
  const token = requireToken();

  const res = await apiRequest('/api/messages', {
    method: 'POST',
    token,
    body: {
      requestId,
      content,
      attachmentUrl: attachmentUrl ?? null,
      attachmentType: attachmentType ?? null,
    },
  });

  return res.data;
}

/**
 * Optional: mark messages as read
 * PUT /api/messages/{conversationId}/read
 */
export async function markConversationRead(conversationId) {
  const token = requireToken();

  const res = await apiRequest(`/api/messages/${conversationId}/read`, {
    method: 'PUT',
    token,
  });

  return res.data;
}
