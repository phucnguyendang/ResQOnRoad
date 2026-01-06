import React, { useEffect, useMemo, useState } from 'react';
import {
  adminGetCommunityPostDetail,
  adminListClosedCommunityPosts,
  adminListDeletedCommunityPosts,
  adminOpenCommunityPostComments,
  adminRestoreCommunityPost,
} from '../service/adminService';
import { loadAuth } from '../utils/authStorage';

function formatDateTime(value) {
  if (!value) return '';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return String(value);
  return date.toLocaleString('vi-VN');
}

export default function AdminPostManagementView({ onNavigate }) {
  const auth = useMemo(() => loadAuth(), []);
  const user = auth?.user;

  const [deletedPosts, setDeletedPosts] = useState([]);
  const [closedPosts, setClosedPosts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const [selectedId, setSelectedId] = useState(null);
  const [detailLoading, setDetailLoading] = useState(false);
  const [detailError, setDetailError] = useState('');
  const [detail, setDetail] = useState(null);

  const refreshLists = async () => {
    setLoading(true);
    setError('');
    try {
      const [deleted, closed] = await Promise.all([
        adminListDeletedCommunityPosts(),
        adminListClosedCommunityPosts(),
      ]);
      setDeletedPosts(Array.isArray(deleted) ? deleted : []);
      setClosedPosts(Array.isArray(closed) ? closed : []);
    } catch (e) {
      setError(e?.message || 'Không thể tải danh sách bài viết.');
    } finally {
      setLoading(false);
    }
  };

  const loadDetail = async (postId) => {
    if (!postId) return;
    setSelectedId(postId);
    setDetail(null);
    setDetailError('');
    setDetailLoading(true);
    try {
      const data = await adminGetCommunityPostDetail(postId);
      setDetail(data);
    } catch (e) {
      setDetailError(e?.message || 'Không thể tải chi tiết bài viết.');
    } finally {
      setDetailLoading(false);
    }
  };

  useEffect(() => {
    if (auth?.token) refreshLists();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [auth?.token]);

  if (!auth?.token) {
    return (
      <div className="min-h-[80vh] flex items-center justify-center bg-gray-100 py-12 px-4">
        <div className="max-w-lg w-full bg-white p-6 rounded-xl shadow-lg">
          <h1 className="text-xl font-extrabold text-gray-900">Bạn cần đăng nhập</h1>
          <p className="text-sm text-gray-700 mt-2">Vui lòng đăng nhập với quyền ADMIN.</p>
          <div className="mt-4">
            <button
              onClick={() => onNavigate('login')}
              className="bg-blue-900 text-white font-bold px-4 py-2 rounded hover:bg-blue-800"
            >
              Đăng nhập
            </button>
          </div>
        </div>
      </div>
    );
  }

  if (user?.role !== 'ADMIN' && !user?.roles?.includes('ROLE_ADMIN')) {
    return (
      <div className="min-h-[80vh] flex items-center justify-center bg-gray-100 py-12 px-4">
        <div className="max-w-lg w-full bg-white p-6 rounded-xl shadow-lg">
          <h1 className="text-xl font-extrabold text-gray-900">Không có quyền</h1>
          <p className="text-sm text-gray-700 mt-2">Chỉ ADMIN mới truy cập trang này.</p>
          <div className="mt-4">
            <button
              onClick={() => onNavigate('home')}
              className="bg-blue-900 text-white font-bold px-4 py-2 rounded hover:bg-blue-800"
            >
              Về trang chủ
            </button>
          </div>
        </div>
      </div>
    );
  }

  const selectedIsDeleted = Boolean(detail?.isDeleted);
  const selectedIsClosed = Boolean(detail?.isResolved);

  const handleRestore = async () => {
    if (!selectedId) return;
    const ok = window.confirm('Khôi phục bài viết này?');
    if (!ok) return;

    setDetailError('');
    try {
      const updated = await adminRestoreCommunityPost(selectedId);
      setDetail(updated);
      await refreshLists();
    } catch (e) {
      setDetailError(e?.message || 'Không thể khôi phục bài viết.');
    }
  };

  const handleOpenComments = async () => {
    if (!selectedId) return;
    const ok = window.confirm('Mở bình luận cho bài viết này?');
    if (!ok) return;

    setDetailError('');
    try {
      const updated = await adminOpenCommunityPostComments(selectedId);
      setDetail(updated);
      await refreshLists();
    } catch (e) {
      setDetailError(e?.message || 'Không thể mở bình luận.');
    }
  };

  return (
    <div className="min-h-[80vh] bg-gray-100 py-10">
      <div className="max-w-7xl mx-auto px-4">
        <div className="flex items-start justify-between gap-4 mb-6">
          <div>
            <h1 className="text-3xl font-extrabold text-gray-900 mb-2">Quản lý bài viết</h1>
            <p className="text-gray-600">Xem bài đã xóa, bài đã đóng bình luận; khôi phục và mở bình luận.</p>
          </div>
          <button
            onClick={refreshLists}
            className="bg-blue-900 text-white font-bold px-4 py-2 rounded hover:bg-blue-800"
            disabled={loading}
          >
            Làm mới
          </button>
        </div>

        {error ? (
          <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg text-sm text-red-700">{error}</div>
        ) : null}

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <section className="bg-white rounded-xl shadow-sm border border-gray-200">
            <div className="px-5 py-4 border-b border-gray-200 flex items-center justify-between">
              <h2 className="text-lg font-bold text-gray-900">Bài viết đã bị xóa</h2>
              <span className="text-sm text-gray-600">{deletedPosts.length}</span>
            </div>
            <div className="p-5">
              {loading ? <div className="text-sm text-gray-600">Đang tải...</div> : null}
              {!loading && deletedPosts.length === 0 ? (
                <div className="text-sm text-gray-600">Không có bài viết đã xóa.</div>
              ) : null}
              <div className="space-y-2">
                {deletedPosts.map((p) => (
                  <button
                    key={p?.id}
                    type="button"
                    onClick={() => loadDetail(p?.id)}
                    className={`w-full text-left px-3 py-2 rounded border hover:bg-gray-50 ${
                      String(selectedId) === String(p?.id) ? 'border-blue-900' : 'border-gray-200'
                    }`}
                  >
                    <div className="flex items-center justify-between gap-3">
                      <div className="font-semibold text-gray-900 truncate">{p?.title || '(Không có tiêu đề)'}</div>
                      <div className="text-xs text-gray-500">#{p?.id}</div>
                    </div>
                    <div className="text-sm text-gray-600 truncate">
                      {p?.author?.fullName || p?.author?.username || 'Ẩn danh'}
                      {p?.deletedAt ? ` · Xóa lúc ${formatDateTime(p.deletedAt)}` : ''}
                    </div>
                  </button>
                ))}
              </div>
            </div>
          </section>

          <section className="bg-white rounded-xl shadow-sm border border-gray-200">
            <div className="px-5 py-4 border-b border-gray-200 flex items-center justify-between">
              <h2 className="text-lg font-bold text-gray-900">Bài viết đã đóng bình luận</h2>
              <span className="text-sm text-gray-600">{closedPosts.length}</span>
            </div>
            <div className="p-5">
              {loading ? <div className="text-sm text-gray-600">Đang tải...</div> : null}
              {!loading && closedPosts.length === 0 ? (
                <div className="text-sm text-gray-600">Không có bài viết đã đóng bình luận.</div>
              ) : null}
              <div className="space-y-2">
                {closedPosts.map((p) => (
                  <button
                    key={p?.id}
                    type="button"
                    onClick={() => loadDetail(p?.id)}
                    className={`w-full text-left px-3 py-2 rounded border hover:bg-gray-50 ${
                      String(selectedId) === String(p?.id) ? 'border-blue-900' : 'border-gray-200'
                    }`}
                  >
                    <div className="flex items-center justify-between gap-3">
                      <div className="font-semibold text-gray-900 truncate">{p?.title || '(Không có tiêu đề)'}</div>
                      <div className="text-xs text-gray-500">#{p?.id}</div>
                    </div>
                    <div className="text-sm text-gray-600 truncate">
                      {p?.author?.fullName || p?.author?.username || 'Ẩn danh'}
                      {p?.createdAt ? ` · ${formatDateTime(p.createdAt)}` : ''}
                    </div>
                  </button>
                ))}
              </div>
            </div>
          </section>
        </div>

        <section className="mt-6 bg-white rounded-xl shadow-sm border border-gray-200">
          <div className="px-5 py-4 border-b border-gray-200">
            <h2 className="text-lg font-bold text-gray-900">Chi tiết bài viết</h2>
          </div>
          <div className="p-5">
            {detailLoading ? <div className="text-sm text-gray-600">Đang tải chi tiết...</div> : null}
            {detailError ? (
              <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded text-sm text-red-700">{detailError}</div>
            ) : null}

            {!detailLoading && !detail && !selectedId ? (
              <div className="text-sm text-gray-600">Chọn một bài viết để xem chi tiết.</div>
            ) : null}

            {!detailLoading && detail ? (
              <div>
                <div className="flex items-start justify-between gap-4">
                  <div>
                    <div className="text-xl font-extrabold text-gray-900">{detail?.title || '(Không có tiêu đề)'}</div>
                    <div className="text-sm text-gray-600 mt-1">
                      {detail?.author?.fullName || detail?.author?.username || 'Ẩn danh'}
                      {detail?.createdAt ? ` · ${formatDateTime(detail.createdAt)}` : ''}
                    </div>
                  </div>

                  <div className="flex items-center gap-2">
                    {selectedIsDeleted ? (
                      <span className="text-xs bg-red-100 text-red-700 px-2 py-1 rounded">Đã xóa</span>
                    ) : null}
                    {selectedIsClosed ? (
                      <span className="text-xs bg-green-100 text-green-700 px-2 py-1 rounded">Đã đóng bình luận</span>
                    ) : (
                      <span className="text-xs bg-yellow-100 text-yellow-800 px-2 py-1 rounded">Đang mở</span>
                    )}
                  </div>
                </div>

                {detail?.deletedAt ? (
                  <div className="text-sm text-gray-600 mt-2">Xóa lúc: {formatDateTime(detail.deletedAt)}</div>
                ) : null}

                <div className="mt-4 text-gray-900 whitespace-pre-wrap">{detail?.content}</div>

                <div className="mt-5 flex flex-wrap gap-2">
                  {selectedIsDeleted ? (
                    <button
                      type="button"
                      onClick={handleRestore}
                      className="bg-blue-900 text-white font-bold px-4 py-2 rounded hover:bg-blue-800"
                    >
                      Khôi phục bài viết
                    </button>
                  ) : null}

                  {selectedIsClosed ? (
                    <button
                      type="button"
                      onClick={handleOpenComments}
                      className="bg-blue-900 text-white font-bold px-4 py-2 rounded hover:bg-blue-800"
                    >
                      Mở bình luận
                    </button>
                  ) : null}
                </div>
              </div>
            ) : null}
          </div>
        </section>
      </div>
    </div>
  );
}
