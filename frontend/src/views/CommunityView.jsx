import React, { useEffect, useMemo, useState } from 'react';
import {
  addCommunityComment,
  createCommunityPost,
  getCommunityComments,
  getCommunityPosts,
} from '../service/communityService';

function formatDateTime(value) {
  if (!value) return '';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return String(value);
  return date.toLocaleString('vi-VN');
}

function readFileAsDataUrl(file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => resolve(String(reader.result || ''));
    reader.onerror = () => reject(new Error('Không thể đọc file ảnh.'));
    reader.readAsDataURL(file);
  });
}

const CommunityView = ({ onNavigate, user }) => {
  const [feedLoading, setFeedLoading] = useState(false);
  const [feedError, setFeedError] = useState('');
  const [postsPage, setPostsPage] = useState(null);
  const [pageIndex, setPageIndex] = useState(0);

  const [composer, setComposer] = useState({
    title: '',
    content: '',
    imageBase64: '',
  });
  const [composerLoading, setComposerLoading] = useState(false);
  const [composerError, setComposerError] = useState('');

  // Per-post UI state
  const [expanded, setExpanded] = useState({});
  const [commentsByPostId, setCommentsByPostId] = useState({});
  const [commentsLoading, setCommentsLoading] = useState({});
  const [commentDraft, setCommentDraft] = useState({});
  const [commentsError, setCommentsError] = useState({});
  const [sendError, setSendError] = useState({});
  const [commentSending, setCommentSending] = useState({});

  const posts = useMemo(() => {
    const content = postsPage?.content;
    return Array.isArray(content) ? content : [];
  }, [postsPage]);

  const hasNextPage = useMemo(() => {
    if (!postsPage) return false;
    if (typeof postsPage.last === 'boolean') return !postsPage.last;
    if (typeof postsPage.totalPages === 'number' && typeof postsPage.number === 'number') {
      return postsPage.number + 1 < postsPage.totalPages;
    }
    return false;
  }, [postsPage]);

  async function loadPosts({ page = 0, append = false } = {}) {
    setFeedError('');
    setFeedLoading(true);
    try {
      const data = await getCommunityPosts({ page, size: 10 });
      if (append && postsPage?.content) {
        const next = {
          ...data,
          content: [...postsPage.content, ...(Array.isArray(data?.content) ? data.content : [])],
        };
        setPostsPage(next);
      } else {
        setPostsPage(data);
      }
      setPageIndex(page);
    } catch (e) {
      setFeedError(e?.message || 'Không thể tải bài đăng cộng đồng.');
    } finally {
      setFeedLoading(false);
    }
  }

  useEffect(() => {
    loadPosts({ page: 0, append: false });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  async function handlePickImage(ev) {
    const file = ev.target.files?.[0];
    if (!file) return;
    try {
      const dataUrl = await readFileAsDataUrl(file);
      setComposer((prev) => ({ ...prev, imageBase64: dataUrl }));
    } catch (e) {
      setComposerError(e?.message || 'Không thể tải ảnh.');
    }
  }

  async function handleCreatePost(ev) {
    ev.preventDefault();
    setComposerError('');

    try {
      setComposerLoading(true);
      const created = await createCommunityPost({
        title: composer.title,
        content: composer.content,
        imageBase64: composer.imageBase64 || null,
      });

      // Prepend new post to current feed
      setPostsPage((prev) => {
        if (!prev) return { content: [created] };
        const content = Array.isArray(prev.content) ? prev.content : [];
        return { ...prev, content: [created, ...content] };
      });

      setComposer({ title: '', content: '', imageBase64: '' });
    } catch (e) {
      setComposerError(e?.message || 'Đăng bài thất bại.');
      if (e?.status === 401) onNavigate?.('login');
    } finally {
      setComposerLoading(false);
    }
  }

  async function ensureCommentsLoaded(postId) {
    if (commentsByPostId[postId]) return;

    setCommentsLoading((prev) => ({ ...prev, [postId]: true }));
    setCommentsError((prev) => ({ ...prev, [postId]: '' }));
    try {
      const list = await getCommunityComments(postId);
      setCommentsByPostId((prev) => ({ ...prev, [postId]: Array.isArray(list) ? list : [] }));
    } catch (e) {
      setCommentsError((prev) => ({
        ...prev,
        [postId]: e?.message || 'Không thể tải bình luận.',
      }));
    } finally {
      setCommentsLoading((prev) => ({ ...prev, [postId]: false }));
    }
  }

  async function toggleComments(postId) {
    setExpanded((prev) => ({ ...prev, [postId]: !prev[postId] }));
    const nextExpanded = !expanded[postId];
    if (nextExpanded) await ensureCommentsLoaded(postId);
  }

  async function handleSendComment(postId) {
    setSendError((prev) => ({ ...prev, [postId]: '' }));

    const text = String(commentDraft[postId] || '').trim();
    if (!text) {
      setSendError((prev) => ({ ...prev, [postId]: 'Vui lòng nhập nội dung bình luận.' }));
      return;
    }

    try {
      setCommentSending((prev) => ({ ...prev, [postId]: true }));
      const created = await addCommunityComment(postId, { content: text });

      setCommentsByPostId((prev) => {
        const existing = Array.isArray(prev[postId]) ? prev[postId] : [];
        return { ...prev, [postId]: [...existing, created] };
      });
      setCommentDraft((prev) => ({ ...prev, [postId]: '' }));
    } catch (e) {
      setSendError((prev) => ({ ...prev, [postId]: e?.message || 'Gửi bình luận thất bại.' }));
      if (e?.status === 401) onNavigate?.('login');
    } finally {
      setCommentSending((prev) => ({ ...prev, [postId]: false }));
    }
  }

  return (
    <main className="bg-gray-100 min-h-[60vh] py-10">
      <div className="container mx-auto px-4 max-w-4xl">
        <div className="flex items-baseline justify-between mb-6">
          <div>
            <h1 className="text-2xl font-bold text-gray-800">Cộng đồng</h1>
            <p className="text-gray-600 text-sm">Chia sẻ sự cố, hỏi đáp và nhận tư vấn từ cộng đồng.</p>
          </div>
          <button
            onClick={() => loadPosts({ page: 0, append: false })}
            className="text-sm bg-blue-900 text-white px-3 py-2 rounded hover:bg-blue-800"
            disabled={feedLoading}
          >
            Làm mới
          </button>
        </div>

        {/* Composer */}
        <section className="bg-white rounded shadow-sm p-4 mb-6">
          {user ? (
            <form onSubmit={handleCreatePost} className="space-y-3">
              <div className="text-sm text-gray-700">
                Đăng bài với tài khoản <span className="font-semibold">{user.username}</span>
              </div>

              <input
                type="text"
                value={composer.title}
                onChange={(e) => setComposer((p) => ({ ...p, title: e.target.value }))}
                placeholder="Tiêu đề..."
                className="w-full border rounded px-3 py-2 focus:outline-none focus:ring"
                maxLength={500}
                required
                disabled={composerLoading}
              />

              <textarea
                value={composer.content}
                onChange={(e) => setComposer((p) => ({ ...p, content: e.target.value }))}
                placeholder="Bạn đang gặp vấn đề gì? Mô tả chi tiết để mọi người dễ hỗ trợ..."
                className="w-full border rounded px-3 py-2 min-h-[110px] focus:outline-none focus:ring"
                required
                disabled={composerLoading}
              />

              <div className="flex items-center justify-between gap-3 flex-wrap">
                <div className="flex items-center gap-3">
                  <input
                    type="file"
                    accept="image/*"
                    onChange={handlePickImage}
                    disabled={composerLoading}
                    className="text-sm"
                  />
                  {composer.imageBase64 ? (
                    <button
                      type="button"
                      onClick={() => setComposer((p) => ({ ...p, imageBase64: '' }))}
                      className="text-sm text-red-600 hover:underline"
                      disabled={composerLoading}
                    >
                      Xóa ảnh
                    </button>
                  ) : null}
                </div>

                <button
                  type="submit"
                  disabled={composerLoading}
                  className="bg-yellow-500 text-blue-900 font-bold px-4 py-2 rounded hover:bg-yellow-400 disabled:opacity-60"
                >
                  {composerLoading ? 'Đang đăng...' : 'Đăng bài'}
                </button>
              </div>

              {composer.imageBase64 ? (
                <img
                  src={composer.imageBase64}
                  alt="Preview"
                  className="w-full rounded border"
                />
              ) : null}

              {composerError ? <div className="text-sm text-red-600">{composerError}</div> : null}
            </form>
          ) : (
            <div className="flex items-center justify-between gap-3 flex-wrap">
              <div>
                <div className="font-semibold text-gray-800">Bạn chưa đăng nhập</div>
                <div className="text-sm text-gray-600">Đăng nhập để đăng bài và bình luận.</div>
              </div>
              <button
                onClick={() => onNavigate?.('login')}
                className="bg-yellow-500 text-blue-900 font-bold px-4 py-2 rounded hover:bg-yellow-400"
              >
                Đăng nhập
              </button>
            </div>
          )}
        </section>

        {/* Feed */}
        {feedError ? (
          <div className="bg-white rounded shadow-sm p-4 text-red-600 mb-4">{feedError}</div>
        ) : null}

        {feedLoading && !postsPage ? (
          <div className="bg-white rounded shadow-sm p-4">Đang tải bài đăng...</div>
        ) : null}

        <div className="space-y-4">
          {posts.map((post) => {
            const postId = post?.id;
            const isOpen = !!expanded[postId];
            const cLoading = !!commentsLoading[postId];
            const cError = commentsError[postId] || '';
            const comments = commentsByPostId[postId] || [];
            const draft = commentDraft[postId] || '';
            const sending = !!commentSending[postId];
            const sError = sendError[postId] || '';

            return (
              <article key={postId} className="bg-white rounded shadow-sm p-4">
                <div className="flex items-start justify-between gap-3">
                  <div className="flex items-start gap-3">
                    <div className="h-10 w-10 rounded-full bg-gray-200 overflow-hidden flex items-center justify-center text-gray-500 text-sm">
                      {post?.author?.avatarBase64 ? (
                        <img
                          src={post.author.avatarBase64}
                          alt="avatar"
                          className="h-full w-full object-cover"
                        />
                      ) : (
                        (post?.author?.fullName || post?.author?.username || '?')
                          .trim()
                          .slice(0, 1)
                          .toUpperCase()
                      )}
                    </div>

                    <div>
                      <div className="font-semibold text-gray-800">
                        {post?.author?.fullName || post?.author?.username || 'Ẩn danh'}
                      </div>
                      <div className="text-xs text-gray-500">{formatDateTime(post?.createdAt)}</div>
                    </div>
                  </div>

                  {post?.isResolved ? (
                    <span className="text-xs bg-green-100 text-green-700 px-2 py-1 rounded">Đã giải quyết</span>
                  ) : (
                    <span className="text-xs bg-yellow-100 text-yellow-800 px-2 py-1 rounded">Đang mở</span>
                  )}
                </div>

                <h2 className="mt-3 text-lg font-bold text-gray-900">{post?.title}</h2>
                <p className="mt-2 text-gray-800 whitespace-pre-wrap">{post?.content}</p>

                {post?.imageBase64 ? (
                  <img
                    src={post.imageBase64}
                    alt="post"
                    className="w-full rounded border mt-3"
                  />
                ) : null}

                <div className="mt-3 flex items-center justify-between text-sm text-gray-600">
                  <div>
                    {typeof post?.commentCount === 'number' ? post.commentCount : comments.length} bình luận
                    {typeof post?.viewCount === 'number' ? ` · ${post.viewCount} lượt xem` : ''}
                  </div>
                  <button
                    onClick={() => toggleComments(postId)}
                    className="text-blue-900 hover:underline"
                    type="button"
                  >
                    {isOpen ? 'Ẩn bình luận' : 'Xem bình luận'}
                  </button>
                </div>

                {isOpen ? (
                  <div className="mt-4 border-t pt-4">
                    {cLoading ? <div className="text-sm text-gray-600">Đang tải bình luận...</div> : null}
                    {cError ? <div className="text-sm text-red-600">{cError}</div> : null}

                    <div className="space-y-3 mt-3">
                      {comments.map((c) => (
                        <div key={c?.id} className="flex items-start gap-3">
                          <div className="h-8 w-8 rounded-full bg-gray-200 overflow-hidden flex items-center justify-center text-gray-500 text-xs">
                            {c?.author?.avatarBase64 ? (
                              <img
                                src={c.author.avatarBase64}
                                alt="avatar"
                                className="h-full w-full object-cover"
                              />
                            ) : (
                              (c?.author?.fullName || c?.author?.username || '?')
                                .trim()
                                .slice(0, 1)
                                .toUpperCase()
                            )}
                          </div>
                          <div className="flex-1">
                            <div className="bg-gray-50 border rounded px-3 py-2">
                              <div className="text-sm font-semibold text-gray-800">
                                {c?.author?.fullName || c?.author?.username || 'Ẩn danh'}
                              </div>
                              <div className="text-sm text-gray-800 whitespace-pre-wrap">{c?.content}</div>
                            </div>
                            <div className="text-xs text-gray-500 mt-1">{formatDateTime(c?.createdAt)}</div>
                          </div>
                        </div>
                      ))}

                      {comments.length === 0 && !cLoading ? (
                        <div className="text-sm text-gray-600">Chưa có bình luận. Hãy là người đầu tiên!</div>
                      ) : null}
                    </div>

                    <div className="mt-4">
                      {user ? (
                        <div className="flex gap-2">
                          <input
                            type="text"
                            value={draft}
                            onChange={(e) =>
                              setCommentDraft((prev) => ({ ...prev, [postId]: e.target.value }))
                            }
                            placeholder="Viết bình luận..."
                            className="flex-1 border rounded px-3 py-2 focus:outline-none focus:ring"
                            disabled={sending}
                          />
                          <button
                            type="button"
                            onClick={() => handleSendComment(postId)}
                            disabled={sending}
                            className="bg-blue-900 text-white px-4 py-2 rounded hover:bg-blue-800 disabled:opacity-60"
                          >
                            {sending ? 'Đang gửi...' : 'Gửi'}
                          </button>
                        </div>
                      ) : (
                        <div className="flex items-center justify-between gap-2 flex-wrap">
                          <div className="text-sm text-gray-700">Đăng nhập để bình luận.</div>
                          <button
                            type="button"
                            onClick={() => onNavigate?.('login')}
                            className="text-sm bg-yellow-500 text-blue-900 font-bold px-3 py-2 rounded hover:bg-yellow-400"
                          >
                            Đăng nhập
                          </button>
                        </div>
                      )}

                      {sError ? <div className="text-sm text-red-600 mt-2">{sError}</div> : null}
                    </div>
                  </div>
                ) : null}
              </article>
            );
          })}
        </div>

        <div className="mt-6 flex justify-center">
          {posts.length > 0 && hasNextPage ? (
            <button
              onClick={() => loadPosts({ page: pageIndex + 1, append: true })}
              className="bg-blue-900 text-white px-4 py-2 rounded hover:bg-blue-800 disabled:opacity-60"
              disabled={feedLoading}
            >
              {feedLoading ? 'Đang tải...' : 'Tải thêm'}
            </button>
          ) : null}
        </div>
      </div>
    </main>
  );
};

export default CommunityView;
