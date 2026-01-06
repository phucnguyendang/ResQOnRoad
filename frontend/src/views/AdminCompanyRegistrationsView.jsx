import React, { useEffect, useMemo, useState } from 'react';
import {
  adminApproveCompanyRegistration,
  adminListCompanyRegistrations,
  adminRejectCompanyRegistration,
} from '../service/companyRegistrationService';
import { loadAuth } from '../utils/authStorage';

export default function AdminCompanyRegistrationsView({ onNavigate }) {
  const auth = useMemo(() => loadAuth(), []);
  const user = auth?.user;

  const [status, setStatus] = useState('PENDING');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [items, setItems] = useState([]);

  const [actionBusyId, setActionBusyId] = useState(null);

  const load = async () => {
    setLoading(true);
    setError(null);
    try {
      const page = await adminListCompanyRegistrations({ status, page: 0, size: 50 });
      const content = Array.isArray(page?.content) ? page.content : [];
      setItems(content);
    } catch (err) {
      const details = Array.isArray(err?.details) && err.details.length > 0 ? `: ${err.details.join(', ')}` : '';
      setError(`${err?.message || 'Không thể tải danh sách'}${details}`);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (auth?.token) load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [status]);

  if (!auth?.token) {
    return (
      <div className="min-h-[80vh] flex items-center justify-center bg-gray-100 py-12 px-4">
        <div className="max-w-lg w-full bg-white p-6 rounded-xl shadow-lg">
          <h1 className="text-xl font-extrabold text-gray-900">Bạn cần đăng nhập</h1>
          <p className="text-sm text-gray-700 mt-2">Vui lòng đăng nhập với quyền ADMIN.</p>
          <div className="mt-4">
            <button onClick={() => onNavigate('login')} className="bg-blue-900 text-white font-bold px-4 py-2 rounded hover:bg-blue-800">Đăng nhập</button>
          </div>
        </div>
      </div>
    );
  }

  if (user?.role !== 'ADMIN') {
    return (
      <div className="min-h-[80vh] flex items-center justify-center bg-gray-100 py-12 px-4">
        <div className="max-w-lg w-full bg-white p-6 rounded-xl shadow-lg">
          <h1 className="text-xl font-extrabold text-gray-900">Không có quyền</h1>
          <p className="text-sm text-gray-700 mt-2">Chỉ ADMIN mới truy cập trang này.</p>
          <div className="mt-4">
            <button onClick={() => onNavigate('home')} className="bg-blue-900 text-white font-bold px-4 py-2 rounded hover:bg-blue-800">Về trang chủ</button>
          </div>
        </div>
      </div>
    );
  }

  const doApprove = async (id) => {
    setActionBusyId(id);
    setError(null);
    try {
      await adminApproveCompanyRegistration(id);
      await load();
    } catch (err) {
      const details = Array.isArray(err?.details) && err.details.length > 0 ? `: ${err.details.join(', ')}` : '';
      setError(`${err?.message || 'Duyệt thất bại'}${details}`);
    } finally {
      setActionBusyId(null);
    }
  };

  const doReject = async (id) => {
    const reason = window.prompt('Nhập lý do từ chối:');
    if (!reason) return;

    setActionBusyId(id);
    setError(null);
    try {
      await adminRejectCompanyRegistration(id, reason);
      await load();
    } catch (err) {
      const details = Array.isArray(err?.details) && err.details.length > 0 ? `: ${err.details.join(', ')}` : '';
      setError(`${err?.message || 'Từ chối thất bại'}${details}`);
    } finally {
      setActionBusyId(null);
    }
  };

  return (
    <div className="min-h-[80vh] bg-gray-100 py-10">
      <div className="container mx-auto px-4">
        <div className="max-w-5xl mx-auto bg-white rounded-xl shadow-lg p-6">
          <div className="flex items-start justify-between gap-3">
            <div>
              <h1 className="text-2xl font-extrabold text-gray-900">Duyệt đăng ký công ty</h1>
              <p className="text-sm text-gray-600 mt-1">Quản lý các đơn đăng ký tạo công ty cứu hộ.</p>
            </div>
            <button type="button" onClick={() => onNavigate('home')} className="bg-blue-900 text-white font-bold px-4 py-2 rounded hover:bg-blue-800">Quay lại</button>
          </div>

          <div className="mt-4 flex items-center gap-3">
            <label className="text-sm font-semibold text-gray-700">Trạng thái</label>
            <select value={status} onChange={(e) => setStatus(e.target.value)} className="border border-gray-300 rounded-md px-3 py-2 text-sm">
              <option value="PENDING">PENDING</option>
              <option value="APPROVED">APPROVED</option>
              <option value="REJECTED">REJECTED</option>
            </select>
            <button onClick={load} className="bg-gray-200 text-gray-900 font-semibold px-4 py-2 rounded hover:bg-gray-300 text-sm">Tải lại</button>
          </div>

          {error && <div className="mt-4 text-sm text-red-700 bg-red-50 border border-red-200 rounded-md p-3">{error}</div>}
          {loading && <div className="mt-4 text-sm text-gray-700">Đang tải...</div>}

          {!loading && items.length === 0 && (
            <div className="mt-6 text-sm text-gray-600">Không có đơn nào.</div>
          )}

          {items.length > 0 && (
            <div className="mt-6 overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="bg-gray-50 border-b border-gray-200">
                    <th className="px-4 py-2 text-left">ID</th>
                    <th className="px-4 py-2 text-left">Tên công ty</th>
                    <th className="px-4 py-2 text-left">SĐT</th>
                    <th className="px-4 py-2 text-left">Địa chỉ</th>
                    <th className="px-4 py-2 text-left">Submitted</th>
                    <th className="px-4 py-2 text-left">Action</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-200">
                  {items.map((it) => {
                    const busy = actionBusyId === it.id;
                    return (
                      <tr key={it.id} className="hover:bg-gray-50">
                        <td className="px-4 py-2">{it.id}</td>
                        <td className="px-4 py-2 font-semibold">{it.name}</td>
                        <td className="px-4 py-2">{it.phone}</td>
                        <td className="px-4 py-2">{it.address}</td>
                        <td className="px-4 py-2">{it.submittedAt ? new Date(it.submittedAt).toLocaleString() : '-'}</td>
                        <td className="px-4 py-2">
                          {status === 'PENDING' ? (
                            <div className="flex gap-2">
                              <button disabled={busy} onClick={() => doApprove(it.id)} className="bg-green-600 text-white font-bold px-3 py-1 rounded hover:bg-green-700 disabled:opacity-60">Duyệt</button>
                              <button disabled={busy} onClick={() => doReject(it.id)} className="bg-red-600 text-white font-bold px-3 py-1 rounded hover:bg-red-700 disabled:opacity-60">Từ chối</button>
                            </div>
                          ) : (
                            <span className="text-xs text-gray-600">—</span>
                          )}
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
