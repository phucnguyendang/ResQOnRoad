import React, { useState, useEffect } from 'react';
import reviewService from '../service/reviewService';

/**
 * ReviewList Component - UC102
 * Hiển thị danh sách đánh giá của công ty
 */
const ReviewList = ({ companyId, companyName }) => {
  const [reviews, setReviews] = useState([]);
  const [averageRating, setAverageRating] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [totalItems, setTotalItems] = useState(0);

  const itemsPerPage = 10;

  useEffect(() => {
    fetchReviews();
    fetchAverageRating();
  }, [companyId, currentPage]);

  const fetchReviews = async () => {
    setLoading(true);
    setError('');

    try {
      const response = await reviewService.getCompanyReviews(
        companyId,
        currentPage,
        itemsPerPage
      );

      if (response.data?.data?.items) {
        setReviews(response.data.data.items);
        setTotalPages(response.data.data.pagination.total_pages || 1);
        setTotalItems(response.data.data.pagination.total_items || 0);
      }
    } catch (err) {
      setError(
        err.response?.data?.message || 'Không thể tải danh sách đánh giá'
      );
      console.error('Error fetching reviews:', err);
    } finally {
      setLoading(false);
    }
  };

  const fetchAverageRating = async () => {
    try {
      const response = await reviewService.getCompanyAverageRating(companyId);

      if (response.data?.data?.rating_avg !== undefined) {
        setAverageRating(response.data.data.rating_avg);
      }
    } catch (err) {
      console.error('Error fetching average rating:', err);
    }
  };

  const renderStars = (rating) => {
    return '⭐'.repeat(rating) + '☆'.repeat(5 - rating);
  };

  const handlePageChange = (newPage) => {
    if (newPage > 0 && newPage <= totalPages) {
      setCurrentPage(newPage);
    }
  };

  return (
    <div className="review-list-container max-w-2xl mx-auto p-6 bg-white rounded-lg shadow-md">
      <div className="mb-6">
        <h2 className="text-2xl font-bold mb-2">
          Đánh giá {companyName || 'công ty'}
        </h2>

        {/* Average Rating Summary */}
        <div className="bg-gray-50 p-4 rounded-lg mb-4">
          <div className="flex items-center gap-4">
            <div className="text-center">
              <p className="text-3xl font-bold text-yellow-500">
                {averageRating.toFixed(1)}
              </p>
              <p className="text-sm text-gray-600">trên 5.0</p>
            </div>
            <div className="flex-1">
              <div className="flex items-center gap-2 mb-2">
                <span className="text-lg">{renderStars(Math.round(averageRating))}</span>
                <span className="text-sm text-gray-600">
                  {totalItems} đánh giá
                </span>
              </div>
              <p className="text-xs text-gray-500">
                Dựa trên {totalItems} đánh giá từ khách hàng
              </p>
            </div>
          </div>
        </div>
      </div>

      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
          {error}
        </div>
      )}

      {loading ? (
        <div className="text-center py-8">
          <p className="text-gray-500">Đang tải đánh giá...</p>
        </div>
      ) : reviews.length > 0 ? (
        <>
          {/* Reviews List */}
          <div className="space-y-4">
            {reviews.map((review) => (
              <div
                key={review.id}
                className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow"
              >
                <div className="flex justify-between items-start mb-2">
                  <div>
                    <p className="font-semibold text-gray-800">
                      {review.userName}
                    </p>
                    <p className="text-sm text-gray-500">
                      {new Date(review.createdAt).toLocaleDateString('vi-VN')}
                    </p>
                  </div>
                  <div className="text-right">
                    <span className="text-lg">
                      {renderStars(review.rating)}
                    </span>
                    {review.isVerified && (
                      <p className="text-xs text-green-600 font-semibold">
                        ✓ Đã xác thực
                      </p>
                    )}
                  </div>
                </div>

                {review.comment && (
                  <p className="text-gray-700 text-sm mt-3">{review.comment}</p>
                )}
              </div>
            ))}
          </div>

          {/* Pagination */}
          {totalPages > 1 && (
            <div className="flex justify-center gap-2 mt-6">
              <button
                onClick={() => handlePageChange(currentPage - 1)}
                disabled={currentPage === 1}
                className="px-3 py-1 border border-gray-300 rounded disabled:text-gray-400 disabled:cursor-not-allowed hover:bg-gray-100"
              >
                Trước
              </button>

              {Array.from({ length: totalPages }, (_, i) => i + 1).map((page) => (
                <button
                  key={page}
                  onClick={() => handlePageChange(page)}
                  className={`px-3 py-1 rounded ${
                    currentPage === page
                      ? 'bg-blue-600 text-white'
                      : 'border border-gray-300 hover:bg-gray-100'
                  }`}
                >
                  {page}
                </button>
              ))}

              <button
                onClick={() => handlePageChange(currentPage + 1)}
                disabled={currentPage === totalPages}
                className="px-3 py-1 border border-gray-300 rounded disabled:text-gray-400 disabled:cursor-not-allowed hover:bg-gray-100"
              >
                Sau
              </button>
            </div>
          )}
        </>
      ) : (
        <div className="text-center py-8">
          <p className="text-gray-500">Chưa có đánh giá nào</p>
        </div>
      )}
    </div>
  );
};

export default ReviewList;
