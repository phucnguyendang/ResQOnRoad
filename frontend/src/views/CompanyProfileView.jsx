import React, { useEffect, useMemo, useState } from 'react';
import { Star } from 'lucide-react';
import { getCompanyDetail } from '../service/companyService';
import { getCachedReviewsByCompanyId, getCompanyRating, getReviewsByCompanyId } from '../service/reviewService';
import { getLastCompanyId } from '../utils/companyStorage';
import { getCompanyCommunityPosts } from '../service/communityService';

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

export default function CompanyProfileView({ onNavigate, user }) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [company, setCompany] = useState(null);

  const [ratingAvg, setRatingAvg] = useState(null);
  const [reviewsLoading, setReviewsLoading] = useState(false);
  const [reviewsError, setReviewsError] = useState('');
  const [serverReviews, setServerReviews] = useState([]);

  const [postsLoading, setPostsLoading] = useState(false);
  const [postsError, setPostsError] = useState('');
  const [postsPage, setPostsPage] = useState(null);
  const [pageIndex, setPageIndex] = useState(0);

  const companyId = useMemo(() => getLastCompanyId(), []);

  const canEdit = useMemo(() => {
    if (!user || !companyId) return false;
    const isCompany = user.roles?.includes('ROLE_COMPANY') || String(user.role || '').toUpperCase() === 'COMPANY';
    if (!isCompany) return false;
    const myCompanyId = user.companyId ?? user.company_id;
    return myCompanyId != null && String(myCompanyId) === String(companyId);
  }, [user, companyId]);

  const posts = useMemo(() => {
    const content = postsPage?.content;
    return Array.isArray(content) ? content : [];
  }, [postsPage]);

  const hasNextPage = useMemo(() => {
    if (!postsPage) return false;
    if (typeof postsPage.last === 'boolean') return !postsPage.last;
    if (typeof postsPage.totalPages === 'number' && typeof postsPage.number === 'number') {
      return postsPage.number + 1 < postsPage.totalPages;
    }
    return false;
  }, [postsPage]);

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

  useEffect(() => {
    let alive = true;

    async function loadRatingAndReviews() {
      if (!companyId) {
        setRatingAvg(null);
        setServerReviews([]);
        return;
      }

      setReviewsLoading(true);
      setReviewsError('');
      try {
        const [ratingData, reviews] = await Promise.all([
          getCompanyRating(companyId).catch(() => null),
          getReviewsByCompanyId(companyId, { page: 1, limit: 20 }).catch(() => []),
        ]);

        if (!alive) return;
        const avg = ratingData && ratingData.rating_avg != null ? Number(ratingData.rating_avg) : null;
        setRatingAvg(Number.isFinite(avg) ? avg : null);
        setServerReviews(Array.isArray(reviews) ? reviews : []);
      } catch (e) {
        if (!alive) return;
        setReviewsError(e?.message || 'Không thể tải đánh giá.');
        setRatingAvg(null);
        setServerReviews([]);
      } finally {
        if (alive) setReviewsLoading(false);
      }
    }

    loadRatingAndReviews();
    return () => {
      alive = false;
    };
  }, [companyId]);

  async function loadCompanyPosts({ page = 0, append = false } = {}) {
    if (!companyId) return;
    setPostsError('');
    setPostsLoading(true);
    try {
      const data = await getCompanyCommunityPosts(companyId, { page, size: 10 });
      setPostsPage((prev) => {
        if (!append) return data;
        const prevContent = Array.isArray(prev?.content) ? prev.content : [];
        const nextContent = Array.isArray(data?.content) ? data.content : [];
        return { ...data, content: [...prevContent, ...nextContent] };
      });
      setPageIndex(page);
    } catch (e) {
      setPostsError(e?.message || 'Không thể tải bài đăng của công ty.');
    } finally {
      setPostsLoading(false);
    }
  }

  useEffect(() => {
    loadCompanyPosts({ page: 0, append: false });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [companyId]);

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
    const localCount = localReviews.length;
    const localSum = localReviews.reduce((sum, r) => sum + (Number(r.rating) || 0), 0);

    const serverAvg = Number(ratingAvg);
    const serverCount = Array.isArray(serverReviews) ? serverReviews.length : 0;
    const hasServer = Number.isFinite(serverAvg) && serverCount > 0;
    const total = (hasServer ? serverCount : 0) + localCount;

    if (total <= 0) {
      return { average: 0, total: 0 };
    }

    const sum = (hasServer ? serverAvg * serverCount : 0) + localSum;
    return { average: sum / total, total };
  }, [ratingAvg, serverReviews, localReviews]);

  return (
    <div className="min-h-[80vh] bg-gray-100 py-10">
      <div className="container mx-auto px-4">
        <div className="max-w-3xl mx-auto bg-white rounded-xl shadow-lg p-6">
          <div className="flex items-start justify-between gap-3">
            <div>
              <h1 className="text-2xl font-extrabold text-gray-900">Hồ sơ công ty cứu hộ</h1>
              <p className="text-sm text-gray-600 mt-1">Thông tin công ty + rating + danh sách đánh giá.</p>
            </div>
            <div className="flex items-center gap-2">
              {canEdit ? (
                <button
                  type="button"
                  onClick={() => onNavigate?.('profileEdit')}
                  className="bg-yellow-500 text-blue-900 font-bold px-4 py-2 rounded hover:bg-yellow-400"
                >
                  Chỉnh sửa
                </button>
              ) : null}
              <button
                type="button"
                onClick={() => onNavigate('requestDetail')}
                className="bg-blue-900 text-white font-bold px-4 py-2 rounded hover:bg-blue-800"
              >
                Quay lại
              </button>
            </div>
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

              <div className="bg-white border border-gray-200 rounded-lg p-4">
                <div className="flex items-center justify-between gap-3 flex-wrap">
                  <div>
                    <div className="font-bold text-gray-900">Bài đăng</div>
                    <div className="text-sm text-gray-600">Các bài đăng của công ty trong Cộng đồng.</div>
                  </div>
                  <button
                    type="button"
                    onClick={() => loadCompanyPosts({ page: 0, append: false })}
                    className="text-sm bg-blue-900 text-white px-3 py-2 rounded hover:bg-blue-800"
                    disabled={postsLoading}
                  >
                    Làm mới
                  </button>
                </div>

                {postsError ? (
                  <div className="mt-3 text-sm text-red-600">{postsError}</div>
                ) : null}

                {postsLoading && !postsPage ? (
                  <div className="mt-3 text-sm text-gray-700">Đang tải bài đăng...</div>
                ) : null}

                <div className="mt-4 space-y-3">
                  {posts.map((post) => (
                    <article key={post?.id} className="bg-gray-50 border border-gray-200 rounded-md p-3">
                      <div className="flex items-start justify-between gap-3">
                        <div>
                          <div className="font-semibold text-gray-900">{post?.title}</div>
                          <div className="text-xs text-gray-500 mt-1">{formatDateTime(post?.createdAt)}</div>
                        </div>
                        {post?.isResolved ? (
                          <span className="text-xs bg-green-100 text-green-700 px-2 py-1 rounded">Đã đóng</span>
                        ) : (
                          <span className="text-xs bg-yellow-100 text-yellow-800 px-2 py-1 rounded">Đang mở</span>
                        )}
                      </div>

                      {post?.content ? (
                        <div className="mt-2 text-sm text-gray-800 whitespace-pre-wrap">{post.content}</div>
                      ) : null}

                      {post?.imageBase64 ? (
                        <img src={post.imageBase64} alt="post" className="w-full rounded border mt-3" />
                      ) : null}

                      <div className="mt-2 text-xs text-gray-600">
                        {typeof post?.commentCount === 'number' ? post.commentCount : 0} bình luận
                        {typeof post?.viewCount === 'number' ? ` · ${post.viewCount} lượt xem` : ''}
                      </div>
                    </article>
                  ))}
                </div>

                {!postsLoading && posts.length === 0 ? (
                  <div className="mt-3 text-sm text-gray-700">Chưa có bài đăng nào.</div>
                ) : null}

                <div className="mt-4 flex justify-center">
                  {posts.length > 0 && hasNextPage ? (
                    <button
                      type="button"
                      onClick={() => loadCompanyPosts({ page: pageIndex + 1, append: true })}
                      className="bg-blue-900 text-white px-4 py-2 rounded hover:bg-blue-800 disabled:opacity-60"
                      disabled={postsLoading}
                    >
                      {postsLoading ? 'Đang tải...' : 'Tải thêm'}
                    </button>
                  ) : null}
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
                {reviewsLoading ? (
                  <div className="text-sm text-gray-700">Đang tải đánh giá...</div>
                ) : null}
                {reviewsError ? (
                  <div className="text-sm text-red-600 mb-2">{reviewsError}</div>
                ) : null}
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
