import React, { useState } from 'react'
import { Link, useLocation } from 'react-router-dom'
import { Menu, X, Home, Users, ShoppingBag, DollarSign, Settings, LogOut, User } from 'lucide-react'
import { AdminNotificationBell } from './AdminNotificationBell'

export const Sidebar: React.FC = () => {
  const location = useLocation()
  const [mobileOpen, setMobileOpen] = useState(false)

  const menuItems = [
    { label: 'Dashboard', icon: Home, path: '/admin' },
    { label: 'Vendors', icon: Users, path: '/admin/vendors' },
    { label: 'KYC Review', icon: ShoppingBag, path: '/admin/kyc-review' },
    { label: 'Products', icon: ShoppingBag, path: '/admin/products' },
    { label: 'Orders', icon: ShoppingBag, path: '/admin/orders' },
    { label: 'Payouts', icon: DollarSign, path: '/admin/payouts' },
    { label: 'Analytics', icon: ShoppingBag, path: '/admin/analytics' },
    { label: 'Settings', icon: Settings, path: '/admin/settings' },
  ]

  return (
    <>
      {/* Mobile Header */}
      <div className="md:hidden bg-white border-b border-gray-200 p-4 flex justify-between items-center">
        <h1 className="font-bold text-lg">Admin Panel</h1>
        <button onClick={() => setMobileOpen(!mobileOpen)}>
          {mobileOpen ? <X /> : <Menu />}
        </button>
      </div>

      {/* Sidebar */}
      <aside className={`fixed md:static top-16 md:top-0 left-0 w-64 h-screen bg-gray-900 text-white overflow-y-auto z-30 transition-transform ${mobileOpen ? 'translate-x-0' : '-translate-x-full md:translate-x-0'}`}>
        <div className="p-6">
          <h1 className="text-2xl font-bold mb-8 flex items-center gap-2">
            <div className="w-8 h-8 bg-red-600 rounded flex items-center justify-center text-sm font-bold">A</div>
            Admin
          </h1>

          <nav className="space-y-2">
            {menuItems.map((item) => {
              const Icon = item.icon
              const isActive = location.pathname === item.path
              return (
                <Link
                  key={item.path}
                  to={item.path}
                  onClick={() => setMobileOpen(false)}
                  className={`flex items-center gap-3 px-4 py-3 rounded-lg transition ${isActive ? 'bg-red-600 text-white' : 'text-gray-300 hover:bg-gray-800'}`}
                >
                  <Icon className="w-5 h-5" />
                  {item.label}
                </Link>
              )
            })}
          </nav>

          <hr className="my-6 border-gray-700" />

          <button className="w-full flex items-center gap-3 px-4 py-3 text-gray-300 hover:bg-gray-800 rounded-lg transition">
            <LogOut className="w-5 h-5" />
            Logout
          </button>
        </div>
      </aside>
    </>
  )
}

export const TopBar: React.FC = () => {
  const [profileOpen, setProfileOpen] = useState(false)

  return (
    <div className="hidden md:flex bg-white border-b border-gray-200 px-6 py-4 items-center justify-between">
      <h2 className="text-xl font-semibold text-gray-800">Welcome, Administrator</h2>
      <div className="flex items-center gap-6">
          <AdminNotificationBell />
          <button
            onClick={() => setProfileOpen(!profileOpen)}
            className="w-10 h-10 bg-red-600 rounded-full text-white font-bold flex items-center justify-center hover:bg-red-700 transition"
          >
            A
          </button>
          
          {profileOpen && (
            <div className="absolute right-0 mt-2 w-48 bg-white border border-gray-200 rounded-lg shadow-lg z-10">
              <Link
                to="/admin/profile"
                className="flex items-center gap-2 px-4 py-2 text-gray-700 hover:bg-gray-50 transition"
                onClick={() => setProfileOpen(false)}
              >
                <User className="w-4 h-4" />
                My Profile
              </Link>
              <button className="w-full flex items-center gap-2 px-4 py-2 text-red-600 hover:bg-gray-50 transition text-left">
                <LogOut className="w-4 h-4" />
                Logout
              </button>
            </div>
          )}
        </div>
      </div>
  )
}
