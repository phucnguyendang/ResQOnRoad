import apiClient from './apiClient';

/**
 * Service for Review API calls (UC102)
 */
const reviewService = {
  /**
   * Create or update a review
   * POST /api/reviews
   */
  createOrUpdateReview: async (requestId, rating, comment) => {
    return apiClient.post('/reviews', {
      requestId,
      rating,
      comment,
    });
  },

  /**
   * Get company's reviews with pagination
   * GET /api/companies/{companyId}/reviews
   */
  getCompanyReviews: async (companyId, page = 1, limit = 10) => {
    return apiClient.get(`/reviews/companies/${companyId}/reviews`, {
      params: {
        page,
        limit,
      },
    });
  },

  /**
   * Get average rating of a company
   * GET /api/companies/{companyId}/rating
   */
  getCompanyAverageRating: async (companyId) => {
    return apiClient.get(`/reviews/companies/${companyId}/rating`);
  },

  /**
   * Get current user's reviews
   * GET /api/reviews/my-reviews
   */
  getUserReviews: async () => {
    return apiClient.get('/reviews/my-reviews');
  },

  /**
   * Check if user has reviewed a company
   * GET /api/reviews/check
   */
  checkIfReviewed: async (companyId) => {
    return apiClient.get('/reviews/check', {
      params: {
        companyId,
      },
    });
  },
};

export default reviewService;
