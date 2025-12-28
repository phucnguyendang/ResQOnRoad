import React from 'react';
import { MapPin, Phone, Clock, Wrench, AlertCircle } from 'lucide-react';

const RescueRequestCard = ({ request, onSelect, isSelected }) => {
  const getStatusColor = (status) => {
    switch (status) {
      case 'PENDING_CONFIRMATION':
        return 'bg-yellow-100 text-yellow-800';
      case 'ACCEPTED':
        return 'bg-blue-100 text-blue-800';
      case 'IN_TRANSIT':
        return 'bg-purple-100 text-purple-800';
      case 'IN_PROGRESS':
        return 'bg-orange-100 text-orange-800';
      case 'COMPLETED':
        return 'bg-green-100 text-green-800';
      case 'REJECTED_BY_COMPANY':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getStatusLabel = (status) => {
    const labels = {
      PENDING_CONFIRMATION: 'Chờ xác nhận',
      ACCEPTED: 'Đã tiếp nhận',
      IN_TRANSIT: 'Đang tới',
      IN_PROGRESS: 'Đang xử lý',
      COMPLETED: 'Hoàn thành',
      REJECTED_BY_COMPANY: 'Đã từ chối',
      CANCELLED_BY_USER: 'Hủy bởi người dùng',
    };
    return labels[status] || status;
  };

  return (
    <div
      onClick={() => onSelect(request)}
      className={`p-4 rounded-lg border-2 cursor-pointer transition-all ${
        isSelected
          ? 'border-blue-900 bg-blue-50 shadow-lg'
          : 'border-gray-200 bg-white hover:border-blue-500 hover:shadow-md'
      }`}
    >
      <div className="flex justify-between items-start mb-2">
        <div className="flex-1">
          <h3 className="font-bold text-gray-800">#{request.id}</h3>
          <div className="flex items-center text-sm text-gray-600 mt-1">
            <MapPin size={16} className="mr-1" />
            <span>{request.location}</span>
          </div>
        </div>
        <span className={`text-xs font-semibold px-3 py-1 rounded-full ${getStatusColor(request.status)}`}>
          {getStatusLabel(request.status)}
        </span>
      </div>

      <p className="text-gray-700 text-sm mb-3 line-clamp-2">{request.description}</p>

      <div className="grid grid-cols-2 gap-2 mb-3">
        <div className="flex items-center text-sm text-gray-600">
          <Wrench size={14} className="mr-2" />
          <span>{request.serviceType || 'Không xác định'}</span>
        </div>
        <div className="flex items-center text-sm text-gray-600">
          <Phone size={14} className="mr-2" />
          <a href={`tel:${request.userPhoneNumber}`} className="text-blue-600 hover:underline">
            {request.userPhoneNumber}
          </a>
        </div>
      </div>

      <div className="flex items-center text-xs text-gray-500">
        <Clock size={12} className="mr-1" />
        <span>{new Date(request.createdAt).toLocaleString('vi-VN')}</span>
      </div>
    </div>
  );
};

export default RescueRequestCard;
