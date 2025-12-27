import React, { useEffect, useState } from 'react';
import { getMyRescueRequests, setLastRescueRequestId } from '../service/rescueRequestService';

function formatDateTime(value) {
  if (!value) return '';
  try {
    return new Date(value).toLocaleString();
  } catch {
    return String(value);
  }
}

const STATUS_LABELS = {
  PENDING_CONFIRMATION: 'Đang chờ xác nhận',
  ACCEPTED: 'Đã tiếp nhận',
  IN_TRANSIT: 'Đang di chuyển',
  IN_PROGRESS: 'Đang xử lý',
  COMPLETED: 'Hoàn thành',
  REJECTED_BY_COMPANY: 'Bị từ chối',
  CANCELLED_BY_USER: 'Đã hủy',
};

const TERMINAL_STATUSES = new Set(['COMPLETED', 'REJECTED_BY_COMPANY', 'CANCELLED_BY_USER']);

const RescueRequestListView = ({ onNavigate }) => {
  const [activeList, setActiveList] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchList = async () => {
      setLoading(true);
      setError(null);
      try {
        const list = await getMyRescueRequests();

        const normalized = Array.isArray(list) ? list : [];
        const incomplete = normalized.filter((r) => !TERMINAL_STATUSES.has(String(r.status || '').toUpperCase()));

        incomplete.sort((a, b) => {
          const ta = new Date(a.updatedAt || a.createdAt || 0).getTime();
          const tb = new Date(b.updatedAt || b.createdAt || 0).getTime();
          return tb - ta;
        });

        setActiveList(incomplete);
      } catch (err) {
        setActiveList([]);
        setError(err?.message || 'Không thể tải danh sách yêu cầu');
      } finally {
        setLoading(false);
      }
    };

    fetchList();
  }, []);

  const handleSelect = (id) => {
    setLastRescueRequestId(id);
    onNavigate('requestDetail');
  };

  return (
    <div className="min-h-[80vh] bg-gray-100 py-10">
      <div className="container mx-auto px-4">
        <div className="max-w-3xl mx-auto bg-white rounded-xl shadow-lg p-6">
          <h1 className="text-2xl font-extrabold text-gray-900">Danh sách yêu cầu cứu hộ</h1>
          <p className="text-sm text-gray-600 mt-1">Hiển thị các yêu cầu chưa hoàn thành (mock). Bạn có thể tạo yêu cầu mới để danh sách có dữ liệu.</p>

          <div className="mt-4">
            {loading && <div className="text-sm text-gray-700">Đang tải danh sách...</div>}
            {error && (
              <div className="text-sm text-red-600 bg-red-50 border border-red-200 rounded-md p-2">
                {error}
              </div>
            )}

            {!loading && !error && activeList.length === 0 && (
              <div className="text-sm text-gray-700">Chưa có yêu cầu nào đang xử lý.</div>
            )}

            {activeList.length > 0 && (
              <div className="mt-2 border border-gray-200 rounded-lg overflow-hidden">
                {activeList.map((r) => {
                  const status = String(r.status || '').toUpperCase();
                  const label = STATUS_LABELS[status] || status;
                  const time = r.updatedAt || r.createdAt;
                  return (
                    <button
                      key={r.id}
                      type="button"
                      onClick={() => handleSelect(r.id)}
                      className="w-full text-left p-3 border-b border-gray-200 hover:bg-gray-50"
                    >
                      <div className="flex items-start justify-between gap-3">
                        <div>
                          <div className="font-bold text-gray-900">Yêu cầu #{r.id}</div>
                          <div className="text-xs text-gray-600 mt-1">{label}</div>
                          {r.location && <div className="text-xs text-gray-500 mt-1">{r.location}</div>}
                          {time && <div className="text-xs text-gray-400 mt-1">{formatDateTime(time)}</div>}
                        </div>
                        <div className="text-sm font-extrabold text-blue-900">Xem</div>
                      </div>
                    </button>
                  );
                })}
              </div>
            )}
          </div>

          <div className="mt-4 flex gap-3">
            <button
              onClick={() => onNavigate('createRequest')}
              className="bg-yellow-500 text-blue-900 font-extrabold px-4 py-2 rounded hover:bg-yellow-400"
            >
              Tạo yêu cầu mới
            </button>
            <button
              onClick={() => onNavigate('home')}
              className="bg-gray-200 text-gray-900 font-bold px-4 py-2 rounded hover:bg-gray-300"
            >
              Về trang chủ
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RescueRequestListView;
