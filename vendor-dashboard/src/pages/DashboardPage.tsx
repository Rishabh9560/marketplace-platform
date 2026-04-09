import React, { useEffect } from 'react'
import { BarChart, Bar, LineChart, Line, PieChart, Pie, Cell, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts'
import { Card, Badge, Button } from '@/components/common'
import { formatCurrency, formatNumber, getStatusColor } from '@/lib/utils'
import { useStatistics } from '@/hooks/useApi'
import { useDashboardStore, useAuthStore } from '@/store'
import { TrendingUp, ShoppingBag, DollarSign, Star, ArrowRight, AlertCircle } from 'lucide-react'
import { Link } from 'react-router-dom'

export const DashboardPage: React.FC = () => {
  const { vendor } = useAuthStore()
  const { data: stats, loading } = useStatistics()
  const { summary, setSummary } = useDashboardStore()

  // Mock data for charts
  const salesData = [
    { name: 'Mon', sales: 4000, commission: 240 },
    { name: 'Tue', sales: 3000, commission: 180 },
    { name: 'Wed', sales: 2000, commission: 120 },
    { name: 'Thu', sales: 2780, commission: 167 },
    { name: 'Fri', sales: 1890, commission: 113 },
    { name: 'Sat', sales: 2390, commission: 143 },
    { name: 'Sun', sales: 3490, commission: 209 },
  ]

  const categoryData = [
    { name: 'Electronics', value: 35, fill: '#3B82F6' },
    { name: 'Clothing', value: 25, fill: '#10B981' },
    { name: 'Home & Garden', value: 20, fill: '#F59E0B' },
    { name: 'Sports', value: 15, fill: '#EF4444' },
    { name: 'Others', value: 5, fill: '#8B5CF6' },
  ]

  const performanceData = [
    { name: 'New Orders', value: 2400, status: 'orders' },
    { name: 'Fulfilled', value: 2210, status: 'fulfilled' },
    { name: 'Cancelled', value: 290, status: 'cancelled' },
    { name: 'Refunded', value: 200, status: 'refunded' },
  ]

  useEffect(() => {
    // Initialize dashboard summary
    if (!summary) {
      setSummary({
        totalSales: 54321,
        totalOrders: 156,
        averageOrderValue: 348,
        fulfillmentRate: 96,
        returnRate: 2.4,
        customerSatisfaction: 4.6,
      })
    }
  }, [])

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-600 mx-auto mb-4"></div>
          <p className="text-gray-600">Loading dashboard...</p>
        </div>
      </div>
    )
  }

  const kycStatusColor = getStatusColor(vendor?.kycStatus || 'PENDING')

  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="flex justify-between items-start">
        <div>
          <h1 className="text-3xl font-bold text-gray-900 mb-2">
            Welcome back, {vendor?.businessName}! 👋
          </h1>
          <p className="text-gray-600">
            Here's what's happening with your store today.
          </p>
        </div>
        <Link to="/profile">
          <Button variant="secondary" size="md">
            View Profile
            <ArrowRight className="w-4 h-4 ml-2" />
          </Button>
        </Link>
      </div>

      {/* KYC Status Alert */}
      {vendor?.kycStatus !== 'VERIFIED' && (
        <div className="flex items-start gap-4 p-4 bg-yellow-50 border border-yellow-200 rounded-lg">
          <AlertCircle className="w-5 h-5 text-yellow-600 flex-shrink-0 mt-0.5" />
          <div className="flex-1">
            <h3 className="font-semibold text-yellow-900 mb-1">KYC Verification Pending</h3>
            <p className="text-sm text-yellow-800 mb-3">
              Complete your KYC verification to unlock all features and increase selling limits.
            </p>
            <Link to="/kyc">
              <Button variant="primary" size="sm">
                Complete KYC
              </Button>
            </Link>
          </div>
          <Badge className={kycStatusColor}>{vendor?.kycStatus}</Badge>
        </div>
      )}

      {/* Key Metrics */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {/* Total Sales */}
        <Card className="card-shadow">
          <div className="flex items-start justify-between">
            <div>
              <p className="text-gray-600 text-sm font-medium mb-1">Total Sales</p>
              <p className="text-2xl font-bold text-gray-900">
                {formatCurrency(summary?.totalSales || 0)}
              </p>
              <p className="text-xs text-green-600 mt-2">↑ 12% from last month</p>
            </div>
            <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
              <DollarSign className="w-6 h-6 text-blue-600" />
            </div>
          </div>
        </Card>

        {/* Total Orders */}
        <Card className="card-shadow">
          <div className="flex items-start justify-between">
            <div>
              <p className="text-gray-600 text-sm font-medium mb-1">Total Orders</p>
              <p className="text-2xl font-bold text-gray-900">
                {formatNumber(summary?.totalOrders || 0)}
              </p>
              <p className="text-xs text-green-600 mt-2">↑ 8% from last month</p>
            </div>
            <div className="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center">
              <ShoppingBag className="w-6 h-6 text-green-600" />
            </div>
          </div>
        </Card>

        {/* Average Order Value */}
        <Card className="card-shadow">
          <div className="flex items-start justify-between">
            <div>
              <p className="text-gray-600 text-sm font-medium mb-1">Avg Order Value</p>
              <p className="text-2xl font-bold text-gray-900">
                {formatCurrency(summary?.averageOrderValue || 0)}
              </p>
              <p className="text-xs text-green-600 mt-2">↑ 5% from last month</p>
            </div>
            <div className="w-12 h-12 bg-purple-100 rounded-lg flex items-center justify-center">
              <TrendingUp className="w-6 h-6 text-purple-600" />
            </div>
          </div>
        </Card>

        {/* Customer Satisfaction */}
        <Card className="card-shadow">
          <div className="flex items-start justify-between">
            <div>
              <p className="text-gray-600 text-sm font-medium mb-1">Satisfaction</p>
              <p className="text-2xl font-bold text-gray-900">
                {(summary?.customerSatisfaction || 0).toFixed(1)} / 5
              </p>
              <div className="flex gap-1 mt-2">
                {Array.from({ length: 5 }).map((_, i) => (
                  <Star
                    key={i}
                    className={`w-3 h-3 ${
                      i < Math.floor(summary?.customerSatisfaction || 0)
                        ? 'fill-yellow-400 text-yellow-400'
                        : 'text-gray-300'
                    }`}
                  />
                ))}
              </div>
            </div>
            <div className="w-12 h-12 bg-yellow-100 rounded-lg flex items-center justify-center">
              <Star className="w-6 h-6 text-yellow-600" />
            </div>
          </div>
        </Card>
      </div>

      {/* Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Sales Trend */}
        <Card className="lg:col-span-2 card-shadow">
          <h3 className="text-lg font-bold text-gray-900 mb-6">Sales & Commission Trend</h3>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={salesData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Bar dataKey="sales" fill="#3B82F6" />
              <Bar dataKey="commission" fill="#10B981" />
            </BarChart>
          </ResponsiveContainer>
        </Card>

        {/* Category Distribution */}
        <Card className="card-shadow">
          <h3 className="text-lg font-bold text-gray-900 mb-6">Sales by Category</h3>
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie
                data={categoryData}
                cx="50%"
                cy="50%"
                labelLine={false}
                label={({ name, value }) => `${name} ${value}%`}
                outerRadius={80}
                fill="#8884d8"
                dataKey="value"
              >
                {categoryData.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={entry.fill} />
                ))}
              </Pie>
              <Tooltip />
            </PieChart>
          </ResponsiveContainer>
        </Card>
      </div>

      {/* Performance Metrics */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Performance Breakdown */}
        <Card className="card-shadow">
          <h3 className="text-lg font-bold text-gray-900 mb-6">Performance Breakdown</h3>
          <div className="space-y-4">
            {performanceData.map((item) => (
              <div key={item.name}>
                <div className="flex justify-between items-center mb-2">
                  <span className="text-sm font-medium text-gray-700">{item.name}</span>
                  <span className="text-sm font-bold text-gray-900">
                    {formatNumber(item.value)}
                  </span>
                </div>
                <div className="w-full bg-gray-200 rounded-full h-2">
                  <div
                    className="bg-blue-600 h-2 rounded-full"
                    style={{
                      width: `${(item.value / 2400) * 100}%`,
                    }}
                  ></div>
                </div>
              </div>
            ))}
          </div>
        </Card>

        {/* Key Stats */}
        <Card className="card-shadow">
          <h3 className="text-lg font-bold text-gray-900 mb-6">Key Performance Indicators</h3>
          <div className="space-y-4">
            <div className="flex justify-between items-center p-4 bg-gray-50 rounded-lg">
              <span className="text-gray-700">Fulfillment Rate</span>
              <span className="text-lg font-bold text-green-600">
                {summary?.fulfillmentRate || 0}%
              </span>
            </div>
            <div className="flex justify-between items-center p-4 bg-gray-50 rounded-lg">
              <span className="text-gray-700">Return Rate</span>
              <span className="text-lg font-bold text-orange-600">
                {summary?.returnRate || 0}%
              </span>
            </div>
            <div className="flex justify-between items-center p-4 bg-gray-50 rounded-lg">
              <span className="text-gray-700">Active Listings</span>
              <span className="text-lg font-bold text-blue-600">24</span>
            </div>
            <div className="flex justify-between items-center p-4 bg-gray-50 rounded-lg">
              <span className="text-gray-700">Available Balance</span>
              <span className="text-lg font-bold text-green-600">
                {formatCurrency(vendor?.availableBalance || 0)}
              </span>
            </div>
          </div>
        </Card>
      </div>

      {/* Quick Actions */}
      <Card className="card-shadow">
        <h3 className="text-lg font-bold text-gray-900 mb-4">Quick Actions</h3>
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <Link to="/listings/create">
            <Button variant="outline" className="w-full justify-center">
              List New Product
            </Button>
          </Link>
          <Link to="/listings">
            <Button variant="outline" className="w-full justify-center">
              Manage Listings
            </Button>
          </Link>
          <Link to="/payouts">
            <Button variant="outline" className="w-full justify-center">
              View Payouts
            </Button>
          </Link>
          <Link to="/kyc">
            <Button variant="outline" className="w-full justify-center">
              KYC Status
            </Button>
          </Link>
        </div>
      </Card>
    </div>
  )
}
