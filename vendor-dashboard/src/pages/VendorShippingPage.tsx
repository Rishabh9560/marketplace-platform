import React from 'react'
import { MapPin, Truck, CheckCircle, Clock, AlertCircle } from 'lucide-react'

interface ShippingOrder {
  id: string
  orderNumber: string
  customer: string
  status: 'PENDING' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED'
  items: number
  totalAmount: number
  shippedDate?: string
  deliveredDate?: string
  trackingId?: string
}

export const VendorShippingPage: React.FC = () => {
  const [orders] = React.useState<ShippingOrder[]>([
    {
      id: '1',
      orderNumber: 'ORD-001',
      customer: 'John Doe',
      status: 'DELIVERED',
      items: 2,
      totalAmount: 245.50,
      shippedDate: '2024-01-18',
      deliveredDate: '2024-01-22',
      trackingId: 'TRACK-123456',
    },
    {
      id: '2',
      orderNumber: 'ORD-002',
      customer: 'Jane Smith',
      status: 'SHIPPED',
      items: 1,
      totalAmount: 89.99,
      shippedDate: '2024-01-20',
      trackingId: 'TRACK-789012',
    },
    {
      id: '3',
      orderNumber: 'ORD-003',
      customer: 'Bob Johnson',
      status: 'PROCESSING',
      items: 3,
      totalAmount: 342.75,
    },
    {
      id: '4',
      orderNumber: 'ORD-004',
      customer: 'Alice Brown',
      status: 'PENDING',
      items: 1,
      totalAmount: 129.99,
    },
  ])

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'DELIVERED':
        return <CheckCircle className="w-5 h-5 text-green-600" />
      case 'SHIPPED':
        return <Truck className="w-5 h-5 text-blue-600" />
      case 'PROCESSING':
        return <Clock className="w-5 h-5 text-yellow-600" />
      case 'PENDING':
        return <AlertCircle className="w-5 h-5 text-orange-600" />
      default:
        return null
    }
  }

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'DELIVERED':
        return 'bg-green-100 text-green-800'
      case 'SHIPPED':
        return 'bg-blue-100 text-blue-800'
      case 'PROCESSING':
        return 'bg-yellow-100 text-yellow-800'
      case 'PENDING':
        return 'bg-orange-100 text-orange-800'
      default:
        return 'bg-gray-100 text-gray-800'
    }
  }

  const handleStatusUpdate = (orderId: string, newStatus: string) => {
    alert(`Order ${orderId} status updated to ${newStatus}`)
  }

  return (
    <div className="space-y-6">
      <h1 className="text-3xl font-bold">Shipping & Delivery</h1>

      {/* Status Summary */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <div className="bg-white rounded-lg shadow-md p-6">
          <p className="text-gray-600 text-sm">Pending</p>
          <p className="text-3xl font-bold mt-2 text-orange-600">
            {orders.filter((o) => o.status === 'PENDING').length}
          </p>
        </div>
        <div className="bg-white rounded-lg shadow-md p-6">
          <p className="text-gray-600 text-sm">Processing</p>
          <p className="text-3xl font-bold mt-2 text-yellow-600">
            {orders.filter((o) => o.status === 'PROCESSING').length}
          </p>
        </div>
        <div className="bg-white rounded-lg shadow-md p-6">
          <p className="text-gray-600 text-sm">Shipped</p>
          <p className="text-3xl font-bold mt-2 text-blue-600">
            {orders.filter((o) => o.status === 'SHIPPED').length}
          </p>
        </div>
        <div className="bg-white rounded-lg shadow-md p-6">
          <p className="text-gray-600 text-sm">Delivered</p>
          <p className="text-3xl font-bold mt-2 text-green-600">
            {orders.filter((o) => o.status === 'DELIVERED').length}
          </p>
        </div>
      </div>

      {/* Orders Table */}
      <div className="bg-white rounded-lg shadow-md overflow-hidden">
        <table className="w-full">
          <thead className="bg-gray-50 border-b border-gray-200">
            <tr>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Order</th>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Customer</th>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Items</th>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Amount</th>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Status</th>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Tracking</th>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Action</th>
            </tr>
          </thead>
          <tbody>
            {orders.map((order) => (
              <tr key={order.id} className="border-b border-gray-100 hover:bg-gray-50">
                <td className="px-6 py-4 font-semibold">{order.orderNumber}</td>
                <td className="px-6 py-4">{order.customer}</td>
                <td className="px-6 py-4">{order.items}</td>
                <td className="px-6 py-4 font-semibold">${order.totalAmount}</td>
                <td className="px-6 py-4">
                  <span className={`px-3 py-1 rounded-full text-xs font-semibold flex items-center gap-1 w-fit ${getStatusBadge(order.status)}`}>
                    {getStatusIcon(order.status)}
                    {order.status}
                  </span>
                </td>
                <td className="px-6 py-4 text-sm">
                  {order.trackingId ? (
                    <div className="flex items-center gap-1 text-blue-600 font-mono">
                      <Truck className="w-4 h-4" />
                      {order.trackingId}
                    </div>
                  ) : (
                    <span className="text-gray-400">-</span>
                  )}
                </td>
                <td className="px-6 py-4">
                  {order.status === 'PENDING' && (
                    <button
                      onClick={() => handleStatusUpdate(order.id, 'PROCESSING')}
                      className="bg-yellow-100 text-yellow-800 px-3 py-1 rounded text-xs font-semibold hover:bg-yellow-200"
                    >
                      Mark Processing
                    </button>
                  )}
                  {order.status === 'PROCESSING' && (
                    <button
                      onClick={() => handleStatusUpdate(order.id, 'SHIPPED')}
                      className="bg-blue-100 text-blue-800 px-3 py-1 rounded text-xs font-semibold hover:bg-blue-200"
                    >
                      Mark Shipped
                    </button>
                  )}
                  {order.status === 'SHIPPED' && (
                    <button
                      onClick={() => handleStatusUpdate(order.id, 'DELIVERED')}
                      className="bg-green-100 text-green-800 px-3 py-1 rounded text-xs font-semibold hover:bg-green-200"
                    >
                      Mark Delivered
                    </button>
                  )}
                  {order.status === 'DELIVERED' && (
                    <span className="text-green-600 text-xs font-semibold">✓ Complete</span>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Timeline Legend */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <h3 className="font-bold text-lg mb-4">Order Processing Timeline</h3>
        <div className="flex flex-wrap gap-6">
          <div className="flex items-center gap-3">
            <AlertCircle className="w-6 h-6 text-orange-600" />
            <div>
              <p className="font-semibold">Pending</p>
              <p className="text-sm text-gray-600">Awaiting processing</p>
            </div>
          </div>
          <div className="flex items-center gap-3">
            <Clock className="w-6 h-6 text-yellow-600" />
            <div>
              <p className="font-semibold">Processing</p>
              <p className="text-sm text-gray-600">Preparing shipment</p>
            </div>
          </div>
          <div className="flex items-center gap-3">
            <Truck className="w-6 h-6 text-blue-600" />
            <div>
              <p className="font-semibold">Shipped</p>
              <p className="text-sm text-gray-600">In transit</p>
            </div>
          </div>
          <div className="flex items-center gap-3">
            <CheckCircle className="w-6 h-6 text-green-600" />
            <div>
              <p className="font-semibold">Delivered</p>
              <p className="text-sm text-gray-600">Completed</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
