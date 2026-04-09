import React from 'react'
import { Link, useLocation } from 'react-router-dom'
import { useAuthStore } from '@/store'
import { Menu, X, LogOut, User, Settings } from 'lucide-react'

export const Navigation: React.FC = () => {
  const [mobileMenuOpen, setMobileMenuOpen] = React.useState(false)
  const location = useLocation()
  const { vendor, logout } = useAuthStore()

  const navItems = [
    { label: 'Dashboard', path: '/dashboard', icon: '📊' },
    { label: 'Listings', path: '/listings', icon: '📦' },
    { label: 'Payouts', path: '/payouts', icon: '💳' },
    { label: 'KYC', path: '/kyc', icon: '📋' },
    { label: 'Profile', path: '/profile', icon: '👤' },
  ]

  const handleLogout = () => {
    logout()
    window.location.href = '/login'
  }

  return (
    <nav className="bg-white shadow-md">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          {/* Logo */}
          <Link to="/dashboard" className="flex items-center gap-2">
            <div className="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center">
              <span className="text-white font-bold">V</span>
            </div>
            <span className="hidden sm:inline font-bold text-lg">Vendor Hub</span>
          </Link>

          {/* Desktop Menu */}
          <div className="hidden md:flex gap-1">
            {navItems.map((item) => (
              <Link
                key={item.path}
                to={item.path}
                className={`px-3 py-2 rounded-lg transition-colors ${
                  location.pathname === item.path
                    ? 'bg-blue-100 text-blue-600 font-medium'
                    : 'text-gray-700 hover:bg-gray-100'
                }`}
              >
                <span className="mr-1">{item.icon}</span>
                {item.label}
              </Link>
            ))}
          </div>

          {/* User Menu */}
          <div className="flex items-center gap-4">
            <div className="hidden sm:flex items-center gap-4">
              {vendor && (
                <div className="flex items-center gap-2">
                  <div className="w-8 h-8 bg-blue-600 rounded-full flex items-center justify-center text-white text-sm font-bold">
                    {vendor.businessName.charAt(0)}
                  </div>
                  <div>
                    <p className="text-sm font-medium">{vendor.businessName}</p>
                    <p className="text-xs text-gray-500">{vendor.businessEmail}</p>
                  </div>
                </div>
              )}
            </div>

            <button
              onClick={handleLogout}
              className="px-3 py-2 text-gray-700 hover:text-red-600 transition-colors"
              title="Logout"
            >
              <LogOut size={20} />
            </button>

            {/* Mobile Menu Button */}
            <button
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              className="md:hidden"
            >
              {mobileMenuOpen ? <X size={24} /> : <Menu size={24} />}
            </button>
          </div>
        </div>

        {/* Mobile Menu */}
        {mobileMenuOpen && (
          <div className="md:hidden pb-4 flex flex-col gap-2">
            {navItems.map((item) => (
              <Link
                key={item.path}
                to={item.path}
                onClick={() => setMobileMenuOpen(false)}
                className={`px-3 py-2 rounded-lg transition-colors ${
                  location.pathname === item.path
                    ? 'bg-blue-100 text-blue-600 font-medium'
                    : 'text-gray-700 hover:bg-gray-100'
                }`}
              >
                <span className="mr-1">{item.icon}</span>
                {item.label}
              </Link>
            ))}
          </div>
        )}
      </div>
    </nav>
  )
}
