import React, { useState } from 'react';
import { AlertCircle, X } from 'lucide-react';
import { cancelRescueRequest } from '../service/rescueRequestService';

const CancelRequestModal = ({ isOpen, requestId, onClose, onSuccess }) => {
  const [reason, setReason] = useState('');
  const [cancelling, setCancelling] = useState(false);
  const [error, setError] = useState(null);

  const handleCancel = async () => {
    if (!reason.trim()) {
      setError('Vui lòng nhập lý do hủy yêu cầu');
      return;
    }

    setCancelling(true);
    setError(null);

    try {
      await cancelRescueRequest(requestId, reason);
      setReason('');
      onSuccess?.();
      onClose();
    } catch (err) {
      setError(err?.message || 'Hủy yêu cầu thất bại');
    } finally {
      setCancelling(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-lg shadow-xl max-w-md w-full">
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b border-gray-200">
          <div className="flex items-center gap-3">
            <AlertCircle className="text-red-500" size={24} />
            <h2 className="text-xl font-bold text-gray-900">Hủy yêu cầu</h2>
          </div>
          <button
            onClick={onClose}
            className="text-gray-500 hover:text-gray-700"
          >
            <X size={24} />
          </button>
        </div>

        {/* Body */}
        <div className="p-6">
          <p className="text-gray-600 mb-4">
            Bạn có chắc muốn hủy yêu cầu này không? Vui lòng cho chúng tôi biết lý do.
          </p>

          <textarea
            value={reason}
            onChange={(e) => setReason(e.target.value)}
            placeholder="Nhập lý do hủy yêu cầu (ví dụ: Chờ quá lâu, đã tìm được trợ giúp khác...)"
            className="w-full h-24 p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500 focus:border-transparent resize-none"
          />

          {error && (
            <div className="mt-4 p-3 bg-red-100 border border-red-400 rounded-lg">
              <p className="text-sm text-red-700">{error}</p>
            </div>
          )}
        </div>

        {/* Footer */}
        <div className="flex gap-3 p-6 border-t border-gray-200 bg-gray-50">
          <button
            onClick={onClose}
            disabled={cancelling}
            className="flex-1 py-2 px-4 border border-gray-300 rounded-lg text-gray-700 font-medium hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed transition"
          >
            Không hủy
          </button>
          <button
            onClick={handleCancel}
            disabled={cancelling}
            className="flex-1 py-2 px-4 bg-red-500 text-white rounded-lg font-medium hover:bg-red-600 disabled:opacity-50 disabled:cursor-not-allowed transition"
          >
            {cancelling ? 'Đang hủy...' : 'Xác nhận hủy'}
          </button>
        </div>
      </div>
    </div>
  );
};

export default CancelRequestModal;
