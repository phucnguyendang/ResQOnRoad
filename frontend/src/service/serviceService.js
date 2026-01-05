// serviceService.js
// Frontend service để tương tác với UC302 - Service Management API

import apiClient from './apiClient';

class ServiceService {

    /**
     * Lấy danh sách dịch vụ của công ty hiện tại (Authenticated)
     * GET /api/services/company/my
     */
    async getMyCompanyServices() {
        try {
            const response = await apiClient.get('/services/company/my');
            return response.data;
        } catch (error) {
            throw error.response.data;
        }
    }

    /**
     * Lấy danh sách dịch vụ của công ty theo ID (Public)
     * GET /api/services/company/{companyId}
     */
    async getServicesByCompanyId(companyId) {
        try {
            const response = await apiClient.get(`/services/company/${companyId}`);
            return response.data;
        } catch (error) {
            throw error.response.data;
        }
    }

    /**
     * Lấy chi tiết dịch vụ
     * GET /api/services/{serviceId}
     */
    async getServiceById(serviceId) {
        try {
            const response = await apiClient.get(`/services/${serviceId}`);
            return response.data;
        } catch (error) {
            throw error.response.data;
        }
    }

    /**
     * Tạo dịch vụ mới (Authenticated)
     * POST /api/services
     * 
     * @param {Object} serviceData - Dữ liệu dịch vụ
     * @param {string} serviceData.name - Tên dịch vụ (bắt buộc)
     * @param {string} serviceData.description - Mô tả dịch vụ (tùy chọn)
     * @param {string} serviceData.type - Loại dịch vụ (bắt buộc)
     * @param {number} serviceData.basePrice - Giá cơ bản (bắt buộc)
     * @param {string} serviceData.priceUnit - Đơn vị giá (tùy chọn, mặc định: VND)
     * @param {boolean} serviceData.isAvailable - Trạng thái khả dụng (tùy chọn, mặc định: true)
     * @param {number} serviceData.estimatedTime - Thời gian ước tính phút (tùy chọn)
     */
    async createService(serviceData) {
        try {
            const response = await apiClient.post('/services', serviceData);
            return response.data;
        } catch (error) {
            throw error.response.data;
        }
    }

    /**
     * Cập nhật dịch vụ (Authenticated)
     * PUT /api/services/{serviceId}
     * 
     * @param {number} serviceId - ID dịch vụ
     * @param {Object} updateData - Dữ liệu cập nhật (tất cả trường tùy chọn)
     */
    async updateService(serviceId, updateData) {
        try {
            const response = await apiClient.put(`/services/${serviceId}`, updateData);
            return response.data;
        } catch (error) {
            throw error.response.data;
        }
    }

    /**
     * Xóa dịch vụ (Authenticated)
     * DELETE /api/services/{serviceId}
     */
    async deleteService(serviceId) {
        try {
            const response = await apiClient.delete(`/services/${serviceId}`);
            return response.data;
        } catch (error) {
            throw error.response.data;
        }
    }

    /**
     * Lấy danh sách dịch vụ khả dụng (Public)
     * GET /api/services/company/{companyId}/available
     */
    async getAvailableServices(companyId) {
        try {
            const response = await apiClient.get(`/services/company/${companyId}/available`);
            return response.data;
        } catch (error) {
            throw error.response.data;
        }
    }

    /**
     * Service Types enum
     */
    static ServiceTypes = {
        TOW_TRUCK: 'TOW_TRUCK',
        TIRE_CHANGE: 'TIRE_CHANGE',
        BATTERY_JUMP: 'BATTERY_JUMP',
        FUEL_DELIVERY: 'FUEL_DELIVERY',
        LOCKOUT: 'LOCKOUT',
        WINCH_OUT: 'WINCH_OUT',
        ACCIDENT_RECOVERY: 'ACCIDENT_RECOVERY',
        MECHANICAL_REPAIR: 'MECHANICAL_REPAIR',
    };

    /**
     * Service Types display names
     */
    static ServiceTypeNames = {
        TOW_TRUCK: 'Cẩu xe',
        TIRE_CHANGE: 'Vá lốp',
        BATTERY_JUMP: 'Cứu hộ ắc quy',
        FUEL_DELIVERY: 'Giao nhiên liệu',
        LOCKOUT: 'Mở khóa xe',
        WINCH_OUT: 'Kéo xe bị sa lầy',
        ACCIDENT_RECOVERY: 'Cứu hộ tai nạn',
        MECHANICAL_REPAIR: 'Sửa chữa cơ bản',
    };

    /**
     * Get service type display name
     */
    static getServiceTypeName(type) {
        return this.ServiceTypeNames[type] || type;
    }

    /**
     * Validate service data before creating/updating
     */
    static validateService(data, isUpdate = false) {
        const errors = [];

        if (!isUpdate) {
            // Create validation
            if (!data.name || data.name.trim() === '') {
                errors.push('Tên dịch vụ không được để trống');
            }
            if (!data.type) {
                errors.push('Loại dịch vụ không được để trống');
            }
            if (!data.basePrice || data.basePrice <= 0) {
                errors.push('Giá dịch vụ phải lớn hơn 0');
            }
        }

        // Common validation
        if (data.name && data.name.length > 100) {
            errors.push('Tên dịch vụ không được vượt quá 100 ký tự');
        }
        if (data.type && !Object.values(this.ServiceTypes).includes(data.type)) {
            errors.push('Loại dịch vụ không hợp lệ');
        }
        if (data.basePrice && data.basePrice <= 0) {
            errors.push('Giá dịch vụ phải lớn hơn 0');
        }
        if (data.estimatedTime && data.estimatedTime < 0) {
            errors.push('Thời gian ước tính không được âm');
        }

        return errors;
    }
}

export default ServiceService;
