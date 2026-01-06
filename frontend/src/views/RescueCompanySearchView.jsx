import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { Star, Search } from 'lucide-react';
import { searchNearbyCompanies } from '../service/companyService';
import { setLastCompanyId } from '../utils/companyStorage';

function StarRow({ value = 0 }) {
  const safe = Math.max(0, Math.min(5, Number(value) || 0));
  const full = Math.round(safe);
  return (
    <div className="inline-flex items-center gap-1" aria-label={`Rating ${safe} / 5`}>
      {Array.from({ length: 5 }).map((_, i) => (
        <Star
          key={i}
          size={16}
          className={i < full ? 'text-yellow-500' : 'text-gray-300'}
          fill={i < full ? 'currentColor' : 'none'}
        />
      ))}
    </div>
  );
}

function normalizeText(value) {
  return String(value || '').trim().toLowerCase();
}

export default function RescueCompanySearchView({ onNavigate }) {
  const [query, setQuery] = useState('');
  const [maxDistance, setMaxDistance] = useState(10);

  const [latitude, setLatitude] = useState('');
  const [longitude, setLongitude] = useState('');
  const [locating, setLocating] = useState(false);
  const [locationError, setLocationError] = useState('');

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [companies, setCompanies] = useState([]);

  const canGetLocation = useMemo(() => typeof navigator !== 'undefined' && !!navigator.geolocation, []);

  const clampDistance = useCallback((value) => {
    const n = Number(value);
    if (!Number.isFinite(n)) return 10;
    return Math.min(50, Math.max(1, Math.round(n)));
  }, []);

  const hasCoords = useMemo(() => {
    if (latitude === '' || longitude === '') return false;
    const lat = Number(latitude);
    const lng = Number(longitude);
    return Number.isFinite(lat) && Number.isFinite(lng);
  }, [latitude, longitude]);

  const handleGetLocation = useCallback(() => {
    setLocationError('');
    setError('');

    if (!canGetLocation) {
      setLocationError('Trình duyệt không hỗ trợ lấy vị trí (Geolocation).');
      return;
    }

    setLocating(true);
    const timeoutId = setTimeout(() => {
      setLocating(false);
      setLocationError('Hết thời gian chờ GPS (30s). Vui lòng kiểm tra quyền vị trí và thử lại.');
    }, 30000);

    navigator.geolocation.getCurrentPosition(
      (pos) => {
        clearTimeout(timeoutId);
        setLatitude(String(pos.coords.latitude));
        setLongitude(String(pos.coords.longitude));
        setLocating(false);
        setLocationError('');
      },
      (err) => {
        clearTimeout(timeoutId);
        setLocating(false);

        let message = '';
        if (err.code === 1) message = 'Bạn đã từ chối quyền vị trí. Vui lòng cấp quyền và thử lại.';
        else if (err.code === 2) message = 'Không thể lấy vị trí. Bật GPS/định vị và thử lại.';
        else if (err.code === 3) message = 'Hết thời gian chờ GPS. Thử lại ở nơi có tín hiệu tốt hơn.';
        else message = err?.message || 'Lỗi GPS không xác định';

        setLocationError(message);
      },
      { enableHighAccuracy: false, timeout: 30000, maximumAge: 0 }
    );
  }, [canGetLocation]);

  useEffect(() => {
    handleGetLocation();
  }, [handleGetLocation]);

  const loadCompanies = useCallback(async () => {
    if (!hasCoords) {
      setCompanies([]);
      return;
    }

    setLoading(true);
    setError('');
    try {
      const page = await searchNearbyCompanies({
        lat: Number(latitude),
        lng: Number(longitude),
        maxDistance: clampDistance(maxDistance),
        page: 0,
        size: 50,
      });
      const list = Array.isArray(page?.content) ? page.content : [];
      setCompanies(list);
    } catch (e) {
      setCompanies([]);
      setError(e?.message || 'Không thể tải danh sách công ty cứu hộ');
    } finally {
      setLoading(false);
    }
  }, [hasCoords, latitude, longitude, maxDistance]);

  useEffect(() => {
    loadCompanies();
  }, [loadCompanies]);

  const filteredCompanies = useMemo(() => {
    const q = normalizeText(query);
    if (!q) return companies;
    return companies.filter((c) => {
      const name = normalizeText(c?.name);
      const address = normalizeText(c?.address);
      return name.includes(q) || address.includes(q);
    });
  }, [companies, query]);

  const handleOpenCompany = (companyId) => {
    if (!companyId) return;
    setLastCompanyId(companyId);
    onNavigate?.('companyProfilePublic');
  };

  return (
    <div className="min-h-[80vh] bg-gray-100 py-10">
      <div className="container mx-auto px-4">
        <div className="max-w-5xl mx-auto bg-white rounded-xl shadow-lg p-6">
          <div className="flex items-start justify-between gap-3 flex-wrap">
            <div>
              <h1 className="text-2xl font-extrabold text-gray-900">Tìm công ty cứu hộ</h1>
              <p className="text-sm text-gray-600 mt-1">Tìm theo vị trí hiện tại, lọc theo khoảng cách và tên/địa chỉ.</p>
            </div>
            <div className="flex items-center gap-2">
              <button
                type="button"
                onClick={handleGetLocation}
                className="bg-gray-200 text-gray-900 font-semibold px-4 py-2 rounded hover:bg-gray-300 text-sm"
                disabled={locating}
              >
                {locating ? 'Đang lấy GPS...' : 'Lấy lại GPS'}
              </button>
              <button
                type="button"
                onClick={() => onNavigate?.('home')}
                className="bg-blue-900 text-white font-bold px-4 py-2 rounded hover:bg-blue-800"
              >
                Quay lại
              </button>
            </div>
          </div>

          {locationError ? (
            <div className="mt-4 text-sm text-red-700 bg-red-50 border border-red-200 rounded-md p-3">
              {locationError}
            </div>
          ) : null}

          <div className="mt-6 grid grid-cols-1 md:grid-cols-3 gap-3">
            <div className="md:col-span-2">
              <label className="block text-sm font-medium text-gray-700 mb-1">Tìm theo tên/địa chỉ</label>
              <div className="relative">
                <div className="absolute inset-y-0 left-3 flex items-center pointer-events-none">
                  <Search size={18} className="text-gray-400" />
                </div>
                <input
                  type="text"
                  value={query}
                  onChange={(e) => setQuery(e.target.value)}
                  className="w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                  placeholder="Ví dụ: Ba Đình, Gara 247..."
                />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Khoảng cách</label>
              <div className="flex items-center gap-2">
                <button
                  type="button"
                  onClick={() => setMaxDistance((prev) => clampDistance((Number(prev) || 10) - 1))}
                  className="px-3 py-2 border border-gray-300 rounded-md bg-white hover:bg-gray-50 disabled:opacity-60"
                  disabled={!hasCoords}
                  aria-label="Giảm khoảng cách"
                >
                  -
                </button>
                <input
                  type="number"
                  min={1}
                  max={50}
                  step={1}
                  value={String(maxDistance)}
                  onChange={(e) => setMaxDistance(clampDistance(e.target.value))}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500 bg-white"
                  disabled={!hasCoords}
                />
                <span className="text-sm text-gray-700 shrink-0">km</span>
                <button
                  type="button"
                  onClick={() => setMaxDistance((prev) => clampDistance((Number(prev) || 10) + 1))}
                  className="px-3 py-2 border border-gray-300 rounded-md bg-white hover:bg-gray-50 disabled:opacity-60"
                  disabled={!hasCoords}
                  aria-label="Tăng khoảng cách"
                >
                  +
                </button>
              </div>
              {!hasCoords ? <div className="text-xs text-gray-500 mt-1">Cần GPS để lọc theo khoảng cách.</div> : <div className="text-xs text-gray-500 mt-1">Tối thiểu 1 km, tối đa 50 km.</div>}
            </div>
          </div>

          {error ? (
            <div className="mt-4 text-sm text-red-700 bg-red-50 border border-red-200 rounded-md p-3">
              {error}
            </div>
          ) : null}

          {loading ? <div className="mt-4 text-sm text-gray-700">Đang tải danh sách...</div> : null}

          {!loading && hasCoords && filteredCompanies.length === 0 ? (
            <div className="mt-6 text-sm text-gray-600">Không tìm thấy công ty nào phù hợp.</div>
          ) : null}

          {filteredCompanies.length > 0 ? (
            <div className="mt-6 grid grid-cols-1 md:grid-cols-2 gap-4">
              {filteredCompanies.map((c) => (
                <button
                  key={c.id}
                  type="button"
                  onClick={() => handleOpenCompany(c.id)}
                  className="text-left bg-gray-50 border border-gray-200 rounded-lg p-4 hover:bg-gray-100"
                >
                  <div className="flex items-start justify-between gap-3">
                    <div>
                      <div className="text-lg font-extrabold text-gray-900">{c.name || '(không có tên)'}</div>
                      <div className="text-sm text-gray-700 mt-1">
                        <span className="font-semibold">Địa chỉ:</span> {c.address || '(không có)'}
                      </div>
                      {c.phone ? (
                        <div className="text-sm text-gray-700 mt-1">
                          <span className="font-semibold">SĐT:</span> {c.phone}
                        </div>
                      ) : null}
                    </div>

                    <div className="text-right shrink-0">
                      <div className="text-xs text-gray-500">Đánh giá</div>
                      <div className="mt-1 flex items-center justify-end gap-2">
                        <StarRow value={c.averageRating ?? 0} />
                        <span className="text-sm font-bold text-gray-900">{Number(c.averageRating ?? 0).toFixed(1)}</span>
                      </div>
                      <div className="text-xs text-gray-500 mt-1">{c.totalReviews ?? 0} lượt</div>
                      {c.distance != null ? (
                        <div className="text-xs text-blue-900 font-semibold mt-2">{Number(c.distance).toFixed(1)} km</div>
                      ) : null}
                    </div>
                  </div>
                </button>
              ))}
            </div>
          ) : null}
        </div>
      </div>
    </div>
  );
}
