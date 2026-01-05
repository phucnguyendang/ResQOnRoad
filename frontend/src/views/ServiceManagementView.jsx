import React, { useEffect, useState } from 'react';
import { Trash2, Edit2, Plus } from 'lucide-react';

export default function ServiceManagementView({ onNavigate }) {
  return (
    <div className="min-h-screen bg-gradient-to-b from-blue-50 to-white pt-8 pb-16">
      <div className="max-w-6xl mx-auto px-4">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-gray-900 mb-2">Quản lý Dịch vụ</h1>
          <p className="text-gray-600">UC302 - Thêm, sửa giá dịch vụ cứu hộ</p>
        </div>

        {/* Coming Soon Alert */}
        <div className="bg-blue-50 border-l-4 border-blue-500 p-6 rounded-lg max-w-2xl">
          <h2 className="text-xl font-bold text-blue-900 mb-2">
            🚧 Tính năng đang phát triển
          </h2>
          <p className="text-blue-700 mb-4">
            UC302 - Quản lý Dịch vụ sẽ sớm được cập nhật trên backend. 
            Hiện tại, bạn có thể xem danh sách dịch vụ trong hồ sơ công ty.
          </p>
          <button
            onClick={() => onNavigate('companyProfile')}
            className="bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded-lg font-medium transition"
          >
            Xem Hồ sơ Công ty
          </button>
        </div>

        {/* Placeholder for future implementation */}
        <div className="mt-12 p-8 bg-white rounded-lg border-2 border-dashed border-gray-300 text-center">
          <p className="text-gray-600 text-lg">
            Backend API endpoints cho UC302 đang được phát triển
          </p>
          <p className="text-gray-500 mt-2 text-sm">
            Yêu cầu: POST /api/company/services, PUT /api/company/services/{'{id}'}, GET /api/company/services
          </p>
        </div>
      </div>
    </div>
  );
}

