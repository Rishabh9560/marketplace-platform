import React from 'react'
import { Eye, Download } from 'lucide-react'

interface Order {
  id: string
  customer: string
  vendor: string
  amount: number
  orderDate: string
  status: 'PENDING' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED'
}

export const OrdersPage: React.FC = () => {
  const [orders] = React.useState<Order[]>([
    { id: 'ORD-001', customer: 'John Doe', vendor: 'Tech Store', amount: 234.56, orderDate: '2024-01-15', status: 'DELIVERED' },
    { id: 'ORD-002', customer: 'Jane Smith', vendor: 'Fashion Hub', amount: 89.99, orderDate: '2024-01-16', status: 'SHIPPED' },
    { id: 'ORD-003', customer: 'Bob Wilson', vendor: 'Home Essentials', amount: 145.00, orderDate: '2024-01-17', status: 'PROCESSING' },
    { id: 'ORD-004', customer: 'Alice Brown', vendor: 'Tech Store', amount: 456.78, orderDate: '2024-01-18', status: 'PENDING' },
  ])

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'DELIVERED':
        return 'bg-green-100 text-green-800'
      case 'SHIPPED':
        return 'bg-blue-100 text-blue-800'
      case 'PROCESSING':
        return 'bg-yellow-100 text-yellow-800'
      case 'PENDING':
        return 'bg-orange-100 text-orange-800'
      case 'CANCELLED':
        return 'bg-red-100 text-red-800'
      default:
        return 'bg-gray-100 text-gray-800'
    }
  }

  return (
    <div className="space-y-6">
      <h1 className="text-3xl font-bold">Order Management</h1>

      <div className="bg-white rounded-lg shadow-md overflow-hidden">
        <table className="w-full">
          <thead className="bg-gray-50 border-b border-gray-200">
            <tr>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Order ID</th>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Customer</th>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Vendor</th>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Amount</th>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Date</th>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Status</th>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Actions</th>
            </tr>
          </thead>
          <tbody>
            {orders.map((order) => (
              <tr key={order.id} className="border-b border-gray-100 hover:bg-gray-50">
                <td className="px-6 py-4 font-semibold">{order.id}</td>
                <td className="px-6 py-4 text-gray-600">{order.customer}</td>
                <td className="px-6 py-4 text-gray-600">{order.vendor}</td>
                <td className="px-6 py-4 font-semibold">${order.amount}</td>
                <td className="px-6 py-4">{order.orderDate}</td>
                <td className="px-6 py-4">
                  <span className={`px-3 py-1 rounded-full text-xs font-semibold ${getStatusColor(order.status)}`}>
                    {order.status}
                  </span>
                </td>
                <td className="px-6 py-4">
                  <div className="flex gap-2">
                    <button className="p-2 text-blue-600 hover:bg-blue-50 rounded-lg transition">
                      <Eye className="w-5 h-5" />
                    </button>
                    <button className="p-2 text-green-600 hover:bg-green-50 rounded-lg transition">
                      <Download className="w-5 h-5" />
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
