import React, { useEffect, useMemo, useState } from 'react';
import { Star } from 'lucide-react';
import { getCompanyDetail } from '../service/companyService';
import { getCachedReviewsByCompanyId } from '../service/reviewService';
import { getLastCompanyId } from '../utils/companyStorage';

function formatDateTime(value) {
  if (!value) return '';
  try {
    return new Date(value).toLocaleString();
  } catch {
    return String(value);
  }
}

function StarRow({ value = 0 }) {
  const safe = Math.max(0, Math.min(5, Number(value) || 0));
  const full = Math.round(safe);
  return (
    <div className="inline-flex items-center gap-1" aria-label={`Rating ${safe} / 5`}>
      {Array.from({ length: 5 }).map((_, i) => (
        <Star
          key={i}
          size={16}
          className={i < full ? 'text-yellow-500' : 'text-gray-300'}
          fill={i < full ? 'currentColor' : 'none'}
        />
      ))}
    </div>
  );
}

export default function CompanyProfileView({ onNavigate }) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [company, setCompany] = useState(null);

  const companyId = useMemo(() => getLastCompanyId(), []);

  const localReviews = useMemo(() => {
    if (!companyId) return [];
    return getCachedReviewsByCompanyId(companyId).map((r) => ({
      id: `local-${r.requestId}`,
      userName: 'Bạn',
      rating: r.rating,
      comment: r.comment,
      isVerified: false,
      createdAt: r.createdAt,
      _source: 'local',
    }));
  }, [companyId]);

  useEffect(() => {
    let alive = true;

    async function run() {
      if (!companyId) {
        setCompany(null);
        setError('Chưa có công ty nào được chọn để xem hồ sơ.');
        return;
      }

      setLoading(true);
      setError(null);
      try {
        const data = await getCompanyDetail(companyId);
        if (!alive) return;
        setCompany(data);
      } catch (err) {
        if (!alive) return;
        setError(err?.message || 'Không thể tải hồ sơ công ty');
        setCompany(null);
      } finally {
        if (alive) setLoading(false);
      }
    }

    run();
    return () => {
      alive = false;
    };
  }, [companyId]);

  const serverReviews = Array.isArray(company?.reviews) ? company.reviews : [];
  const combinedReviews = useMemo(() => {
    const combined = [...serverReviews];

    // Avoid duplicates if someday backend returns the same (id) as our local
    const serverKeys = new Set(combined.map((r) => String(r?.id ?? '')));
    localReviews.forEach((r) => {
      if (!serverKeys.has(String(r.id))) combined.unshift(r);
    });

    return combined;
  }, [serverReviews, localReviews]);

  const combinedStats = useMemo(() => {
    const serverTotal = Number(company?.totalReviews);
    const serverAvg = Number(company?.averageRating);

    const localCount = localReviews.length;
    const localSum = localReviews.reduce((sum, r) => sum + (Number(r.rating) || 0), 0);

    const hasServer = Number.isFinite(serverTotal) && Number.isFinite(serverAvg) && serverTotal > 0;
    const total = (hasServer ? serverTotal : 0) + localCount;

    if (total <= 0) {
      return { average: 0, total: 0 };
    }

    const sum = (hasServer ? serverAvg * serverTotal : 0) + localSum;
    return { average: sum / total, total };
  }, [company?.totalReviews, company?.averageRating, localReviews]);

  return (
    <div className="min-h-[80vh] bg-gray-100 py-10">
      <div className="container mx-auto px-4">
        <div className="max-w-3xl mx-auto bg-white rounded-xl shadow-lg p-6">
          <div className="flex items-start justify-between gap-3">
            <div>
              <h1 className="text-2xl font-extrabold text-gray-900">Hồ sơ công ty cứu hộ</h1>
              <p className="text-sm text-gray-600 mt-1">Thông tin công ty + rating + danh sách đánh giá.</p>
            </div>
            <button
              type="button"
              onClick={() => onNavigate('requestDetail')}
              className="bg-blue-900 text-white font-bold px-4 py-2 rounded hover:bg-blue-800"
            >
              Quay lại
            </button>
          </div>

          {loading && <div className="mt-6 text-sm text-gray-700">Đang tải hồ sơ...</div>}

          {error && (
            <div className="mt-6 text-sm text-red-600 bg-red-50 border border-red-200 rounded-md p-3">{error}</div>
          )}

          {company && (
            <div className="mt-6 space-y-4">
              <div className="bg-gray-50 border border-gray-200 rounded-lg p-4">
                <div className="flex items-start justify-between gap-4">
                  <div>
                    <div className="text-lg font-extrabold text-gray-900">{company.name || '(không có tên)'}</div>
                    <div className="text-sm text-gray-700 mt-1">
                      <div><span className="font-semibold">Địa chỉ:</span> {company.address || '(không có)'}</div>
                      <div className="mt-1"><span className="font-semibold">Điện thoại:</span> {company.phone || '(không có)'}</div>
                      <div className="mt-1"><span className="font-semibold">Email:</span> {company.email || '(không có)'}</div>
                      {company.description && (
                        <div className="mt-1"><span className="font-semibold">Mô tả:</span> {company.description}</div>
                      )}
                    </div>
                  </div>

                  <div className="text-right">
                    <div className="text-sm text-gray-600">Rating</div>
                    <div className="mt-1 flex items-center justify-end gap-2">
                      <StarRow value={combinedStats.average} />
                      <span className="text-sm font-bold text-gray-900">{combinedStats.average.toFixed(1)}</span>
                    </div>
                    <div className="text-xs text-gray-500 mt-1">{combinedStats.total} đánh giá</div>
                  </div>
                </div>
              </div>

              {Array.isArray(company.services) && company.services.length > 0 && (
                <div className="bg-white border border-gray-200 rounded-lg p-4">
                  <div className="font-bold text-gray-900 mb-2">Dịch vụ</div>
                  <ul className="space-y-2">
                    {company.services.map((s) => (
                      <li key={s.id ?? `${s.name}-${s.type}`} className="text-sm text-gray-800 bg-gray-50 border border-gray-200 rounded-md p-2">
                        <div className="font-semibold">{s.typeDisplayName || s.name || 'Dịch vụ'}</div>
                        <div className="text-xs text-gray-600 mt-1">
                          {s.basePrice != null && <span>Giá từ: {s.basePrice} </span>}
                          {s.priceUnit && <span>({s.priceUnit})</span>}
                        </div>
                      </li>
                    ))}
                  </ul>
                </div>
              )}

              <div className="bg-white border border-gray-200 rounded-lg p-4">
                <div className="font-bold text-gray-900 mb-2">Đánh giá</div>
                {combinedReviews.length === 0 ? (
                  <div className="text-sm text-gray-700">Chưa có đánh giá nào.</div>
                ) : (
                  <ul className="space-y-2">
                    {combinedReviews.map((r, idx) => (
                      <li key={r.id ?? idx} className="text-sm text-gray-800 bg-gray-50 border border-gray-200 rounded-md p-3">
                        <div className="flex items-center justify-between gap-3">
                          <div className="font-semibold text-gray-900">
                            {r.userName || 'Người dùng'}
                            {r._source === 'local' && <span className="text-xs text-gray-500"> (local)</span>}
                          </div>
                          <div className="flex items-center gap-2">
                            <StarRow value={r.rating} />
                            <span className="text-xs text-gray-600">{Number(r.rating || 0)}/5</span>
                          </div>
                        </div>
                        {r.comment && <div className="mt-2 text-sm text-gray-800">{r.comment}</div>}
                        <div className="mt-2 text-xs text-gray-500 flex items-center justify-between">
                          <span>{formatDateTime(r.createdAt)}</span>
                          {r.isVerified != null && <span>{r.isVerified ? 'Đã xác thực' : 'Chưa xác thực'}</span>}
                        </div>
                      </li>
                    ))}
                  </ul>
                )}
              </div>
            </div>
          )}

          {!loading && !company && !error && (
            <div className="mt-6 text-sm text-gray-700">Không có dữ liệu công ty.</div>
          )}
        </div>
      </div>
    </div>
  );
}
