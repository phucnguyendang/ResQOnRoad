import React, { useEffect, useMemo, useState } from 'react';
import { RefreshCw, AlertCircle, CheckCircle, ChevronLeft, ChevronRight, Search } from 'lucide-react';
import { getWorkflowLogs, getWorkflowLogsFiltered } from '../service/workflowService';
import { loadAuth } from '../utils/authStorage';

export default function AdminWorkflowView({ onNavigate }) {
  const auth = useMemo(() => loadAuth(), []);
  const user = auth?.user;

  const [workflows, setWorkflows] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  // Pagination state
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(20);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  // Filter state
  const [filterRequestId, setFilterRequestId] = useState('');
  const [filterUserId, setFilterUserId] = useState('');
  const [filterCompanyId, setFilterCompanyId] = useState('');
  const [filterStatus, setFilterStatus] = useState('');
  const [sortBy, setSortBy] = useState('timestamp');
  const [order, setOrder] = useState('DESC');

  const loadWorkflows = async (page = 0) => {
    setLoading(true);
    setError(null);
    try {
      let data;
      
      if (filterRequestId || filterUserId || filterCompanyId || filterStatus) {
        data = await getWorkflowLogsFiltered({
          page,
          size: pageSize,
          requestId: filterRequestId || null,
          userId: filterUserId || null,
          companyId: filterCompanyId || null,
          status: filterStatus || null,
          sortBy,
          order,
        });
      } else {
        data = await getWorkflowLogs({
          page,
          size: pageSize,
          sortBy,
          order,
        });
      }

      setWorkflows(Array.isArray(data?.content) ? data.content : []);
      setTotalPages(data?.totalPages || 0);
      setTotalElements(data?.totalElements || 0);
      setCurrentPage(page);
    } catch (err) {
      const details = Array.isArray(err?.details) && err.details.length > 0 ? `: ${err.details.join(', ')}` : '';
      setError(`${err?.message || 'Không thể tải danh sách quy trình'}${details}`);
      console.error('Error loading workflows:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (auth?.token) loadWorkflows(0);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [auth?.token]);

  const handleSearch = () => {
    loadWorkflows(0);
  };

  const handleClearFilters = () => {
    setFilterRequestId('');
    setFilterUserId('');
    setFilterCompanyId('');
    setFilterStatus('');
    loadWorkflows(0);
  };

  const handleRefresh = () => {
    loadWorkflows(currentPage);
    setSuccess('Đã cập nhật danh sách');
    setTimeout(() => setSuccess(null), 3000);
  };

  const handlePrevPage = () => {
    if (currentPage > 0) {
      loadWorkflows(currentPage - 1);
    }
  };

  const handleNextPage = () => {
    if (currentPage < totalPages - 1) {
      loadWorkflows(currentPage + 1);
    }
  };

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

  if (user?.role !== 'ADMIN') {
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

  return (
    <div className="min-h-[80vh] bg-gray-100 py-10">
      <div className="max-w-7xl mx-auto px-4">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-extrabold text-gray-900 mb-2">Quản Lý Quy Trình Hệ Thống</h1>
          <p className="text-gray-600">Xem log các quy trình hệ thống, theo dõi hoạt động của yêu cầu cứu hộ</p>
        </div>

        {/* Alert Messages */}
        {error && (
          <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg flex items-start gap-3">
            <AlertCircle className="w-5 h-5 text-red-600 flex-shrink-0 mt-0.5" />
            <div>
              <h3 className="font-semibold text-red-800">Lỗi</h3>
              <p className="text-red-700 text-sm mt-1">{error}</p>
            </div>
          </div>
        )}

        {success && (
          <div className="mb-6 p-4 bg-green-50 border border-green-200 rounded-lg flex items-start gap-3">
            <CheckCircle className="w-5 h-5 text-green-600 flex-shrink-0 mt-0.5" />
            <div>
              <h3 className="font-semibold text-green-800">Thành công</h3>
              <p className="text-green-700 text-sm mt-1">{success}</p>
            </div>
          </div>
        )}

        {/* Filter Section */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <h2 className="text-lg font-semibold text-gray-900 mb-4">Bộ Lọc</h2>
          
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">ID Yêu Cầu</label>
              <input
                type="text"
                placeholder="Nhập ID yêu cầu..."
                value={filterRequestId}
                onChange={(e) => setFilterRequestId(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">ID User</label>
              <input
                type="text"
                placeholder="Nhập ID user..."
                value={filterUserId}
                onChange={(e) => setFilterUserId(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">ID Công Ty</label>
              <input
                type="text"
                placeholder="Nhập ID công ty..."
                value={filterCompanyId}
                onChange={(e) => setFilterCompanyId(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Trạng Thái</label>
              <select
                value={filterStatus}
                onChange={(e) => setFilterStatus(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="">-- Tất cả --</option>
                <option value="CREATED">Tạo mới</option>
                <option value="ACCEPTED">Đã chấp nhận</option>
                <option value="REJECTED">Bị từ chối</option>
                <option value="CANCELLED">Đã hủy</option>
                <option value="IN_PROGRESS">Đang tiến hành</option>
                <option value="COMPLETED">Hoàn thành</option>
              </select>
            </div>
          </div>

          <div className="flex gap-2">
            <button
              onClick={handleSearch}
              disabled={loading}
              className="flex items-center gap-2 px-4 py-2 bg-blue-900 text-white font-semibold rounded-lg hover:bg-blue-800 disabled:bg-gray-400"
            >
              <Search size={18} />
              Tìm kiếm
            </button>
            <button
              onClick={handleClearFilters}
              className="px-4 py-2 bg-gray-400 text-white font-semibold rounded-lg hover:bg-gray-500"
            >
              Xóa bộ lọc
            </button>
            <button
              onClick={handleRefresh}
              disabled={loading}
              className="flex items-center gap-2 px-4 py-2 bg-green-600 text-white font-semibold rounded-lg hover:bg-green-700 disabled:bg-gray-400"
            >
              <RefreshCw size={18} className={loading ? 'animate-spin' : ''} />
              Làm mới
            </button>
          </div>
        </div>

        {/* Sorting and PageSize Controls */}
        <div className="bg-white rounded-lg shadow-md p-4 mb-6 flex flex-col md:flex-row gap-4 justify-between items-start md:items-center">
          <div className="flex gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Sắp xếp theo</label>
              <select
                value={sortBy}
                onChange={(e) => setSortBy(e.target.value)}
                className="px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="timestamp">Thời gian</option>
                <option value="requestId">ID Yêu cầu</option>
                <option value="status">Trạng thái</option>
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Thứ tự</label>
              <select
                value={order}
                onChange={(e) => setOrder(e.target.value)}
                className="px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="DESC">Mới nhất trước</option>
                <option value="ASC">Cũ nhất trước</option>
              </select>
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Dòng mỗi trang</label>
            <select
              value={pageSize}
              onChange={(e) => {
                setPageSize(parseInt(e.target.value));
                loadWorkflows(0);
              }}
              className="px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="10">10</option>
              <option value="20">20</option>
              <option value="50">50</option>
              <option value="100">100</option>
            </select>
          </div>
        </div>

        {/* Table */}
        <div className="bg-white rounded-lg shadow-md overflow-x-auto">
          {loading ? (
            <div className="flex justify-center items-center py-16">
              <div className="text-center">
                <RefreshCw className="w-8 h-8 text-blue-900 animate-spin mx-auto mb-4" />
                <p className="text-gray-600">Đang tải dữ liệu...</p>
              </div>
            </div>
          ) : workflows.length === 0 ? (
            <div className="text-center py-16 text-gray-500">
              <p className="text-lg font-semibold">Không có dữ liệu quy trình</p>
              <p className="text-sm">Hãy thử thay đổi bộ lọc hoặc tạo yêu cầu cứu hộ mới</p>
            </div>
          ) : (
            <table className="w-full">
              <thead className="bg-gray-50 border-b border-gray-200">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-gray-900 uppercase tracking-wider">
                    ID Yêu Cầu
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-gray-900 uppercase tracking-wider">
                    Trạng Thái
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-gray-900 uppercase tracking-wider">
                    ID User
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-gray-900 uppercase tracking-wider">
                    ID Công Ty
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-gray-900 uppercase tracking-wider">
                    Mô Tả
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-gray-900 uppercase tracking-wider">
                    Thời Gian
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {workflows.map((workflow) => (
                  <tr key={workflow.id || workflow.requestId} className="hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-blue-600">
                      {workflow.requestId || 'N/A'}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm">
                      <span
                        className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-medium ${
                          workflow.status === 'COMPLETED'
                            ? 'bg-green-100 text-green-800'
                            : workflow.status === 'CANCELLED'
                            ? 'bg-red-100 text-red-800'
                            : workflow.status === 'REJECTED'
                            ? 'bg-red-100 text-red-800'
                            : workflow.status === 'IN_PROGRESS'
                            ? 'bg-blue-100 text-blue-800'
                            : workflow.status === 'ACCEPTED'
                            ? 'bg-yellow-100 text-yellow-800'
                            : 'bg-gray-100 text-gray-800'
                        }`}
                      >
                        {workflow.status || 'UNKNOWN'}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                      {workflow.userId || 'N/A'}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                      {workflow.companyId || 'N/A'}
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-600 max-w-xs truncate">
                      {workflow.description || workflow.action || 'N/A'}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                      {workflow.timestamp
                        ? new Date(workflow.timestamp).toLocaleString('vi-VN')
                        : 'N/A'}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>

        {/* Pagination */}
        {!loading && workflows.length > 0 && (
          <div className="mt-6 flex items-center justify-between bg-white rounded-lg shadow-md p-4">
            <div className="text-sm text-gray-600">
              Hiển thị {workflows.length > 0 ? currentPage * pageSize + 1 : 0} đến{' '}
              {Math.min((currentPage + 1) * pageSize, totalElements)} trong {totalElements} kết quả
            </div>

            <div className="flex gap-2">
              <button
                onClick={handlePrevPage}
                disabled={currentPage === 0}
                className="flex items-center gap-1 px-4 py-2 bg-gray-300 text-gray-700 font-semibold rounded-lg hover:bg-gray-400 disabled:bg-gray-200 disabled:cursor-not-allowed"
              >
                <ChevronLeft size={18} />
                Trước
              </button>

              <div className="flex items-center gap-2">
                {Array.from({ length: Math.min(5, totalPages) }, (_, i) => {
                  let pageNum;
                  if (totalPages <= 5) {
                    pageNum = i;
                  } else if (currentPage < 3) {
                    pageNum = i;
                  } else if (currentPage >= totalPages - 3) {
                    pageNum = totalPages - 5 + i;
                  } else {
                    pageNum = currentPage - 2 + i;
                  }

                  return (
                    <button
                      key={pageNum}
                      onClick={() => loadWorkflows(pageNum)}
                      className={`px-3 py-2 rounded-lg font-semibold text-sm ${
                        currentPage === pageNum
                          ? 'bg-blue-900 text-white'
                          : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                      }`}
                    >
                      {pageNum + 1}
                    </button>
                  );
                })}
              </div>

              <button
                onClick={handleNextPage}
                disabled={currentPage >= totalPages - 1}
                className="flex items-center gap-1 px-4 py-2 bg-gray-300 text-gray-700 font-semibold rounded-lg hover:bg-gray-400 disabled:bg-gray-200 disabled:cursor-not-allowed"
              >
                Sau
                <ChevronRight size={18} />
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
