import React, { useEffect, useMemo, useState } from 'react';
import { getCommunityComments, getCommunityPostDetail } from '../service/communityService';
import { getLastPostBackView, getLastPostId } from '../utils/postStorage';
import { setLastUserId } from '../utils/userStorage';

function formatDateTime(value) {
  if (!value) return '';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return String(value);
  return date.toLocaleString('vi-VN');
}

function AvatarCircle({ label, src, size = 10 }) {
  const fallback = String(label || '?').trim().slice(0, 1).toUpperCase();
  const cls = `h-${size} w-${size}`;
  return (
    <div className={`${cls} rounded-full bg-gray-200 overflow-hidden flex items-center justify-center text-gray-600 text-sm font-bold`}>
      {src ? <img src={src} alt="avatar" className="h-full w-full object-cover" /> : fallback}
    </div>
  );
}

function toAvatarSrc(raw) {
  if (!raw) return null;
  const s = String(raw);
  if (s.startsWith('data:')) return s;
  return `data:image/jpeg;base64,${s}`;
}

export default function CommunityPostDetailView({ onNavigate }) {
  const postId = useMemo(() => getLastPostId(), []);
  const backView = useMemo(() => getLastPostBackView() || 'publicUserProfile', []);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [post, setPost] = useState(null);

  const [commentsLoading, setCommentsLoading] = useState(false);
  const [commentsError, setCommentsError] = useState('');
  const [comments, setComments] = useState([]);

  useEffect(() => {
    let alive = true;

    async function run() {
      if (!postId) {
        setError('Chưa có bài đăng nào được chọn để xem chi tiết.');
        setPost(null);
        setComments([]);
        return;
      }

      setLoading(true);
      setError('');
      try {
        const data = await getCommunityPostDetail(postId);
        if (!alive) return;
        setPost(data);
      } catch (e) {
        if (!alive) return;
        setError(e?.message || 'Không thể tải chi tiết bài đăng.');
        setPost(null);
      } finally {
        if (alive) setLoading(false);
      }
    }

    run();
    return () => {
      alive = false;
    };
  }, [postId]);

  useEffect(() => {
    let alive = true;

    async function run() {
      if (!postId) return;
      setCommentsLoading(true);
      setCommentsError('');
      try {
        const list = await getCommunityComments(postId);
        if (!alive) return;
        setComments(Array.isArray(list) ? list : []);
      } catch (e) {
        if (!alive) return;
        setComments([]);
        setCommentsError(e?.message || 'Không thể tải bình luận.');
      } finally {
        if (alive) setCommentsLoading(false);
      }
    }

    run();
    return () => {
      alive = false;
    };
  }, [postId]);

  function goToUserProfile(authorId) {
    if (!authorId) return;
    setLastUserId(authorId);
    onNavigate?.('publicUserProfile');
  }

  const authorName = post?.author?.fullName || post?.author?.username || 'Ẩn danh';
  const authorAvatar = toAvatarSrc(post?.author?.avatarBase64);

  return (
    <main className="bg-gray-100 min-h-[60vh] py-10">
      <div className="container mx-auto px-4 max-w-3xl">
        <section className="bg-white rounded shadow-sm p-6">
          <div className="flex items-start justify-between gap-4 flex-wrap">
            <div>
              <div className="text-2xl font-bold text-gray-900">Chi tiết bài đăng</div>
              <div className="text-sm text-gray-600 mt-1">Xem nội dung và bình luận.</div>
            </div>
            <button
              type="button"
              onClick={() => onNavigate?.(backView)}
              className="bg-blue-900 text-white font-bold px-4 py-2 rounded hover:bg-blue-800"
            >
              Quay lại
            </button>
          </div>

          {loading ? <div className="mt-4 text-sm text-gray-700">Đang tải...</div> : null}
          {error ? <div className="mt-4 text-sm text-red-600 bg-red-50 border border-red-200 rounded p-3">{error}</div> : null}

          {post ? (
            <article className="mt-6">
              <div className="flex items-start justify-between gap-3">
                <div className="flex items-start gap-3">
                  <AvatarCircle label={authorName} src={authorAvatar} size={10} />
                  <div>
                    <button
                      type="button"
                      onClick={() => goToUserProfile(post?.author?.id)}
                      className="font-semibold text-gray-800 hover:underline text-left"
                    >
                      {authorName}
                    </button>
                    <div className="text-xs text-gray-500">{formatDateTime(post?.createdAt)}</div>
                  </div>
                </div>

                {post?.isResolved ? (
                  <span className="text-xs bg-green-100 text-green-700 px-2 py-1 rounded">Đã đóng bình luận</span>
                ) : (
                  <span className="text-xs bg-yellow-100 text-yellow-800 px-2 py-1 rounded">Đang mở</span>
                )}
              </div>

              <h1 className="mt-3 text-xl font-bold text-gray-900">{post?.title}</h1>
              <p className="mt-2 text-gray-800 whitespace-pre-wrap">{post?.content}</p>

              {post?.imageBase64 ? (
                <img
                  src={toAvatarSrc(post.imageBase64) || post.imageBase64}
                  alt="post"
                  className="w-full rounded border mt-3"
                />
              ) : null}

              <div className="mt-3 text-sm text-gray-600">
                {typeof post?.commentCount === 'number' ? post.commentCount : comments.length} bình luận
                {typeof post?.viewCount === 'number' ? ` · ${post.viewCount} lượt xem` : ''}
              </div>

              <div className="mt-6 border-t pt-4">
                <div className="font-bold text-gray-900">Bình luận</div>

                {commentsLoading ? <div className="mt-3 text-sm text-gray-700">Đang tải bình luận...</div> : null}
                {commentsError ? <div className="mt-3 text-sm text-red-600">{commentsError}</div> : null}

                <div className="mt-3 space-y-3">
                  {comments.map((c) => {
                    const name = c?.author?.fullName || c?.author?.username || 'Ẩn danh';
                    const ava = toAvatarSrc(c?.author?.avatarBase64);
                    return (
                      <div key={c?.id ?? `${c?.createdAt}-${name}`} className="bg-gray-50 border rounded p-3">
                        <div className="flex items-start gap-3">
                          <AvatarCircle label={name} src={ava} size={8} />
                          <div className="flex-1">
                            <div className="flex items-start justify-between gap-3">
                              <button
                                type="button"
                                onClick={() => goToUserProfile(c?.author?.id)}
                                className="font-semibold text-gray-800 hover:underline text-left"
                              >
                                {name}
                              </button>
                              <div className="text-xs text-gray-500">{formatDateTime(c?.createdAt)}</div>
                            </div>
                            <div className="mt-1 text-sm text-gray-800 whitespace-pre-wrap">{c?.content}</div>
                          </div>
                        </div>
                      </div>
                    );
                  })}
                </div>

                {!commentsLoading && comments.length === 0 ? (
                  <div className="mt-3 text-sm text-gray-700">Chưa có bình luận nào.</div>
                ) : null}
              </div>
            </article>
          ) : null}
        </section>
      </div>
    </main>
  );
}
