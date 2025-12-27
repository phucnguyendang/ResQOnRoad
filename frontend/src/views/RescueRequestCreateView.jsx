import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { createRescueRequestMock } from '../service/rescueRequestService';
import { searchNearbyCompanies } from '../service/companyService';

function readFileAsDataUrl(file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => resolve(String(reader.result || ''));
    reader.onerror = () => reject(new Error('Không thể đọc file ảnh.'));
    reader.readAsDataURL(file);
  });
}

const RescueRequestCreateView = ({ onNavigate }) => {
  const [companyId, setCompanyId] = useState('');
  const [selectedCompany, setSelectedCompany] = useState(null);
  const [companyModalOpen, setCompanyModalOpen] = useState(false);
  const [companies, setCompanies] = useState([]);
  const [companiesLoading, setCompaniesLoading] = useState(false);
  const [companiesError, setCompaniesError] = useState(null);

  const [incidentDesc, setIncidentDesc] = useState('');
  const [address, setAddress] = useState('');
  const [latitude, setLatitude] = useState('');
  const [longitude, setLongitude] = useState('');

  const [imagesBase64, setImagesBase64] = useState([]);
  const [imagesLoading, setImagesLoading] = useState(false);

  const [locating, setLocating] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);
  const [created, setCreated] = useState(null);

  const canGetLocation = useMemo(() => typeof navigator !== 'undefined' && !!navigator.geolocation, []);

  const hasCoords = useMemo(() => {
    if (latitude === '' || longitude === '') return false;
    const lat = Number(latitude);
    const lng = Number(longitude);
    return Number.isFinite(lat) && Number.isFinite(lng);
  }, [latitude, longitude]);

  const handleGetLocation = useCallback(() => {
    setError(null);
    setCreated(null);

    if (!canGetLocation) {
      setError('Trình duyệt không hỗ trợ lấy vị trí (Geolocation).');
      return;
    }

    setLocating(true);
    navigator.geolocation.getCurrentPosition(
      (pos) => {
        setLatitude(String(pos.coords.latitude));
        setLongitude(String(pos.coords.longitude));
        setSelectedCompany(null);
        setCompanyId('');
        setCompanies([]);
        setCompaniesError(null);
        setLocating(false);
      },
      (err) => {
        const online = typeof navigator !== 'undefined' ? navigator.onLine : true;
        const base = err?.message || 'Không thể lấy vị trí hiện tại.';
        const hint = online
          ? 'Vui lòng bật GPS, cấp quyền truy cập vị trí cho website và thử lại.'
          : 'Vui lòng kiểm tra kết nối Internet (Wi‑Fi/4G) rồi thử lại.';
        setError(`Lỗi GPS: ${base}. ${hint}`);
        setLocating(false);
      },
      {
        enableHighAccuracy: true,
        timeout: 10000,
        maximumAge: 0,
      }
    );
  }, [canGetLocation]);

  // Auto-request GPS when opening the page
  useEffect(() => {
    handleGetLocation();
  }, [handleGetLocation]);

  const handleImagesChange = async (e) => {
    const files = Array.from(e.target.files || []);
    if (files.length === 0) {
      setImagesBase64([]);
      return;
    }

    setImagesLoading(true);
    setError(null);
    try {
      const images = files.filter((f) => (f.type || '').startsWith('image/'));
      const dataUrls = await Promise.all(images.map(readFileAsDataUrl));
      setImagesBase64(dataUrls.filter(Boolean));
    } catch (err) {
      setImagesBase64([]);
      setError(err?.message || 'Upload ảnh thất bại');
    } finally {
      setImagesLoading(false);
    }
  };

  const handleOpenCompanyModal = async () => {
    setError(null);
    setCreated(null);

    if (!hasCoords) {
      setError('Vui lòng lấy tọa độ GPS trước khi chọn công ty cứu hộ.');
      return;
    }

    setCompanyModalOpen(true);
    setCompaniesError(null);

    // Fetch each time the user opens (simple + consistent with spec)
    setCompaniesLoading(true);
    try {
      const page = await searchNearbyCompanies({
        lat: Number(latitude),
        lng: Number(longitude),
        maxDistance: 50.0,
        page: 0,
        size: 20,
      });
      const list = Array.isArray(page?.content) ? page.content : [];
      setCompanies(list);
    } catch (err) {
      setCompanies([]);
      setCompaniesError(err?.message || 'Không thể tải danh sách công ty cứu hộ');
    } finally {
      setCompaniesLoading(false);
    }
  };

  const handleSelectCompany = (company) => {
    if (!company) return;
    setSelectedCompany(company);
    setCompanyId(String(company.id));
    setCompanyModalOpen(false);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    setSubmitting(true);
    setError(null);
    setCreated(null);

    try {
      if (!address.trim()) {
        throw new Error('Vui lòng nhập địa chỉ cụ thể.');
      }

      if (!companyId) {
        throw new Error('Vui lòng chọn công ty cứu hộ.');
      }

      const result = await createRescueRequestMock({
        company_id: companyId,
        incident_desc: incidentDesc,
        location_address: address,
        latitude,
        longitude,
        images_base64: imagesBase64,
      });

      setCreated(result);
    } catch (err) {
      setError(err?.message || 'Gửi yêu cầu thất bại');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="min-h-[80vh] bg-gray-100 py-10">
      <div className="container mx-auto px-4">
        <div className="max-w-2xl mx-auto bg-white rounded-xl shadow-lg p-6">
          <h1 className="text-2xl font-extrabold text-gray-900">Gửi yêu cầu cứu hộ (UC201)</h1>
          <p className="text-sm text-gray-600 mt-1">Backend tạo yêu cầu chưa hoàn thiện → frontend dùng mock API để giả lập gửi thành công.</p>

          <form className="mt-6 space-y-4" onSubmit={handleSubmit}>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Công ty cứu hộ <span className="text-red-600">*</span></label>
              <button
                type="button"
                onClick={handleOpenCompanyModal}
                disabled={!hasCoords}
                className="w-full text-left px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500 bg-white disabled:opacity-60"
              >
                {selectedCompany ? (
                  <div className="flex items-center justify-between">
                    <div>
                      <div className="font-semibold text-gray-900">{selectedCompany.name}</div>
                      <div className="text-xs text-gray-600">
                        Đánh giá: {selectedCompany.averageRating ?? 'N/A'} ({selectedCompany.totalReviews ?? 0} lượt)
                        {selectedCompany.distance != null ? ` • ${Number(selectedCompany.distance).toFixed(1)} km` : ''}
                      </div>
                    </div>
                    <div className="text-sm font-bold text-blue-900">Đổi</div>
                  </div>
                ) : (
                  <div className="text-gray-700">
                    {hasCoords ? 'Bấm để chọn công ty cứu hộ gần bạn' : 'Vui lòng lấy GPS trước'}
                  </div>
                )}
              </button>
              <p className="text-xs text-gray-500 mt-1">Danh sách lấy từ backend theo vị trí (GET /api/companies/search).</p>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Mô tả sự cố (incident_desc)</label>
              <textarea
                value={incidentDesc}
                onChange={(e) => setIncidentDesc(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                rows={4}
                placeholder="Ví dụ: Xe bị thủng lốp, không có lốp dự phòng..."
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Địa chỉ cụ thể (location_address) <span className="text-red-600">*</span></label>
              <input
                type="text"
                value={address}
                onChange={(e) => setAddress(e.target.value)}
                required
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                placeholder="Ví dụ: Số 1 Đại Cồ Việt, Hai Bà Trưng, Hà Nội"
              />
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Latitude</label>
                <input
                  type="number"
                  step="any"
                  value={latitude}
                  onChange={(e) => setLatitude(e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                  placeholder="21.0285"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Longitude</label>
                <input
                  type="number"
                  step="any"
                  value={longitude}
                  onChange={(e) => setLongitude(e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                  placeholder="105.8542"
                />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Hình ảnh mô tả (images_base64)</label>
              <input
                type="file"
                accept="image/*"
                multiple
                onChange={handleImagesChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-md bg-white"
              />
              <div className="text-xs text-gray-500 mt-1">
                {imagesLoading ? 'Đang xử lý ảnh...' : `Đã chọn: ${imagesBase64.length} ảnh`}
              </div>
            </div>

            <div className="flex items-center gap-3">
              <button
                type="button"
                onClick={handleGetLocation}
                disabled={locating}
                className="bg-blue-900 text-white font-bold px-4 py-2 rounded hover:bg-blue-800 disabled:opacity-60"
              >
                {locating ? 'Đang lấy GPS...' : 'Thử lại GPS'}
              </button>
              {!canGetLocation && (
                <span className="text-xs text-gray-500">Thiết bị/trình duyệt không hỗ trợ GPS.</span>
              )}
            </div>

            {error && (
              <div className="text-sm text-red-600 bg-red-50 border border-red-200 rounded-md p-2">
                {error}
              </div>
            )}

            <button
              type="submit"
              disabled={submitting}
              className="w-full bg-yellow-500 text-blue-900 font-extrabold py-2 rounded-md hover:bg-yellow-400 disabled:opacity-60"
            >
              {submitting ? 'Đang gửi...' : 'Gửi yêu cầu cứu hộ'}
            </button>
          </form>

          {created && (
            <div className="mt-6 bg-green-50 border border-green-200 rounded-lg p-4">
              <div className="font-bold text-green-800">Gửi yêu cầu thành công (mock)</div>
              <div className="text-sm text-green-800 mt-1">Mã yêu cầu: <span className="font-semibold">{created.id}</span></div>
              <div className="text-xs text-green-700 mt-2">Lưu ý: Vì tạo yêu cầu đang mock, backend có thể chưa có dữ liệu tương ứng khi bạn theo dõi.</div>
              <div className="mt-3 flex gap-3">
                <button
                  onClick={() => onNavigate('requestDetail')}
                  className="bg-blue-900 text-white font-bold px-4 py-2 rounded hover:bg-blue-800"
                >
                  Theo dõi yêu cầu
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

          {companyModalOpen && (
            <div className="fixed inset-0 z-50 flex items-center justify-center">
              <div
                className="absolute inset-0 bg-black/50"
                onClick={() => setCompanyModalOpen(false)}
              />

              <div className="relative bg-white w-full max-w-2xl mx-4 rounded-xl shadow-2xl p-4">
                <div className="flex items-center justify-between">
                  <div>
                    <div className="text-lg font-extrabold text-gray-900">Chọn công ty cứu hộ</div>
                    <div className="text-xs text-gray-600">Hiển thị danh sách công ty gần vị trí hiện tại của bạn.</div>
                  </div>
                  <button
                    onClick={() => setCompanyModalOpen(false)}
                    className="px-3 py-1 rounded bg-gray-200 hover:bg-gray-300 font-bold text-gray-900"
                  >
                    Đóng
                  </button>
                </div>

                <div className="mt-3">
                  {companiesLoading && (
                    <div className="text-sm text-gray-700">Đang tải danh sách công ty...</div>
                  )}
                  {companiesError && (
                    <div className="text-sm text-red-600 bg-red-50 border border-red-200 rounded-md p-2">
                      {companiesError}
                    </div>
                  )}

                  {!companiesLoading && !companiesError && companies.length === 0 && (
                    <div className="text-sm text-gray-700">Không có công ty phù hợp trong bán kính mặc định.</div>
                  )}

                  {!companiesLoading && companies.length > 0 && (
                    <div className="max-h-[60vh] overflow-auto border border-gray-200 rounded-lg">
                      {companies.map((c) => (
                        <button
                          key={c.id}
                          type="button"
                          onClick={() => handleSelectCompany(c)}
                          className="w-full text-left p-3 border-b border-gray-200 hover:bg-gray-50"
                        >
                          <div className="flex items-start justify-between gap-3">
                            <div>
                              <div className="font-bold text-gray-900">{c.name}</div>
                              <div className="text-xs text-gray-600 mt-1">
                                Đánh giá: {c.averageRating ?? 'N/A'} ({c.totalReviews ?? 0} lượt)
                                {c.distance != null ? ` • ${Number(c.distance).toFixed(1)} km` : ''}
                              </div>
                              {c.address && <div className="text-xs text-gray-500 mt-1">{c.address}</div>}
                            </div>
                            <div className="text-sm font-extrabold text-blue-900">Chọn</div>
                          </div>
                        </button>
                      ))}
                    </div>
                  )}
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default RescueRequestCreateView;
