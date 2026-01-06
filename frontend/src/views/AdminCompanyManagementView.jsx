import React, { useEffect, useMemo, useState } from 'react';
import { adminListCompanies } from '../service/adminCompanyService';
import { loadAuth } from '../utils/authStorage';
import { setLastCompanyId } from '../utils/companyStorage';

export default function AdminCompanyManagementView({ onNavigate }) {
  const auth = useMemo(() => loadAuth(), []);
  const user = auth?.user;

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [page, setPage] = useState(null);

  const items = useMemo(() => {
    const content = page?.content;
    return Array.isArray(content) ? content : [];
  }, [page]);

  async function load() {
    setLoading(true);
    setError('');
    try {
      const data = await adminListCompanies({ page: 0, size: 50, sortBy: 'createdAt', sortDirection: 'desc' });
      setPage(data);
    } catch (e) {
      setError(e?.message || 'Không thể tải danh sách công ty.');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    if (auth?.token) load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

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

  return (
    <div className="min-h-[80vh] bg-gray-100 py-10">
      <div className="container mx-auto px-4">
        <div className="max-w-6xl mx-auto bg-white rounded-xl shadow-lg p-6">
          <div className="flex items-start justify-between gap-3">
            <div>
              <h1 className="text-2xl font-extrabold text-gray-900">Quản lý công ty cứu hộ</h1>
              <p className="text-sm text-gray-600 mt-1">Danh sách công ty (bấm để xem trang cá nhân công ty).</p>
            </div>
            <div className="flex items-center gap-2">
              <button
                type="button"
                onClick={load}
                className="bg-gray-200 text-gray-900 font-semibold px-4 py-2 rounded hover:bg-gray-300 text-sm"
                disabled={loading}
              >
                Tải lại
              </button>
              <button type="button" onClick={() => onNavigate('home')} className="bg-blue-900 text-white font-bold px-4 py-2 rounded hover:bg-blue-800">Quay lại</button>
            </div>
          </div>

          {error ? <div className="mt-4 text-sm text-red-700 bg-red-50 border border-red-200 rounded-md p-3">{error}</div> : null}
          {loading ? <div className="mt-4 text-sm text-gray-700">Đang tải...</div> : null}

          {!loading && items.length === 0 ? (
            <div className="mt-6 text-sm text-gray-600">Không có công ty nào.</div>
          ) : null}

          {items.length > 0 ? (
            <div className="mt-6 overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="bg-gray-50 border-b border-gray-200">
                    <th className="px-4 py-2 text-left">ID</th>
                    <th className="px-4 py-2 text-left">Tên công ty</th>
                    <th className="px-4 py-2 text-left">SĐT</th>
                    <th className="px-4 py-2 text-left">Email</th>
                    <th className="px-4 py-2 text-left">Trạng thái</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-200">
                  {items.map((c) => (
                    <tr
                      key={c?.id}
                      className="hover:bg-gray-50 cursor-pointer"
                      onClick={() => {
                        if (!c?.id) return;
                        setLastCompanyId(c.id);
                        onNavigate?.('companyProfile');
                      }}
                      role="button"
                      tabIndex={0}
                      onKeyDown={(e) => {
                        if (e.key === 'Enter' || e.key === ' ') {
                          e.preventDefault();
                          if (!c?.id) return;
                          setLastCompanyId(c.id);
                          onNavigate?.('companyProfile');
                        }
                      }}
                    >
                      <td className="px-4 py-2">{c?.id}</td>
                      <td className="px-4 py-2 font-semibold text-blue-900">{c?.name || '(không có tên)'}</td>
                      <td className="px-4 py-2">{c?.phone || '-'}</td>
                      <td className="px-4 py-2">{c?.email || '-'}</td>
                      <td className="px-4 py-2">{c?.profileStatus || '-'}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : null}
        </div>
      </div>
    </div>
  );
}
