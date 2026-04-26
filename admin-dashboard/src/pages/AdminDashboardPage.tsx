import { useState, useEffect } from 'react'

/**
 * Admin Dashboard - Role-Based Access Control
 * Only accessible by ADMIN role users
 */
export const AdminDashboardPage = () => {
  const [stats, setStats] = useState({
    totalUsers: 0,
    totalVendors: 0,
    totalOrders: 0,
    totalRevenue: 0,
    pendingKYC: 0,
    failedPayments: 0,
  })

  useEffect(() => {
    // Fetch dashboard statistics
    loadDashboardStats()
  }, [])

  const loadDashboardStats = async () => {
    try {
      // Fetch from admin analytics API
      const response = await fetch('/api/v1/admin/analytics/dashboard', {
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
      })
      const data = await response.json()
      setStats(data)
    } catch (error) {
      console.error('Failed to load dashboard stats:', error)
    }
  }

  return (
    <div className="space-y-8 p-6">
      <h1 className="text-3xl font-bold text-gray-900">Admin Dashboard</h1>
      
      {/* Key Metrics */}
      <section>
        <h2 className="text-2xl font-bold mb-4">📊 Key Metrics</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {/* Total Users Card */}
          <div className="bg-white rounded-lg shadow p-6 hover:shadow-lg transition">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-gray-600 text-sm font-medium">Total Users</p>
                <p className="text-3xl font-bold text-gray-900 mt-2">{stats.totalUsers}</p>
              </div>
              <div className="text-4xl">👥</div>
            </div>
            <a href="/admin/users" className="text-blue-600 text-sm mt-4 inline-block hover:underline">
              View Users →
            </a>
          </div>

          {/* Total Vendors Card */}
          <div className="bg-white rounded-lg shadow p-6 hover:shadow-lg transition">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-gray-600 text-sm font-medium">Total Vendors</p>
                <p className="text-3xl font-bold text-gray-900 mt-2">{stats.totalVendors}</p>
              </div>
              <div className="text-4xl">🏪</div>
            </div>
            <a href="/admin/vendors" className="text-blue-600 text-sm mt-4 inline-block hover:underline">
              Manage Vendors →
            </a>
          </div>

          {/* Total Orders Card */}
          <div className="bg-white rounded-lg shadow p-6 hover:shadow-lg transition">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-gray-600 text-sm font-medium">Total Orders</p>
                <p className="text-3xl font-bold text-gray-900 mt-2">{stats.totalOrders}</p>
              </div>
              <div className="text-4xl">📋</div>
            </div>
            <a href="/admin/orders" className="text-blue-600 text-sm mt-4 inline-block hover:underline">
              View Orders →
            </a>
          </div>
        </div>
      </section>

      {/* Quick Actions */}
      <section>
        <h2 className="text-2xl font-bold mb-4">🎯 Quick Actions</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <a href="/admin/kyc-review" className="bg-blue-600 hover:bg-blue-700 text-white px-6 py-3 rounded-lg font-medium transition">
            KYC Reviews
          </a>
          <a href="/admin/users" className="bg-green-600 hover:bg-green-700 text-white px-6 py-3 rounded-lg font-medium transition">
            Manage Users
          </a>
        </div>
      </section>
    </div>
  )
}
