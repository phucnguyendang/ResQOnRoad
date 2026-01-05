import React, { useState, useEffect, useRef } from 'react';
import { User, Phone, Upload, Save, X } from 'lucide-react';
import { getUserProfile, updateUserProfile } from '../service/userProfileService';

const UserProfileView = ({ user, onUpdate }) => {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);
  const [isEditing, setIsEditing] = useState(false);

  const [formData, setFormData] = useState({
    fullName: '',
    phoneNumber: '',
    avatarBase64: null,
    avatarPreview: null,
  });

  const fileInputRef = useRef(null);

  // Load profile on mount
  useEffect(() => {
    const loadProfile = async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await getUserProfile();
        setProfile(data);
        setFormData({
          fullName: data.full_name || '',
          phoneNumber: data.phone_number || '',
          avatarBase64: data.avatar_base64 || null,
          avatarPreview: data.avatar_base64 ? `data:image/jpeg;base64,${data.avatar_base64}` : null,
        });
      } catch (err) {
        // Nếu lỗi, vẫn hiển thị form với giá trị mặc định (avatar trống)
        setProfile(null);
        setFormData({
          fullName: user?.fullName || '',
          phoneNumber: user?.phoneNumber || '',
          avatarBase64: null,
          avatarPreview: null,
        });
      } finally {
        setLoading(false);
      }
    };

    loadProfile();
  }, []);

  const handleAvatarChange = (e) => {
    const file = e.target.files?.[0];
    if (!file) return;

    // Validate file type
    if (!file.type.startsWith('image/')) {
      setError('Vui lòng chọn một tệp hình ảnh');
      return;
    }

    // Validate file size (max 5MB)
    if (file.size > 5 * 1024 * 1024) {
      setError('Kích thước hình ảnh không được vượt quá 5MB');
      return;
    }

    const reader = new FileReader();
    reader.onload = (event) => {
      const base64 = event.target.result.split(',')[1];
      setFormData({
        ...formData,
        avatarBase64: base64,
        avatarPreview: event.target.result,
      });
      setError(null);
    };
    reader.readAsDataURL(file);
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
    setError(null);
  };

  const handleSave = async () => {
    // Validate
    if (!formData.fullName.trim()) {
      setError('Vui lòng nhập họ tên');
      return;
    }

    if (!formData.phoneNumber.trim()) {
      setError('Vui lòng nhập số điện thoại');
      return;
    }

    // Validate phone number format
    const phoneRegex = /^(\+84|0)[1-9]\d{8,9}$/;
    if (!phoneRegex.test(formData.phoneNumber.replace(/\s/g, ''))) {
      setError('Số điện thoại không hợp lệ');
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const updated = await updateUserProfile({
        fullName: formData.fullName,
        phoneNumber: formData.phoneNumber,
        avatarBase64: formData.avatarBase64 !== profile?.avatar_base64 ? formData.avatarBase64 : undefined,
      });

      setProfile(updated);
      onUpdate?.({
        fullName: updated.full_name,
        phoneNumber: updated.phone_number,
        avatarBase64: updated.avatar_base64,
      });

      setSuccess('Cập nhật hồ sơ thành công');
      setIsEditing(false);

      setTimeout(() => setSuccess(null), 3000);
    } catch (err) {
      setError(err?.message || 'Cập nhật hồ sơ thất bại');
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = () => {
    setFormData({
      fullName: profile?.full_name || '',
      phoneNumber: profile?.phone_number || '',
      avatarBase64: profile?.avatar_base64 || null,
      avatarPreview: profile?.avatar_base64 ? `data:image/jpeg;base64,${profile.avatar_base64}` : null,
    });
    setError(null);
    setSuccess(null);
    setIsEditing(false);
  };

  if (loading) {
    return (
      <div className="min-h-[80vh] flex items-center justify-center bg-gray-100">
        <div className="text-center">
          <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
          <p className="mt-4 text-gray-600">Đang tải hồ sơ...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-[80vh] bg-gray-100 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-2xl mx-auto">
        <div className="bg-white rounded-xl shadow-lg overflow-hidden">
          {/* Header */}
          <div className="bg-gradient-to-r from-blue-500 to-blue-600 px-6 py-8 sm:px-8">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-4">
                <div className="bg-white rounded-full p-2">
                  <User className="text-blue-600" size={32} />
                </div>
                <div>
                  <h1 className="text-3xl font-bold text-white">Hồ sơ người dùng</h1>
                  <p className="text-blue-100 mt-1">Quản lý thông tin cá nhân của bạn</p>
                </div>
              </div>
              {!isEditing && (
                <button
                  onClick={() => setIsEditing(true)}
                  className="px-4 py-2 bg-white text-blue-600 rounded-lg font-medium hover:bg-blue-50 transition"
                >
                  Chỉnh sửa
                </button>
              )}
            </div>
          </div>

          {/* Content */}
          <div className="p-6 sm:p-8">
            {/* Avatar Section */}
            <div className="mb-8 flex flex-col items-center">
              <div className="relative">
                {formData.avatarPreview ? (
                  <img
                    src={formData.avatarPreview}
                    alt="Avatar"
                    className="w-24 h-24 rounded-full object-cover border-4 border-blue-200"
                  />
                ) : (
                  <div className="w-24 h-24 rounded-full bg-gray-200 flex items-center justify-center border-4 border-blue-200">
                    <User className="text-gray-400" size={40} />
                  </div>
                )}

                {isEditing && (
                  <button
                    onClick={() => fileInputRef.current?.click()}
                    className="absolute bottom-0 right-0 bg-blue-500 text-white p-2 rounded-full hover:bg-blue-600 transition"
                    title="Thay đổi ảnh đại diện"
                  >
                    <Upload size={16} />
                  </button>
                )}
              </div>

              <input
                ref={fileInputRef}
                type="file"
                accept="image/*"
                onChange={handleAvatarChange}
                className="hidden"
              />

              <p className="mt-2 text-sm text-gray-500">Ảnh đại diện</p>
            </div>

            {/* Messages */}
            {error && (
              <div className="mb-4 p-4 bg-red-100 border border-red-400 rounded-lg">
                <p className="text-sm text-red-700">{error}</p>
              </div>
            )}

            {success && (
              <div className="mb-4 p-4 bg-green-100 border border-green-400 rounded-lg">
                <p className="text-sm text-green-700">{success}</p>
              </div>
            )}

            {/* Form Fields */}
            <div className="space-y-6">
              {/* Full Name */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Họ tên
                </label>
                {isEditing ? (
                  <input
                    type="text"
                    name="fullName"
                    value={formData.fullName}
                    onChange={handleInputChange}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    placeholder="Nhập họ tên"
                  />
                ) : (
                  <div className="px-4 py-2 bg-gray-50 border border-gray-300 rounded-lg text-gray-900">
                    {formData.fullName || '—'}
                  </div>
                )}
              </div>

              {/* Phone Number */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  <div className="flex items-center gap-2">
                    <Phone size={16} />
                    Số điện thoại
                  </div>
                </label>
                {isEditing ? (
                  <input
                    type="tel"
                    name="phoneNumber"
                    value={formData.phoneNumber}
                    onChange={handleInputChange}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    placeholder="Nhập số điện thoại (ví dụ: 0912345678)"
                  />
                ) : (
                  <div className="px-4 py-2 bg-gray-50 border border-gray-300 rounded-lg text-gray-900">
                    {formData.phoneNumber || '—'}
                  </div>
                )}
              </div>

              {/* Username (read-only) */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Tên tài khoản
                </label>
                <div className="px-4 py-2 bg-gray-50 border border-gray-300 rounded-lg text-gray-600">
                  {user?.username || profile?.username || '—'}
                </div>
              </div>

              {/* Email (read-only) */}
              {profile?.email && (
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Email
                  </label>
                  <div className="px-4 py-2 bg-gray-50 border border-gray-300 rounded-lg text-gray-600">
                    {profile.email}
                  </div>
                </div>
              )}
            </div>

            {/* Actions */}
            {isEditing && (
              <div className="flex gap-3 mt-8">
                <button
                  onClick={handleCancel}
                  disabled={saving}
                  className="flex-1 py-3 px-4 border border-gray-300 rounded-lg text-gray-700 font-medium hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed transition flex items-center justify-center gap-2"
                >
                  <X size={18} />
                  Hủy
                </button>
                <button
                  onClick={handleSave}
                  disabled={saving}
                  className="flex-1 py-3 px-4 bg-blue-600 text-white rounded-lg font-medium hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition flex items-center justify-center gap-2"
                >
                  <Save size={18} />
                  {saving ? 'Đang lưu...' : 'Lưu thay đổi'}
                </button>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default UserProfileView;
