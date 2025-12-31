import React, { useEffect, useState } from 'react';
import { getLastRescueRequestId, getRescueRequestDetail, setLastRescueRequestId } from '../service/rescueRequestService';

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

function normalizeTimelineFromBackend(detail) {
  const timeline = detail?.timeline || detail?.history || null;
  if (!Array.isArray(timeline)) return null;
  return timeline.map((item) => ({
    status: item.status || item.newStatus || item.current_status,
    updatedAt: item.updated_at || item.time || item.changedAt || item.updatedAt,
    note: item.note || item.reason || '',
  }));
}

function buildMockTimeline(detail) {
  const createdAt = detail?.createdAt || detail?.created_at || new Date().toISOString();
  const current = String(detail?.status || '').toUpperCase();
  const steps = ['PENDING_CONFIRMATION', 'ACCEPTED', 'IN_TRANSIT', 'IN_PROGRESS', 'COMPLETED'];
  const endIndex = Math.max(0, steps.indexOf(current));
  const upto = endIndex === -1 ? ['PENDING_CONFIRMATION'] : steps.slice(0, endIndex + 1);
  const base = new Date(createdAt).getTime();
  return upto.map((s, idx) => ({
    status: s,
    updatedAt: new Date(base + idx * 5 * 60 * 1000).toISOString(),
    note: '',
  }));
}

