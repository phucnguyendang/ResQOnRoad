import React, { useState, useEffect } from 'react';
import { AlertCircle, RefreshCw, Filter, ClipboardList } from 'lucide-react';
import RescueRequestCard from '../components/RescueRequestCard.jsx';
import RescueRequestDetail from '../components/RescueRequestDetail.jsx';
import { getCompanyAssignedRequests } from '../service/rescueRequestService.js';
import { loadAuth } from '../utils/authStorage.js';

const CompanyDashboard = () => {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [selectedRequest, setSelectedRequest] = useState(null);
  const [filter, setFilter] = useState('all');

  useEffect(() => {
    fetchRequests();
  }, []);

  const fetchRequests = async () => {
    setLoading(true);
    setError(null);

    try {
      const auth = loadAuth();
      if (!auth?.token) {
        setError('Bạn chưa đăng nhập. Vui lòng đăng nhập lại.');
        return;
      }

      const data = await getCompanyAssignedRequests(auth.token);
      console.log('API Response:', data); // DEBUG
      setRequests(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error('Fetch error:', err);
      setError(err.message || 'Lỗi khi tải danh sách yêu cầu');
    } finally {
      setLoading(false);
    }
  };

  const handleRequestUpdated = (updatedRequest) => {
    setRequests(
      requests.map((req) => (req.id === updatedRequest.id ? updatedRequest : req))
    );
    setSelectedRequest(updatedRequest);
  };

  const getFilteredRequests = () => {
    if (filter === 'all') {
      return requests;
    }
    return requests.filter((req) => req.status === filter);
  };

  const filteredRequests = getFilteredRequests();

  const statusCounts = {
    PENDING_CONFIRMATION: requests.filter((r) => r.status === 'PENDING_CONFIRMATION')
      .length,
    ACCEPTED: requests.filter((r) => r.status === 'ACCEPTED').length,
    IN_TRANSIT: requests.filter((r) => r.status === 'IN_TRANSIT').length,
    IN_PROGRESS: requests.filter((r) => r.status === 'IN_PROGRESS').length,
    COMPLETED: requests.filter((r) => r.status === 'COMPLETED').length,
  };

  if (selectedRequest) {
    return (
      <main className="min-h-screen bg-gray-50 py-4 px-4">
        <div className="container mx-auto max-w-6xl">
          <RescueRequestDetail
            request={selectedRequest}
            onBack={() => setSelectedRequest(null)}
            onRequestUpdated={handleRequestUpdated}
          />
        </div>
      </main>
    );
  }

  return (
    <main className="min-h-screen bg-gray-50 py-4 px-4">
      <div className="container mx-auto max-w-6xl">
        {/* Header */}
        <div className="mb-6">
          <h1 className="text-3xl font-bold text-gray-800 mb-2 flex items-center">
            <ClipboardList size={32} className="mr-3" />
            Bảng Điều Khiển Công Ty
          </h1>
          <p className="text-gray-600">Quản lý các yêu cầu cứu hộ được giao cho bạn</p>
        </div>

        {/* Error Message */}
        {error && (
          <div className="mb-4 p-4 bg-red-50 border border-red-200 rounded-lg flex items-start">
            <AlertCircle size={20} className="text-red-600 mr-3 mt-0.5 flex-shrink-0" />
            <p className="text-red-800">{error}</p>
          </div>
        )}

        {/* Stats Cards */}
        <div className="grid grid-cols-2 md:grid-cols-5 gap-4 mb-6">
          <div className="bg-white p-4 rounded-lg shadow">
            <p className="text-xs text-gray-600 uppercase">Chờ xác nhận</p>
            <p className="text-2xl font-bold text-yellow-600">
              {statusCounts.PENDING_CONFIRMATION}
            </p>
          </div>
          <div className="bg-white p-4 rounded-lg shadow">
            <p className="text-xs text-gray-600 uppercase">Đã tiếp nhận</p>
            <p className="text-2xl font-bold text-blue-600">{statusCounts.ACCEPTED}</p>
          </div>
          <div className="bg-white p-4 rounded-lg shadow">
            <p className="text-xs text-gray-600 uppercase">Đang tới</p>
            <p className="text-2xl font-bold text-purple-600">{statusCounts.IN_TRANSIT}</p>
          </div>
          <div className="bg-white p-4 rounded-lg shadow">
            <p className="text-xs text-gray-600 uppercase">Đang xử lý</p>
            <p className="text-2xl font-bold text-orange-600">{statusCounts.IN_PROGRESS}</p>
          </div>
          <div className="bg-white p-4 rounded-lg shadow">
            <p className="text-xs text-gray-600 uppercase">Hoàn thành</p>
            <p className="text-2xl font-bold text-green-600">{statusCounts.COMPLETED}</p>
          </div>
        </div>

        {/* Controls */}
        <div className="mb-6 bg-white p-4 rounded-lg shadow flex flex-wrap items-center gap-4">
          <button
            onClick={fetchRequests}
            disabled={loading}
            className="flex items-center gap-2 px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700 transition disabled:opacity-50"
          >
            <RefreshCw size={18} className={loading ? 'animate-spin' : ''} />
            Tải Lại
          </button>

          <div className="flex-1"></div>

          <div className="flex items-center gap-2">
            <Filter size={18} className="text-gray-600" />
            <select
              value={filter}
              onChange={(e) => setFilter(e.target.value)}
              className="px-4 py-2 border border-gray-300 rounded hover:border-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-900"
            >
              <option value="all">Tất Cả Yêu Cầu ({requests.length})</option>
              <option value="PENDING_CONFIRMATION">
                Chờ Xác Nhận ({statusCounts.PENDING_CONFIRMATION})
              </option>
              <option value="ACCEPTED">
                Đã Tiếp Nhận ({statusCounts.ACCEPTED})
              </option>
              <option value="IN_TRANSIT">
                Đang Tới ({statusCounts.IN_TRANSIT})
              </option>
              <option value="IN_PROGRESS">
                Đang Xử Lý ({statusCounts.IN_PROGRESS})
              </option>
              <option value="COMPLETED">
                Hoàn Thành ({statusCounts.COMPLETED})
              </option>
            </select>
          </div>
        </div>

        {/* Requests List */}
        <div className="bg-white rounded-lg shadow">
          {loading ? (
            <div className="flex items-center justify-center h-96">
              <div className="text-center">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-900 mx-auto mb-4"></div>
                <p className="text-gray-600">Đang tải danh sách yêu cầu...</p>
              </div>
            </div>
          ) : filteredRequests.length === 0 ? (
            <div className="flex items-center justify-center h-96">
              <div className="text-center">
                <ClipboardList size={48} className="mx-auto text-gray-400 mb-4" />
                <p className="text-gray-600">Không có yêu cầu nào để hiển thị</p>
                <p className="text-sm text-gray-500 mt-2">
                  {filter === 'all'
                    ? 'Bạn chưa có yêu cầu cứu hộ nào'
                    : 'Không có yêu cầu nào ở trạng thái này'}
                </p>
              </div>
            </div>
          ) : (
            <div className="p-4">
              <div className="space-y-4">
                {filteredRequests.map((request) => (
                  <RescueRequestCard
                    key={request.id}
                    request={request}
                    onSelect={setSelectedRequest}
                    isSelected={false}
                  />
                ))}
              </div>
            </div>
          )}
        </div>
      </div>
    </main>
  );
};

export default CompanyDashboard;
