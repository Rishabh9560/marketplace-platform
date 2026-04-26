import React from 'react'
import { LineChart, Line, BarChart, Bar, PieChart, Pie, Cell, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts'

const analyticsData = [
  { month: 'Jan', users: 2890, orders: 1234, revenue: 45000 },
  { month: 'Feb', users: 3100, orders: 1456, revenue: 52000 },
  { month: 'Mar', users: 3400, orders: 1678, revenue: 61000 },
  { month: 'Apr', users: 3950, orders: 1890, revenue: 68500 },
  { month: 'May', users: 4200, orders: 2100, revenue: 76000 },
]

const vendorPerformance = [
  { name: 'Tech Store', value: 35 },
  { name: 'Fashion Hub', value: 25 },
  { name: 'Home Essentials', value: 20 },
  { name: 'Others', value: 20 },
]

const COLORS = ['#DC2626', '#3B82F6', '#10B981', '#F59E0B']

export const AnalyticsPage: React.FC = () => {
  return (
    <div className="space-y-6">
      <h1 className="text-3xl font-bold">Platform Analytics</h1>

      {/* Key Metrics */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <MetricCard label="Total Revenue" value="$302,500" change="+12%" />
        <MetricCard label="Total Orders" value="8,358" change="+8%" />
        <MetricCard label="Active Customers" value="4,200" change="+15%" />
        <MetricCard label="Active Vendors" value="56" change="+3%" />
      </div>

      {/* Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Revenue and Users Trend */}
        <div className="bg-white rounded-lg shadow-md p-6">
          <h3 className="font-bold text-lg mb-4">Revenue & User Growth</h3>
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={analyticsData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="month" />
              <YAxis yAxisId="left" />
              <YAxis yAxisId="right" orientation="right" />
              <Tooltip />
              <Legend />
              <Line yAxisId="left" type="monotone" dataKey="revenue" stroke="#DC2626" strokeWidth={2} />
              <Line yAxisId="right" type="monotone" dataKey="users" stroke="#3B82F6" strokeWidth={2} />
            </LineChart>
          </ResponsiveContainer>
        </div>

        {/* Orders Trend */}
        <div className="bg-white rounded-lg shadow-md p-6">
          <h3 className="font-bold text-lg mb-4">Orders Analytics</h3>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={analyticsData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="month" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Bar dataKey="orders" fill="#3B82F6" />
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* Vendor Performance Distribution */}
        <div className="bg-white rounded-lg shadow-md p-6">
          <h3 className="font-bold text-lg mb-4">Vendor Performance</h3>
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie
                data={vendorPerformance}
                cx="50%"
                cy="50%"
                labelLine={false}
                label={({ name, value }) => `${name} ${value}%`}
                outerRadius={80}
                fill="#8884d8"
                dataKey="value"
              >
                {vendorPerformance.map((_, index) => (
                  <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                ))}
              </Pie>
              <Tooltip />
            </PieChart>
          </ResponsiveContainer>
        </div>

        {/* Category Breakdown */}
        <div className="bg-white rounded-lg shadow-md p-6">
          <h3 className="font-bold text-lg mb-4">Top Categories</h3>
          <div className="space-y-4">
            <CategoryBar label="Electronics" value={45} />
            <CategoryBar label="Fashion" value={32} />
            <CategoryBar label="Home & Garden" value={28} />
            <CategoryBar label="Sports" value={22} />
            <CategoryBar label="Books" value={18} />
          </div>
        </div>
      </div>
    </div>
  )
}

const MetricCard: React.FC<{ label: string; value: string; change: string }> = ({ label, value, change }) => (
  <div className="bg-white rounded-lg shadow-md p-6">
    <p className="text-gray-600 text-sm">{label}</p>
    <p className="text-3xl font-bold mt-2">{value}</p>
    <p className="text-green-600 text-sm mt-2">{change}</p>
  </div>
)

const CategoryBar: React.FC<{ label: string; value: number }> = ({ label, value }) => (
  <div>
    <div className="flex justify-between mb-1">
      <span className="text-sm font-semibold">{label}</span>
      <span className="text-sm text-gray-600">{value}%</span>
    </div>
    <div className="w-full bg-gray-200 rounded-full h-2">
      <div className="bg-red-600 h-2 rounded-full" style={{ width: `${value}%` }}></div>
    </div>
  </div>
)
