import React, { useState, useEffect } from 'react';
import { MapPin, List, Map, Filter, RefreshCw, Sliders } from 'lucide-react';
import CompanyList from '../components/CompanyList.jsx';
import CompanyMapDisplay from '../components/CompanyMapDisplay.jsx';
import { searchNearbyCompanies } from '../service/companySearchService.js';
import { loadAuth } from '../utils/authStorage.js';

const SearchMapView = () => {
  // State
  const [viewMode, setViewMode] = useState('list'); // 'list' or 'map'
  const [companies, setCompanies] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [userLocation, setUserLocation] = useState(null);
  const [selectedCompany, setSelectedCompany] = useState(null);
  const [filters, setFilters] = useState({
    maxDistance: 50,
    serviceType: 'all',
  });
  const [showFilters, setShowFilters] = useState(false);

  // Get user location on mount
  useEffect(() => {
    getUserLocation();
  }, []);

  // Search when location or filters change
  useEffect(() => {
    if (userLocation) {
      searchCompanies();
    }
  }, [userLocation, filters]);

  const getUserLocation = () => {
    setError(null);
    if ('geolocation' in navigator) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          const { latitude, longitude } = position.coords;
          setUserLocation({ latitude, longitude });
          console.log('User location:', latitude, longitude);
        },
        (err) => {
          console.error('Geolocation error:', err);
          setError('Không thể lấy vị trí của bạn. Vui lòng bật GPS và thử lại.');
          // Fallback location (Hà Nội)
          setUserLocation({ latitude: 21.0285, longitude: 105.8542 });
        }
      );
    } else {
      setError('Trình duyệt của bạn không hỗ trợ định vị GPS.');
      // Fallback location
      setUserLocation({ latitude: 21.0285, longitude: 105.8542 });
    }
  };

  const searchCompanies = async () => {
    if (!userLocation) return;

    setLoading(true);
    setError(null);

    try {
      const auth = loadAuth();
      const data = await searchNearbyCompanies({
        latitude: userLocation.latitude,
        longitude: userLocation.longitude,
        maxDistance: filters.maxDistance,
        token: auth?.token,
      });

      // Transform response data
      let companiesList = data.content || [];

      // Filter by service type if needed
      if (filters.serviceType !== 'all') {
        companiesList = companiesList.filter((company) =>
          company.services?.some(
            (service) => service.toLowerCase().includes(filters.serviceType.toLowerCase())
          )
        );
      }

      setCompanies(companiesList);

      if (companiesList.length === 0) {
        setError(`Không tìm thấy công ty nào trong bán kính ${filters.maxDistance}km`);
      }
    } catch (err) {
      console.error('Search error:', err);
      setError(err.message || 'Lỗi khi tìm kiếm công ty');
    } finally {
      setLoading(false);
    }
  };

  const handleCompanySelect = (company) => {
    setSelectedCompany(company);
  };

  const handleFilterChange = (newFilters) => {
    setFilters({ ...filters, ...newFilters });
    setShowFilters(false);
  };

  const handleRefresh = () => {
    getUserLocation();
  };

  return (
    <main className="min-h-screen bg-gray-50 py-4 px-4">
      <div className="container mx-auto max-w-7xl">
        {/* Header */}
        <div className="mb-6">
          <h1 className="text-3xl font-bold text-gray-800 mb-2">Tìm Công Ty Cứu Hộ Gần Bạn</h1>
          <p className="text-gray-600">
            Tìm kiếm nhanh các công ty cứu hộ uy tín trong khu vực của bạn
          </p>
        </div>

        {/* Error Message */}
        {error && (
          <div className="mb-4 p-4 bg-yellow-50 border border-yellow-200 rounded-lg flex items-start">
            <MapPin size={20} className="text-yellow-600 mr-3 mt-0.5 flex-shrink-0" />
            <p className="text-yellow-800">{error}</p>
          </div>
        )}

        {/* Controls */}
        <div className="mb-6 bg-white p-4 rounded-lg shadow flex flex-wrap items-center gap-4">
          <div className="flex items-center gap-2">
            <button
              onClick={() => setViewMode('list')}
              className={`flex items-center gap-2 px-4 py-2 rounded transition ${
                viewMode === 'list'
                  ? 'bg-blue-900 text-white'
                  : 'bg-gray-200 text-gray-800 hover:bg-gray-300'
              }`}
            >
              <List size={18} />
              Danh Sách
            </button>
            <button
              onClick={() => setViewMode('map')}
              className={`flex items-center gap-2 px-4 py-2 rounded transition ${
                viewMode === 'map'
                  ? 'bg-blue-900 text-white'
                  : 'bg-gray-200 text-gray-800 hover:bg-gray-300'
              }`}
            >
              <Map size={18} />
              Bản Đồ
            </button>
          </div>

          <div className="flex-1"></div>

          <button
            onClick={handleRefresh}
            disabled={loading}
            className="flex items-center gap-2 px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700 transition disabled:opacity-50"
          >
            <RefreshCw size={18} className={loading ? 'animate-spin' : ''} />
            Tìm Lại
          </button>

          <button
            onClick={() => setShowFilters(!showFilters)}
            className="flex items-center gap-2 px-4 py-2 bg-gray-200 text-gray-800 rounded hover:bg-gray-300 transition"
          >
            <Sliders size={18} />
            Bộ Lọc
          </button>
        </div>

        {/* Filters Panel */}
        {showFilters && (
          <div className="mb-6 bg-white p-4 rounded-lg shadow">
            <h3 className="font-bold text-gray-800 mb-4">Bộ Lọc Tìm Kiếm</h3>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">
                  Bán Kính Tìm Kiếm: {filters.maxDistance}km
                </label>
                <input
                  type="range"
                  min="5"
                  max="100"
                  step="5"
                  value={filters.maxDistance}
                  onChange={(e) => handleFilterChange({ maxDistance: parseFloat(e.target.value) })}
                  className="w-full"
                />
              </div>

              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">
                  Loại Dịch Vụ
                </label>
                <select
                  value={filters.serviceType}
                  onChange={(e) => handleFilterChange({ serviceType: e.target.value })}
                  className="w-full p-2 border border-gray-300 rounded"
                >
                  <option value="all">Tất Cả Dịch Vụ</option>
                  <option value="sửa chữa">Sửa Chữa</option>
                  <option value="nổ lốp">Nổ Lốp</option>
                  <option value="mất điện">Mất Điện</option>
                  <option value="kéo xe">Kéo Xe</option>
                  <option value="khác">Dịch Vụ Khác</option>
                </select>
              </div>

              <div className="flex items-end">
                <button
                  onClick={() => {
                    setFilters({ maxDistance: 50, serviceType: 'all' });
                    setShowFilters(false);
                  }}
                  className="w-full px-4 py-2 bg-gray-300 text-gray-800 rounded hover:bg-gray-400 transition"
                >
                  Xóa Bộ Lọc
                </button>
              </div>
            </div>
          </div>
        )}

        {/* Main Content */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* List/Map View */}
          <div className="lg:col-span-2">
            {viewMode === 'list' ? (
              <div className="bg-white p-4 rounded-lg shadow">
                <h2 className="text-xl font-bold text-gray-800 mb-4">
                  Công Ty Gần Bạn ({companies.length})
                </h2>
                <CompanyList
                  companies={companies}
                  loading={loading}
                  onCompanySelect={handleCompanySelect}
                  selectedCompanyId={selectedCompany?.id}
                />
              </div>
            ) : (
              <div className="bg-white rounded-lg shadow overflow-hidden">
                <CompanyMapDisplay
                  companies={companies}
                  userLocation={userLocation}
                  selectedCompanyId={selectedCompany?.id}
                  onCompanySelect={handleCompanySelect}
                />
              </div>
            )}
          </div>

          {/* Company Details Sidebar */}
          <div className="lg:col-span-1">
            {selectedCompany ? (
              <div className="bg-white rounded-lg shadow p-6 sticky top-4">
                <h3 className="text-xl font-bold text-gray-800 mb-2">{selectedCompany.name}</h3>
                
                <div className="space-y-3 text-sm text-gray-600">
                  {selectedCompany.distance && (
                    <div className="flex items-center">
                      <MapPin size={16} className="mr-2 text-blue-600" />
                      <span>{selectedCompany.distance.toFixed(1)} km từ bạn</span>
                    </div>
                  )}

                  {selectedCompany.rating && (
                    <div className="flex items-center">
                      <span className="text-yellow-500">★</span>
                      <span className="ml-2">{selectedCompany.rating}/5 (Đánh giá)</span>
                    </div>
                  )}

                  {selectedCompany.responseTime && (
                    <div className="flex items-center">
                      <span className="font-semibold">Thời gian phản hồi:</span>
                      <span className="ml-2">{selectedCompany.responseTime} phút</span>
                    </div>
                  )}

                  {selectedCompany.phone && (
                    <div className="flex items-center">
                      <span className="font-semibold">Điện thoại:</span>
                      <a 
                        href={`tel:${selectedCompany.phone}`}
                        className="ml-2 text-blue-600 hover:underline"
                      >
                        {selectedCompany.phone}
                      </a>
                    </div>
                  )}

                  {selectedCompany.address && (
                    <div>
                      <span className="font-semibold">Địa chỉ:</span>
                      <p className="mt-1">{selectedCompany.address}</p>
                    </div>
                  )}
                </div>

                {selectedCompany.services && selectedCompany.services.length > 0 && (
                  <div className="mt-4 pt-4 border-t">
                    <h4 className="font-semibold text-gray-800 mb-2">Dịch Vụ</h4>
                    <div className="flex flex-wrap gap-2">
                      {selectedCompany.services.map((service, idx) => (
                        <span key={idx} className="bg-blue-100 text-blue-800 text-xs px-3 py-1 rounded-full">
                          {typeof service === 'string' ? service : service.name || service.type}
                        </span>
                      ))}
                    </div>
                  </div>
                )}

                <button className="w-full mt-6 bg-red-600 hover:bg-red-700 text-white font-bold py-3 rounded transition">
                  Gọi Cứu Hộ Ngay
                </button>

                <button className="w-full mt-2 bg-gray-200 hover:bg-gray-300 text-gray-800 font-bold py-2 rounded transition">
                  Xem Chi Tiết
                </button>
              </div>
            ) : (
              <div className="bg-white rounded-lg shadow p-6 flex items-center justify-center h-96">
                <div className="text-center text-gray-500">
                  <p>Chọn một công ty từ danh sách để xem chi tiết</p>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </main>
  );
};

export default SearchMapView;
