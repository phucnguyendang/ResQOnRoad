import React, { useEffect, useMemo, useState } from 'react';
import { RefreshCw, AlertCircle } from 'lucide-react';
import { getAllRescueRequestsAdmin } from '../service/rescueRequestService';
import { loadAuth } from '../utils/authStorage';

const STATUS_LABELS = {
  PENDING_CONFIRMATION: 'Đang chờ xác nhận',
  ACCEPTED: 'Đã tiếp nhận',
  IN_TRANSIT: 'Đang di chuyển',
  IN_PROGRESS: 'Đang xử lý',
  COMPLETED: 'Hoàn thành',
  REJECTED_BY_COMPANY: 'Bị từ chối',
  CANCELLED_BY_USER: 'Đã hủy',
};

function normalizeStatus(raw) {
  return String(raw || '').toUpperCase();
}

export default function AdminWorkflowView({ onNavigate }) {
  const auth = useMemo(() => loadAuth(), []);
  const user = auth?.user;

  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [query, setQuery] = useState('');

  const loadRequests = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getAllRescueRequestsAdmin();
      const list = Array.isArray(data) ? data : [];
      list.sort((a, b) => {
        const ta = new Date(a.updatedAt || a.createdAt || 0).getTime();
        const tb = new Date(b.updatedAt || b.createdAt || 0).getTime();
        return tb - ta;
      });
      setRequests(list);
    } catch (err) {
      const details = Array.isArray(err?.details) && err.details.length > 0 ? `: ${err.details.join(', ')}` : '';
      setError(`${err?.message || 'Không thể tải danh sách yêu cầu cứu hộ'}${details}`);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (auth?.token) loadRequests();
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

  const totalSent = requests.length;
  const totalSuccess = requests.filter((r) => normalizeStatus(r.status) === 'COMPLETED').length;
  const totalCancelled = requests.filter((r) => normalizeStatus(r.status) === 'CANCELLED_BY_USER').length;

  const filtered = requests.filter((r) => {
    const q = String(query || '').trim().toLowerCase();
    if (!q) return true;
    const id = String(r.id ?? '').toLowerCase();
    const company = String(r.companyName || '').toLowerCase();
    const location = String(r.location || '').toLowerCase();
    return id.includes(q) || company.includes(q) || location.includes(q);
  });

  return (
    <div className="min-h-[80vh] bg-gray-100 py-10">
      <div className="max-w-7xl mx-auto px-4">
        <div className="mb-6 flex items-start justify-between gap-3">
          <div>
            <h1 className="text-3xl font-extrabold text-gray-900">Quản lý yêu cầu cứu hộ</h1>
            <p className="text-gray-600 mt-1">Thống kê và danh sách yêu cầu cứu hộ trong hệ thống</p>
          </div>
          <button
            type="button"
            onClick={loadRequests}
            disabled={loading}
            className="flex items-center gap-2 px-4 py-2 bg-blue-900 text-white font-semibold rounded-lg hover:bg-blue-800 disabled:bg-gray-400"
          >
            <RefreshCw size={18} className={loading ? 'animate-spin' : ''} />
            Làm mới
          </button>
        </div>

        {error && (
          <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg flex items-start gap-3">
            <AlertCircle className="w-5 h-5 text-red-600 flex-shrink-0 mt-0.5" />
            <div>
              <h3 className="font-semibold text-red-800">Lỗi</h3>
              <p className="text-red-700 text-sm mt-1">{error}</p>
            </div>
          </div>
        )}

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
          <div className="bg-white rounded-xl shadow p-5 border border-gray-100">
            <div className="text-sm text-gray-600">Số lượng yêu cầu đã gửi</div>
            <div className="text-3xl font-extrabold text-gray-900 mt-1">{totalSent}</div>
          </div>
          <div className="bg-white rounded-xl shadow p-5 border border-gray-100">
            <div className="text-sm text-gray-600">Số lượng yêu cầu thành công</div>
            <div className="text-3xl font-extrabold text-green-700 mt-1">{totalSuccess}</div>
          </div>
          <div className="bg-white rounded-xl shadow p-5 border border-gray-100">
            <div className="text-sm text-gray-600">Số lượng yêu cầu hủy</div>
            <div className="text-3xl font-extrabold text-red-700 mt-1">{totalCancelled}</div>
          </div>
        </div>

        <div className="bg-white rounded-xl shadow border border-gray-100 overflow-hidden">
          <div className="p-4 border-b border-gray-200 flex flex-col md:flex-row gap-3 md:items-center md:justify-between">
            <div className="font-bold text-gray-900">Danh sách yêu cầu cứu hộ</div>
            <input
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              placeholder="Tìm theo ID / công ty / địa chỉ..."
              className="w-full md:w-96 px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          {loading ? (
            <div className="flex justify-center items-center py-16">
              <div className="text-center">
                <RefreshCw className="w-8 h-8 text-blue-900 animate-spin mx-auto mb-4" />
                <p className="text-gray-600">Đang tải dữ liệu...</p>
              </div>
            </div>
          ) : filtered.length === 0 ? (
            <div className="text-center py-16 text-gray-500">
              <p className="text-lg font-semibold">Không có yêu cầu nào</p>
              <p className="text-sm">Hãy thử thay đổi từ khóa tìm kiếm</p>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-gray-50 border-b border-gray-200">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-900 uppercase tracking-wider">Yêu cầu</th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-900 uppercase tracking-wider">Công ty cứu hộ</th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-900 uppercase tracking-wider">Địa chỉ cứu hộ</th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-900 uppercase tracking-wider">Trạng thái</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-200">
                  {filtered.map((r) => {
                    const status = normalizeStatus(r.status);
                    const label = STATUS_LABELS[status] || status || 'N/A';
                    return (
                      <tr key={r.id} className="hover:bg-gray-50 transition-colors">
                        <td className="px-6 py-4 whitespace-nowrap text-sm font-extrabold text-blue-900">#{r.id}</td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-800">{r.companyName || '(chưa gán)'}</td>
                        <td className="px-6 py-4 text-sm text-gray-700 max-w-xl truncate" title={r.location || ''}>
                          {r.location || '(không có)'}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm">
                          <span
                            className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold ${
                              status === 'COMPLETED'
                                ? 'bg-green-100 text-green-800'
                                : status === 'CANCELLED_BY_USER'
                                ? 'bg-red-100 text-red-800'
                                : status === 'REJECTED_BY_COMPANY'
                                ? 'bg-red-100 text-red-800'
                                : status === 'IN_PROGRESS'
                                ? 'bg-blue-100 text-blue-800'
                                : status === 'ACCEPTED'
                                ? 'bg-yellow-100 text-yellow-800'
                                : 'bg-gray-100 text-gray-800'
                            }`}
                          >
                            {label}
                          </span>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
