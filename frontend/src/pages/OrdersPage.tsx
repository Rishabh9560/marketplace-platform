import React from 'react'
import { useNavigate } from 'react-router-dom'
import { Package, Truck, CheckCircle, XCircle, Clock } from 'lucide-react'
import { useAuthStore, useOrderStore } from '@/store'

export const OrdersPage: React.FC = () => {
  const navigate = useNavigate()
  const { isAuthenticated } = useAuthStore()
  const { getOrders } = useOrderStore()
  const [orders] = React.useState(() => {
    // Combine stored orders with sample orders for demo
    const storedOrders = getOrders()
    const sampleOrders = [
      {
        id: 'ORD-001',
        date: '2024-01-15',
        total: 234.56,
        status: 'DELIVERED' as const,
        items: 3,
        trackingId: 'TRACK-123456789',
      },
      {
        id: 'ORD-002',
        date: '2024-01-18',
        total: 89.99,
        status: 'SHIPPED' as const,
        items: 1,
        trackingId: 'TRACK-987654321',
      },
    ]
    return [...sampleOrders, ...storedOrders].slice(0, 20) // Limit to 20 for display
  })

  if (!isAuthenticated) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <button onClick={() => navigate('/login')} className="bg-blue-600 text-white px-8 py-3 rounded-lg">
          Login to View Orders
        </button>
      </div>
    )
  }

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'DELIVERED':
        return <span className="flex items-center gap-1 bg-green-100 text-green-800 px-3 py-1 rounded-full text-sm font-semibold"><CheckCircle className="w-4 h-4" /> Delivered</span>
      case 'SHIPPED':
        return <span className="flex items-center gap-1 bg-blue-100 text-blue-800 px-3 py-1 rounded-full text-sm font-semibold"><Truck className="w-4 h-4" /> Shipped</span>
      case 'CONFIRMED':
        return <span className="flex items-center gap-1 bg-yellow-100 text-yellow-800 px-3 py-1 rounded-full text-sm font-semibold"><Clock className="w-4 h-4" /> Processing</span>
      case 'PENDING':
        return <span className="flex items-center gap-1 bg-orange-100 text-orange-800 px-3 py-1 rounded-full text-sm font-semibold"><Clock className="w-4 h-4" /> Pending</span>
      case 'CANCELLED':
        return <span className="flex items-center gap-1 bg-red-100 text-red-800 px-3 py-1 rounded-full text-sm font-semibold"><XCircle className="w-4 h-4" /> Cancelled</span>
    }
  }

  return (
    <div className="max-w-6xl mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-8">My Orders</h1>

      {orders.length === 0 ? (
        <div className="text-center py-12">
          <Package className="w-16 h-16 text-gray-300 mx-auto mb-4" />
          <p className="text-gray-600 text-lg">You haven't placed any orders yet</p>
          <button onClick={() => navigate('/')} className="mt-4 bg-blue-600 text-white px-8 py-2 rounded-lg">
            Start Shopping
          </button>
        </div>
      ) : (
        <div className="space-y-4">
          {orders.map((order) => (
            <div key={order.id} className="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition">
              <div className="grid grid-cols-1 md:grid-cols-5 gap-4 items-center">
                <div>
                  <p className="text-sm text-gray-600">Order ID</p>
                  <p className="font-bold text-lg">{order.id}</p>
                </div>

                <div>
                  <p className="text-sm text-gray-600">Order Date</p>
                  <p className="font-semibold">{order.date}</p>
                </div>

                <div>
                  <p className="text-sm text-gray-600">Items</p>
                  <p className="font-semibold">{order.items} Product{order.items > 1 ? 's' : ''}</p>
                </div>

                <div>
                  <p className="text-sm text-gray-600">Status</p>
                  {getStatusBadge(order.status)}
                </div>

                <div className="text-right">
                  <p className="text-sm text-gray-600">Total</p>
                  <p className="text-2xl font-bold text-blue-600">${order.total}</p>
                </div>
              </div>

              {order.trackingId && (
                <div className="mt-4 pt-4 border-t border-gray-200">
                  <div className="flex items-center gap-2 text-sm text-gray-600">
                    <Truck className="w-4 h-4" />
                    Tracking: <span className="font-mono font-semibold text-gray-800">{order.trackingId}</span>
                  </div>
                </div>
              )}

              <div className="mt-4 flex gap-2">
                <button 
                  onClick={() => navigate(`/order/${order.id}`)}
                  className="flex-1 bg-blue-50 text-blue-600 py-2 rounded-lg font-semibold hover:bg-blue-100 transition"
                >
                  View Details
                </button>
                {(order.status === 'SHIPPED' || order.status === 'DELIVERED') && (
                  <button 
                    onClick={() => navigate(`/track/${order.trackingId}`)}
                    className="flex-1 bg-green-50 text-green-600 py-2 rounded-lg font-semibold hover:bg-green-100 transition flex items-center justify-center gap-2"
                  >
                    <Truck className="w-4 h-4" />
                    Track Order
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
