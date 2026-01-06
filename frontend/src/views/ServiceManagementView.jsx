// ServiceManagementView.jsx
// Component React để hiển thị và quản lý dịch vụ cứu hộ (UC302)

import React, { useState, useEffect } from 'react';
import ServiceService from '../service/serviceService';
import './ServiceManagementView.css';

function ServiceManagementView({ companyId, isAdmin = false }) {
    const serviceService = new ServiceService(); // Instantiate the class
    const [services, setServices] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(null);
    const [showForm, setShowForm] = useState(false);
    const [editingService, setEditingService] = useState(null);
    const [formData, setFormData] = useState({
        name: '',
        description: '',
        type: '',
        basePrice: '',
        priceUnit: 'VND',
        isAvailable: true,
        estimatedTime: '',
    });

    // Load services on component mount
    useEffect(() => {
        loadServices();
    }, [companyId]);

    // Load services from API
    const loadServices = async () => {
        try {
            setLoading(true);
            setError(null);

            // Always use authenticated endpoint for company's own services
            const response = await serviceService.getMyCompanyServices();
            console.log('📋 Services loaded:', response); // DEBUG
            
            // Handle both array and object responses
            let servicesArray = [];
            if (Array.isArray(response)) {
                servicesArray = response;
            } else if (response?.data && Array.isArray(response.data)) {
                servicesArray = response.data;
            } else if (response?.data && Array.isArray(response.data.data)) {
                servicesArray = response.data.data;
            }
            console.log('📊 Setting services array:', servicesArray); // DEBUG
            setServices(servicesArray);
        } catch (err) {
            setError(err.message || 'Lỗi khi tải danh sách dịch vụ');
            console.error('❌ Error loading services:', err); // DEBUG
            console.error('📍 Full error:', JSON.stringify(err)); // DEBUG
        } finally {
            setLoading(false);
        }
    };

    // Handle form input change
    const handleInputChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value,
        }));
    };

    // Handle form submission (create or update)
    const handleSubmit = async (e) => {
        e.preventDefault();

        // Validate form data
        const errors = ServiceService.validateService(formData, editingService !== null);
        if (errors.length > 0) {
            setError(errors.join(', '));
            return;
        }

        try {
            setLoading(true);
            setError(null);
            setSuccess(null);

            if (editingService) {
                // Update existing service
                const response = await serviceService.updateService(editingService.id, {
                    ...formData,
                    basePrice: parseFloat(formData.basePrice),
                    estimatedTime: formData.estimatedTime ? parseInt(formData.estimatedTime) : null,
                });
                setSuccess(`Dịch vụ "${formData.name}" đã được cập nhật thành công`);
                setEditingService(null);
            } else {
                // Create new service
                const response = await serviceService.createService({
                    ...formData,
                    basePrice: parseFloat(formData.basePrice),
                    estimatedTime: formData.estimatedTime ? parseInt(formData.estimatedTime) : null,
                });
                console.log('✅ Service created:', response); // DEBUG
                setSuccess(`Dịch vụ "${formData.name}" đã được thêm thành công`);
            }

            // Reload services
            console.log('🔄 Reloading services...'); // DEBUG
            await loadServices();
            setShowForm(false);
            resetForm();
            
            // Clear success message after 3 seconds
            setTimeout(() => setSuccess(null), 3000);
        } catch (err) {
            setError(err.message || 'Lỗi khi lưu dịch vụ');
            console.error('Error saving service:', err);
        } finally {
            setLoading(false);
        }
    };

    // Handle edit service
    const handleEdit = (service) => {
        setEditingService(service);
        setFormData({
            name: service.name,
            description: service.description || '',
            type: service.type,
            basePrice: service.basePrice.toString(),
            priceUnit: service.priceUnit,
            isAvailable: service.isAvailable,
            estimatedTime: service.estimatedTime ? service.estimatedTime.toString() : '',
        });
        setShowForm(true);
    };

    // Handle delete service
    const handleDelete = async (serviceId) => {
        if (window.confirm('Bạn chắc chắn muốn xóa dịch vụ này? Thao tác này không thể hoàn tác.')) {
            try {
                setLoading(true);
                setError(null);
                setSuccess(null);
                await serviceService.deleteService(serviceId);
                await loadServices();
                setSuccess('Dịch vụ đã được xóa thành công');
                setTimeout(() => setSuccess(null), 3000);
            } catch (err) {
                setError(err.message || 'Lỗi khi xóa dịch vụ');
                console.error('Error deleting service:', err);
            } finally {
                setLoading(false);
            }
        }
    };

    // Reset form
    const resetForm = () => {
        setFormData({
            name: '',
            description: '',
            type: '',
            basePrice: '',
            priceUnit: 'VND',
            isAvailable: true,
            estimatedTime: '',
        });
        setEditingService(null);
    };

    // Handle cancel
    const handleCancel = () => {
        setShowForm(false);
        resetForm();
    };

    return (
        <div className="service-management-container">
            <h1>Quản lý Dịch vụ Cứu hộ</h1>

            {/* Success message */}
            {success && (
                <div className="alert alert-success">
                    {success}
                    <button onClick={() => setSuccess(null)} className="close-btn">✕</button>
                </div>
            )}

            {/* Error message */}
            {error && (
                <div className="alert alert-error">
                    {error}
                    <button onClick={() => setError(null)} className="close-btn">✕</button>
                </div>
            )}

            {/* Loading indicator */}
            {loading && (
                <div className="loading">
                    <div className="spinner"></div>
                    Đang xử lý...
                </div>
            )}

            {/* Services list or form */}
            {!showForm ? (
                <div>
                    {/* Add service button */}
                    {isAdmin && (
                        <button
                            onClick={() => setShowForm(true)}
                            className="btn btn-primary"
                            disabled={loading}
                        >
                            + Thêm dịch vụ mới
                        </button>
                    )}

                    {/* Services list */}
                    {services.length === 0 ? (
                        <div className="empty-state">
                            <p>{isAdmin ? 'Chưa có dịch vụ nào. Hãy thêm dịch vụ đầu tiên!' : 'Công ty chưa có dịch vụ nào.'}</p>
                        </div>
                    ) : (
                        <div className="services-list">
                            {services.map(service => (
                                <div key={service.id} className="service-card">
                                    <div className="service-header">
                                        <h3>{service.name}</h3>
                                        <span className={`badge ${service.isAvailable ? 'available' : 'unavailable'}`}>
                                            {service.isAvailable ? 'Khả dụng' : 'Không khả dụng'}
                                        </span>
                                    </div>

                                    {service.description && (
                                        <p className="service-description">{service.description}</p>
                                    )}

                                    <div className="service-details">
                                        <div className="detail-row">
                                            <span className="label">Loại dịch vụ:</span>
                                            <span className="value">
                                                {ServiceService.getServiceTypeName(service.type)}
                                            </span>
                                        </div>
                                        <div className="detail-row">
                                            <span className="label">Giá:</span>
                                            <span className="value">
                                                {service.basePrice.toLocaleString('vi-VN')} {service.priceUnit}
                                            </span>
                                        </div>
                                        {service.estimatedTime && (
                                            <div className="detail-row">
                                                <span className="label">Thời gian ước tính:</span>
                                                <span className="value">{service.estimatedTime} phút</span>
                                            </div>
                                        )}
                                    </div>

                                    {/* Action buttons */}
                                    {isAdmin && (
                                        <div className="service-actions">
                                            <button
                                                onClick={() => handleEdit(service)}
                                                className="btn btn-sm btn-edit"
                                                disabled={loading}
                                            >
                                                ✎ Chỉnh sửa
                                            </button>
                                            <button
                                                onClick={() => handleDelete(service.id)}
                                                className="btn btn-sm btn-delete"
                                                disabled={loading}
                                            >
                                                ✗ Xóa
                                            </button>
                                        </div>
                                    )}
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            ) : (
                /* Service form */
                <form onSubmit={handleSubmit} className="service-form">
                    <h2>{editingService ? 'Cập nhật dịch vụ' : 'Thêm dịch vụ mới'}</h2>

                    <div className="form-group">
                        <label htmlFor="name">Tên dịch vụ *</label>
                        <input
                            type="text"
                            id="name"
                            name="name"
                            value={formData.name}
                            onChange={handleInputChange}
                            placeholder="VD: Vá lốp"
                            required
                            maxLength="100"
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="description">Mô tả</label>
                        <textarea
                            id="description"
                            name="description"
                            value={formData.description}
                            onChange={handleInputChange}
                            placeholder="Mô tả chi tiết về dịch vụ"
                            rows="3"
                        />
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label htmlFor="type">Loại dịch vụ *</label>
                            <select
                                id="type"
                                name="type"
                                value={formData.type}
                                onChange={handleInputChange}
                                required
                            >
                                <option value="">-- Chọn loại dịch vụ --</option>
                                {Object.entries(ServiceService.ServiceTypes).map(([key, value]) => (
                                    <option key={value} value={value}>
                                        {ServiceService.getServiceTypeName(value)}
                                    </option>
                                ))}
                            </select>
                        </div>

                        <div className="form-group">
                            <label htmlFor="basePrice">Giá cơ bản (VND) *</label>
                            <input
                                type="number"
                                id="basePrice"
                                name="basePrice"
                                value={formData.basePrice}
                                onChange={handleInputChange}
                                placeholder="150000"
                                required
                                min="0"
                                step="1000"
                            />
                        </div>
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label htmlFor="priceUnit">Đơn vị giá</label>
                            <select
                                id="priceUnit"
                                name="priceUnit"
                                value={formData.priceUnit}
                                onChange={handleInputChange}
                            >
                                <option value="VND">VND</option>
                                <option value="USD">USD</option>
                                <option value="per km">per km</option>
                                <option value="per hour">per hour</option>
                            </select>
                        </div>

                        <div className="form-group">
                            <label htmlFor="estimatedTime">Thời gian ước tính (phút)</label>
                            <input
                                type="number"
                                id="estimatedTime"
                                name="estimatedTime"
                                value={formData.estimatedTime}
                                onChange={handleInputChange}
                                placeholder="30"
                                min="0"
                                step="5"
                            />
                        </div>
                    </div>

                    <div className="form-group form-checkbox">
                        <label htmlFor="isAvailable">
                            <input
                                type="checkbox"
                                id="isAvailable"
                                name="isAvailable"
                                checked={formData.isAvailable}
                                onChange={handleInputChange}
                            />
                            Dịch vụ khả dụng
                        </label>
                    </div>

                    {/* Form buttons */}
                    <div className="form-buttons">
                        <button
                            type="submit"
                            className="btn btn-primary"
                            disabled={loading}
                        >
                            {editingService ? 'Cập nhật' : 'Thêm'} dịch vụ
                        </button>
                        <button
                            type="button"
                            onClick={handleCancel}
                            className="btn btn-secondary"
                            disabled={loading}
                        >
                            Hủy
                        </button>
                    </div>
                </form>
            )}
        </div>
    );
}

export default ServiceManagementView;
