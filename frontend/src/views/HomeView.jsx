import React from 'react';
import { Phone, Wrench } from 'lucide-react';

const HomeView = ({ onNavigate }) => {
  return (
    <main>
      {/* Hero Section */}
      <section className="bg-blue-900 text-white py-20">
        <div className="container mx-auto px-4 flex flex-col md:flex-row items-center">
          <div className="md:w-1/2 mb-10 md:mb-0">
            <h1 className="text-4xl md:text-5xl font-bold mb-6 leading-tight">
              Sự cố trên đường? <br/>
              <span className="text-yellow-400">Có ResQOnRoad lo!</span>
            </h1>
            <p className="text-lg mb-8 text-blue-100">
              Kết nối nhanh chóng với hơn 500+ đơn vị cứu hộ uy tín. Phục vụ 24/7, mọi lúc, mọi nơi.
            </p>
            <div className="flex space-x-4">
              <button 
                onClick={() => alert("Chức năng đang phát triển: Gửi yêu cầu nhanh")}
                className="bg-red-600 hover:bg-red-700 text-white font-bold py-3 px-8 rounded-full shadow-lg transform transition hover:scale-105 flex items-center"
              >
                <Phone className="mr-2" /> GỌI CỨU HỘ NGAY
              </button>
            </div>
          </div>
          <div className="md:w-1/2 flex justify-center">
            <div className="bg-blue-800 p-8 rounded-full h-80 w-80 flex items-center justify-center shadow-2xl relative">
              <div className="absolute animate-ping h-full w-full rounded-full bg-blue-500 opacity-20"></div>
              <Wrench size={100} className="text-yellow-400" />
            </div>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="bg-gray-100 py-16">
        <div className="container mx-auto px-4 text-center">
          <h2 className="text-3xl font-bold text-gray-800 mb-4">Bạn là đơn vị cứu hộ?</h2>
          <p className="text-gray-600 mb-8 max-w-2xl mx-auto">Tham gia vào mạng lưới ResQOnRoad để tiếp cận hàng ngàn khách hàng tiềm năng và quản lý đội xe hiệu quả hơn.</p>
          <button onClick={() => onNavigate('register')} className="bg-blue-900 text-white font-bold py-3 px-8 rounded hover:bg-blue-800 transition">
            Đăng ký đối tác ngay
          </button>
        </div>
      </section>
    </main>
  );
};

export default HomeView;