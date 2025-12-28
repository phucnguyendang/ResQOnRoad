import React, { useState, useEffect } from 'react';
import {
  MapPin,
  Phone,
  Clock,
  Wrench,
  CheckCircle,
  Truck,
  XCircle,
  Navigation,
  AlertCircle,
  ArrowLeft,
  User,
} from 'lucide-react';
import {
  acceptRescueRequest,
  updateRescueRequestStatus,
  rejectRescueRequest,
} from '../service/rescueRequestService.js';
import { loadAuth } from '../utils/authStorage.js';

const RescueRequestDetail = ({ request, onBack, onRequestUpdated }) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [showRejectModal, setShowRejectModal] = useState(false);
  const [rejectReason, setRejectReason] = useState('');
  const [currentRequest, setCurrentRequest] = useState(request);

  useEffect(() => {
    setCurrentRequest(request);
  }, [request]);

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

  const handleAccept = async () => {
    setLoading(true);
    setError(null);

    try {
      const auth = loadAuth();
      const updated = await acceptRescueRequest(currentRequest.id, auth?.token);
      setCurrentRequest(updated);
      if (onRequestUpdated) {
        onRequestUpdated(updated);
      }
    } catch (err) {
      console.error('Accept error:', err);
      setError(err.message || 'Lỗi khi tiếp nhận yêu cầu');
    } finally {
      setLoading(false);
    }
  };

  const handleStatusUpdate = async (newStatus) => {
    setLoading(true);
    setError(null);

    try {
      const auth = loadAuth();
      const updated = await updateRescueRequestStatus(
        currentRequest.id,
        newStatus,
        null,
        auth?.token
      );
      setCurrentRequest(updated);
      if (onRequestUpdated) {
        onRequestUpdated(updated);
      }
    } catch (err) {
      console.error('Status update error:', err);
      setError(err.message || 'Lỗi khi cập nhật trạng thái');
    } finally {
      setLoading(false);
    }
  };

  const handleReject = async () => {
    if (!rejectReason.trim()) {
      setError('Vui lòng nhập lý do từ chối');
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const auth = loadAuth();
      const updated = await rejectRescueRequest(
        currentRequest.id,
        rejectReason,
        auth?.token
      );
      setCurrentRequest(updated);
      setShowRejectModal(false);
      setRejectReason('');
      if (onRequestUpdated) {
        onRequestUpdated(updated);
      }
    } catch (err) {
      console.error('Reject error:', err);
      setError(err.message || 'Lỗi khi từ chối yêu cầu');
    } finally {
      setLoading(false);
    }
  };

  const openGoogleMaps = () => {
    const { latitude, longitude } = currentRequest;
    const query = `${latitude},${longitude}`;

    // Detect if mobile
    const isMobile =
      /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(
        navigator.userAgent
      );

    if (isMobile) {
      // Open Google Maps app or web
      window.location.href = `https://maps.google.com/?q=${query}`;
    } else {
      // Open in new window for desktop
      window.open(`https://maps.google.com/?q=${query}`, '_blank');
    }
  };

  const canAccept = currentRequest.status === 'PENDING_CONFIRMATION';
  const canUpdateStatus =
    currentRequest.status === 'ACCEPTED' || currentRequest.status === 'IN_TRANSIT';
  const canReject = ['PENDING_CONFIRMATION', 'ACCEPTED'].includes(
    currentRequest.status
  );
  const isCompleted = currentRequest.status === 'COMPLETED';

  return (
    <div className="bg-white rounded-lg shadow-lg p-6">
      {/* Header */}
      <div className="flex items-center mb-6 pb-4 border-b">
        <button
          onClick={onBack}
          className="flex items-center text-blue-600 hover:text-blue-800 mr-4"
        >
          <ArrowLeft size={20} />
        </button>
        <div className="flex-1">
          <h2 className="text-2xl font-bold text-gray-800">
            Yêu Cầu Cứu Hộ #{currentRequest.id}
          </h2>
          <span
            className={`inline-block text-sm font-semibold px-3 py-1 rounded-full mt-2 ${getStatusColor(
              currentRequest.status
            )}`}
          >
            {getStatusLabel(currentRequest.status)}
          </span>
        </div>
      </div>

      {error && (
        <div className="mb-4 p-4 bg-red-50 border border-red-200 rounded-lg flex items-start">
          <AlertCircle size={20} className="text-red-600 mr-3 mt-0.5 flex-shrink-0" />
          <p className="text-red-800">{error}</p>
        </div>
      )}

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
        {/* User Information */}
        <div className="bg-gray-50 p-4 rounded-lg">
          <h3 className="font-bold text-gray-800 mb-3 flex items-center">
            <User size={18} className="mr-2" />
            Thông tin người gửi yêu cầu
          </h3>
          <div className="space-y-2 text-sm">
            <div>
              <p className="text-gray-600">Tên:</p>
              <p className="font-semibold text-gray-800">{currentRequest.userName}</p>
            </div>
            <div>
              <p className="text-gray-600">Điện thoại:</p>
              <a
                href={`tel:${currentRequest.userPhoneNumber}`}
                className="text-blue-600 hover:underline font-semibold"
              >
                {currentRequest.userPhoneNumber}
              </a>
            </div>
          </div>
        </div>

        {/* Request Details */}
        <div className="bg-gray-50 p-4 rounded-lg">
          <h3 className="font-bold text-gray-800 mb-3 flex items-center">
            <Wrench size={18} className="mr-2" />
            Chi tiết yêu cầu
          </h3>
          <div className="space-y-2 text-sm">
            <div>
              <p className="text-gray-600">Loại dịch vụ:</p>
              <p className="font-semibold text-gray-800">{currentRequest.serviceType}</p>
            </div>
            <div>
              <p className="text-gray-600">Thời gian tạo:</p>
              <p className="font-semibold text-gray-800">
                {new Date(currentRequest.createdAt).toLocaleString('vi-VN')}
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* Location Information */}
      <div className="bg-blue-50 p-4 rounded-lg mb-6">
        <h3 className="font-bold text-gray-800 mb-3 flex items-center">
          <MapPin size={18} className="mr-2 text-blue-600" />
          Vị trí yêu cầu
        </h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <p className="text-sm text-gray-600">Địa chỉ:</p>
            <p className="font-semibold text-gray-800">{currentRequest.location}</p>
          </div>
          <div>
            <p className="text-sm text-gray-600">Vĩ độ:</p>
            <p className="font-mono text-gray-800">{currentRequest.latitude.toFixed(6)}</p>
          </div>
          <div>
            <p className="text-sm text-gray-600">Kinh độ:</p>
            <p className="font-mono text-gray-800">{currentRequest.longitude.toFixed(6)}</p>
          </div>
        </div>
        <button
          onClick={openGoogleMaps}
          className="mt-4 w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded flex items-center justify-center transition"
        >
          <Navigation size={18} className="mr-2" />
          Mở Google Maps
        </button>
      </div>

      {/* Description */}
      {currentRequest.description && (
        <div className="bg-gray-50 p-4 rounded-lg mb-6">
          <h3 className="font-bold text-gray-800 mb-2">Mô tả chi tiết:</h3>
          <p className="text-gray-700 whitespace-pre-wrap">{currentRequest.description}</p>
        </div>
      )}

      {/* Rejection Reason */}
      {currentRequest.rejectionReason && (
        <div className="bg-red-50 p-4 rounded-lg mb-6 border border-red-200">
          <h3 className="font-bold text-red-800 mb-2">Lý do từ chối:</h3>
          <p className="text-red-700">{currentRequest.rejectionReason}</p>
        </div>
      )}

      {/* Action Buttons */}
      <div className="border-t pt-6">
        <div className="flex flex-wrap gap-3">
          {canAccept && (
            <button
              onClick={handleAccept}
              disabled={loading}
              className="flex-1 min-w-xs bg-green-600 hover:bg-green-700 disabled:opacity-50 text-white font-bold py-3 px-4 rounded transition flex items-center justify-center"
            >
              <CheckCircle size={18} className="mr-2" />
              Chấp Nhận Yêu Cầu
            </button>
          )}

          {canUpdateStatus && (
            <>
              <button
                onClick={() => handleStatusUpdate('IN_TRANSIT')}
                disabled={loading}
                className="flex-1 min-w-xs bg-purple-600 hover:bg-purple-700 disabled:opacity-50 text-white font-bold py-3 px-4 rounded transition flex items-center justify-center"
              >
                <Truck size={18} className="mr-2" />
                Đang Tới
              </button>
              <button
                onClick={() => handleStatusUpdate('IN_PROGRESS')}
                disabled={loading}
                className="flex-1 min-w-xs bg-orange-600 hover:bg-orange-700 disabled:opacity-50 text-white font-bold py-3 px-4 rounded transition flex items-center justify-center"
              >
                <Wrench size={18} className="mr-2" />
                Đang Xử Lý
              </button>
              <button
                onClick={() => handleStatusUpdate('COMPLETED')}
                disabled={loading}
                className="flex-1 min-w-xs bg-green-600 hover:bg-green-700 disabled:opacity-50 text-white font-bold py-3 px-4 rounded transition flex items-center justify-center"
              >
                <CheckCircle size={18} className="mr-2" />
                Hoàn Thành
              </button>
            </>
          )}

          {canReject && (
            <button
              onClick={() => setShowRejectModal(true)}
              disabled={loading}
              className="flex-1 min-w-xs bg-red-600 hover:bg-red-700 disabled:opacity-50 text-white font-bold py-3 px-4 rounded transition flex items-center justify-center"
            >
              <XCircle size={18} className="mr-2" />
              Từ Chối
            </button>
          )}
        </div>
      </div>

      {/* Reject Modal */}
      {showRejectModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg p-6 max-w-md w-full">
            <h3 className="text-xl font-bold text-gray-800 mb-4">Từ Chối Yêu Cầu</h3>
            <p className="text-gray-600 mb-4">Vui lòng nhập lý do từ chối:</p>
            <textarea
              value={rejectReason}
              onChange={(e) => setRejectReason(e.target.value)}
              placeholder="Nhập lý do từ chối..."
              className="w-full p-3 border border-gray-300 rounded-lg mb-4 focus:outline-none focus:ring-2 focus:ring-red-600"
              rows="4"
            />
            <div className="flex gap-3">
              <button
                onClick={() => setShowRejectModal(false)}
                className="flex-1 bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold py-2 rounded transition"
              >
                Hủy
              </button>
              <button
                onClick={handleReject}
                disabled={loading}
                className="flex-1 bg-red-600 hover:bg-red-700 disabled:opacity-50 text-white font-bold py-2 rounded transition"
              >
                {loading ? 'Đang xử lý...' : 'Từ Chối'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default RescueRequestDetail;
