import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'

/**
 * Vendor Dashboard - Basic vendor statistics
 */
export const VendorDashboard = () => {
  const navigate = useNavigate()
  const [stats, setStats] = useState({
    totalProducts: 0,
    totalOrders: 0,
    totalRevenue: 0,
    pendingSales: 0,
    kycStatus: 'PENDING',
    inventory: 0,
  })

  useEffect(() => {
    loadVendorStats()
  }, [])

  const loadVendorStats = async () => {
    try {
      const token = localStorage.getItem('token')
      const response = await fetch('/api/v1/vendor/dashboard/stats', {
        headers: { Authorization: `Bearer ${token}` },
      })
      if (response.ok) {
        const data = await response.json()
        setStats(data)
      }
    } catch (error) {
      console.error('Failed to load vendor stats:', error)
    }
  }

  return (
    <div className="space-y-8">
      {/* KYC Status Alert */}
      {stats.kycStatus === 'PENDING' && (
        <div className="bg-yellow-50 border-l-4 border-yellow-500 p-6 rounded">
          <h3 className="text-lg font-semibold text-yellow-900 mb-2">📋 Complete Your KYC</h3>
          <p className="text-yellow-800 mb-4">
            Complete your KYC verification to unlock full selling features and increase your selling limits.
          </p>
          <button
            onClick={() => navigate('/kyc')}
            className="text-blue-600 font-semibold hover:underline"
          >
            Complete KYC →
          </button>
        </div>
      )}

      {/* Key Metrics */}
      <section>
        <h2 className="text-2xl font-bold mb-4">📊 Your Performance</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {/* Total Products */}
          <div className="bg-white rounded-lg shadow p-6 hover:shadow-lg transition">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-gray-600 text-sm font-medium">Active Products</p>
                <p className="text-3xl font-bold text-gray-900 mt-2">{stats.totalProducts}</p>
              </div>
              <div className="text-4xl">📦</div>
            </div>
            <button
              onClick={() => navigate('/products')}
              className="text-blue-600 text-sm mt-4 inline-block hover:underline"
            >
              Manage Products →
            </button>
          </div>

          {/* Total Orders */}
          <div className="bg-white rounded-lg shadow p-6 hover:shadow-lg transition">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-gray-600 text-sm font-medium">Total Orders</p>
                <p className="text-3xl font-bold text-gray-900 mt-2">{stats.totalOrders}</p>
              </div>
              <div className="text-4xl">📋</div>
            </div>
            <button
              onClick={() => navigate('/orders')}
              className="text-blue-600 text-sm mt-4 inline-block hover:underline"
            >
              View Orders →
            </button>
          </div>

          {/* Total Revenue */}
          <div className="bg-white rounded-lg shadow p-6 hover:shadow-lg transition">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-gray-600 text-sm font-medium">Total Revenue</p>
                <p className="text-3xl font-bold text-green-600 mt-2">₹{stats.totalRevenue.toLocaleString()}</p>
              </div>
              <div className="text-4xl">💰</div>
            </div>
            <button
              onClick={() => navigate('/dashboard')}
              className="text-blue-600 text-sm mt-4 inline-block hover:underline"
            >
              View Analytics →
            </button>
          </div>

          {/* Inventory */}
          <div className="bg-white rounded-lg shadow p-6 hover:shadow-lg transition">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-gray-600 text-sm font-medium">Inventory Items</p>
                <p className="text-3xl font-bold text-gray-900 mt-2">{stats.inventory}</p>
              </div>
              <div className="text-4xl">📊</div>
            </div>
            <button
              onClick={() => navigate('/dashboard')}
              className="text-blue-600 text-sm mt-4 inline-block hover:underline"
            >
              Manage Inventory →
            </button>
          </div>
        </div>
      </section>

      {/* Quick Actions */}
      <section>
        <h2 className="text-2xl font-bold mb-4">⚡ Quick Actions</h2>
        <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-5 gap-4">
          <button
            onClick={() => navigate('/create-product')}
            className="bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 transition"
          >
            ➕ Add Product
          </button>

          <button
            onClick={() => navigate('/orders')}
            className="bg-green-600 text-white px-6 py-3 rounded-lg hover:bg-green-700 transition"
          >
            📦 Prepare Shipment
          </button>

          <button
            onClick={() => navigate('/dashboard')}
            className="bg-purple-600 text-white px-6 py-3 rounded-lg hover:bg-purple-700 transition"
          >
            📊 Update Stock
          </button>

          <button
            onClick={() => navigate('/payouts')}
            className="bg-orange-600 text-white px-6 py-3 rounded-lg hover:bg-orange-700 transition"
          >
            💳 Request Payout
          </button>

          <button
            onClick={() => navigate('/dashboard')}
            className="bg-indigo-600 text-white px-6 py-3 rounded-lg hover:bg-indigo-700 transition"
          >
            📈 Export Sales
          </button>
        </div>
      </section>
    </div>
  )
}
