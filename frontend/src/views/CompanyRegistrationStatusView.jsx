import React, { useEffect, useMemo, useState } from 'react';
import { getMyCompanyRegistration } from '../service/companyRegistrationService';
import { loadAuth } from '../utils/authStorage';

function badge(status) {
  const s = String(status || '').toUpperCase();
  if (s === 'APPROVED') return 'bg-green-100 text-green-800';
  if (s === 'REJECTED') return 'bg-red-100 text-red-800';
  return 'bg-yellow-100 text-yellow-800';
}

export default function CompanyRegistrationStatusView({ onNavigate }) {
  const auth = useMemo(() => loadAuth(), []);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [data, setData] = useState(null);

  useEffect(() => {
    let alive = true;

    async function run() {
      if (!auth?.token) {
        setError('Bạn cần đăng nhập để xem trạng thái.');
        return;
      }

      setLoading(true);
      setError(null);
      try {
        const res = await getMyCompanyRegistration();
        if (!alive) return;
        setData(res);
      } catch (err) {
        if (!alive) return;
        if (err?.status === 404) {
          setData(null);
          setError('Bạn chưa có đơn đăng ký nào.');
        } else {
          const details = Array.isArray(err?.details) && err.details.length > 0 ? `: ${err.details.join(', ')}` : '';
          setError(`${err?.message || 'Không thể tải trạng thái'}${details}`);
        }
      } finally {
        if (alive) setLoading(false);
      }
    }

    run();
    return () => {
      alive = false;
    };
  }, [auth?.token]);

  return (
    <div className="min-h-[80vh] bg-gray-100 py-10">
      <div className="container mx-auto px-4">
        <div className="max-w-2xl mx-auto bg-white rounded-xl shadow-lg p-6">
          <div className="flex items-start justify-between gap-3">
            <div>
              <h1 className="text-2xl font-extrabold text-gray-900">Trạng thái đăng ký công ty</h1>
              <p className="text-sm text-gray-600 mt-1">Theo dõi đơn đăng ký gần nhất của bạn.</p>
            </div>
            <button type="button" onClick={() => onNavigate('companyRegistration')} className="bg-blue-900 text-white font-bold px-4 py-2 rounded hover:bg-blue-800">Tạo/ gửi đơn</button>
          </div>

          {loading && <div className="mt-6 text-sm text-gray-700">Đang tải...</div>}

          {error && <div className="mt-6 text-sm text-red-700 bg-red-50 border border-red-200 rounded-md p-3">{error}</div>}

          {data && (
            <div className="mt-6 space-y-4">
              <div className="flex items-center justify-between">
                <div className={`inline-flex px-3 py-1 rounded-full text-xs font-bold ${badge(data.status)}`}>{data.status}</div>
                {data.submittedAt && <div className="text-xs text-gray-500">Gửi lúc: {new Date(data.submittedAt).toLocaleString()}</div>}
              </div>

              <div className="bg-gray-50 border border-gray-200 rounded-lg p-4 text-sm text-gray-800">
                <div className="font-bold text-gray-900">{data.name}</div>
                <div className="mt-1"><span className="font-semibold">Địa chỉ:</span> {data.address}</div>
                <div className="mt-1"><span className="font-semibold">Điện thoại:</span> {data.phone}</div>
                {data.email && <div className="mt-1"><span className="font-semibold">Email:</span> {data.email}</div>}
                <div className="mt-1"><span className="font-semibold">Tọa độ:</span> {data.latitude}, {data.longitude}</div>
                {data.serviceRadius != null && <div className="mt-1"><span className="font-semibold">Bán kính:</span> {data.serviceRadius} km</div>}
                {data.description && <div className="mt-2"><span className="font-semibold">Mô tả:</span> {data.description}</div>}
              </div>

              {data.status === 'REJECTED' && data.rejectionReason && (
                <div className="text-sm text-red-800 bg-red-50 border border-red-200 rounded-md p-3">
                  <div className="font-bold">Lý do từ chối</div>
                  <div className="mt-1">{data.rejectionReason}</div>
                </div>
              )}

              {data.status === 'APPROVED' && (
                <div className="text-sm text-green-800 bg-green-50 border border-green-200 rounded-md p-3">
                  <div className="font-bold">Đã được duyệt</div>
                  <div className="mt-1">Tài khoản sẽ chuyển sang COMPANY sau khi bạn đăng nhập lại.</div>
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
