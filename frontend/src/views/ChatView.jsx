import React, { useEffect, useMemo, useRef, useState } from 'react';
import { loadAuth } from '../utils/authStorage';
import { getLastRescueRequestId } from '../service/rescueRequestService';
import { getOrCreateConversation, getConversationMessages, markConversationRead, sendMessage } from '../service/messageService';

function formatTime(value) {
  if (!value) return '';
  try {
    return new Date(value).toLocaleString();
  } catch {
    return String(value);
  }
}

function normalizeConversation(raw) {
  if (!raw || typeof raw !== 'object') return null;
  const messages = Array.isArray(raw.messages) ? raw.messages : [];
  return {
    id: raw.id,
    requestId: raw.requestId,
    userId: raw.userId,
    userName: raw.userName,
    companyId: raw.companyId,
    companyName: raw.companyName,
    status: raw.status,
    unreadCount: raw.unreadCount,
    messages: messages.map((m) => ({
      id: m.id,
      senderId: m.senderId,
      senderName: m.senderName,
      senderRole: m.senderRole,
      content: m.content,
      sentAt: m.sentAt,
      isRead: m.isRead,
    })),
  };
}

const ChatView = ({ onNavigate }) => {
  const [loading, setLoading] = useState(false);
  const [sending, setSending] = useState(false);
  const [error, setError] = useState(null);
  const [conversation, setConversation] = useState(null);
  const [text, setText] = useState('');

  const bottomRef = useRef(null);
  const requestId = useMemo(() => getLastRescueRequestId(), []);
  const auth = useMemo(() => loadAuth(), []);
  const myAccountId = auth?.user?.accountId ?? auth?.account_id;

  const scrollToBottom = () => {
    try {
      bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
    } catch {
      // ignore
    }
  };

  const refresh = async () => {
    setLoading(true);
    setError(null);
    try {
      if (!requestId) {
        throw new Error('Chưa có mã yêu cầu để mở tin nhắn. Hãy chọn 1 yêu cầu trước.');
      }

      // Ensure conversation exists
      const conv = normalizeConversation(await getOrCreateConversation(requestId));

      // Fetch message history (some backends might not return full messages from create)
      const withMessages = normalizeConversation(await getConversationMessages(requestId)) || conv;
      setConversation(withMessages);

      // Mark read if possible (non-blocking)
      if (withMessages?.id) {
        markConversationRead(withMessages.id).catch(() => {});
      }

      setTimeout(scrollToBottom, 50);
    } catch (err) {
      const details = Array.isArray(err?.details) && err.details.length > 0
        ? `: ${err.details.join(', ')}`
        : '';
      setError(`${err?.message || 'Không thể tải hội thoại'}${details}`);
      setConversation(null);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    refresh();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleSend = async (e) => {
    e.preventDefault();
    if (sending) return;
    setError(null);

    const content = text.trim();
    if (!content) return;

    setSending(true);
    try {
      await sendMessage({
        requestId: Number(requestId),
        content,
      });
      setText('');
      await refresh();
    } catch (err) {
      const details = Array.isArray(err?.details) && err.details.length > 0
        ? `: ${err.details.join(', ')}`
        : '';
      setError(`${err?.message || 'Gửi tin nhắn thất bại'}${details}`);
    } finally {
      setSending(false);
    }
  };

  const title = conversation
    ? `Tin nhắn - Yêu cầu #${conversation.requestId}`
    : 'Tin nhắn';

  return (
    <div className="min-h-[80vh] bg-gray-100 py-10">
      <div className="container mx-auto px-4">
        <div className="max-w-3xl mx-auto bg-white rounded-xl shadow-lg p-6">
          <div className="flex items-start justify-between gap-3">
            <div>
              <h1 className="text-2xl font-extrabold text-gray-900">{title}</h1>
              {conversation && (
                <p className="text-sm text-gray-600 mt-1">
                  {conversation.companyName ? `Công ty: ${conversation.companyName}` : ''}
                  {conversation.userName ? `${conversation.companyName ? ' • ' : ''}Khách: ${conversation.userName}` : ''}
                </p>
              )}
            </div>
            <div className="flex gap-2">
              <button
                type="button"
                onClick={() => onNavigate('requestDetail')}
                className="bg-gray-200 text-gray-900 font-bold px-4 py-2 rounded hover:bg-gray-300"
              >
                Quay lại
              </button>
              <button
                type="button"
                onClick={refresh}
                className="bg-blue-900 text-white font-bold px-4 py-2 rounded hover:bg-blue-800 disabled:opacity-60"
                disabled={loading}
              >
                Tải lại
              </button>
            </div>
          </div>

          {!requestId && (
            <div className="mt-6 bg-yellow-50 border border-yellow-200 rounded-lg p-4 text-sm text-yellow-900">
              Bạn chưa chọn yêu cầu nào. Hãy mở chi tiết yêu cầu trước, sau đó vào Tin nhắn.
            </div>
          )}

          {error && (
            <div className="mt-4 text-sm text-red-600 bg-red-50 border border-red-200 rounded-md p-2">
              {error}
            </div>
          )}

          <div className="mt-6 border border-gray-200 rounded-lg bg-gray-50 h-[420px] overflow-y-auto p-3">
            {loading && (
              <div className="text-sm text-gray-700">Đang tải tin nhắn...</div>
            )}

            {!loading && conversation && conversation.messages.length === 0 && (
              <div className="text-sm text-gray-700">Chưa có tin nhắn nào. Hãy gửi tin nhắn đầu tiên.</div>
            )}

            {!loading && conversation && conversation.messages.length > 0 && (
              <div className="space-y-2">
                {conversation.messages.map((m) => {
                  const isMine = myAccountId != null && String(m.senderId) === String(myAccountId);
                  return (
                    <div key={m.id} className={`flex ${isMine ? 'justify-end' : 'justify-start'}`}>
                      <div className={`max-w-[80%] rounded-lg px-3 py-2 ${isMine ? 'bg-blue-900 text-white' : 'bg-white border border-gray-200 text-gray-900'}`}>
                        <div className="text-xs opacity-80">
                          {m.senderName || (isMine ? 'Bạn' : 'Đối phương')}
                          {m.sentAt ? ` • ${formatTime(m.sentAt)}` : ''}
                        </div>
                        <div className="text-sm mt-1 whitespace-pre-wrap break-words">{m.content}</div>
                      </div>
                    </div>
                  );
                })}
                <div ref={bottomRef} />
              </div>
            )}
          </div>

          <form onSubmit={handleSend} className="mt-4 flex gap-2">
            <input
              type="text"
              value={text}
              onChange={(e) => setText(e.target.value)}
              placeholder="Nhập tin nhắn..."
              className="flex-1 border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
              disabled={sending || !requestId}
            />
            <button
              type="submit"
              disabled={sending || !requestId}
              className="bg-yellow-500 text-blue-900 font-extrabold px-4 py-2 rounded hover:bg-yellow-400 disabled:opacity-60"
            >
              {sending ? 'Đang gửi...' : 'Gửi'}
            </button>
          </form>

          <div className="text-xs text-gray-500 mt-3">
            UC101 dùng REST API: <span className="font-semibold">/v1/api/messages</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ChatView;
