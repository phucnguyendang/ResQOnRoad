# ✅ UC102 Implementation Complete - Testing Guide

## 📋 What's Been Implemented

### Backend Components
| Component | File | Status | Lines |
|-----------|------|--------|-------|
| Controller | `ReviewController.java` | ✅ Complete | 200+ |
| Service Interface | `ReviewService.java` | ✅ Complete | 45 |
| Service Impl | `ReviewServiceImpl.java` | ✅ Complete | 150+ |
| DTO Request | `CreateReviewRequest.java` | ✅ Complete | 65 |
| Repository | `ReviewRepository.java` | ✅ Updated | 25 |

### Frontend Components
| Component | File | Status | Lines |
|-----------|------|--------|-------|
| Review Form | `ReviewForm.jsx` | ✅ Complete | 150+ |
| API Service | `reviewService.js` | ✅ Complete | 60 |

### API Endpoints (5 Total)
1. ✅ `POST /api/reviews` - Create/Update review
2. ✅ `GET /api/companies/{companyId}/reviews` - List reviews
3. ✅ `GET /api/companies/{companyId}/rating` - Average rating
4. ✅ `GET /api/reviews/my-reviews` - User's reviews
5. ✅ `GET /api/reviews/check` - Review status check

---

## 🧪 How to Test UC102

### Quick Test (5 minutes)

#### 1. Test Create Review API
```bash
curl -X POST http://localhost:8080/api/reviews \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "requestId": 1,
    "rating": 5,
    "comment": "Excellent service!"
  }'
```

**Expected Response (201 Created):**
```json
{
  "status": 201,
  "message": "Gửi đánh giá thành công",
  "data": {
    "id": 1,
    "userName": "Nguyễn Văn A",
    "rating": 5,
    "comment": "Excellent service!",
    "isVerified": true,
    "createdAt": "2026-01-04T..."
  }
}
```

#### 2. Test Get Company Reviews
```bash
curl -X GET "http://localhost:8080/api/reviews/companies/1/reviews?page=1&limit=10"
```

**Expected Response (200 OK):**
```json
{
  "message": "Lấy danh sách đánh giá thành công",
  "data": {
    "items": [...],
    "pagination": {
      "current_page": 1,
      "total_pages": 1,
      "total_items": 1
    }
  }
}
```

#### 3. Test Get Average Rating
```bash
curl -X GET "http://localhost:8080/api/companies/1/rating"
```

**Expected Response (200 OK):**
```json
{
  "message": "Lấy điểm đánh giá thành công",
  "data": {
    "company_id": 1,
    "rating_avg": 5.0
  }
}
```

### Complete Test Guide

**📖 Full Testing Instructions:** See [UC102_TEST_GUIDE.md](./UC102_TEST_GUIDE.md)
- 6 detailed test scenarios
- Expected results for each
- Database verification queries
- Integration test workflows

### Postman Testing

**📮 Import Collection:** Use [UC102_Postman_Collection.json](./UC102_Postman_Collection.json)

1. Open Postman
2. Click "Import"
3. Select `test/UC102_Postman_Collection.json`
4. Update variables:
   - `base_url`: `http://localhost:8080/api`
   - `token`: Your JWT token
   - `company_id`: Test company ID
5. Run collection

### Automated Test Script

```bash
chmod +x test/test-uc102.sh
./test/test-uc102.sh
```

---

## ✨ Features

### For Users
- ⭐ **Rate Service** (1-5 stars)
- 💬 **Leave Comments** (optional, up to 1000 chars)
- 📝 **Update Reviews** (edit rating/comment)
- 👀 **View Company Ratings** (average + all reviews)
- ✓ **Skip Feedback** (optional, not required)

### For Admins
- 📊 **View All Reviews** (paginated)
- 🔍 **Filter by Company**
- 📈 **See Trends** (ratings over time)
- 📋 **Analyze Feedback** (identify improvements)
- 🏆 **Rank Companies** (by rating)

### Data Validation
- ✅ Rating must be 1-5
- ✅ Comment max 1000 characters
- ✅ Request must be COMPLETED
- ✅ User must own the request
- ✅ Database constraints enforced

---

## 📊 Test Checklist

### Backend Tests
- [ ] Create review successfully
- [ ] Update existing review
- [ ] Reject incomplete requests
- [ ] Reject unauthorized users
- [ ] Validate rating range
- [ ] Calculate average correctly
- [ ] Paginate results

