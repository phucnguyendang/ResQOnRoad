import React from 'react';
import { MapPin, Phone, Star, DollarSign, Clock } from 'lucide-react';

const CompanyList = ({ companies, loading, onCompanySelect, selectedCompanyId }) => {
  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-900 mx-auto mb-4"></div>
          <p className="text-gray-600">Đang tìm kiếm công ty gần bạn...</p>
        </div>
      </div>
    );
  }

  if (!companies || companies.length === 0) {
    return (
      <div className="flex items-center justify-center h-96">
        <div className="text-center">
          <MapPin size={48} className="mx-auto text-gray-400 mb-4" />
          <p className="text-gray-600">Không tìm thấy công ty cứu hộ nào gần bạn</p>
          <p className="text-sm text-gray-500 mt-2">Hãy thử tăng bán kính tìm kiếm</p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {companies.map((company) => (
        <div
          key={company.id}
          onClick={() => onCompanySelect(company)}
          className={`p-4 rounded-lg border-2 cursor-pointer transition-all ${
            selectedCompanyId === company.id
              ? 'border-blue-900 bg-blue-50 shadow-lg'
              : 'border-gray-200 bg-white hover:border-blue-500 hover:shadow-md'
          }`}
        >
          <div className="flex justify-between items-start mb-2">
            <div className="flex-1">
              <h3 className="font-bold text-gray-800 text-lg">{company.name}</h3>
              <div className="flex items-center text-sm text-gray-600 mt-1">
                <MapPin size={16} className="mr-1" />
                <span>{company.distance?.toFixed(1)} km</span>
              </div>
            </div>
            {company.rating && (
              <div className="flex items-center bg-yellow-100 px-2 py-1 rounded">
                <Star size={16} className="text-yellow-500 mr-1" fill="currentColor" />
                <span className="font-semibold text-sm">{company.rating}</span>
              </div>
            )}
          </div>

          <p className="text-gray-700 text-sm mb-3">{company.description}</p>

          <div className="grid grid-cols-2 gap-2 mb-3">
            {company.phone && (
              <div className="flex items-center text-sm text-gray-600">
                <Phone size={14} className="mr-2" />
                <a href={`tel:${company.phone}`} className="text-blue-600 hover:underline">
                  {company.phone}
                </a>
              </div>
            )}
            {company.responseTime && (
              <div className="flex items-center text-sm text-gray-600">
                <Clock size={14} className="mr-2" />
                <span>{company.responseTime} phút</span>
              </div>
            )}
          </div>

          {company.services && company.services.length > 0 && (
            <div className="mb-3">
              <p className="text-xs font-semibold text-gray-600 mb-1">Dịch vụ:</p>
              <div className="flex flex-wrap gap-1">
                {company.services.slice(0, 3).map((service, idx) => (
                  <span key={idx} className="text-xs bg-blue-100 text-blue-800 px-2 py-1 rounded">
                    {typeof service === 'string' ? service : service.name || service.type}
                  </span>
                ))}
                {company.services.length > 3 && (
                  <span className="text-xs bg-gray-100 text-gray-600 px-2 py-1 rounded">
                    +{company.services.length - 3}
                  </span>
                )}
              </div>
            </div>
          )}

          <button className="w-full bg-red-600 hover:bg-red-700 text-white font-bold py-2 rounded transition">
            Gọi Cứu Hộ
          </button>
        </div>
      ))}
    </div>
  );
};

export default CompanyList;
