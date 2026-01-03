import React, { useState } from 'react';
import reviewService from '../service/reviewService';

/**
 * ReviewForm Component - UC102
 * Cho phép người dùng gửi đánh giá và phản hồi
 */
const ReviewForm = ({ requestId, companyName, onSuccess, onCancel }) => {
    const [rating, setRating] = useState(5);
    const [comment, setComment] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');
        setLoading(true);

        try {
            const response = await reviewService.createOrUpdateReview(
                requestId,
                rating,
                comment
            );

            setSuccess('Đánh giá của bạn đã được gửi thành công!');
            setRating(5);
            setComment('');

            if (onSuccess) {
                onSuccess(response.data);
            }

            // Clear success message after 3 seconds
            setTimeout(() => setSuccess(''), 3000);
        } catch (err) {
            setError(err.response?.data?.message || 'Có lỗi xảy ra khi gửi đánh giá');
            console.error('Error submitting review:', err);
        } finally {
            setLoading(false);
        }
    };

    const handleSkip = () => {
        if (onCancel) {
            onCancel();
        }
    };

    return (
        <div className="review-form-container max-w-md mx-auto p-6 bg-white rounded-lg shadow-md">
            <h2 className="text-2xl font-bold mb-4">
                Đánh giá {companyName || 'dịch vụ'}
            </h2>
            <p className="text-gray-600 mb-4">
                Chia sẻ trải nghiệm của bạn để giúp chúng tôi cải thiện dịch vụ
            </p>

            {error && (
                <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
                    {error}
                </div>
            )}

            {success && (
                <div className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded mb-4">
                    {success}
                </div>
            )}

            <form onSubmit={handleSubmit}>
                {/* Rating Stars */}
                <div className="mb-6">
                    <label className="block text-sm font-semibold mb-2">
                        Đánh giá của bạn
                    </label>
                    <div className="flex gap-2">
                        {[1, 2, 3, 4, 5].map((star) => (
                            <button
                                key={star}
                                type="button"
                                onClick={() => setRating(star)}
                                className={`text-3xl transition-colors ${star <= rating ? 'text-yellow-400' : 'text-gray-300'
                                    }`}
                            >
                                ⭐
                            </button>
                        ))}
                    </div>
                    <p className="text-sm text-gray-500 mt-2">
                        {rating === 1 && 'Rất không hài lòng'}
                        {rating === 2 && 'Không hài lòng'}
                        {rating === 3 && 'Bình thường'}
                        {rating === 4 && 'Hài lòng'}
                        {rating === 5 && 'Rất hài lòng'}
                    </p>
                </div>

                {/* Comment */}
                <div className="mb-6">
                    <label htmlFor="comment" className="block text-sm font-semibold mb-2">
                        Nhận xét (tuỳ chọn)
                    </label>
                    <textarea
                        id="comment"
                        value={comment}
                        onChange={(e) => setComment(e.target.value)}
                        placeholder="Chia sẻ chi tiết về trải nghiệm của bạn..."
                        maxLength={1000}
                        rows={4}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                    <p className="text-xs text-gray-500 mt-1">
                        {comment.length}/1000 ký tự
                    </p>
                </div>

                {/* Buttons */}
                <div className="flex gap-3">
                    <button
                        type="submit"
                        disabled={loading}
                        className="flex-1 bg-blue-600 text-white font-semibold py-2 px-4 rounded-lg hover:bg-blue-700 disabled:bg-gray-400 transition-colors"
                    >
                        {loading ? 'Đang gửi...' : 'Gửi đánh giá'}
                    </button>
                    <button
                        type="button"
                        onClick={handleSkip}
                        disabled={loading}
                        className="flex-1 bg-gray-300 text-gray-800 font-semibold py-2 px-4 rounded-lg hover:bg-gray-400 disabled:bg-gray-400 transition-colors"
                    >
                        Bỏ qua
                    </button>
                </div>
            </form>
        </div>
    );
};

export default ReviewForm;
