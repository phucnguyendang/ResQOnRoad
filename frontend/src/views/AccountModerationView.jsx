import React, { useEffect, useMemo, useState } from 'react';
import { getAdminStats, getAllUsers, toggleUserLock } from '../service/adminService';

function AccountModerationView({ onNavigate }) {
  const [stats, setStats] = useState(null);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const lockedCount = useMemo(() => {
    return users.filter((u) => !!u?.locked).length;
  }, [users]);

  async function loadAll() {
    setError('');
    setLoading(true);
    try {
      const [statsData, usersData] = await Promise.all([getAdminStats(), getAllUsers()]);
      setStats(statsData || null);
      setUsers(Array.isArray(usersData) ? usersData : []);
    } catch (e) {
      setError(e?.message || 'Không thể tải dữ liệu kiểm duyệt tài khoản.');
      if (e?.status === 401) onNavigate?.('login');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadAll();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  async function handleToggleLock(userId, currentlyLocked) {
    const ok = window.confirm(currentlyLocked ? 'Mở khóa tài khoản này?' : 'Khóa tài khoản này?');
    if (!ok) return;

    setError('');
    try {
      await toggleUserLock(userId, !currentlyLocked);
      setUsers((prev) => prev.map((u) => (u.id === userId ? { ...u, locked: !currentlyLocked } : u)));
    } catch (e) {
      setError(e?.message || 'Không thể cập nhật trạng thái khóa.');
      if (e?.status === 401) onNavigate?.('login');
    }
  }

  return (
    <main className="min-h-[60vh] bg-gray-50 py-8">
      <div className="max-w-6xl mx-auto px-4">
        <div className="flex items-baseline justify-between gap-3 flex-wrap mb-6">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Kiểm duyệt tài khoản</h1>
            <p className="text-sm text-gray-600">Thống kê và khóa/mở khóa tài khoản người dùng.</p>
          </div>
          <button
            type="button"
            onClick={loadAll}
            disabled={loading}
            className="text-sm bg-blue-900 text-white px-3 py-2 rounded hover:bg-blue-800 disabled:opacity-60"
          >
            {loading ? 'Đang tải...' : 'Làm mới'}
          </button>
        </div>

        {error ? (
          <div className="bg-white rounded shadow-sm p-4 text-red-600 mb-4">{error}</div>
        ) : null}

        <section className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
          <div className="bg-white rounded shadow-sm p-4">
            <div className="text-sm text-gray-600">Tài khoản đăng ký</div>
            <div className="text-2xl font-bold text-gray-900 mt-1">{stats?.totalUsers ?? '-'}</div>
          </div>
          <div className="bg-white rounded shadow-sm p-4">
            <div className="text-sm text-gray-600">Bài đăng</div>
            <div className="text-2xl font-bold text-gray-900 mt-1">{stats?.totalPosts ?? '-'}</div>
          </div>
          <div className="bg-white rounded shadow-sm p-4">
            <div className="text-sm text-gray-600">Bình luận</div>
            <div className="text-2xl font-bold text-gray-900 mt-1">{stats?.totalComments ?? '-'}</div>
          </div>
          <div className="bg-white rounded shadow-sm p-4">
            <div className="text-sm text-gray-600">Tài khoản bị khóa</div>
            <div className="text-2xl font-bold text-gray-900 mt-1">{stats?.lockedAccounts ?? lockedCount}</div>
          </div>
        </section>

        <section className="bg-white rounded shadow-sm overflow-hidden">
          <div className="p-4 border-b">
            <div className="font-semibold text-gray-900">Danh sách tài khoản</div>
            <div className="text-sm text-gray-600">Admin có thể khóa/mở khóa mọi tài khoản.</div>
          </div>

          {loading && users.length === 0 ? (
            <div className="p-4 text-gray-700">Đang tải...</div>
          ) : users.length === 0 ? (
            <div className="p-4 text-gray-700">Chưa có dữ liệu tài khoản.</div>
          ) : (
            <div className="overflow-x-auto">
              <table className="min-w-full text-sm">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="text-left px-4 py-3 font-semibold text-gray-700">Username</th>
                    <th className="text-left px-4 py-3 font-semibold text-gray-700">Email</th>
                    <th className="text-left px-4 py-3 font-semibold text-gray-700">Role</th>
                    <th className="text-left px-4 py-3 font-semibold text-gray-700">Trạng thái</th>
                    <th className="text-left px-4 py-3 font-semibold text-gray-700">Hành động</th>
                  </tr>
                </thead>
                <tbody className="divide-y">
                  {users.map((u) => {
                    const isLocked = !!u?.locked;
                    return (
                      <tr key={u?.id} className="hover:bg-gray-50">
                        <td className="px-4 py-3 font-medium text-gray-900">{u?.username}</td>
                        <td className="px-4 py-3 text-gray-700">{u?.email || '-'}</td>
                        <td className="px-4 py-3 text-gray-700">{u?.role || '-'}</td>
                        <td className="px-4 py-3">
                          <span
                            className={`inline-flex px-2 py-1 rounded text-xs ${isLocked ? 'bg-red-100 text-red-700' : 'bg-green-100 text-green-700'}`}
                          >
                            {isLocked ? 'Đã khóa' : 'Hoạt động'}
                          </span>
                        </td>
                        <td className="px-4 py-3">
                          <button
                            type="button"
                            onClick={() => handleToggleLock(u.id, isLocked)}
                            className={`text-xs px-3 py-2 rounded border ${isLocked ? 'border-green-700 text-green-700 hover:bg-green-50' : 'border-red-700 text-red-700 hover:bg-red-50'}`}
                            disabled={loading}
                          >
                            {isLocked ? 'Mở khóa' : 'Khóa'}
                          </button>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
        </section>
      </div>
    </main>
  );
}

export default AccountModerationView;
