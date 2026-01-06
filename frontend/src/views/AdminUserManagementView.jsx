import React, { useState, useEffect } from 'react';
import { Lock, Unlock, RefreshCw, AlertCircle, CheckCircle } from 'lucide-react';
import { getAllUsers, toggleUserLock } from '../service/adminService';

const AdminUserManagementView = ({ onNavigate }) => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);
  const [actionInProgress, setActionInProgress] = useState(null);
  const [filterRole, setFilterRole] = useState('');
  const [filterStatus, setFilterStatus] = useState('');
  const [searchTerm, setSearchTerm] = useState('');

  // Load users on mount
  useEffect(() => {
    loadUsers();
  }, []);

  const loadUsers = async () => {
    try {
      setLoading(true);
      setError(null);
      setSuccess(null);
      const data = await getAllUsers();
      setUsers(data);
    } catch (err) {
      setError(err.message || 'Failed to load users');
      console.error('Error loading users:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleToggleLock = async (userId, currentlyLocked) => {
    try {
      setActionInProgress(userId);
      setError(null);
      setSuccess(null);

      const newLockStatus = !currentlyLocked;
      await toggleUserLock(userId, newLockStatus);

      // Update local state
      setUsers(users.map(user =>
        user.id === userId
          ? { ...user, status: newLockStatus ? 'locked' : 'active' }
          : user
      ));

      setSuccess(
        newLockStatus
          ? 'User account locked successfully'
          : 'User account unlocked successfully'
      );

      // Clear success message after 3 seconds
      setTimeout(() => setSuccess(null), 3000);
    } catch (err) {
      setError(err.message || 'Failed to update user lock status');
      console.error('Error toggling user lock:', err);
    } finally {
      setActionInProgress(null);
    }
  };

  // Filter users based on search and filter criteria
  const filteredUsers = users.filter(user => {
    const matchesSearch =
      user.username?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      user.full_name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      user.email?.toLowerCase().includes(searchTerm.toLowerCase());

    const matchesRole = !filterRole || user.role === filterRole;
    const matchesStatus = !filterStatus || user.status === filterStatus;

    return matchesSearch && matchesRole && matchesStatus;
  });

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 py-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">
            User Management
          </h1>
          <p className="text-gray-600">Manage user accounts - lock/unlock users</p>
        </div>

        {/* Alert Messages */}
        {error && (
          <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg flex items-start gap-3">
            <AlertCircle className="w-5 h-5 text-red-600 flex-shrink-0 mt-0.5" />
            <div>
              <h3 className="font-semibold text-red-800">Error</h3>
              <p className="text-red-700 text-sm mt-1">{error}</p>
            </div>
          </div>
        )}

        {success && (
          <div className="mb-6 p-4 bg-green-50 border border-green-200 rounded-lg flex items-start gap-3">
            <CheckCircle className="w-5 h-5 text-green-600 flex-shrink-0 mt-0.5" />
            <div>
              <h3 className="font-semibold text-green-800">Success</h3>
              <p className="text-green-700 text-sm mt-1">{success}</p>
            </div>
          </div>
        )}

        {/* Filters and Search */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
            {/* Search */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Search
              </label>
              <input
                type="text"
                placeholder="Username, name, or email"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            {/* Filter by Role */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Role
              </label>
              <select
                value={filterRole}
                onChange={(e) => setFilterRole(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="">All Roles</option>
                <option value="USER">User</option>
                <option value="COMPANY">Company</option>
                <option value="ADMIN">Admin</option>
              </select>
            </div>

            {/* Filter by Status */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Status
              </label>
              <select
                value={filterStatus}
                onChange={(e) => setFilterStatus(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="">All Status</option>
                <option value="active">Active</option>
                <option value="locked">Locked</option>
              </select>
            </div>

            {/* Refresh Button */}
            <div className="flex items-end">
              <button
                onClick={loadUsers}
                disabled={loading}
                className="w-full bg-blue-600 hover:bg-blue-700 disabled:bg-gray-400 text-white font-semibold py-2 px-4 rounded-lg flex items-center justify-center gap-2 transition-colors"
              >
                <RefreshCw className="w-4 h-4" />
                Refresh
              </button>
            </div>
          </div>
        </div>

        {/* Users Table */}
        <div className="bg-white rounded-lg shadow-md overflow-hidden">
          {loading ? (
            <div className="p-8 text-center">
              <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
              <p className="mt-4 text-gray-600">Loading users...</p>
            </div>
          ) : filteredUsers.length === 0 ? (
            <div className="p-8 text-center text-gray-500">
              <p>No users found</p>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="bg-gray-50 border-b border-gray-200">
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-700 uppercase tracking-wider">
                      Username
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-700 uppercase tracking-wider">
                      Full Name
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-700 uppercase tracking-wider">
                      Email
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-700 uppercase tracking-wider">
                      Phone
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-700 uppercase tracking-wider">
                      Role
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-700 uppercase tracking-wider">
                      Status
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-700 uppercase tracking-wider">
                      Action
                    </th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-200">
                  {filteredUsers.map((user) => {
                    const isLocked = user.status === 'locked';
                    const isProcessing = actionInProgress === user.id;

                    return (
                      <tr key={user.id} className="hover:bg-gray-50 transition-colors">
                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                          {user.username}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">
                          {user.full_name || '-'}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">
                          {user.email || '-'}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">
                          {user.phone_number || '-'}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm">
                          <span
                            className={`inline-flex px-3 py-1 rounded-full text-xs font-semibold ${
                              user.role === 'ADMIN'
                                ? 'bg-purple-100 text-purple-800'
                                : user.role === 'COMPANY'
                                ? 'bg-blue-100 text-blue-800'
                                : 'bg-green-100 text-green-800'
                            }`}
                          >
                            {user.role}
                          </span>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm">
                          <span
                            className={`inline-flex px-3 py-1 rounded-full text-xs font-semibold ${
                              isLocked
                                ? 'bg-red-100 text-red-800'
                                : 'bg-green-100 text-green-800'
                            }`}
                          >
                            {isLocked ? 'Locked' : 'Active'}
                          </span>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm">
                          <button
                            onClick={() => handleToggleLock(user.id, isLocked)}
                            disabled={isProcessing}
                            className={`inline-flex items-center gap-2 px-4 py-2 rounded-lg font-semibold transition-colors ${
                              isLocked
                                ? 'bg-green-600 hover:bg-green-700 disabled:bg-gray-400 text-white'
                                : 'bg-red-600 hover:bg-red-700 disabled:bg-gray-400 text-white'
                            }`}
                          >
                            {isProcessing ? (
                              <RefreshCw className="w-4 h-4 animate-spin" />
                            ) : isLocked ? (
                              <>
                                <Unlock className="w-4 h-4" />
                                Unlock
                              </>
                            ) : (
                              <>
                                <Lock className="w-4 h-4" />
                                Lock
                              </>
                            )}
                          </button>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
        </div>

        {/* Stats */}
        {!loading && users.length > 0 && (
          <div className="mt-6 grid grid-cols-1 md:grid-cols-4 gap-4">
            <div className="bg-blue-50 rounded-lg p-4">
              <p className="text-sm text-gray-600">Total Users</p>
              <p className="text-2xl font-bold text-blue-600">{users.length}</p>
            </div>
            <div className="bg-green-50 rounded-lg p-4">
              <p className="text-sm text-gray-600">Active Users</p>
              <p className="text-2xl font-bold text-green-600">
                {users.filter(u => u.status === 'active').length}
              </p>
            </div>
            <div className="bg-red-50 rounded-lg p-4">
              <p className="text-sm text-gray-600">Locked Users</p>
              <p className="text-2xl font-bold text-red-600">
                {users.filter(u => u.status === 'locked').length}
              </p>
            </div>
            <div className="bg-purple-50 rounded-lg p-4">
              <p className="text-sm text-gray-600">Admin Users</p>
              <p className="text-2xl font-bold text-purple-600">
                {users.filter(u => u.role === 'ADMIN').length}
              </p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default AdminUserManagementView;
