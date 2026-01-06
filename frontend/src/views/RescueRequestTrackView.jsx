import React, { useEffect, useState } from 'react';
import { getLastRescueRequestId, getRescueRequestDetail, setLastRescueRequestId } from '../service/rescueRequestService';
import { Star } from 'lucide-react';
import { getReviewByRequestId, upsertReview } from '../service/reviewService';
import { setLastCompanyId } from '../utils/companyStorage';
import { loadAuth } from '../utils/authStorage';

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
  return timeline
    .filter(Boolean)
    .map((raw) => {
      const item = raw && typeof raw === 'object' ? raw : { status: String(raw) };
      return {
        status: item.status || item.newStatus || item.current_status,
        updatedAt: item.updated_at || item.time || item.changedAt || item.updatedAt,
        note: item.note || item.reason || '',
      };
    });
}

const RescueRequestTrackView = ({ onNavigate }) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [detail, setDetail] = useState(null);
  const [timeline, setTimeline] = useState(null);
  const [selectedId, setSelectedId] = useState('');

  const [ratingOpen, setRatingOpen] = useState(false);
  const [ratingValue, setRatingValue] = useState(5);
  const [ratingComment, setRatingComment] = useState('');
  const [ratingSaving, setRatingSaving] = useState(false);
  const [ratingError, setRatingError] = useState(null);
  const [existingReview, setExistingReview] = useState(null);

  const auth = loadAuth();
  const role = auth?.user?.role;
  const canRate = role === 'USER';

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
      setTimeline(fromBackend || []);
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
        companyId:
          detail.company?.id ||
          detail.companyId ||
          detail.company_id ||
          detail.rescueCompanyId ||
          detail.rescue_company_id,
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

  const isCompleted = String(viewModel?.status || '').toUpperCase() === 'COMPLETED';
  const hasCompany = Boolean(viewModel?.companyName) || viewModel?.companyId != null;

  const openRating = () => {
    if (!canRate) return;
    if (!isCompleted) return;
    if (!viewModel?.id) return;
    if (!hasCompany) return;

    const review = getReviewByRequestId(viewModel.id);
    setExistingReview(review);
    setRatingValue(review?.rating ?? 5);
    setRatingComment(review?.comment ?? '');
    setRatingError(null);
    setRatingOpen(true);
  };

  const openCompanyProfile = () => {
    if (!viewModel?.companyId) return;
    setLastCompanyId(viewModel.companyId);
    onNavigate('companyProfile');
  };

  const closeRating = () => {
    setRatingOpen(false);
    setRatingError(null);
  };

  const submitRating = async (e) => {
    e.preventDefault();
    if (!canRate) return;
    if (ratingSaving) return;
    setRatingSaving(true);
    setRatingError(null);
    try {
      await upsertReview({
        requestId: viewModel.id,
        companyId: viewModel.companyId ?? 0,
        rating: ratingValue,
        comment: ratingComment,
      });
      const updated = getReviewByRequestId(viewModel.id);
      setExistingReview(updated);
      setRatingOpen(false);
    } catch (err) {
      setRatingError(err?.message || 'Không thể gửi đánh giá');
    } finally {
      setRatingSaving(false);
    }
  };

  return (
    <div className="min-h-[80vh] bg-gray-100 py-10">
      <div className="container mx-auto px-4">
        <div className="max-w-3xl mx-auto bg-white rounded-xl shadow-lg p-6">
          <h1 className="text-2xl font-extrabold text-gray-900">Chi tiết yêu cầu cứu hộ</h1>
          <p className="text-sm text-gray-600 mt-1">Đang lấy chi tiết từ backend (GET /api/rescue-requests/{'{id}'}). Timeline dùng dữ liệu backend.</p>

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
                  <div className="mt-1 flex items-center justify-between gap-3">
                    <div>
                      <span className="font-semibold">Công ty:</span> {viewModel.companyName || '(chưa gán)'} {viewModel.companyPhone ? `- ${viewModel.companyPhone}` : ''}
                      {existingReview && (
                        <div className="text-xs text-gray-500 mt-1">
                          Bạn đã đánh giá: {existingReview.rating}/5 sao
                        </div>
                      )}
                    </div>

                    <div className="flex items-center gap-2">
                      <button
                        type="button"
                        onClick={openCompanyProfile}
                        disabled={!viewModel?.companyId}
                        className={`inline-flex items-center gap-2 font-bold px-3 py-2 rounded border ${
                          !viewModel?.companyId
                            ? 'bg-gray-100 text-gray-400 border-gray-200 cursor-not-allowed'
                            : 'bg-white text-blue-900 border-blue-200 hover:bg-blue-50'
                        }`}
                        title={!viewModel?.companyId ? 'Không có companyId để xem hồ sơ' : 'Xem hồ sơ công ty cứu hộ'}
                      >
                        Hồ sơ
                      </button>

                      <button
                        type="button"
                        onClick={openRating}
                        disabled={!canRate || !isCompleted || !hasCompany}
                        className={`inline-flex items-center gap-2 font-bold px-3 py-2 rounded border ${
                          !canRate || !isCompleted || !hasCompany
                            ? 'bg-gray-100 text-gray-400 border-gray-200 cursor-not-allowed'
                            : 'bg-yellow-500 text-blue-900 border-yellow-500 hover:bg-yellow-400'
                        }`}
                        title={
                          !canRate
                            ? 'Chỉ tài khoản USER mới có thể đánh giá'
                            :
                          !hasCompany
                            ? 'Chưa có công ty để đánh giá'
                            : !isCompleted
                              ? 'Chỉ có thể đánh giá khi yêu cầu đã hoàn thành'
                              : 'Đánh giá công ty cứu hộ'
                        }
                      >
                        <Star size={18} />
                        Đánh giá
                      </button>
                    </div>
                  </div>
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
                    {viewModel.timeline.map((rawItem, idx) => {
                      const item = rawItem && typeof rawItem === 'object' ? rawItem : { status: String(rawItem) };
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
                  onClick={() => onNavigate('chat')}
                  className="bg-blue-900 text-white font-bold px-4 py-2 rounded hover:bg-blue-800"
                >
                  Tin nhắn
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

      {ratingOpen && viewModel && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 px-4">
          <div className="w-full max-w-lg bg-white rounded-xl shadow-lg border border-gray-200 p-6">
            <div className="flex items-start justify-between gap-3">
              <div>
                <div className="text-xl font-extrabold text-gray-900">Đánh giá công ty cứu hộ</div>
                <div className="text-sm text-gray-600 mt-1">
                  {viewModel.companyName || 'Công ty'} • Yêu cầu #{viewModel.id}
                </div>
              </div>
              <button
                type="button"
                onClick={closeRating}
                className="bg-gray-200 text-gray-900 font-bold px-3 py-1 rounded hover:bg-gray-300"
              >
                Đóng
              </button>
            </div>

            {ratingError && (
              <div className="mt-4 text-sm text-red-600 bg-red-50 border border-red-200 rounded-md p-2">
                {ratingError}
              </div>
            )}

            <form onSubmit={submitRating} className="mt-5 space-y-4">
              <div>
                <div className="text-sm font-bold text-gray-900">Rating</div>
                <div className="mt-2 flex items-center gap-2">
                  {[1, 2, 3, 4, 5].map((v) => {
                    const active = v <= ratingValue;
                    return (
                      <button
                        key={v}
                        type="button"
                        onClick={() => setRatingValue(v)}
                        className={`p-1 rounded ${active ? 'text-yellow-500' : 'text-gray-300'} hover:text-yellow-500`}
                        aria-label={`${v} sao`}
                      >
                        <Star size={26} fill={active ? 'currentColor' : 'none'} />
                      </button>
                    );
                  })}
                  <div className="text-sm text-gray-700 font-semibold">{ratingValue}/5</div>
                </div>
              </div>

              <div>
                <div className="text-sm font-bold text-gray-900">Bình luận</div>
                <textarea
                  value={ratingComment}
                  onChange={(e) => setRatingComment(e.target.value)}
                  rows={4}
                  className="mt-2 w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="Chia sẻ trải nghiệm của bạn..."
                />
              </div>

              <div className="flex justify-end gap-2">
                <button
                  type="button"
                  onClick={closeRating}
                  className="bg-gray-200 text-gray-900 font-bold px-4 py-2 rounded hover:bg-gray-300"
                >
                  Hủy
                </button>
                <button
                  type="submit"
                  disabled={ratingSaving}
                  className="bg-blue-900 text-white font-bold px-4 py-2 rounded hover:bg-blue-800 disabled:opacity-60"
                >
                  {ratingSaving ? 'Đang gửi...' : (existingReview ? 'Cập nhật' : 'Gửi đánh giá')}
                </button>
              </div>
            </form>

            {!isCompleted && (
              <div className="mt-4 text-xs text-gray-500">
                Chỉ có thể đánh giá khi yêu cầu đã hoàn thành.
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default RescueRequestTrackView;
