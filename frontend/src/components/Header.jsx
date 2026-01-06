import React, { useState } from 'react';
import { Car, Menu, X, LogIn, UserPlus, User } from 'lucide-react';

const Header = ({ onNavigate, currentView, user, onLogout }) => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  // DEBUG: Log user info when it changes
  React.useEffect(() => {
    if (user) {
      console.log('👤 User info:', user);
      console.log('📋 User roles:', user.roles);
      console.log('🏢 Company ID:', user.companyId);
    }
  }, [user]);

  return (
    <header className="bg-blue-900 text-white shadow-md sticky top-0 z-50">
      <div className="container mx-auto px-4 py-3 flex justify-between items-center">
        {/* Logo */}
        <div 
          className="flex items-center space-x-2 cursor-pointer" 
          onClick={() => onNavigate('home')}
        >
          <div className="bg-yellow-500 p-2 rounded-full">
            <Car size={24} className="text-blue-900" />
          </div>
          <span className="text-xl font-bold tracking-wider">ResQOnRoad</span>
        </div>

        {/* Desktop Navigation */}
        <nav className="hidden md:flex items-center space-x-6">
          <button onClick={() => onNavigate('home')} className={`hover:text-yellow-400 ${currentView === 'home' ? 'text-yellow-400 font-semibold' : ''}`}>Trang chủ</button>
          {user?.role === 'USER' && (
            <button onClick={() => onNavigate('createRequest')} className={`hover:text-yellow-400 ${currentView === 'createRequest' ? 'text-yellow-400 font-semibold' : ''}`}>Gửi yêu cầu</button>
          )}
          {(user?.role === 'USER' || user?.role === 'COMPANY') && (
            <button onClick={() => onNavigate('requestList')} className={`hover:text-yellow-400 ${currentView === 'requestList' ? 'text-yellow-400 font-semibold' : ''}`}>Theo dõi</button>
          )}
          <button onClick={() => onNavigate('community')} className={`hover:text-yellow-400 ${currentView === 'community' ? 'text-yellow-400 font-semibold' : ''}`}>Cộng đồng</button>
          {user && user.roles?.includes('ROLE_COMPANY') && (
            <>
              <button onClick={() => onNavigate('serviceManagement')} className={`hover:text-yellow-400 ${currentView === 'serviceManagement' ? 'text-yellow-400 font-semibold' : ''}`}>Quản lý dịch vụ</button>
              <button onClick={() => onNavigate('vehicleManagement')} className={`hover:text-yellow-400 ${currentView === 'vehicleManagement' ? 'text-yellow-400 font-semibold' : ''}`}>Quản lý phương tiện</button>
            </>
          )}
          {user?.role === 'USER' && (
            <button onClick={() => onNavigate('companyRegistration')} className={`hover:text-yellow-400 ${currentView === 'companyRegistration' ? 'text-yellow-400 font-semibold' : ''}`}>Đăng ký công ty</button>
          )}
          {user?.role === 'ADMIN' && (
            <>
              <button onClick={() => onNavigate('adminCompanyRegistrations')} className={`hover:text-yellow-400 ${currentView === 'adminCompanyRegistrations' ? 'text-yellow-400 font-semibold' : ''}`}>Duyệt công ty</button>
              <button onClick={() => onNavigate('adminRescueCompanies')} className={`hover:text-yellow-400 ${currentView === 'adminRescueCompanies' ? 'text-yellow-400 font-semibold' : ''}`}>Quản lý công ty</button>
              <button onClick={() => onNavigate('adminWorkflows')} className={`hover:text-yellow-400 ${currentView === 'adminWorkflows' ? 'text-yellow-400 font-semibold' : ''}`}>Quy trình</button>
              <button onClick={() => onNavigate('adminPostManagement')} className={`hover:text-yellow-400 ${currentView === 'adminPostManagement' ? 'text-yellow-400 font-semibold' : ''}`}>Quản lý bài viết</button>
              <button onClick={() => onNavigate('moderation')} className={`hover:text-yellow-400 ${currentView === 'moderation' ? 'text-yellow-400 font-semibold' : ''}`}>Kiểm duyệt tài khoản</button>
            </>
          )}
        </nav>

        {/* Auth Buttons */}
        <div className="hidden md:flex items-center space-x-3">
          {user ? (
            <div className="flex items-center space-x-2">
              <span className="text-sm">Xin chào, {user.username}</span>
              <button
                onClick={() => onNavigate('profile')}
                className="bg-blue-600 hover:bg-blue-700 px-3 py-1 rounded text-sm flex items-center gap-1"
              >
                <User size={16} />
                Hồ sơ
              </button>
              <button
                onClick={onLogout}
                className="bg-red-500 hover:bg-red-600 px-3 py-1 rounded text-sm"
              >
                Đăng xuất
              </button>
            </div>
          ) : (
            <>
              <button 
                onClick={() => onNavigate('login')}
                className="flex items-center space-x-1 hover:text-yellow-400"
              >
                <LogIn size={18} />
                <span>Đăng nhập</span>
              </button>
              <button 
                onClick={() => onNavigate('register')}
                className="bg-yellow-500 text-blue-900 px-4 py-2 rounded-full font-bold hover:bg-yellow-400 flex items-center space-x-1 transition"
              >
                <UserPlus size={18} />
                <span>Đăng ký</span>
              </button>
            </>
          )}
        </div>

        {/* Mobile Menu Toggle */}
        <div className="md:hidden">
          <button onClick={() => setIsMenuOpen(!isMenuOpen)}>
            {isMenuOpen ? <X size={24} /> : <Menu size={24} />}
          </button>
        </div>
      </div>

      {/* Mobile Menu */}
      {isMenuOpen && (
        <div className="md:hidden bg-blue-800 p-4 space-y-3">
          <button onClick={() => { onNavigate('home'); setIsMenuOpen(false); }} className="block w-full text-left py-2 hover:bg-blue-700 px-2 rounded">Trang chủ</button>
          {user?.role === 'USER' && (
            <button onClick={() => { onNavigate('createRequest'); setIsMenuOpen(false); }} className="block w-full text-left py-2 hover:bg-blue-700 px-2 rounded">Gửi yêu cầu</button>
          )}
          {(user?.role === 'USER' || user?.role === 'COMPANY') && (
            <button onClick={() => { onNavigate('requestList'); setIsMenuOpen(false); }} className="block w-full text-left py-2 hover:bg-blue-700 px-2 rounded">Theo dõi</button>
          )}
          <button onClick={() => { onNavigate('community'); setIsMenuOpen(false); }} className="block w-full text-left py-2 hover:bg-blue-700 px-2 rounded">Cộng đồng</button>
          {user && user.roles?.includes('ROLE_COMPANY') && (
            <>
              <button onClick={() => { onNavigate('serviceManagement'); setIsMenuOpen(false); }} className="block w-full text-left py-2 hover:bg-blue-700 px-2 rounded">Quản lý dịch vụ</button>
              <button onClick={() => { onNavigate('vehicleManagement'); setIsMenuOpen(false); }} className="block w-full text-left py-2 hover:bg-blue-700 px-2 rounded">Quản lý phương tiện</button>
            </>
          )}
          {user?.role === 'USER' && (
            <button onClick={() => { onNavigate('companyRegistration'); setIsMenuOpen(false); }} className="block w-full text-left py-2 hover:bg-blue-700 px-2 rounded">Đăng ký công ty</button>
          )}
          {user?.role === 'ADMIN' && (
            <>
              <button onClick={() => { onNavigate('adminCompanyRegistrations'); setIsMenuOpen(false); }} className="block w-full text-left py-2 hover:bg-blue-700 px-2 rounded">Duyệt công ty</button>
              <button onClick={() => { onNavigate('adminRescueCompanies'); setIsMenuOpen(false); }} className="block w-full text-left py-2 hover:bg-blue-700 px-2 rounded">Quản lý công ty</button>
              <button onClick={() => { onNavigate('adminWorkflows'); setIsMenuOpen(false); }} className="block w-full text-left py-2 hover:bg-blue-700 px-2 rounded">Quy trình</button>
              <button onClick={() => { onNavigate('adminPostManagement'); setIsMenuOpen(false); }} className="block w-full text-left py-2 hover:bg-blue-700 px-2 rounded">Quản lý bài viết</button>
              <button onClick={() => { onNavigate('moderation'); setIsMenuOpen(false); }} className="block w-full text-left py-2 hover:bg-blue-700 px-2 rounded">Kiểm duyệt tài khoản</button>
            </>
          )}
          {user ? (
            <>
              <button onClick={() => { onNavigate('profile'); setIsMenuOpen(false); }} className="block w-full text-left py-2 hover:bg-blue-700 px-2 rounded">Hồ sơ</button>
              <button
                onClick={() => { onLogout(); setIsMenuOpen(false); }}
                className="block w-full text-left py-2 bg-red-500 hover:bg-red-600 px-2 rounded"
              >
                Đăng xuất
              </button>
            </>
          ) : (
            <>
              <button onClick={() => { onNavigate('login'); setIsMenuOpen(false); }} className="block w-full text-left py-2 hover:bg-blue-700 px-2 rounded">Đăng nhập</button>
              <button onClick={() => { onNavigate('register'); setIsMenuOpen(false); }} className="block w-full text-left py-2 bg-yellow-500 text-blue-900 font-bold px-2 rounded">Đăng ký</button>
            </>
          )}
        </div>
      )}
    </header>
  );
};

export default Header;