const RescueRequestTrackView = ({ onNavigate }) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [detail, setDetail] = useState(null);
  const [timeline, setTimeline] = useState(null);
  const [selectedId, setSelectedId] = useState('');

  useEffect(() => {
    const last = getLastRescueRequestId();
    if (last) setSelectedId(String(last));
  }, []);

  const fetchDetail = async (explicitId) => {
    const effectiveId = explicitId ?? selectedId;

    setLoading(true);
    setError(null);
    setDetail(null);
    setTimeline(null);

    try {
      if (!effectiveId) {
        throw new Error('Vui lòng nhập mã yêu cầu.');
      }
      setLastRescueRequestId(effectiveId);

      const data = await getRescueRequestDetail(effectiveId);
      setDetail(data);

      const fromBackend = normalizeTimelineFromBackend(data);
      setTimeline(fromBackend || buildMockTimeline(data));
    } catch (err) {
      const details = Array.isArray(err?.details) && err.details.length > 0
        ? `: ${err.details.join(', ')}`
        : '';
      setError(`${err?.message || 'Không thể lấy chi tiết yêu cầu'}${details}`);
    } finally {
      setLoading(false);
    }
  };

  // Auto-fetch when opening detail if we have an id
  useEffect(() => {
    if (!selectedId) return;
    fetchDetail(selectedId);
  }, [selectedId]);

  // Support both api_docs-style and current backend DTO style
  const viewModel = detail
    ? {
        id: detail.id,
        status: detail.status || detail.current_status || detail?.data?.status,
        userName: detail.user?.full_name || detail.userName,
        userPhone: detail.user?.phone || detail.userPhoneNumber,
        address: detail.incident?.address || detail.location,
        incidentDesc: detail.incident?.desc || detail.description,
        companyName: detail.company?.name || detail.companyName,
        companyPhone: detail.company?.hotline || detail.companyPhoneNumber,
        timeline: timeline,
        createdAt: detail.createdAt,
        updatedAt: detail.updatedAt,
        completedAt: detail.completedAt,
        latitude: detail.latitude,
        longitude: detail.longitude,
      }
    : null;

  return (
    <div className="min-h-[80vh] bg-gray-100 py-10">
      <div className="container mx-auto px-4">
        <div className="max-w-3xl mx-auto bg-white rounded-xl shadow-lg p-6">
          <h1 className="text-2xl font-extrabold text-gray-900">Chi tiết yêu cầu cứu hộ</h1>
          <p className="text-sm text-gray-600 mt-1">Dữ liệu chi tiết + timeline đang lấy từ mock để bạn xem giao diện.</p>

          {!selectedId && (
            <div className="mt-6 bg-yellow-50 border border-yellow-200 rounded-lg p-4 text-sm text-yellow-900">
              Bạn chưa chọn yêu cầu nào. Hãy quay lại danh sách và chọn một yêu cầu để xem chi tiết.
              <div className="mt-3">
                <button
                  onClick={() => onNavigate('requestList')}
                  className="bg-blue-900 text-white font-bold px-4 py-2 rounded hover:bg-blue-800"
                >
                  Về danh sách yêu cầu
                </button>
              </div>
            </div>
          )}

          {error && (
            <div className="mt-4 text-sm text-red-600 bg-red-50 border border-red-200 rounded-md p-2">
              {error}
            </div>
          )}

          {selectedId && loading && (
            <div className="mt-4 text-sm text-gray-700">Đang tải chi tiết...</div>
          )}

          {viewModel && (
            <div className="mt-6 space-y-4">
              <div className="bg-gray-50 border border-gray-200 rounded-lg p-4">
                <div className="flex items-center justify-between">
                  <div className="font-bold text-gray-900">Yêu cầu #{viewModel.id}</div>
                  <div className="text-sm font-semibold text-blue-900">{String(viewModel.status || '')}</div>
                </div>
                <div className="text-sm text-gray-700 mt-2">
                  <div><span className="font-semibold">Khách hàng:</span> {viewModel.userName || '(không có)'} {viewModel.userPhone ? `- ${viewModel.userPhone}` : ''}</div>
                  <div className="mt-1"><span className="font-semibold">Địa chỉ:</span> {viewModel.address || '(không có)'}</div>
                  <div className="mt-1"><span className="font-semibold">Mô tả:</span> {viewModel.incidentDesc || '(không có)'}</div>
                  <div className="mt-1"><span className="font-semibold">Công ty:</span> {viewModel.companyName || '(chưa gán)'} {viewModel.companyPhone ? `- ${viewModel.companyPhone}` : ''}</div>
                  {(viewModel.latitude != null && viewModel.longitude != null) && (
                    <div className="mt-1"><span className="font-semibold">Tọa độ:</span> {viewModel.latitude}, {viewModel.longitude}</div>
                  )}
                </div>
                <div className="text-xs text-gray-500 mt-3">
                  {viewModel.createdAt && <div>Created: {formatDateTime(viewModel.createdAt)}</div>}
                  {viewModel.updatedAt && <div>Updated: {formatDateTime(viewModel.updatedAt)}</div>}
                  {viewModel.completedAt && <div>Completed: {formatDateTime(viewModel.completedAt)}</div>}
                </div>
              </div>

              {Array.isArray(viewModel.timeline) && viewModel.timeline.length > 0 && (
                <div className="bg-white border border-gray-200 rounded-lg p-4">
                  <div className="font-bold text-gray-900 mb-2">Tiến trình</div>
                  <ul className="space-y-2">
                    {viewModel.timeline.map((item, idx) => {
                      const status = item.status || item.current_status;
                      const time = item.updated_at || item.time || item.updatedAt;
                      const note = item.note;
                      const label = STATUS_LABELS[String(status || '').toUpperCase()] || String(status || '');
                      return (
                        <li key={idx} className="text-sm text-gray-800 bg-gray-50 border border-gray-200 rounded-md p-2">
                          <div className="font-semibold">{label}</div>
                          {time && <div className="text-xs text-gray-500">{formatDateTime(time)}</div>}
                          {note && <div className="text-xs text-gray-600 mt-1">{note}</div>}
                        </li>
                      );
                    })}
                  </ul>
                </div>
              )}

              <div className="flex gap-3">
                <button
                  onClick={() => onNavigate('requestList')}
                  className="bg-gray-200 text-gray-900 font-bold px-4 py-2 rounded hover:bg-gray-300"
                >
                  Về danh sách
                </button>
                <button
                  onClick={() => onNavigate('home')}
                  className="bg-gray-200 text-gray-900 font-bold px-4 py-2 rounded hover:bg-gray-300"
                >
                  Về trang chủ
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default RescueRequestTrackView;
