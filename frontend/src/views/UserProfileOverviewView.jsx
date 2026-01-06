import React, { useEffect, useMemo, useState } from 'react';
import { Building2, Mail, MapPin, Pencil, Phone, User } from 'lucide-react';
import { getUserProfile } from '../service/userProfileService';
import { getMyCompanyProfile } from '../service/companyProfileService';
import { getMyCommunityPosts } from '../service/communityService';

function formatDateTime(value) {
  if (!value) return '';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return String(value);
  return date.toLocaleString('vi-VN');
}

function AvatarCircle({ label, src }) {
  const fallback = String(label || '?').trim().slice(0, 1).toUpperCase();
  return (
    <div className="h-16 w-16 rounded-full bg-gray-200 overflow-hidden flex items-center justify-center text-gray-600 text-xl font-bold">
      {src ? <img src={src} alt="avatar" className="h-full w-full object-cover" /> : fallback}
    </div>
  );
}

function PostCard({ post }) {
  if (!post) return null;
  return (
    <article className="bg-white rounded shadow-sm p-4">
      <div className="flex items-start justify-between gap-3">
        <div className="flex items-start gap-3">
          <div className="h-10 w-10 rounded-full bg-gray-200 overflow-hidden flex items-center justify-center text-gray-500 text-sm">
            {post?.author?.avatarBase64 ? (
              <img src={post.author.avatarBase64} alt="avatar" className="h-full w-full object-cover" />
            ) : (
              (post?.author?.fullName || post?.author?.username || '?').trim().slice(0, 1).toUpperCase()
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
          <span className="text-xs bg-green-100 text-green-700 px-2 py-1 rounded">Đã đóng bình luận</span>
        ) : (
          <span className="text-xs bg-yellow-100 text-yellow-800 px-2 py-1 rounded">Đang mở</span>
        )}
      </div>

      <h2 className="mt-3 text-lg font-bold text-gray-900">{post?.title}</h2>
      <p className="mt-2 text-gray-800 whitespace-pre-wrap">{post?.content}</p>

      {post?.imageBase64 ? (
        <img src={post.imageBase64} alt="post" className="w-full rounded border mt-3" />
      ) : null}

      <div className="mt-3 text-sm text-gray-600">
        {typeof post?.commentCount === 'number' ? post.commentCount : 0} bình luận
        {typeof post?.viewCount === 'number' ? ` · ${post.viewCount} lượt xem` : ''}
      </div>
    </article>
  );
}

