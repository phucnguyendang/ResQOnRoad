import React, { useEffect, useRef } from 'react';
import { MapPin } from 'lucide-react';

const CompanyMapDisplay = ({ companies, userLocation, selectedCompanyId, onCompanySelect }) => {
  const mapRef = useRef(null);
  const mapInstanceRef = useRef(null);
  const markersRef = useRef({});

  useEffect(() => {
    // Initialize map only once
    if (!mapInstanceRef.current && mapRef.current) {
      initializeMap();
    }
  }, []);

  const initializeMap = () => {
    // Using OpenStreetMap with Leaflet-like API
    // For now, we'll create a simple canvas-based map representation
    const canvas = mapRef.current;
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    ctx.fillStyle = '#e5e7eb';
    ctx.fillRect(0, 0, canvas.width, canvas.height);

    // Draw grid
    ctx.strokeStyle = '#d1d5db';
    ctx.lineWidth = 1;
    for (let i = 0; i < canvas.width; i += 50) {
      ctx.beginPath();
      ctx.moveTo(i, 0);
      ctx.lineTo(i, canvas.height);
      ctx.stroke();
    }
    for (let i = 0; i < canvas.height; i += 50) {
      ctx.beginPath();
      ctx.moveTo(0, i);
      ctx.lineTo(canvas.width, i);
      ctx.stroke();
    }

    mapInstanceRef.current = { ctx, canvas };
  };

  useEffect(() => {
    if (!mapInstanceRef.current || !userLocation || !companies) return;

    const { ctx, canvas } = mapInstanceRef.current;

    // Clear canvas
    ctx.fillStyle = '#e5e7eb';
    ctx.fillRect(0, 0, canvas.width, canvas.height);

    // Draw grid
    ctx.strokeStyle = '#d1d5db';
    ctx.lineWidth = 1;
    for (let i = 0; i < canvas.width; i += 50) {
      ctx.beginPath();
      ctx.moveTo(i, 0);
      ctx.lineTo(i, canvas.height);
      ctx.stroke();
    }
    for (let i = 0; i < canvas.height; i += 50) {
      ctx.beginPath();
      ctx.moveTo(0, i);
      ctx.lineTo(canvas.width, i);
      ctx.stroke();
    }

    // Draw user location (center)
    const centerX = canvas.width / 2;
    const centerY = canvas.height / 2;

    ctx.fillStyle = '#2563eb';
    ctx.beginPath();
    ctx.arc(centerX, centerY, 8, 0, 2 * Math.PI);
    ctx.fill();

    // Draw company markers
    const maxDistance = Math.max(...companies.map(c => c.distance || 0));
    const scale = Math.min(canvas.width, canvas.height) / (maxDistance * 2.5 || 200);

    companies.forEach((company, idx) => {
      if (!company.latitude || !company.longitude) return;

      // Calculate position on canvas
      const angle = (idx / companies.length) * 2 * Math.PI;
      const distance = (company.distance || 5) * scale;
      const x = centerX + distance * Math.cos(angle);
      const y = centerY + distance * Math.sin(angle);

      // Draw company marker
      const isSelected = selectedCompanyId === company.id;
      ctx.fillStyle = isSelected ? '#dc2626' : '#f97316';
      ctx.beginPath();
      ctx.arc(x, y, isSelected ? 10 : 6, 0, 2 * Math.PI);
      ctx.fill();

      if (isSelected) {
        ctx.strokeStyle = '#991b1b';
        ctx.lineWidth = 2;
        ctx.beginPath();
        ctx.arc(x, y, 14, 0, 2 * Math.PI);
        ctx.stroke();
      }

      // Draw label
      ctx.fillStyle = '#1f2937';
      ctx.font = 'bold 11px Arial';
      ctx.textAlign = 'center';
      ctx.fillText(company.name.substring(0, 10), x, y - 15);
    });
  }, [companies, userLocation, selectedCompanyId]);

  return (
    <div className="flex flex-col h-full bg-white rounded-lg shadow">
      <div className="flex-1 flex flex-col">
        <div className="flex-1 relative bg-gray-100 rounded-t-lg overflow-hidden">
          <canvas
            ref={mapRef}
            width={800}
            height={600}
            className="w-full h-full cursor-pointer"
            onClick={(e) => handleMapClick(e)}
          />
          {!userLocation && (
            <div className="absolute inset-0 bg-black bg-opacity-50 flex items-center justify-center rounded-t-lg">
              <div className="text-center text-white">
                <MapPin size={48} className="mx-auto mb-2" />
                <p>Đang lấy vị trí của bạn...</p>
              </div>
            </div>
          )}
        </div>
        <div className="p-4 bg-white rounded-b-lg border-t">
          <div className="flex items-center gap-4 text-sm">
            <div className="flex items-center">
              <div className="w-3 h-3 bg-blue-600 rounded-full mr-2"></div>
              <span className="text-gray-600">Bạn ở đây</span>
            </div>
            <div className="flex items-center">
              <div className="w-3 h-3 bg-orange-500 rounded-full mr-2"></div>
              <span className="text-gray-600">Công ty gần nhất</span>
            </div>
            <div className="flex items-center">
              <div className="w-3 h-3 bg-red-600 rounded-full mr-2"></div>
              <span className="text-gray-600">Công ty được chọn</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );

  function handleMapClick(e) {
    const rect = mapRef.current.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;

    const centerX = mapRef.current.width / 2;
    const centerY = mapRef.current.height / 2;

    const maxDistance = Math.max(...(companies?.map(c => c.distance || 0) || [0]));
    const scale = Math.min(mapRef.current.width, mapRef.current.height) / (maxDistance * 2.5 || 200);

    companies?.forEach((company, idx) => {
      const angle = (idx / (companies?.length || 1)) * 2 * Math.PI;
      const distance = (company.distance || 5) * scale;
      const markerX = centerX + distance * Math.cos(angle);
      const markerY = centerY + distance * Math.sin(angle);

      const dist = Math.sqrt((x - markerX) ** 2 + (y - markerY) ** 2);
      if (dist < 20) {
        onCompanySelect(company);
      }
    });
  }
};

export default CompanyMapDisplay;
