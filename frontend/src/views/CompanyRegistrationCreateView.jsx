import React, { useCallback, useMemo, useState } from 'react';
import { createCompanyRegistration } from '../service/companyRegistrationService';
import { loadAuth } from '../utils/authStorage';

export default function CompanyRegistrationCreateView({ onNavigate }) {
  const auth = useMemo(() => loadAuth(), []);
  const user = auth?.user;

  const [locating, setLocating] = useState(false);
  const canGetLocation = useMemo(() => typeof navigator !== 'undefined' && !!navigator.geolocation, []);

  const [form, setForm] = useState({
    name: '',
    address: '',
    phone: '',
    email: '',
    latitude: '',
    longitude: '',
    serviceRadius: '50',
    taxCode: '',
    hotline: '',
    operatingHours: '',
    businessLicense: '',
    licenseDocumentUrl: '',
    description: '',
  });

  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

  if (!auth?.token) {
    return (
      <div className="min-h-[80vh] flex items-center justify-center bg-gray-100 py-12 px-4">
        <div className="max-w-lg w-full bg-white p-6 rounded-xl shadow-lg">
          <h1 className="text-xl font-extrabold text-gray-900">Bạn cần đăng nhập</h1>
          <p className="text-sm text-gray-700 mt-2">Vui lòng đăng nhập để gửi đơn đăng ký công ty.</p>
          <div className="mt-4 flex gap-2">
            <button onClick={() => onNavigate('login')} className="bg-blue-900 text-white font-bold px-4 py-2 rounded hover:bg-blue-800">Đăng nhập</button>
            <button onClick={() => onNavigate('home')} className="bg-gray-200 text-gray-900 font-semibold px-4 py-2 rounded hover:bg-gray-300">Về trang chủ</button>
          </div>
        </div>
      </div>
    );
  }

  if (user?.role && user.role !== 'USER') {
    return (
      <div className="min-h-[80vh] flex items-center justify-center bg-gray-100 py-12 px-4">
        <div className="max-w-lg w-full bg-white p-6 rounded-xl shadow-lg">
          <h1 className="text-xl font-extrabold text-gray-900">Không thể gửi đơn</h1>
          <p className="text-sm text-gray-700 mt-2">Chỉ tài khoản USER mới có thể gửi đăng ký công ty.</p>
          <div className="mt-4">
            <button onClick={() => onNavigate('home')} className="bg-blue-900 text-white font-bold px-4 py-2 rounded hover:bg-blue-800">Về trang chủ</button>
          </div>
        </div>
      </div>
    );
  }

  const setField = (k) => (e) => setForm((prev) => ({ ...prev, [k]: e.target.value }));

  const handleGetLocation = useCallback(() => {
    setError(null);

    if (!canGetLocation) {
      setError('Trình duyệt không hỗ trợ lấy vị trí (Geolocation). Vui lòng nhập tọa độ thủ công.');
      return;
    }

    setLocating(true);
    const timeoutId = setTimeout(() => {
      setLocating(false);
      setError('Hết thời gian chờ GPS (30s). Vui lòng kiểm tra: 1) Bật GPS/định vị, 2) Cấp quyền truy cập vị trí cho website, 3) Kết nối mạng ổn định.');
    }, 30000);

    navigator.geolocation.getCurrentPosition(
      (pos) => {
        clearTimeout(timeoutId);
        setForm((prev) => ({
          ...prev,
          latitude: String(pos.coords.latitude),
          longitude: String(pos.coords.longitude),
        }));
        setLocating(false);
        setError(null);
      },
      (err) => {
        clearTimeout(timeoutId);
        setLocating(false);

        let message = '';
        if (err.code === 1) {
          message = 'GPS bị từ chối: Vui lòng vào Cài đặt > Quyền riêng tư > Vị trí, cấp quyền cho trình duyệt.';
        } else if (err.code === 2) {
          message = 'Không thể lấy vị trí: Bật GPS/định vị trên thiết bị, đảm bảo có tín hiệu, rồi thử lại.';
        } else if (err.code === 3) {
          message = 'Hết thời gian chờ GPS: Tín hiệu GPS yếu hoặc kết nối mạng chậm. Thử lại ở ngoài trời.';
        } else {
          message = err?.message || 'Lỗi GPS không xác định';
        }
        setError(`Lỗi GPS: ${message}`);
      },
      {
        enableHighAccuracy: false,
        timeout: 30000,
        maximumAge: 0,
      }
    );
  }, [canGetLocation]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError(null);

    try {
      const payload = {
        name: form.name,
        address: form.address,
        phone: form.phone,
        email: form.email || null,
        latitude: Number(form.latitude),
        longitude: Number(form.longitude),
        serviceRadius: form.serviceRadius !== '' ? Number(form.serviceRadius) : null,
        taxCode: form.taxCode || null,
        hotline: form.hotline || null,
        operatingHours: form.operatingHours || null,
        businessLicense: form.businessLicense || null,
        licenseDocumentUrl: form.licenseDocumentUrl || null,
        description: form.description || null,
      };

      await createCompanyRegistration(payload);
      onNavigate('companyRegistrationStatus');
    } catch (err) {
      const details = Array.isArray(err?.details) && err.details.length > 0 ? `: ${err.details.join(', ')}` : '';
      setError(`${err?.message || 'Gửi đơn thất bại'}${details}`);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="min-h-[80vh] bg-gray-100 py-10">
      <div className="container mx-auto px-4">
        <div className="max-w-2xl mx-auto bg-white rounded-xl shadow-lg p-6">
          <div className="flex items-start justify-between gap-3">
            <div>
              <h1 className="text-2xl font-extrabold text-gray-900">Đăng ký tạo công ty cứu hộ</h1>
              <p className="text-sm text-gray-600 mt-1">Gửi thông tin để admin xét duyệt. Sau khi duyệt, công ty sẽ xuất hiện trong tìm kiếm.</p>
            </div>
            <button type="button" onClick={() => onNavigate('home')} className="bg-blue-900 text-white font-bold px-4 py-2 rounded hover:bg-blue-800">Quay lại</button>
          </div>

          {error && <div className="mt-4 text-sm text-red-700 bg-red-50 border border-red-200 rounded-md p-3">{error}</div>}

          <form className="mt-6 space-y-4" onSubmit={handleSubmit}>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <input className="w-full px-3 py-2 border border-gray-300 rounded-md" placeholder="Tên công ty *" value={form.name} onChange={setField('name')} required />
              <input className="w-full px-3 py-2 border border-gray-300 rounded-md" placeholder="Số điện thoại *" value={form.phone} onChange={setField('phone')} required />
              <input className="md:col-span-2 w-full px-3 py-2 border border-gray-300 rounded-md" placeholder="Địa chỉ *" value={form.address} onChange={setField('address')} required />
              <input className="md:col-span-2 w-full px-3 py-2 border border-gray-300 rounded-md" placeholder="Email" value={form.email} onChange={setField('email')} />

              <div className="md:col-span-2 flex items-center gap-3 flex-wrap">
                <button
                  type="button"
                  onClick={handleGetLocation}
                  disabled={locating}
                  className="bg-blue-900 text-white font-bold px-4 py-2 rounded hover:bg-blue-800 disabled:opacity-60"
                >
                  {locating ? 'Đang lấy GPS...' : '📍 Lấy GPS hiện tại'}
                </button>
                {!canGetLocation && (
                  <span className="text-xs text-orange-600 bg-orange-50 px-2 py-1 rounded">⚠️ Thiết bị không hỗ trợ GPS → nhập tọa độ thủ công</span>
                )}
              </div>

              <input className="w-full px-3 py-2 border border-gray-300 rounded-md" placeholder="Vĩ độ (latitude) *" value={form.latitude} onChange={setField('latitude')} required />
              <input className="w-full px-3 py-2 border border-gray-300 rounded-md" placeholder="Kinh độ (longitude) *" value={form.longitude} onChange={setField('longitude')} required />

              <input className="w-full px-3 py-2 border border-gray-300 rounded-md" placeholder="Bán kính hoạt động (km)" value={form.serviceRadius} onChange={setField('serviceRadius')} />
              <input className="w-full px-3 py-2 border border-gray-300 rounded-md" placeholder="Hotline" value={form.hotline} onChange={setField('hotline')} />

              <input className="w-full px-3 py-2 border border-gray-300 rounded-md" placeholder="Mã số thuế" value={form.taxCode} onChange={setField('taxCode')} />
              <input className="w-full px-3 py-2 border border-gray-300 rounded-md" placeholder="Giờ hoạt động" value={form.operatingHours} onChange={setField('operatingHours')} />

              <input className="w-full px-3 py-2 border border-gray-300 rounded-md" placeholder="Giấy phép kinh doanh" value={form.businessLicense} onChange={setField('businessLicense')} />
              <input className="w-full px-3 py-2 border border-gray-300 rounded-md" placeholder="Link tài liệu giấy phép" value={form.licenseDocumentUrl} onChange={setField('licenseDocumentUrl')} />

              <textarea className="md:col-span-2 w-full px-3 py-2 border border-gray-300 rounded-md" placeholder="Mô tả" value={form.description} onChange={setField('description')} rows={3} />
            </div>

            <div className="flex items-center justify-between gap-3 pt-2">
              <button type="button" onClick={() => onNavigate('companyRegistrationStatus')} className="text-sm font-semibold text-blue-900 hover:underline">
                Xem trạng thái đơn của tôi
              </button>
              <button type="submit" disabled={submitting} className="bg-yellow-500 text-blue-900 font-bold px-5 py-2 rounded hover:bg-yellow-400 disabled:opacity-60">
                {submitting ? 'Đang gửi...' : 'Gửi đơn'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
