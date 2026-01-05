import React, { useEffect, useState } from 'react';
import { Edit2, Plus } from 'lucide-react';
import { getCompanyVehicles, createCompanyVehicle } from '../service/companyService';

export default function VehicleManagementView({ onNavigate }) {
  const [vehicles, setVehicles] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [editingId, setEditingId] = useState(null);
  const [showForm, setShowForm] = useState(false);

  // Form state
  const [formData, setFormData] = useState({
    plateNumber: '',
    vehicleType: 'TRUCK',
    equipmentDesc: ''
  });

  const [formError, setFormError] = useState(null);
  const [formLoading, setFormLoading] = useState(false);

  // Load vehicles on component mount
  useEffect(() => {
    loadVehicles();
  }, []);

  const loadVehicles = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getCompanyVehicles();
      setVehicles(Array.isArray(data) ? data : []);
    } catch (err) {
      setError(err?.message || 'Lỗi khi tải danh sách phương tiện');
      setVehicles([]);
    } finally {
      setLoading(false);
    }
  };

  const handleOpenForm = (vehicle = null) => {
    if (vehicle) {
      setEditingId(vehicle.id || vehicle.vehicleId);
      setFormData({
        plateNumber: vehicle.plateNumber || '',
        vehicleType: vehicle.vehicleType || 'TRUCK',
        equipmentDesc: vehicle.equipmentDesc || ''
      });
    } else {
      setEditingId(null);
      setFormData({
        plateNumber: '',
        vehicleType: 'TRUCK',
        equipmentDesc: ''
      });
    }
    setFormError(null);
    setShowForm(true);
  };

  const handleCloseForm = () => {
    setShowForm(false);
    setEditingId(null);
    setFormData({
      plateNumber: '',
      vehicleType: 'TRUCK',
      equipmentDesc: ''
    });
    setFormError(null);
  };

  const handleFormChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const validateForm = () => {
    if (!formData.plateNumber.trim()) {
      setFormError('Vui lòng nhập biển số xe');
      return false;
    }
    if (!formData.vehicleType) {
      setFormError('Vui lòng chọn loại phương tiện');
      return false;
    }
    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) return;

    setFormLoading(true);
    setFormError(null);

    try {
      const submitData = {
        plateNumber: formData.plateNumber.trim(),
        vehicleType: formData.vehicleType,
        equipmentDesc: formData.equipmentDesc.trim()
      };

      // For now, API only supports POST (create). Editing would need PUT endpoint
      if (!editingId) {
        await createCompanyVehicle(submitData);
      } else {
        setFormError('Chức năng sửa phương tiện sẽ được cập nhật');
        setFormLoading(false);
        return;
      }

      handleCloseForm();
      loadVehicles();
    } catch (err) {
      setFormError(err?.message || 'Lỗi khi lưu phương tiện');
    } finally {
      setFormLoading(false);
    }
  };

  const getVehicleTypeLabel = (type) => {
    const types = {
      'TRUCK': 'Xe Cẩu',
      'MOTORCYCLE': 'Xe Máy',
      'CAR': 'Xe Ô Tô',
      'VEHICLE': 'Phương tiện khác'
    };
    return types[type] || type;
  };

  const getStatusLabel = (status) => {
    const statuses = {
      'AVAILABLE': 'Sẵn sàng',
      'BUSY': 'Đang làm việc',
      'MAINTENANCE': 'Bảo dưỡng',
      'INACTIVE': 'Không hoạt động'
    };
    return statuses[status] || status;
  };

  const getStatusColor = (status) => {
    const colors = {
      'AVAILABLE': 'bg-green-100 text-green-800',
      'BUSY': 'bg-yellow-100 text-yellow-800',
      'MAINTENANCE': 'bg-blue-100 text-blue-800',
      'INACTIVE': 'bg-gray-100 text-gray-800'
    };
    return colors[status] || 'bg-gray-100 text-gray-800';
  };

  return (
    <div className="min-h-screen bg-gradient-to-b from-blue-50 to-white pt-8 pb-16">
      <div className="max-w-6xl mx-auto px-4">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-gray-900 mb-2">Quản lý Phương tiện</h1>
          <p className="text-gray-600">UC303 - Thêm xe cứu hộ vào đội xe</p>
        </div>

        {/* Error Alert */}
        {error && (
          <div className="mb-6 p-4 bg-red-100 border border-red-400 text-red-700 rounded">
            {error}
          </div>
        )}

        {/* Add Vehicle Button */}
        <button
          onClick={() => handleOpenForm()}
          className="mb-6 inline-flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-6 py-3 rounded-lg font-medium transition"
        >
          <Plus size={20} />
          Thêm Phương tiện Mới
        </button>

        {/* Vehicle Form Modal */}
        {showForm && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-lg max-w-2xl w-full max-h-96 overflow-y-auto">
              <div className="sticky top-0 bg-white border-b p-6 flex justify-between items-center">
                <h2 className="text-2xl font-bold">
                  {editingId ? 'Sửa Phương tiện' : 'Thêm Phương tiện Mới'}
                </h2>
                <button
                  onClick={handleCloseForm}
                  className="text-gray-500 hover:text-gray-700 text-2xl"
                >
                  ×
                </button>
              </div>

              <form onSubmit={handleSubmit} className="p-6 space-y-4">
                {formError && (
                  <div className="p-3 bg-red-100 border border-red-400 text-red-700 rounded">
                    {formError}
                  </div>
                )}

                <div>
                  <label className="block text-gray-700 font-medium mb-2">
                    Biển số xe <span className="text-red-500">*</span>
                  </label>
                  <input
                    type="text"
                    name="plateNumber"
                    value={formData.plateNumber}
                    onChange={handleFormChange}
                    placeholder="Ví dụ: 29A-12345"
                    maxLength="20"
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-gray-700 font-medium mb-2">
                      Loại phương tiện <span className="text-red-500">*</span>
                    </label>
                    <select
                      name="vehicleType"
                      value={formData.vehicleType}
                      onChange={handleFormChange}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    >
                      <option value="TRUCK">Xe Cẩu</option>
                      <option value="MOTORCYCLE">Xe Máy</option>
                      <option value="CAR">Xe Ô Tô</option>
                      <option value="VEHICLE">Phương tiện khác</option>
                    </select>
                  </div>

                  <div>
                    <label className="block text-gray-700 font-medium mb-2">
                      Mô tả Thiết bị
                    </label>
                    <input
                      type="text"
                      name="equipmentDesc"
                      value={formData.equipmentDesc}
                      onChange={handleFormChange}
                      placeholder="Ví dụ: Xe cẩu 30 tấn, có puli"
                      maxLength="500"
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                  </div>
                </div>

                <div className="flex gap-3 justify-end pt-4">
                  <button
                    type="button"
                    onClick={handleCloseForm}
                    className="px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50"
                  >
                    Hủy
                  </button>
                  <button
                    type="submit"
                    disabled={formLoading}
                    className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg disabled:opacity-50"
                  >
                    {formLoading ? 'Đang lưu...' : (editingId ? 'Cập nhật' : 'Thêm')}
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}

        {/* Vehicles List */}
        {loading ? (
          <div className="text-center py-12">
            <p className="text-gray-600">Đang tải danh sách phương tiện...</p>
          </div>
        ) : vehicles.length === 0 ? (
          <div className="bg-white rounded-lg p-8 text-center">
            <p className="text-gray-600 mb-4">Chưa có phương tiện nào</p>
            <button
              onClick={() => handleOpenForm()}
              className="inline-flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded-lg"
            >
              <Plus size={18} />
              Thêm phương tiện đầu tiên
            </button>
          </div>
        ) : (
          <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
            {vehicles.map((vehicle) => (
              <div
                key={vehicle.id || vehicle.vehicleId}
                className="bg-white rounded-lg shadow-md hover:shadow-lg transition p-6"
              >
                <div className="flex justify-between items-start mb-4">
                  <h3 className="text-lg font-bold text-gray-900">
                    {vehicle.plateNumber}
                  </h3>
                  <div className="flex gap-2">
                    <button
                      onClick={() => handleOpenForm(vehicle)}
                      className="p-2 text-blue-600 hover:bg-blue-100 rounded-lg transition"
                      title="Sửa"
                    >
                      <Edit2 size={18} />
                    </button>
                  </div>
                </div>

                <div className="flex justify-between">
                    <span className="text-gray-600">Biển số:</span>
                    <span className="font-semibold text-gray-900">
                      {vehicle.plateNumber}
                    </span>
                  </div>

                  <div className="flex justify-between">
                    <span className="text-gray-600">Loại:</span>
                    <span className="font-semibold text-gray-900">
                      {getVehicleTypeLabel(vehicle.vehicleType)}
                    </span>
                  </div>

                  {vehicle.equipmentDesc && (
                    <div className="flex justify-between">
                      <span className="text-gray-600">Thiết bị:</span>
                      <span className="font-semibold text-gray-900">
                        {vehicle.equipmentDesc}
                      </span>
                    </div>
                  )}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