export default function UserProfileOverviewView({ user, onNavigate }) {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [accountProfile, setAccountProfile] = useState(null);
  const [companyProfile, setCompanyProfile] = useState(null);

  const [postsLoading, setPostsLoading] = useState(false);
  const [postsError, setPostsError] = useState('');
  const [postsPage, setPostsPage] = useState(null);
  const [pageIndex, setPageIndex] = useState(0);

  const role = String(user?.role || '').toUpperCase();
  const canEdit = Boolean(user);

  const avatarSrc = useMemo(() => {
    const base = accountProfile?.avatarBase64 ?? accountProfile?.avatar_base64;
    return base ? `data:image/jpeg;base64,${base}` : null;
  }, [accountProfile]);

  const displayName = useMemo(() => {
    const fullName = accountProfile?.fullName ?? accountProfile?.full_name;
    return fullName || user?.fullName || user?.username || 'Người dùng';
  }, [accountProfile, user]);

  const hasNextPage = useMemo(() => {
    if (!postsPage) return false;
    if (typeof postsPage.last === 'boolean') return !postsPage.last;
    if (typeof postsPage.totalPages === 'number' && typeof postsPage.number === 'number') {
      return postsPage.number + 1 < postsPage.totalPages;
    }
    return false;
  }, [postsPage]);

  const posts = useMemo(() => {
    const content = postsPage?.content;
    return Array.isArray(content) ? content : [];
  }, [postsPage]);

  async function loadMyPosts({ page = 0, append = false } = {}) {
    setPostsError('');
    setPostsLoading(true);
    try {
      const data = await getMyCommunityPosts({ page, size: 10 });
      setPostsPage((prev) => {
        if (!append) return data;
        const prevContent = Array.isArray(prev?.content) ? prev.content : [];
        const nextContent = Array.isArray(data?.content) ? data.content : [];
        return { ...data, content: [...prevContent, ...nextContent] };
      });
      setPageIndex(page);
    } catch (e) {
      setPostsError(e?.message || 'Không thể tải bài đăng của bạn.');
      if (e?.status === 401) onNavigate?.('login');
    } finally {
      setPostsLoading(false);
    }
  }

  useEffect(() => {
    let alive = true;

    async function run() {
      setLoading(true);
      setError('');

      if (!user) {
        setAccountProfile(null);
        setCompanyProfile(null);
        setLoading(false);
        return;
      }

      try {
        const data = await getUserProfile();
        if (!alive) return;
        setAccountProfile(data);
      } catch (e) {
        if (!alive) return;
        setAccountProfile(null);
        setError(e?.message || 'Không thể tải hồ sơ người dùng.');
      }

      if (role === 'COMPANY') {
        try {
          const cp = await getMyCompanyProfile();
          if (!alive) return;
          setCompanyProfile(cp);
        } catch (e) {
          if (!alive) return;
          setCompanyProfile(null);
        }
      } else {
        setCompanyProfile(null);
      }

      setLoading(false);
    }

    run();
    return () => {
      alive = false;
    };
  }, [user, role]);

  useEffect(() => {
    if (!user) return;
    loadMyPosts({ page: 0, append: false });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [user?.id, user?.accountId, user?.account_id]);

  return (
    <main className="bg-gray-100 min-h-[60vh] py-10">
      <div className="container mx-auto px-4 max-w-5xl">
        <section className="bg-white rounded shadow-sm p-6">
          <div className="flex items-start justify-between gap-4 flex-wrap">
            <div className="flex items-start gap-4">
              <AvatarCircle label={displayName} src={avatarSrc} />
              <div>
                <div className="text-2xl font-bold text-gray-900 flex items-center gap-2">
                  <span>{displayName}</span>
                </div>
                <div className="text-sm text-gray-600 mt-1">
                  {role ? `Vai trò: ${role}` : ''}
                </div>
                {user?.username ? <div className="text-sm text-gray-600">@{user.username}</div> : null}
              </div>
            </div>

            {canEdit ? (
              <button
                type="button"
                onClick={() => onNavigate?.('profileEdit')}
                className="bg-blue-900 text-white font-bold px-4 py-2 rounded hover:bg-blue-800 flex items-center gap-2"
              >
                <Pencil size={18} />
                Chỉnh sửa hồ sơ
              </button>
            ) : null}
          </div>

          {loading ? <div className="mt-4 text-sm text-gray-700">Đang tải hồ sơ...</div> : null}
          {error ? <div className="mt-4 text-sm text-red-600 bg-red-50 border border-red-200 rounded p-3">{error}</div> : null}
        </section>

        <div className="mt-6 grid grid-cols-1 lg:grid-cols-3 gap-4">
          {/* Left: Basic info */}
          <section className="lg:col-span-1 space-y-4">
            <div className="bg-white rounded shadow-sm p-4">
              <div className="font-bold text-gray-900 mb-3">Thông tin cơ bản</div>

              <div className="space-y-2 text-sm text-gray-800">
                <div className="flex items-center gap-2">
                  <User size={16} className="text-gray-500" />
                  <span className="font-semibold">Họ tên:</span>
                  <span>{accountProfile?.fullName ?? accountProfile?.full_name ?? user?.fullName ?? '—'}</span>
                </div>

                <div className="flex items-center gap-2">
                  <Phone size={16} className="text-gray-500" />
                  <span className="font-semibold">SĐT:</span>
                  <span>{accountProfile?.phoneNumber ?? accountProfile?.phone_number ?? user?.phoneNumber ?? '—'}</span>
                </div>

                <div className="flex items-center gap-2">
                  <Mail size={16} className="text-gray-500" />
                  <span className="font-semibold">Email:</span>
                  <span>{accountProfile?.email ?? user?.email ?? '—'}</span>
                </div>
              </div>
            </div>

            {role === 'COMPANY' ? (
              <div className="bg-white rounded shadow-sm p-4">
                <div className="font-bold text-gray-900 mb-3">Hồ sơ công ty</div>

                <div className="space-y-2 text-sm text-gray-800">
                  <div className="flex items-center gap-2">
                    <Building2 size={16} className="text-gray-500" />
                    <span className="font-semibold">Tên:</span>
                    <span>{companyProfile?.name || '—'}</span>
                  </div>

                  <div className="flex items-start gap-2">
                    <MapPin size={16} className="text-gray-500 mt-[2px]" />
                    <span className="font-semibold">Địa chỉ:</span>
                    <span className="flex-1">{companyProfile?.address || '—'}</span>
                  </div>

                  <div className="flex items-center gap-2">
                    <Phone size={16} className="text-gray-500" />
                    <span className="font-semibold">SĐT:</span>
                    <span>{companyProfile?.phone || '—'}</span>
                  </div>

                  <div className="flex items-center gap-2">
                    <Mail size={16} className="text-gray-500" />
                    <span className="font-semibold">Email:</span>
                    <span>{companyProfile?.email || '—'}</span>
                  </div>

                  {companyProfile?.description ? (
                    <div className="text-sm text-gray-800 whitespace-pre-wrap pt-2 border-t">
                      {companyProfile.description}
                    </div>
                  ) : null}
                </div>
              </div>
            ) : null}
          </section>

          {/* Right: Posts timeline */}
          <section className="lg:col-span-2">
            <div className="bg-white rounded shadow-sm p-4 mb-4">
              <div className="flex items-center justify-between gap-3 flex-wrap">
                <div>
                  <div className="font-bold text-gray-900">Bài đăng</div>
                  <div className="text-sm text-gray-600">Các bài bạn đã đăng trong Cộng đồng.</div>
                </div>
                <button
                  type="button"
                  onClick={() => loadMyPosts({ page: 0, append: false })}
                  className="text-sm bg-blue-900 text-white px-3 py-2 rounded hover:bg-blue-800"
                  disabled={postsLoading || !user}
                >
                  Làm mới
                </button>
              </div>

              {postsError ? <div className="mt-3 text-sm text-red-600">{postsError}</div> : null}
            </div>

            {postsLoading && !postsPage ? (
              <div className="bg-white rounded shadow-sm p-4">Đang tải bài đăng...</div>
            ) : null}

            <div className="space-y-4">
              {posts.map((p) => (
                <PostCard key={p?.id ?? `${p?.title}-${p?.createdAt}`} post={p} />
              ))}
            </div>

            {!postsLoading && posts.length === 0 && user ? (
              <div className="bg-white rounded shadow-sm p-4 mt-4 text-sm text-gray-700">
                Bạn chưa có bài đăng nào.
              </div>
            ) : null}

            <div className="mt-6 flex justify-center">
              {posts.length > 0 && hasNextPage ? (
                <button
                  type="button"
                  onClick={() => loadMyPosts({ page: pageIndex + 1, append: true })}
                  className="bg-blue-900 text-white px-4 py-2 rounded hover:bg-blue-800 disabled:opacity-60"
                  disabled={postsLoading}
                >
                  {postsLoading ? 'Đang tải...' : 'Tải thêm'}
                </button>
              ) : null}
            </div>

            {!user ? (
              <div className="bg-white rounded shadow-sm p-4 mt-4 flex items-center justify-between gap-3 flex-wrap">
                <div>
                  <div className="font-semibold text-gray-800">Bạn chưa đăng nhập</div>
                  <div className="text-sm text-gray-600">Đăng nhập để xem hồ sơ và bài đăng của bạn.</div>
                </div>
                <button
                  type="button"
                  onClick={() => onNavigate?.('login')}
                  className="bg-yellow-500 text-blue-900 font-bold px-4 py-2 rounded hover:bg-yellow-400"
                >
                  Đăng nhập
                </button>
              </div>
            ) : null}
          </section>
        </div>
      </div>
    </main>
  );
}
