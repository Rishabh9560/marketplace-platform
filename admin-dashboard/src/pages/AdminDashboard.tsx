import React from 'react'
import { BarChart, Bar, LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts'
import { TrendingUp, Users, ShoppingBag, DollarSign } from 'lucide-react'

const statsData = [
  { name: 'Jan', revenue: 4000, orders: 240, vendors: 24 },
  { name: 'Feb', revenue: 3000, orders: 221, vendors: 22 },
  { name: 'Mar', revenue: 2000, orders: 229, vendors: 20 },
  { name: 'Apr', revenue: 2780, orders: 200, vendors: 21 },
  { name: 'May', revenue: 1890, orders: 308, vendors: 25 },
]

export const AdminDashboard: React.FC = () => {
  return (
    <div className="space-y-6">
      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <StatCard icon={DollarSign} label="Total Revenue" value="$125,400" change="+12%" />
        <StatCard icon={ShoppingBag} label="Total Orders" value="1,234" change="+5%" />
        <StatCard icon={Users} label="Active Vendors" value="56" change="+3%" />
        <StatCard icon={Users} label="Total Customers" value="2,890" change="+8%" />
      </div>

      {/* Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Revenue Chart */}
        <div className="bg-white rounded-lg shadow-md p-6">
          <h3 className="font-bold text-lg mb-4">Revenue Trend</h3>
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={statsData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Line type="monotone" dataKey="revenue" stroke="#DC2626" strokeWidth={2} />
            </LineChart>
          </ResponsiveContainer>
        </div>

        {/* Orders Chart */}
        <div className="bg-white rounded-lg shadow-md p-6">
          <h3 className="font-bold text-lg mb-4">Orders vs Vendors</h3>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={statsData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Bar dataKey="orders" fill="#3B82F6" />
              <Bar dataKey="vendors" fill="#10B981" />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Recent Activity */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <h3 className="font-bold text-lg mb-4">Recent Orders</h3>
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b border-gray-200">
              <th className="px-4 py-2 text-left text-gray-600">Order ID</th>
              <th className="px-4 py-2 text-left text-gray-600">Customer</th>
              <th className="px-4 py-2 text-left text-gray-600">Amount</th>
              <th className="px-4 py-2 text-left text-gray-600">Status</th>
            </tr>
          </thead>
          <tbody>
            {['ORD-001', 'ORD-002', 'ORD-003', 'ORD-004'].map((id) => (
              <tr key={id} className="border-b border-gray-100 hover:bg-gray-50">
                <td className="px-4 py-3 font-semibold">{id}</td>
                <td className="px-4 py-3">John Doe</td>
                <td className="px-4 py-3">$234.56</td>
                <td className="px-4 py-3"><span className="bg-green-100 text-green-800 px-2 py-1 rounded text-xs font-semibold">Delivered</span></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}

interface StatCardProps {
  icon: React.ComponentType<{ className?: string }>
  label: string
  value: string
  change: string
}

const StatCard: React.FC<StatCardProps> = ({ icon: Icon, label, value, change }) => (
  <div className="bg-white rounded-lg shadow-md p-6">
    <div className="flex items-center justify-between">
      <div>
        <p className="text-gray-600 text-sm">{label}</p>
        <p className="text-3xl font-bold mt-2">{value}</p>
        <p className="text-green-600 text-sm mt-2 flex items-center gap-1">
          <TrendingUp className="w-4 h-4" /> {change}
        </p>
      </div>
      <div className="w-12 h-12 bg-red-100 rounded-lg flex items-center justify-center">
        <Icon className="w-6 h-6 text-red-600" />
      </div>
    </div>
  </div>
)
