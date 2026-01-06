import React, { useEffect, useState } from 'react';
import moderationService from '../service/moderationService';

function ModerationView({ onNavigate }) {
  // Danh sách nội dung cần kiểm duyệt
  const [contents, setContents] = useState([]);
  const [selectedContent, setSelectedContent] = useState(null);
  const [isDetailOpen, setIsDetailOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState(null);

  // Filters
  const [statusFilter, setStatusFilter] = useState('PENDING');
  const [typeFilter, setTypeFilter] = useState('');
  const [searchKeyword, setSearchKeyword] = useState('');
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [totalPages, setTotalPages] = useState(0);

  // Statistics
  const [stats, setStats] = useState(null);

  // Modal state
  const [actionModal, setActionModal] = useState({
    isOpen: false,
    action: null, // 'approve', 'reject', 'remove', 'delete'
    contentId: null,
    reason: '',
  });

  // Load moderation list
  useEffect(() => {
    fetchModerationList();
    fetchStatistics();
  }, [statusFilter, typeFilter, currentPage, pageSize]);

  const fetchModerationList = async () => {
    setLoading(true);
    setError(null);
    try {
      const params = {
        page: currentPage,
        size: pageSize,
      };

      if (statusFilter) {
        params.status = statusFilter;
      }
      if (typeFilter) {
        params.type = typeFilter;
      }

      const data = await moderationService.getModerationList(params);

      console.log('Moderation response:', data);

      // Handle different response formats
      let items = [];
      let totalPages = 0;

      if (data?.items) {
        items = data.items;
        if (data.pagination?.total_pages) {
          totalPages = data.pagination.total_pages;
        }
      } else if (Array.isArray(data)) {
        items = data;
      } else if (data?.data?.items) {
        items = data.data.items;
        if (data.data?.pagination?.total_pages) {
          totalPages = data.data.pagination.total_pages;
        }
      } else if (data?.data && Array.isArray(data.data)) {
        items = data.data;
      }

      setContents(items || []);
      if (totalPages > 0) {
        setTotalPages(totalPages);
      }
    } catch (err) {
      setError(err.message || 'Lỗi khi tải danh sách kiểm duyệt');
      console.error('Error fetching moderation list:', err);
      setContents([]);
    } finally {
      setLoading(false);
    }
  };

  const fetchStatistics = async () => {
    try {
      const data = await moderationService.getModerationStatistics();
      // Handle different response formats
      const stats = data?.data || data || {};
      setStats(stats);
    } catch (err) {
      console.error('Error fetching statistics:', err);
    }
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!searchKeyword.trim()) {
      fetchModerationList();
      return;
    }

    setLoading(true);
    setError(null);
    try {
      const data = await moderationService.searchContent(searchKeyword);
      
      console.log('Search response:', data);

      // Handle different response formats
      let items = [];

      if (data?.items) {
        items = data.items;
      } else if (Array.isArray(data)) {
        items = data;
      } else if (data?.data?.items) {
        items = data.data.items;
      } else if (data?.data && Array.isArray(data.data)) {
        items = data.data;
      }

      setContents(items || []);
    } catch (err) {
      setError(err.message || 'Lỗi khi tìm kiếm');
      console.error('Error searching:', err);
      setContents([]);
    } finally {
      setLoading(false);
    }
  };

  const handleViewDetail = async (content) => {
    setLoading(true);
    try {
      const data = await moderationService.getContentDetail(content.id);
      // Handle different response formats
      const contentDetail = data?.data || data || content;
      setSelectedContent(contentDetail);
      setIsDetailOpen(true);
    } catch (err) {
      setError(err.message || 'Lỗi khi tải chi tiết nội dung');
      console.error('Error fetching detail:', err);
    } finally {
      setLoading(false);
    }
  };

  const openActionModal = (action, contentId) => {
    setActionModal({
      isOpen: true,
      action,
      contentId,
      reason: '',
    });
  };

  const closeActionModal = () => {
    setActionModal({
      isOpen: false,
      action: null,
      contentId: null,
      reason: '',
    });
  };

  const handleAction = async () => {
    const { action, contentId, reason } = actionModal;
    setLoading(true);
    setError(null);

    try {
      let response;
      switch (action) {
        case 'approve':
          response = await moderationService.approveContent(contentId);
          break;
        case 'reject':
          response = await moderationService.rejectContent(contentId, { reason });
          break;
        case 'remove':
          response = await moderationService.removeContent(contentId, { reason });
          break;
        case 'delete':
          response = await moderationService.deleteContent(contentId);
          break;
        default:
          throw new Error('Invalid action');
      }

      setSuccessMessage(
        `Đã ${action === 'approve' ? 'phê duyệt' : action === 'reject' ? 'từ chối' : action === 'remove' ? 'gỡ' : 'xóa'} nội dung thành công`,
      );
      closeActionModal();
      setIsDetailOpen(false);
      setSelectedContent(null);

      // Reload list
      setTimeout(() => {
        fetchModerationList();
        fetchStatistics();
        setSuccessMessage(null);
      }, 1500);
    } catch (err) {
      setError(err.message || `Lỗi khi thực hiện hành động ${action}`);
      console.error(`Error ${action}:`, err);
    } finally {
      setLoading(false);
    }
  };

  const getStatusBadgeClass = (status) => {
    switch (status) {
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-800';
      case 'APPROVED':
        return 'bg-green-100 text-green-800';
      case 'REJECTED':
        return 'bg-red-100 text-red-800';
      case 'REMOVED':
        return 'bg-orange-100 text-orange-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getTypeBadgeClass = (type) => {
    switch (type) {
      case 'POST':
        return 'bg-blue-100 text-blue-800';
      case 'COMMENT':
        return 'bg-purple-100 text-purple-800';
      case 'REVIEW':
        return 'bg-indigo-100 text-indigo-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">Kiểm duyệt nội dung</h1>
          <p className="text-gray-600">Quản lý và kiểm duyệt bài viết, bình luận và đánh giá</p>
        </div>

        {/* Statistics */}
        {stats && (
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8">
            <div className="bg-white rounded-lg shadow p-6">
              <div className="text-gray-600 text-sm font-medium">Chờ duyệt</div>
              <div className="text-2xl font-bold text-yellow-600 mt-2">{stats.pending || 0}</div>
            </div>
            <div className="bg-white rounded-lg shadow p-6">
              <div className="text-gray-600 text-sm font-medium">Đã phê duyệt</div>
              <div className="text-2xl font-bold text-green-600 mt-2">{stats.approved || 0}</div>
            </div>
            <div className="bg-white rounded-lg shadow p-6">
              <div className="text-gray-600 text-sm font-medium">Bị từ chối</div>
              <div className="text-2xl font-bold text-red-600 mt-2">{stats.rejected || 0}</div>
            </div>
            <div className="bg-white rounded-lg shadow p-6">
              <div className="text-gray-600 text-sm font-medium">Bị gỡ</div>
              <div className="text-2xl font-bold text-orange-600 mt-2">{stats.removed || 0}</div>
            </div>
          </div>
        )}

        {/* Messages */}
        {error && (
          <div className="mb-4 p-4 bg-red-50 border border-red-200 rounded-lg text-red-700">
            {error}
          </div>
        )}
        {successMessage && (
          <div className="mb-4 p-4 bg-green-50 border border-green-200 rounded-lg text-green-700">
            {successMessage}
          </div>
        )}

        {/* Search and Filter */}
        <div className="bg-white rounded-lg shadow p-6 mb-8">
          <form onSubmit={handleSearch} className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Trạng thái
                </label>
                <select
                  value={statusFilter}
                  onChange={(e) => {
                    setStatusFilter(e.target.value);
                    setCurrentPage(0);
                  }}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option value="">Tất cả</option>
                  <option value="PENDING">Chờ duyệt</option>
                  <option value="APPROVED">Đã phê duyệt</option>
                  <option value="REJECTED">Bị từ chối</option>
                  <option value="REMOVED">Bị gỡ</option>
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Loại nội dung
                </label>
                <select
                  value={typeFilter}
                  onChange={(e) => {
                    setTypeFilter(e.target.value);
                    setCurrentPage(0);
                  }}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option value="">Tất cả</option>
                  <option value="POST">Bài viết</option>
                  <option value="COMMENT">Bình luận</option>
                  <option value="REVIEW">Đánh giá</option>
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Tìm kiếm
                </label>
                <input
                  type="text"
                  value={searchKeyword}
                  onChange={(e) => setSearchKeyword(e.target.value)}
                  placeholder="Nhập từ khóa..."
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>

              <div className="flex items-end">
                <button
                  type="submit"
                  className="w-full bg-blue-600 text-white font-medium py-2 rounded-lg hover:bg-blue-700 transition"
                >
                  Tìm kiếm
                </button>
              </div>
            </div>
          </form>
        </div>

        {/* Content List */}
        <div className="bg-white rounded-lg shadow overflow-hidden">
          {loading && !contents.length ? (
            <div className="p-8 text-center text-gray-600">Đang tải...</div>
          ) : contents.length === 0 ? (
            <div className="p-8 text-center text-gray-600">Không có nội dung để hiển thị</div>
          ) : (
            <>
              <div className="overflow-x-auto">
                <table className="w-full">
                  <thead className="bg-gray-100 border-b border-gray-200">
                    <tr>
                      <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">
                        ID
                      </th>
                      <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">
                        Loại
                      </th>
                      <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">
                        Tiêu đề / Nội dung
                      </th>
                      <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">
                        Người đăng
                      </th>
                      <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">
                        Trạng thái
                      </th>
                      <th className="px-6 py-3 text-center text-sm font-semibold text-gray-900">
                        Hành động
                      </th>
                    </tr>
                  </thead>
                  <tbody>
                    {contents.map((content) => (
                      <tr key={content.id} className="border-b border-gray-200 hover:bg-gray-50">
                        <td className="px-6 py-4 text-sm text-gray-600">{content.id}</td>
                        <td className="px-6 py-4 text-sm">
                          <span className={`inline-block px-3 py-1 rounded-full text-xs font-medium ${getTypeBadgeClass(content.type)}`}>
                            {content.type === 'POST' && 'Bài viết'}
                            {content.type === 'COMMENT' && 'Bình luận'}
                            {content.type === 'REVIEW' && 'Đánh giá'}
                            {!['POST', 'COMMENT', 'REVIEW'].includes(content.type) && content.type}
                          </span>
                        </td>
                        <td className="px-6 py-4">
                          <div className="text-sm font-medium text-gray-900 line-clamp-2">
                            {content.title || content.content || 'N/A'}
                          </div>
                        </td>
                        <td className="px-6 py-4 text-sm text-gray-600">
                          {content.userName || content.authorName || 'N/A'}
                        </td>
                        <td className="px-6 py-4 text-sm">
                          <span className={`inline-block px-3 py-1 rounded-full text-xs font-medium ${getStatusBadgeClass(content.status)}`}>
                            {content.status === 'PENDING' && 'Chờ duyệt'}
                            {content.status === 'APPROVED' && 'Đã phê duyệt'}
                            {content.status === 'REJECTED' && 'Bị từ chối'}
                            {content.status === 'REMOVED' && 'Bị gỡ'}
                            {!['PENDING', 'APPROVED', 'REJECTED', 'REMOVED'].includes(content.status) && content.status}
                          </span>
                        </td>
                        <td className="px-6 py-4 text-center">
                          <button
                            onClick={() => handleViewDetail(content)}
                            className="inline-block bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 transition text-sm font-medium"
                          >
                            Chi tiết
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              {/* Pagination */}
              {totalPages > 1 && (
                <div className="px-6 py-4 border-t border-gray-200 flex justify-between items-center">
                  <button
                    onClick={() => setCurrentPage(Math.max(0, currentPage - 1))}
                    disabled={currentPage === 0}
                    className="px-4 py-2 border border-gray-300 rounded-lg text-gray-700 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
                  >
                    Trước
                  </button>
                  <span className="text-sm text-gray-600">
                    Trang {currentPage + 1} / {totalPages}
                  </span>
                  <button
                    onClick={() => setCurrentPage(Math.min(totalPages - 1, currentPage + 1))}
                    disabled={currentPage >= totalPages - 1}
                    className="px-4 py-2 border border-gray-300 rounded-lg text-gray-700 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
                  >
                    Sau
                  </button>
                </div>
              )}
            </>
          )}
        </div>
      </div>

      {/* Detail Modal */}
      {isDetailOpen && selectedContent && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg shadow-xl max-w-2xl w-full max-h-96 overflow-y-auto">
            <div className="p-6">
              <div className="flex justify-between items-start mb-4">
                <h2 className="text-xl font-bold text-gray-900">Chi tiết nội dung</h2>
                <button
                  onClick={() => {
                    setIsDetailOpen(false);
                    setSelectedContent(null);
                  }}
                  className="text-gray-400 hover:text-gray-600 text-2xl"
                >
                  ×
                </button>
              </div>

              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700">ID</label>
                  <p className="text-sm text-gray-600">{selectedContent.id}</p>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700">Loại</label>
                  <p className="text-sm">
                    <span className={`inline-block px-3 py-1 rounded-full text-xs font-medium ${getTypeBadgeClass(selectedContent.type)}`}>
                      {selectedContent.type === 'POST' && 'Bài viết'}
                      {selectedContent.type === 'COMMENT' && 'Bình luận'}
                      {selectedContent.type === 'REVIEW' && 'Đánh giá'}
                      {!['POST', 'COMMENT', 'REVIEW'].includes(selectedContent.type) && selectedContent.type}
                    </span>
                  </p>
                </div>

                {selectedContent.title && (
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Tiêu đề</label>
                    <p className="text-sm text-gray-600">{selectedContent.title}</p>
                  </div>
                )}

                {selectedContent.content && (
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Nội dung</label>
                    <p className="text-sm text-gray-600 whitespace-pre-wrap break-words">
                      {selectedContent.content}
                    </p>
                  </div>
                )}

                <div>
                  <label className="block text-sm font-medium text-gray-700">Người đăng</label>
                  <p className="text-sm text-gray-600">{selectedContent.userName || selectedContent.authorName || 'N/A'}</p>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700">Trạng thái</label>
                  <p className="text-sm">
                    <span className={`inline-block px-3 py-1 rounded-full text-xs font-medium ${getStatusBadgeClass(selectedContent.status)}`}>
                      {selectedContent.status === 'PENDING' && 'Chờ duyệt'}
                      {selectedContent.status === 'APPROVED' && 'Đã phê duyệt'}
                      {selectedContent.status === 'REJECTED' && 'Bị từ chối'}
                      {selectedContent.status === 'REMOVED' && 'Bị gỡ'}
                      {!['PENDING', 'APPROVED', 'REJECTED', 'REMOVED'].includes(selectedContent.status) && selectedContent.status}
                    </span>
                  </p>
                </div>

                {selectedContent.createdAt && (
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Ngày đăng</label>
                    <p className="text-sm text-gray-600">
                      {new Date(selectedContent.createdAt).toLocaleString('vi-VN')}
                    </p>
                  </div>
                )}

                {selectedContent.reason && (
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Lý do từ chối</label>
                    <p className="text-sm text-gray-600">{selectedContent.reason}</p>
                  </div>
                )}
              </div>

              {/* Action Buttons */}
              {selectedContent.status === 'PENDING' && (
                <div className="mt-6 flex gap-3">
                  <button
                    onClick={() => openActionModal('approve', selectedContent.id)}
                    className="flex-1 bg-green-600 text-white font-medium py-2 rounded-lg hover:bg-green-700 transition"
                  >
                    Phê duyệt
                  </button>
                  <button
                    onClick={() => openActionModal('reject', selectedContent.id)}
                    className="flex-1 bg-red-600 text-white font-medium py-2 rounded-lg hover:bg-red-700 transition"
                  >
                    Từ chối
                  </button>
                  <button
                    onClick={() => openActionModal('delete', selectedContent.id)}
                    className="flex-1 bg-red-900 text-white font-medium py-2 rounded-lg hover:bg-red-950 transition"
                  >
                    Xóa
                  </button>
                </div>
              )}

              {selectedContent.status === 'APPROVED' && (
                <div className="mt-6 flex gap-3">
                  <button
                    onClick={() => openActionModal('remove', selectedContent.id)}
                    className="flex-1 bg-orange-600 text-white font-medium py-2 rounded-lg hover:bg-orange-700 transition"
                  >
                    Gỡ nội dung
                  </button>
                  <button
                    onClick={() => openActionModal('delete', selectedContent.id)}
                    className="flex-1 bg-red-900 text-white font-medium py-2 rounded-lg hover:bg-red-950 transition"
                  >
                    Xóa vĩnh viễn
                  </button>
                </div>
              )}

              <button
                onClick={() => {
                  setIsDetailOpen(false);
                  setSelectedContent(null);
                }}
                className="w-full mt-3 bg-gray-200 text-gray-800 font-medium py-2 rounded-lg hover:bg-gray-300 transition"
              >
                Đóng
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Action Modal */}
      {actionModal.isOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg shadow-xl max-w-md w-full">
            <div className="p-6">
              <h2 className="text-lg font-bold text-gray-900 mb-4">
                {actionModal.action === 'approve' && 'Phê duyệt nội dung?'}
                {actionModal.action === 'reject' && 'Từ chối nội dung?'}
                {actionModal.action === 'remove' && 'Gỡ nội dung?'}
                {actionModal.action === 'delete' && 'Xóa nội dung vĩnh viễn?'}
              </h2>

              {(actionModal.action === 'reject' || actionModal.action === 'remove') && (
                <div className="mb-4">
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Lý do
                  </label>
                  <textarea
                    value={actionModal.reason}
                    onChange={(e) =>
                      setActionModal({ ...actionModal, reason: e.target.value })
                    }
                    placeholder="Nhập lý do (không bắt buộc)"
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    rows="4"
                  />
                </div>
              )}

              {actionModal.action === 'delete' && (
                <p className="text-sm text-red-600 mb-4">
                  ⚠️ Hành động này không thể hoàn tác. Nội dung sẽ bị xóa vĩnh viễn.
                </p>
              )}

              <div className="flex gap-3">
                <button
                  onClick={closeActionModal}
                  className="flex-1 bg-gray-200 text-gray-800 font-medium py-2 rounded-lg hover:bg-gray-300 transition"
                >
                  Hủy
                </button>
                <button
                  onClick={handleAction}
                  disabled={loading}
                  className="flex-1 bg-blue-600 text-white font-medium py-2 rounded-lg hover:bg-blue-700 transition disabled:opacity-50"
                >
                  {loading ? 'Đang xử lý...' : 'Xác nhận'}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default ModerationView;
