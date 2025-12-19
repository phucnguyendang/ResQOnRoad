import React, { useState } from 'react';
import { register } from '../service/authService';

const RegisterView = ({ onNavigate }) => {
  const [formData, setFormData] = useState({
    username: '',
    password: '',
    fullName: '',
    phone: '',
    role: 'USER'
  });

  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();

    setSubmitting(true);
    setError(null);
    try {
      await register({
        username: formData.username,
        password: formData.password,
        fullName: formData.fullName,
        phoneNumber: formData.phone,
        role: formData.role,
      });

      alert('Đăng ký thành công! Vui lòng đăng nhập.');
      onNavigate('login');
    } catch (err) {
      const details = Array.isArray(err?.details) && err.details.length > 0
        ? `: ${err.details.join(', ')}`
        : '';
      setError(`${err?.message || 'Đăng ký thất bại'}${details}`);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="min-h-[80vh] flex items-center justify-center bg-gray-100 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8 bg-white p-8 rounded-xl shadow-lg">
        <div className="text-center">
          <h2 className="mt-6 text-3xl font-extrabold text-gray-900">Tạo tài khoản</h2>
          <p className="mt-2 text-sm text-gray-600">
            Đã có tài khoản? <button onClick={() => onNavigate('login')} className="font-medium text-blue-600 hover:text-blue-500">Đăng nhập ngay</button>
          </p>
        </div>
        <form className="mt-8 space-y-4" onSubmit={handleSubmit}>
          <div className="grid grid-cols-1 gap-4">
            <input
              type="text"
              required
              className="appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
              placeholder="Họ và tên"
              value={formData.fullName}
              onChange={(e) => setFormData({...formData, fullName: e.target.value})}
            />
            <input
              type="tel"
              required
              className="appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
              placeholder="Số điện thoại"
              value={formData.phone}
              onChange={(e) => setFormData({...formData, phone: e.target.value})}
            />
            <input
              type="text"
              required
              className="appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
              placeholder="Tên đăng nhập"
              value={formData.username}
              onChange={(e) => setFormData({...formData, username: e.target.value})}
            />
            <input
              type="password"
              required
              className="appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
              placeholder="Mật khẩu"
              value={formData.password}
              onChange={(e) => setFormData({...formData, password: e.target.value})}
            />
            <div className="flex flex-col">
              <label className="text-sm font-medium text-gray-700 mb-1">Bạn là:</label>
              <select 
                className="block w-full px-3 py-2 border border-gray-300 bg-white rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                value={formData.role}
                onChange={(e) => setFormData({...formData, role: e.target.value})}
              >
                <option value="USER">Người dùng cá nhân (Cần cứu hộ)</option>
                <option value="COMPANY">Đơn vị cứu hộ (Cung cấp dịch vụ)</option>
              </select>
            </div>
          </div>

          <button type="submit" className="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-yellow-500 hover:bg-yellow-400 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-yellow-500 text-blue-900 font-bold mt-6">
            {submitting ? 'Đang đăng ký...' : 'Đăng ký tài khoản'}
          </button>

          {error && (
            <div className="text-sm text-red-600 bg-red-50 border border-red-200 rounded-md p-2 mt-3">
              {error}
            </div>
          )}
        </form>
      </div>
    </div>
  );
};

export default RegisterView;