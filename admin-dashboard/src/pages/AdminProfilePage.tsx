import React, { useState } from 'react'
import { User, Mail, Lock, LogOut, Edit } from 'lucide-react'

export const AdminProfilePage: React.FC = () => {
  const [isEditing, setIsEditing] = useState(false)
  const [adminData] = useState({
    name: 'John Administrator',
    email: 'admin@admin.marketplace.com',
    role: 'Super Administrator',
    lastLogin: '2024-01-22 14:30:00',
  })

  const handleLogout = () => {
    localStorage.removeItem('admin_token')
    window.location.href = '/admin-login'
  }

  return (
    <div className="max-w-4xl mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-8">Admin Profile</h1>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Profile Card */}
        <div className="lg:col-span-1">
          <div className="bg-white rounded-lg shadow-md p-6">
            <div className="w-20 h-20 bg-gradient-to-br from-red-400 to-red-600 rounded-full flex items-center justify-center mx-auto mb-4">
              <User className="w-10 h-10 text-white" />
            </div>
            <h2 className="text-2xl font-bold text-center">{adminData.name}</h2>
            <p className="text-center text-red-600 font-semibold">{adminData.role}</p>
            <p className="text-center text-gray-600 text-sm mt-2">{adminData.email}</p>

            <div className="mt-6 space-y-2">
              <button className="w-full bg-blue-50 text-blue-600 py-2 rounded-lg font-semibold hover:bg-blue-100 transition">
                Dashboard
              </button>
              <button
                onClick={handleLogout}
                className="w-full bg-red-50 text-red-600 py-2 rounded-lg font-semibold hover:bg-red-100 transition flex items-center justify-center gap-2"
              >
                <LogOut className="w-4 h-4" />
                Logout
              </button>
            </div>
          </div>
        </div>

        {/* Profile Details */}
        <div className="lg:col-span-2">
          <div className="bg-white rounded-lg shadow-md p-6">
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-2xl font-bold">Account Details</h2>
              <button
                onClick={() => setIsEditing(!isEditing)}
                className="flex items-center gap-2 bg-blue-50 text-blue-600 px-4 py-2 rounded-lg hover:bg-blue-100 transition"
              >
                <Edit className="w-4 h-4" />
                {isEditing ? 'Cancel' : 'Edit'}
              </button>
            </div>

            <div className="space-y-6">
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">Full Name</label>
                <input
                  type="text"
                  value={adminData.name}
                  disabled={!isEditing}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg disabled:bg-gray-50"
                />
              </div>

              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2 flex items-center gap-2">
                  <Mail className="w-4 h-4" />
                  Email Address
                </label>
                <input
                  type="email"
                  value={adminData.email}
                  disabled
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg bg-gray-50"
                />
              </div>

              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">Role</label>
                <input
                  type="text"
                  value={adminData.role}
                  disabled
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg bg-gray-50"
                />
              </div>

              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">Last Login</label>
                <input
                  type="text"
                  value={adminData.lastLogin}
                  disabled
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg bg-gray-50"
                />
              </div>

              {isEditing && (
                <div className="flex gap-4 pt-4 border-t border-gray-200">
                  <button className="flex-1 bg-blue-600 text-white py-2 rounded-lg font-semibold hover:bg-blue-700 transition">
                    Save Changes
                  </button>
                  <button onClick={() => setIsEditing(false)} className="flex-1 bg-gray-200 text-gray-800 py-2 rounded-lg font-semibold hover:bg-gray-300 transition">
                    Cancel
                  </button>
                </div>
              )}
            </div>
          </div>

          {/* Security Section */}
          <div className="bg-white rounded-lg shadow-md p-6 mt-6">
            <h3 className="text-xl font-bold mb-4 flex items-center gap-2">
              <Lock className="w-5 h-5" />
              Security Settings
            </h3>
            <div className="space-y-3">
              <button className="w-full text-left px-4 py-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition font-semibold text-gray-700">
                Change Password
              </button>
              <button className="w-full text-left px-4 py-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition font-semibold text-gray-700">
                Enable Two-Factor Authentication
              </button>
              <button className="w-full text-left px-4 py-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition font-semibold text-gray-700">
                View Login History
              </button>
            </div>
          </div>

          {/* Admin Permissions */}
          <div className="bg-white rounded-lg shadow-md p-6 mt-6">
            <h3 className="text-xl font-bold mb-4">Admin Permissions</h3>
            <div className="space-y-2">
              <div className="flex items-center gap-3 text-gray-700 text-sm">
                <input type="checkbox" checked disabled className="w-4 h-4" />
                <span>Vendor Management (Approve/Reject)</span>
              </div>
              <div className="flex items-center gap-3 text-gray-700 text-sm">
                <input type="checkbox" checked disabled className="w-4 h-4" />
                <span>Product Moderation</span>
              </div>
              <div className="flex items-center gap-3 text-gray-700 text-sm">
                <input type="checkbox" checked disabled className="w-4 h-4" />
                <span>Order Management</span>
              </div>
              <div className="flex items-center gap-3 text-gray-700 text-sm">
                <input type="checkbox" checked disabled className="w-4 h-4" />
                <span>Payment Settlement</span>
              </div>
              <div className="flex items-center gap-3 text-gray-700 text-sm">
                <input type="checkbox" checked disabled className="w-4 h-4" />
                <span>View Analytics & Reports</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