### API Tests
- [ ] `POST /api/reviews` returns 201
- [ ] `GET .../reviews` returns 200
- [ ] `GET .../rating` returns correct value
- [ ] `GET .../my-reviews` returns user's only
- [ ] `GET .../check` returns boolean
- [ ] Error responses with correct codes
- [ ] Authorization working

### Frontend Tests
- [ ] ReviewForm renders correctly
- [ ] Star rating clickable (1-5)
- [ ] Comment textarea works
- [ ] Submit button calls API
- [ ] Success message appears
- [ ] Error message shows
- [ ] Loading state visible
- [ ] Skip button works

---

## 🚀 Files to Review

### Implementation Files
```
backend/src/main/java/com/rescue/system/
├── controller/ReviewController.java        (200+ lines)
├── service/ReviewService.java             (45 lines)
├── service/impl/ReviewServiceImpl.java    (150+ lines)
└── dto/request/CreateReviewRequest.java   (65 lines)

frontend/src/
├── components/ReviewForm.jsx              (150+ lines)
└── service/reviewService.js              (60 lines)
```

### Test Files
```
test/
├── UC102_TEST_GUIDE.md                   (Complete scenarios)
├── UC102_TEST_SUMMARY.md                 (Checklist & results)
├── UC102_Postman_Collection.json         (API tests)
└── test-uc102.sh                         (Automation script)
```

### Documentation
```
api_docs.md                               (API specs updated)
```

---

## 🐛 Debugging Tips

### If API Returns 400 Bad Request
Check:
- ✅ Rating is 1-5
- ✅ RequestId exists
- ✅ Request status is COMPLETED
- ✅ You own the request

### If API Returns 401 Unauthorized
Check:
- ✅ JWT token is valid
- ✅ Token not expired
- ✅ Authorization header format: `Bearer {token}`

### If API Returns 403 Forbidden
Check:
- ✅ You are the request owner
- ✅ You have USER role

### If Frontend Component Doesn't Load
Check:
- ✅ reviewService.js imported correctly
- ✅ API client configured
- ✅ Base URL correct
- ✅ CORS enabled on backend

---

## 📞 Common Issues & Solutions

| Issue | Solution |
|-------|----------|
| 404 Not Found | Request ID doesn't exist - create request first |
| Invalid Rating | Rating must be 1-5, not 0 or 10 |
| Permission Denied | You must be the request creator |
| Request Not Complete | Request status must be COMPLETED |
| Token Expired | Get new JWT token from login |
| Comment Too Long | Maximum 1000 characters |
| Empty Response | Check if reviews exist for company |

---

## 📈 Next Steps

1. **Run Tests** - Execute test script and verify all pass
2. **Check Database** - Verify reviews stored correctly
3. **Test Frontend** - Run React component tests
4. **Integration Test** - Complete workflow from UC201 to UC102
5. **Load Test** - Test with 100+ reviews
6. **Production Ready** - Deploy to production

---

## 📚 Additional Resources

| Resource | Location |
|----------|----------|
| API Documentation | `api_docs.md` (Section 3.2) |
| Test Guide | `test/UC102_TEST_GUIDE.md` |
| Test Summary | `test/UC102_TEST_SUMMARY.md` |
| Postman Collection | `test/UC102_Postman_Collection.json` |
| Implementation Plan | `implementation_plan.md` |

---

## ✅ Verification Checklist

Before marking UC102 as complete:

- [ ] All 5 API endpoints created
- [ ] ReviewController compiles without errors
- [ ] ReviewService implemented correctly
- [ ] CreateReviewRequest validation works
- [ ] ReviewRepository has required methods
- [ ] Frontend ReviewForm component works
- [ ] reviewService.js API calls work
- [ ] All test cases pass
- [ ] Database schema verified
- [ ] Error handling tested
- [ ] Documentation complete
- [ ] Code committed to git

---

## 🎯 Summary

**UC102: Review and Feedback** has been successfully implemented with:
- ✅ 5 API endpoints
- ✅ Complete backend logic
- ✅ React components
- ✅ Comprehensive testing suite
- ✅ Full documentation

**Status: READY FOR TESTING** 🚀

Start with the Quick Test above, then refer to UC102_TEST_GUIDE.md for complete scenarios.
