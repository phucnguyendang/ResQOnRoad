import React, { useEffect, useState } from 'react';
import './App.css'; // Import CSS chung (nếu có)

// Import Components
import Header from './components/Header.jsx';
import Footer from './components/Footer.jsx';

// Import Views
import HomeView from './views/HomeView.jsx';
import LoginView from './views/LoginView.jsx';
import RegisterView from './views/RegisterView.jsx';
import RescueRequestCreateView from './views/RescueRequestCreateView.jsx';
import RescueRequestListView from './views/RescueRequestListView.jsx';
import RescueRequestTrackView from './views/RescueRequestTrackView.jsx';
import UserProfileView from './views/UserProfileView.jsx';
import ChatView from './views/ChatView.jsx';
import CompanyProfileView from './views/CompanyProfileView.jsx';
import CommunityView from './views/CommunityView.jsx';
import CompanyRegistrationCreateView from './views/CompanyRegistrationCreateView.jsx';
import CompanyRegistrationStatusView from './views/CompanyRegistrationStatusView.jsx';
import AdminCompanyRegistrationsView from './views/AdminCompanyRegistrationsView.jsx';

import { clearAuth, loadAuth, saveAuth } from './utils/authStorage.js';

function App() {
  // Trạng thái điều hướng (mặc định là trang chủ)
  const [currentView, setCurrentView] = useState('home');
  
  // Trạng thái user (null = chưa đăng nhập)
  const [user, setUser] = useState(null); 

  useEffect(() => {
    const saved = loadAuth();
    if (saved?.user) {
      setUser(saved.user);
    }
  }, []);

  const handleLoginSuccess = (authPayload) => {
    // Persist minimal user info for Header
    setUser(authPayload.user);
    saveAuth(authPayload);
    setCurrentView('home');
    window.scrollTo(0, 0);
  };

  const handleLogout = () => {
    clearAuth();
    setUser(null);
    setCurrentView('home');
  };

  const handleProfileUpdate = (patch) => {
    if (!patch) return;
    setUser((prev) => (prev ? { ...prev, ...patch } : prev));

    const current = loadAuth();
    if (current?.user) {
      saveAuth({
        ...current,
        user: { ...current.user, ...patch },
      });
    }
  };

  // Hàm điều hướng chuyển trang
  const handleNavigate = (view) => {
    setCurrentView(view);
    window.scrollTo(0, 0); // Cuộn lên đầu trang khi chuyển view
  };

  return (
    <div className="flex flex-col min-h-screen font-sans">
      {/* 1. Header luôn hiển thị */}
      <Header 
        onNavigate={handleNavigate} 
        currentView={currentView} 
        user={user} 
        onLogout={handleLogout}
      />
      
      {/* 2. Phần nội dung chính (thay đổi tùy theo currentView) */}
      <div className="flex-grow">
        {currentView === 'home' && <HomeView onNavigate={handleNavigate} />}
        {currentView === 'login' && <LoginView onNavigate={handleNavigate} onLoginSuccess={handleLoginSuccess} />}
        {currentView === 'register' && <RegisterView onNavigate={handleNavigate} />}
        {currentView === 'createRequest' && <RescueRequestCreateView onNavigate={handleNavigate} />}
        {currentView === 'requestList' && <RescueRequestListView onNavigate={handleNavigate} />}
        {currentView === 'requestDetail' && <RescueRequestTrackView onNavigate={handleNavigate} />}
        {currentView === 'profile' && <UserProfileView user={user} onUpdate={handleProfileUpdate} />}
        {currentView === 'chat' && <ChatView onNavigate={handleNavigate} />}
        {currentView === 'companyProfile' && <CompanyProfileView onNavigate={handleNavigate} />}
        {currentView === 'community' && <CommunityView onNavigate={handleNavigate} user={user} />}
        {currentView === 'companyRegistration' && <CompanyRegistrationCreateView onNavigate={handleNavigate} />}
        {currentView === 'companyRegistrationStatus' && <CompanyRegistrationStatusView onNavigate={handleNavigate} />}
        {currentView === 'adminCompanyRegistrations' && <AdminCompanyRegistrationsView onNavigate={handleNavigate} />}
      </div>

      {/* 3. Footer luôn hiển thị */}
      <Footer />
    </div>
  );
}

export default App;