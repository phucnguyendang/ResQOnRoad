import React from 'react';
import { Car, Phone, MapPin, User } from 'lucide-react';

const Footer = () => {
  return (
    <footer className="bg-gray-800 text-gray-300 py-8">
      <div className="container mx-auto px-4 grid grid-cols-1 md:grid-cols-3 gap-8">
        <div>
          <div className="flex items-center space-x-2 mb-4">
            <Car className="text-yellow-500" />
            <span className="text-xl font-bold text-white">ResQOnRoad</span>
          </div>
          <p className="text-sm">
            Ứng dụng hỗ trợ cứu hộ giao thông 24/7. Kết nối bạn với những đơn vị cứu hộ uy tín nhất trong thời gian ngắn nhất.
          </p>
        </div>
        <div>
          <h3 className="text-white font-bold mb-4">Liên hệ</h3>
          <ul className="space-y-2 text-sm">
            <li className="flex items-center space-x-2"><Phone size={16} /> <span>1900 1234</span></li>
            <li className="flex items-center space-x-2"><MapPin size={16} /> <span>1 Đại Cồ Việt, Hà Nội</span></li>
            <li className="flex items-center space-x-2"><User size={16} /> <span>support@resqonroad.vn</span></li>
          </ul>
        </div>
        <div>
          <h3 className="text-white font-bold mb-4">Dịch vụ chính</h3>
          <ul className="space-y-2 text-sm">
            <li>Vá lốp lưu động</li>
            <li>Kích bình ắc quy</li>
            <li>Cứu hộ xe tai nạn</li>
            <li>Tiếp nhiên liệu</li>
          </ul>
        </div>
      </div>
      <div className="text-center mt-8 pt-4 border-t border-gray-700 text-sm">
        © 2025 ResQOnRoad. All rights reserved.
      </div>
    </footer>
  );
};

export default Footer;