import React, { useState, useEffect, useRef } from 'react';
import { User, Phone, Upload, Save, X } from 'lucide-react';
import { getUserProfile, updateUserProfile } from '../service/userProfileService';
import { getMyCompanyProfile, updateMyCompanyProfile } from '../service/companyProfileService';

const UserProfileView = ({ user, onUpdate }) => {
  const [profile, setProfile] = useState(null);
  const [companyProfile, setCompanyProfile] = useState(null);
  const [companyLoading, setCompanyLoading] = useState(false);
  const [companyError, setCompanyError] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);
  const [isEditing, setIsEditing] = useState(false);

  const role = user?.role;

  const formatApiError = (err, fallbackMessage) => {
    const base = err?.message || fallbackMessage;
    const details = Array.isArray(err?.details) && err.details.length > 0
      ? `: ${err.details.join(', ')}`
      : '';
    return `${base}${details}`;
  };

  const [formData, setFormData] = useState({
    fullName: '',
    phoneNumber: '',
    email: '',
    avatarBase64: null,
    avatarPreview: null,
  });

  const [companyFormData, setCompanyFormData] = useState({
    name: '',
    address: '',
    phone: '',
    email: '',
    latitude: '',
    longitude: '',
    serviceRadius: '',
    taxCode: '',
    hotline: '',
    operatingHours: '',
    businessLicense: '',
    licenseDocumentUrl: '',
    description: '',
  });

  const fileInputRef = useRef(null);

  // Load profile on mount
  useEffect(() => {
    const loadProfile = async () => {
      setLoading(true);
      setError(null);

      // 1) Load account profile
      try {
        const data = await getUserProfile();
        const fullName = data?.fullName ?? data?.full_name ?? '';
        const phoneNumber = data?.phoneNumber ?? data?.phone_number ?? '';
        const email = data?.email ?? '';
        const avatarBase64 = data?.avatarBase64 ?? data?.avatar_base64 ?? null;

        setProfile(data);
        setFormData({
          fullName,
          phoneNumber,
          email,
          avatarBase64,
          avatarPreview: avatarBase64 ? `data:image/jpeg;base64,${avatarBase64}` : null,
        });
      } catch (err) {
        // If error, still show cached/basic info
        setProfile(null);
        setError(err?.message || 'Không thể tải hồ sơ người dùng');
        setFormData({
          fullName: user?.fullName || '',
          phoneNumber: user?.phoneNumber || '',
          email: user?.email || '',
          avatarBase64: null,
          avatarPreview: null,
        });
      }

      // 2) Load company profile (independent from account profile)
      if (role === 'COMPANY') {
        setCompanyLoading(true);
        setCompanyError(null);
        try {
          const cp = await getMyCompanyProfile();
          setCompanyProfile(cp);
          setCompanyFormData({
            name: cp?.name || '',
            address: cp?.address || '',
            phone: cp?.phone || '',
            email: cp?.email || '',
            latitude: cp?.latitude != null ? String(cp.latitude) : '',
            longitude: cp?.longitude != null ? String(cp.longitude) : '',
            serviceRadius: cp?.serviceRadius != null ? String(cp.serviceRadius) : '',
            taxCode: cp?.taxCode || '',
            hotline: cp?.hotline || '',
            operatingHours: cp?.operatingHours || '',
            businessLicense: cp?.businessLicense || '',
            licenseDocumentUrl: cp?.licenseDocumentUrl || '',
            description: cp?.description || '',
          });
        } catch (err) {
          setCompanyProfile(null);
          setCompanyError(err?.message || 'Không thể tải hồ sơ công ty');
        } finally {
          setCompanyLoading(false);
        }
      } else {
        setCompanyProfile(null);
        setCompanyError(null);
      }

      setLoading(false);
    };

    loadProfile();
  }, [role]);

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

  const handleCompanyInputChange = (e) => {
    const { name, value } = e.target;
    setCompanyFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
    setError(null);
  };

  const handleSave = async () => {
    // Validate (account)
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

    // Validate (company) before sending any request to avoid partial updates
    if (role === 'COMPANY') {
      if (!companyFormData.name.trim()) {
        setError('Vui lòng nhập tên công ty');
        return;
      }
      if (!companyFormData.address.trim()) {
        setError('Vui lòng nhập địa chỉ công ty');
        return;
      }
      if (!companyFormData.phone.trim()) {
        setError('Vui lòng nhập số điện thoại công ty');
        return;
      }
      const companyPhoneRegex = /^(0|\+84)[0-9]{9,10}$/;
      if (!companyPhoneRegex.test(companyFormData.phone.replace(/\s/g, ''))) {
        setError('Số điện thoại công ty không hợp lệ');
        return;
      }
      if (companyFormData.hotline) {
        const hotlineDigits = companyFormData.hotline.replace(/\s/g, '');
        const hotlineRegex = /^[0-9]{1,20}$/;
        if (!hotlineRegex.test(hotlineDigits)) {
          setError('Hotline chỉ được chứa chữ số (tối đa 20 ký tự)');
          return;
        }
      }
      if (companyFormData.latitude === '' || Number.isNaN(Number(companyFormData.latitude))) {
        setError('Vui lòng nhập latitude hợp lệ');
        return;
      }
      if (companyFormData.longitude === '' || Number.isNaN(Number(companyFormData.longitude))) {
        setError('Vui lòng nhập longitude hợp lệ');
        return;
      }
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      let updatedCompany = companyProfile;
      if (role === 'COMPANY') {
        const companyPayload = {
          name: companyFormData.name,
          address: companyFormData.address,
          phone: companyFormData.phone,
          email: companyFormData.email || null,
          latitude: Number(companyFormData.latitude),
          longitude: Number(companyFormData.longitude),
          serviceRadius: companyFormData.serviceRadius !== '' ? Number(companyFormData.serviceRadius) : null,
          taxCode: companyFormData.taxCode || null,
          hotline: companyFormData.hotline || null,
          operatingHours: companyFormData.operatingHours || null,
          businessLicense: companyFormData.businessLicense || null,
          licenseDocumentUrl: companyFormData.licenseDocumentUrl || null,
          description: companyFormData.description || null,
        };

        // Update company first so we don't partially update account (e.g., avatar) when company fields fail server validation
        updatedCompany = await updateMyCompanyProfile(companyPayload);
        setCompanyProfile(updatedCompany);
      }

      const updatedAccount = await updateUserProfile({
        fullName: formData.fullName,
        phoneNumber: formData.phoneNumber,
        email: formData.email.trim() === '' ? null : formData.email.trim(),
        avatarBase64: formData.avatarBase64 !== profile?.avatarBase64 ? formData.avatarBase64 : undefined,
      });

      setProfile(updatedAccount);
      onUpdate?.({
        fullName: updatedAccount.fullName,
        phoneNumber: updatedAccount.phoneNumber,
        email: updatedAccount.email,
        avatarBase64: updatedAccount.avatarBase64,
        companyProfile: updatedCompany,
      });

      setSuccess('Cập nhật hồ sơ thành công');
      setIsEditing(false);

      setTimeout(() => setSuccess(null), 3000);
    } catch (err) {
      setError(formatApiError(err, 'Cập nhật hồ sơ thất bại'));
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = () => {
    setFormData({
      fullName: (profile?.fullName ?? profile?.full_name) || '',
      phoneNumber: (profile?.phoneNumber ?? profile?.phone_number) || '',
      email: profile?.email || '',
      avatarBase64: (profile?.avatarBase64 ?? profile?.avatar_base64) || null,
      avatarPreview: (profile?.avatarBase64 ?? profile?.avatar_base64)
        ? `data:image/jpeg;base64,${profile?.avatarBase64 ?? profile?.avatar_base64}`
        : null,
    });
    if (role === 'COMPANY' && companyProfile) {
      setCompanyFormData({
        name: companyProfile?.name || '',
        address: companyProfile?.address || '',
        phone: companyProfile?.phone || '',
        email: companyProfile?.email || '',
        latitude: companyProfile?.latitude != null ? String(companyProfile.latitude) : '',
        longitude: companyProfile?.longitude != null ? String(companyProfile.longitude) : '',
        serviceRadius: companyProfile?.serviceRadius != null ? String(companyProfile.serviceRadius) : '',
        taxCode: companyProfile?.taxCode || '',
        hotline: companyProfile?.hotline || '',
        operatingHours: companyProfile?.operatingHours || '',
        businessLicense: companyProfile?.businessLicense || '',
        licenseDocumentUrl: companyProfile?.licenseDocumentUrl || '',
        description: companyProfile?.description || '',
      });
    }
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
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Email
                </label>
                {isEditing ? (
                  <input
                    type="email"
                    name="email"
                    value={formData.email}
                    onChange={handleInputChange}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    placeholder="Nhập email"
                  />
                ) : (
                  <div className="px-4 py-2 bg-gray-50 border border-gray-300 rounded-lg text-gray-600">
                    {formData.email || '—'}
                  </div>
                )}
              </div>

              {/* COMPANY: rescue company profile fields */}
              {role === 'COMPANY' && (
                <div className="pt-2">
                  <div className="text-sm font-semibold text-gray-900 mb-3">
                    Thông tin công ty cứu hộ
                    {companyLoading && <span className="ml-2 text-xs font-normal text-gray-500">(đang tải...)</span>}
                  </div>

                  {companyError && (
                    <div className="mb-4 p-3 bg-yellow-50 border border-yellow-200 rounded-lg text-sm text-yellow-800">
                      {companyError}
                    </div>
                  )}

                  <div className="space-y-4">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">Tên công ty</label>
                      {isEditing ? (
                        <input
                          type="text"
                          name="name"
                          value={companyFormData.name}
                          onChange={handleCompanyInputChange}
                          className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                          placeholder="Nhập tên công ty"
                        />
                      ) : (
                        <div className="px-4 py-2 bg-gray-50 border border-gray-300 rounded-lg text-gray-900">
                          {companyFormData.name || '—'}
                        </div>
                      )}
                    </div>

                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">Địa chỉ</label>
                      {isEditing ? (
                        <input
                          type="text"
                          name="address"
                          value={companyFormData.address}
                          onChange={handleCompanyInputChange}
                          className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                          placeholder="Nhập địa chỉ"
                        />
                      ) : (
                        <div className="px-4 py-2 bg-gray-50 border border-gray-300 rounded-lg text-gray-900">
                          {companyFormData.address || '—'}
                        </div>
                      )}
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">Điện thoại công ty</label>
                        {isEditing ? (
                          <input
                            type="tel"
                            name="phone"
                            value={companyFormData.phone}
                            onChange={handleCompanyInputChange}
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                            placeholder="Nhập số điện thoại"
                          />
                        ) : (
                          <div className="px-4 py-2 bg-gray-50 border border-gray-300 rounded-lg text-gray-900">
                            {companyFormData.phone || '—'}
                          </div>
                        )}
                      </div>

                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">Email công ty</label>
                        {isEditing ? (
                          <input
                            type="email"
                            name="email"
                            value={companyFormData.email}
                            onChange={handleCompanyInputChange}
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                            placeholder="Nhập email"
                          />
                        ) : (
                          <div className="px-4 py-2 bg-gray-50 border border-gray-300 rounded-lg text-gray-900">
                            {companyFormData.email || '—'}
                          </div>
                        )}
                      </div>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">Latitude</label>
                        {isEditing ? (
                          <input
                            type="number"
                            name="latitude"
                            value={companyFormData.latitude}
                            onChange={handleCompanyInputChange}
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                            placeholder="VD: 10.1234"
                          />
                        ) : (
                          <div className="px-4 py-2 bg-gray-50 border border-gray-300 rounded-lg text-gray-900">
                            {companyFormData.latitude || '—'}
                          </div>
                        )}
                      </div>

                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">Longitude</label>
                        {isEditing ? (
                          <input
                            type="number"
                            name="longitude"
                            value={companyFormData.longitude}
                            onChange={handleCompanyInputChange}
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                            placeholder="VD: 106.1234"
                          />
                        ) : (
                          <div className="px-4 py-2 bg-gray-50 border border-gray-300 rounded-lg text-gray-900">
                            {companyFormData.longitude || '—'}
                          </div>
                        )}
                      </div>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">Bán kính hoạt động (km)</label>
                        {isEditing ? (
                          <input
                            type="number"
                            name="serviceRadius"
                            value={companyFormData.serviceRadius}
                            onChange={handleCompanyInputChange}
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                            placeholder="VD: 50"
                          />
                        ) : (
                          <div className="px-4 py-2 bg-gray-50 border border-gray-300 rounded-lg text-gray-900">
                            {companyFormData.serviceRadius || '—'}
                          </div>
                        )}
                      </div>

                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">Hotline</label>
                        {isEditing ? (
                          <input
                            type="text"
                            name="hotline"
                            value={companyFormData.hotline}
                            onChange={handleCompanyInputChange}
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                            placeholder="Hotline"
                          />
                        ) : (
                          <div className="px-4 py-2 bg-gray-50 border border-gray-300 rounded-lg text-gray-900">
                            {companyFormData.hotline || '—'}
                          </div>
                        )}
                      </div>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">Mã số thuế</label>
                        {isEditing ? (
                          <input
                            type="text"
                            name="taxCode"
                            value={companyFormData.taxCode}
                            onChange={handleCompanyInputChange}
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                            placeholder="Mã số thuế"
                          />
                        ) : (
                          <div className="px-4 py-2 bg-gray-50 border border-gray-300 rounded-lg text-gray-900">
                            {companyFormData.taxCode || '—'}
                          </div>
                        )}
                      </div>

                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">Giờ hoạt động</label>
                        {isEditing ? (
                          <input
                            type="text"
                            name="operatingHours"
                            value={companyFormData.operatingHours}
                            onChange={handleCompanyInputChange}
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                            placeholder="VD: 8:00-22:00"
                          />
                        ) : (
                          <div className="px-4 py-2 bg-gray-50 border border-gray-300 rounded-lg text-gray-900">
                            {companyFormData.operatingHours || '—'}
                          </div>
                        )}
                      </div>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">Giấy phép kinh doanh</label>
                        {isEditing ? (
                          <input
                            type="text"
                            name="businessLicense"
                            value={companyFormData.businessLicense}
                            onChange={handleCompanyInputChange}
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                            placeholder="Giấy phép"
                          />
                        ) : (
                          <div className="px-4 py-2 bg-gray-50 border border-gray-300 rounded-lg text-gray-900">
                            {companyFormData.businessLicense || '—'}
                          </div>
                        )}
                      </div>

                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">Link tài liệu giấy phép</label>
                        {isEditing ? (
                          <input
                            type="text"
                            name="licenseDocumentUrl"
                            value={companyFormData.licenseDocumentUrl}
                            onChange={handleCompanyInputChange}
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                            placeholder="URL tài liệu"
                          />
                        ) : (
                          <div className="px-4 py-2 bg-gray-50 border border-gray-300 rounded-lg text-gray-900">
                            {companyFormData.licenseDocumentUrl || '—'}
                          </div>
                        )}
                      </div>
                    </div>

                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">Mô tả</label>
                      {isEditing ? (
                        <textarea
                          name="description"
                          value={companyFormData.description}
                          onChange={handleCompanyInputChange}
                          rows={3}
                          className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                          placeholder="Mô tả"
                        />
                      ) : (
                        <div className="px-4 py-2 bg-gray-50 border border-gray-300 rounded-lg text-gray-900 whitespace-pre-wrap">
                          {companyFormData.description || '—'}
                        </div>
                      )}
                    </div>
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
