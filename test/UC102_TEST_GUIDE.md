# UC102 Test Guide - Review and Feedback Feature

## Prerequisites
- Backend server running on `http://localhost:8080`
- Database initialized with test data
- Valid JWT token for authentication

## Test Scenarios

### Scenario 1: Create a Review (Positive Case)
**Use Case**: User completes a rescue request and wants to leave feedback

**Steps**:
1. User completes a rescue request (status = COMPLETED)
2. User goes to review page
3. User selects rating (1-5 stars)
4. User enters optional comment
5. User clicks "Gửi đánh giá"

**Expected Result**: 
- Review created in database
- Response: 201 Created with review details
- Message: "Gửi đánh giá thành công"

**API Call**:
```bash
curl -X POST http://localhost:8080/api/reviews \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "requestId": 1,
    "rating": 5,
    "comment": "Tới nhanh, xử lý chuyên nghiệp."
  }'
```

**Response (Success)**:
```json
{
  "status": 201,
  "message": "Gửi đánh giá thành công",
  "data": {
    "id": 1,
    "userName": "Nguyễn Văn A",
    "rating": 5,
    "comment": "Tới nhanh, xử lý chuyên nghiệp.",
    "isVerified": true,
    "createdAt": "2026-01-04T08:00:00Z"
  }
}
```

---

### Scenario 2: View Company Reviews
**Use Case**: User browses reviews of a rescue company

**Steps**:
1. User opens company detail page
2. User scrolls to "Reviews" section
3. System displays average rating
4. System displays paginated reviews

**Expected Result**:
- Shows average rating (e.g., 4.6/5)
- Shows list of verified reviews
- Pagination works (next/previous pages)

**API Calls**:

#### Get Company Rating:
```bash
curl -X GET http://localhost:8080/api/reviews/companies/1/rating
```

**Response**:
```json
{
  "message": "Lấy điểm đánh giá thành công",
  "data": {
    "company_id": 1,
    "rating_avg": 4.6
  }
}
```

#### Get Company Reviews (Page 1):
```bash
curl -X GET http://localhost:8080/api/reviews/companies/1/reviews?page=1&limit=10
```

**Response**:
```json
{
  "message": "Lấy danh sách đánh giá thành công",
  "data": {
    "items": [
      {
        "id": 1,
        "userName": "Nguyễn Văn A",
        "rating": 5,
        "comment": "Tới nhanh, xử lý chuyên nghiệp.",
        "isVerified": true,
        "createdAt": "2026-01-04T08:00:00Z"
      }
    ],
    "pagination": {
      "current_page": 1,
      "total_pages": 1,
      "total_items": 1
    }
  }
}
```

---

### Scenario 3: Update Review
**Use Case**: User updates their previous review

**Steps**:
1. User opens their review
2. User changes rating or comment
3. User submits

**Expected Result**:
- Previous review is updated
- Creation date remains same
- New data is reflected

**API Call** (Same as Create):
```bash
curl -X POST http://localhost:8080/api/reviews \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "requestId": 1,
    "rating": 4,
    "comment": "Tốt nhưng tốn thời gian một chút."
  }'
```

---

### Scenario 4: Error Cases

#### Case A: Incomplete Rescue Request
**Condition**: Try to review a request with status != COMPLETED

**API Call**:
```bash
curl -X POST http://localhost:8080/api/reviews \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "requestId": 2,
    "rating": 5,
    "comment": "Test"
  }'
```

**Expected Response (400)**:
```json
{
  "status": 400,
  "message": "Chỉ có thể đánh giá khi yêu cầu cứu hộ đã hoàn thành",
  "data": null
}
```

#### Case B: Invalid User (Not Request Owner)
**Condition**: User tries to review someone else's request

**Expected Response (403)**:
```json
{
  "status": 403,
  "message": "Bạn không có quyền đánh giá yêu cầu này",
  "data": null
}
```

#### Case C: Invalid Rating
**Condition**: Rating outside 1-5 range

**API Call**:
```bash
curl -X POST http://localhost:8080/api/reviews \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "requestId": 1,
    "rating": 10,
    "comment": "Test"
  }'
```

**Expected Response (400)**:
```json
{
  "status": 400,
  "message": "rating phải từ 1 đến 5",
  "data": null
}
```

---

### Scenario 5: Check If User Reviewed
**Use Case**: Frontend needs to show review button or not

**API Call**:
```bash
curl -X GET http://localhost:8080/api/reviews/check?companyId=1 \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

**Response (Already Reviewed)**:
```json
{
  "message": "Kiểm tra trạng thái đánh giá thành công",
  "data": {
    "company_id": 1,
    "has_reviewed": true
  }
}
```

**Response (Not Reviewed Yet)**:
```json
{
  "message": "Kiểm tra trạng thái đánh giá thành công",
  "data": {
    "company_id": 1,
    "has_reviewed": false
  }
}
```

---

### Scenario 6: Get User's Reviews
**Use Case**: User views their own review history

**API Call**:
```bash
curl -X GET http://localhost:8080/api/reviews/my-reviews \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

**Response**:
```json
{
  "message": "Lấy danh sách đánh giá của bạn thành công",
  "data": [
    {
      "id": 1,
      "userName": "Nguyễn Văn A",
      "rating": 5,
      "comment": "Tới nhanh, xử lý chuyên nghiệp.",
      "isVerified": true,
      "createdAt": "2026-01-04T08:00:00Z"
    }
  ]
}
```

---

## Frontend Component Tests

### ReviewForm Component
```jsx
import ReviewForm from './components/ReviewForm';

<ReviewForm 
  requestId={1}
  companyName="Cứu hộ Ba Đình"
  onSuccess={(data) => {
    console.log('Review submitted:', data);
    // Refresh company page or show success message
  }}
  onCancel={() => {
    console.log('User skipped review');
    // Close modal or navigate away
  }}
/>
```

### ReviewList Component
```jsx
import ReviewList from './components/ReviewList';

<ReviewList 
  companyId={1}
  companyName="Cứu hộ Ba Đình"
/>
```

---

## Database Verification

### Check Reviews in Database
```sql
-- View all reviews
SELECT * FROM reviews;

-- View reviews for specific company
SELECT r.*, a.full_name FROM reviews r
JOIN accounts a ON r.user_id = a.id
WHERE r.company_id = ?
ORDER BY r.created_at DESC;

-- View user's reviews
SELECT * FROM reviews
WHERE user_id = ?
ORDER BY created_at DESC;

-- Get average rating for company
SELECT AVG(rating) as avg_rating FROM reviews
WHERE company_id = ?;
```

---

## Integration Tests

### Test Flow 1: Complete Review Workflow
1. ✅ Create rescue request (UC201)
2. ✅ Update request status to COMPLETED (UC205)
3. ✅ Create review (UC102)
4. ✅ View company reviews (UC102)
5. ✅ Verify rating updated

### Test Flow 2: Admin Review Management
1. ✅ Admin views all reviews
2. ✅ Admin filters by company
3. ✅ Admin analyzes feedback
4. ✅ Admin uses insights for improvement

---

## Performance Tests

- Test with 100+ reviews per company
- Test pagination with limit=50
- Verify query response time < 500ms
- Test concurrent review submissions

---

## Notes
- All timestamps in UTC format
- Rating range: 1-5 only
- Comment max length: 1000 characters
- Reviews are immutable after creation (can only update, not delete)
- isVerified always true for user-submitted reviews
