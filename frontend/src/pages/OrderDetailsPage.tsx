import React from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { ArrowLeft, Package, MapPin, Truck, CheckCircle, Calendar, CreditCard, AlertCircle } from 'lucide-react'
import { useAuthStore, useOrderStore } from '@/store'

interface OrderDetail {
  id: string
  date: string
  total: number
  status: 'PENDING' | 'CONFIRMED' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED'
  items: number
  trackingId?: string
  items_detail?: { name: string; quantity: number; price: number }[]
  subtotal?: number
  shipping?: number
  tax?: number
  shippingAddress?: {
    fullName: string
    street: string
    city: string
    state: string
    zipCode: string
    phone: string
  }
  paymentMethod?: string
  paymentStatus?: string
}

export const OrderDetailsPage: React.FC = () => {
  const { orderId } = useParams<{ orderId: string }>()
  const navigate = useNavigate()
  const { isAuthenticated } = useAuthStore()
  const { getOrders } = useOrderStore()

  // Get order from store
  const allOrders = getOrders()
  const order = allOrders.find((o) => o.id === orderId)

  // Sample orders for demo
  const sampleOrdersDetail: { [key: string]: OrderDetail } = {
    'ORD-001': {
      id: 'ORD-001',
      date: '2024-01-15',
      total: 234.56,
      status: 'DELIVERED',
      items: 3,
      trackingId: 'TRACK-123456789',
      subtotal: 200.00,
      shipping: 10.00,
      tax: 24.56,
      items_detail: [
        { name: 'Premium Wireless Headphones', quantity: 1, price: 129.99 },
        { name: 'USB-C Cable', quantity: 2, price: 35.00 },
      ],
      shippingAddress: {
        fullName: 'John Doe',
        street: '123 Main Street',
        city: 'New York',
        state: 'NY',
        zipCode: '10001',
        phone: '+1 (555) 123-4567',
      },
      paymentMethod: 'Visa ending in 4242',
      paymentStatus: 'Paid',
    },
    'ORD-002': {
      id: 'ORD-002',
      date: '2024-01-18',
      total: 89.99,
      status: 'SHIPPED',
      items: 1,
      trackingId: 'TRACK-987654321',
      subtotal: 79.99,
      shipping: 5.00,
      tax: 5.00,
      items_detail: [
        { name: 'Organic Cotton T-Shirt', quantity: 1, price: 29.99 },
      ],
      shippingAddress: {
        fullName: 'Jane Smith',
        street: '456 Oak Avenue',
        city: 'Los Angeles',
        state: 'CA',
        zipCode: '90001',
        phone: '+1 (555) 987-6543',
      },
      paymentMethod: 'MasterCard ending in 5555',
      paymentStatus: 'Paid',
    },
  }

  if (!isAuthenticated) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <button onClick={() => navigate('/login')} className="bg-blue-600 text-white px-8 py-3 rounded-lg">
          Login to View Order Details
        </button>
      </div>
    )
  }

  // Get order detail (either from store or sample)
  const orderDetail = order ? { ...order, ...sampleOrdersDetail[order.id] } : sampleOrdersDetail[orderId || 'ORD-001']

  if (!orderDetail) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <AlertCircle className="w-16 h-16 text-red-500 mx-auto mb-4" />
          <p className="text-xl font-semibold text-gray-800 mb-4">Order Not Found</p>
          <button onClick={() => navigate('/orders')} className="bg-blue-600 text-white px-8 py-3 rounded-lg">
            Back to Orders
          </button>
        </div>
      </div>
    )
  }

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'DELIVERED':
        return (
          <div className="inline-flex items-center gap-2 bg-green-100 text-green-800 px-4 py-2 rounded-full">
            <CheckCircle className="w-5 h-5" />
            <span className="font-semibold">Delivered</span>
          </div>
        )
      case 'SHIPPED':
        return (
          <div className="inline-flex items-center gap-2 bg-blue-100 text-blue-800 px-4 py-2 rounded-full">
            <Truck className="w-5 h-5" />
            <span className="font-semibold">Shipped</span>
          </div>
        )
      case 'CONFIRMED':
        return (
          <div className="inline-flex items-center gap-2 bg-yellow-100 text-yellow-800 px-4 py-2 rounded-full">
            <Package className="w-5 h-5" />
            <span className="font-semibold">Processing</span>
          </div>
        )
      case 'PENDING':
        return (
          <div className="inline-flex items-center gap-2 bg-orange-100 text-orange-800 px-4 py-2 rounded-full">
            <Calendar className="w-5 h-5" />
            <span className="font-semibold">Pending</span>
          </div>
        )
      default:
        return <span className="text-gray-600">{status}</span>
    }
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-4xl mx-auto px-4">
        {/* Header */}
        <button
          onClick={() => navigate('/orders')}
          className="flex items-center gap-2 text-blue-600 hover:text-blue-700 font-semibold mb-8 transition"
        >
          <ArrowLeft className="w-5 h-5" />
          Back to Orders
        </button>

        {/* Order Header */}
        <div className="bg-white rounded-lg shadow-md p-8 mb-6">
          <div className="flex items-start justify-between mb-6">
            <div>
              <p className="text-sm text-gray-600 mb-2">ORDER ID</p>
              <h1 className="text-3xl font-bold text-gray-900 mb-4">{orderDetail.id}</h1>
              {getStatusBadge(orderDetail.status)}
            </div>
            <div className="text-right">
              <p className="text-sm text-gray-600 mb-1">Order Date</p>
              <p className="text-2xl font-bold text-gray-900">{orderDetail.date}</p>
            </div>
          </div>
        </div>

        {/* Main Content Grid */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Order Items */}
          <div className="lg:col-span-2">
            <div className="bg-white rounded-lg shadow-md p-6 mb-6">
              <h2 className="text-2xl font-bold mb-6 text-gray-900">Order Items</h2>
              <div className="space-y-4">
                {orderDetail.items_detail && orderDetail.items_detail.length > 0 ? (
                  orderDetail.items_detail.map((item, index) => (
                    <div key={index} className="flex items-start justify-between p-4 border border-gray-200 rounded-lg hover:bg-gray-50 transition">
                      <div className="flex-1">
                        <h3 className="font-semibold text-gray-900 mb-1">{item.name}</h3>
                        <p className="text-sm text-gray-600">Quantity: {item.quantity}</p>
                      </div>
                      <div className="text-right">
                        <p className="font-semibold text-gray-900">${(item.price * item.quantity).toFixed(2)}</p>
                        <p className="text-xs text-gray-600">${item.price.toFixed(2)} each</p>
                      </div>
                    </div>
                  ))
                ) : (
                  <p className="text-gray-600">{orderDetail.items} item(s)</p>
                )}
              </div>
            </div>

            {/* Shipping Address */}
            <div className="bg-white rounded-lg shadow-md p-6 mb-6">
              <h2 className="text-xl font-bold mb-4 text-gray-900 flex items-center gap-2">
                <MapPin className="w-5 h-5 text-blue-600" />
                Shipping Address
              </h2>
              {orderDetail.shippingAddress ? (
                <div className="bg-gray-50 p-4 rounded-lg">
                  <p className="font-semibold text-gray-900 mb-2">{orderDetail.shippingAddress.fullName}</p>
                  <p className="text-gray-700 mb-1">{orderDetail.shippingAddress.street}</p>
                  <p className="text-gray-700 mb-1">
                    {orderDetail.shippingAddress.city}, {orderDetail.shippingAddress.state} {orderDetail.shippingAddress.zipCode}
                  </p>
                  <p className="text-gray-700">Phone: {orderDetail.shippingAddress.phone}</p>
                </div>
              ) : (
                <p className="text-gray-600">Address not available</p>
              )}
            </div>

            {/* Tracking Info */}
            {orderDetail.trackingId && (
              <div className="bg-white rounded-lg shadow-md p-6">
                <h2 className="text-xl font-bold mb-4 text-gray-900 flex items-center gap-2">
                  <Truck className="w-5 h-5 text-blue-600" />
                  Tracking Information
                </h2>
                <div className="bg-blue-50 p-4 rounded-lg border border-blue-200">
                  <p className="text-sm text-blue-700 font-semibold mb-1">Tracking ID</p>
                  <p className="text-lg font-mono font-bold text-blue-900 mb-4">{orderDetail.trackingId}</p>
                  <button
                    onClick={() => navigate(`/track/${orderDetail.trackingId}`)}
                    className="bg-blue-600 text-white px-6 py-2 rounded-lg font-semibold hover:bg-blue-700 transition"
                  >
                    Track Shipment
                  </button>
                </div>
              </div>
            )}
          </div>

          {/* Order Summary Sidebar */}
          <div className="lg:col-span-1">
            {/* Payment Info */}
            <div className="bg-white rounded-lg shadow-md p-6 mb-6">
              <h2 className="text-xl font-bold mb-4 text-gray-900 flex items-center gap-2">
                <CreditCard className="w-5 h-5 text-blue-600" />
                Payment
              </h2>
              {orderDetail.paymentMethod && (
                <div className="space-y-2 mb-4">
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-600">Method:</span>
                    <span className="font-semibold text-gray-900">{orderDetail.paymentMethod}</span>
                  </div>
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-600">Status:</span>
                    <span className="font-semibold text-green-600">{orderDetail.paymentStatus}</span>
                  </div>
                </div>
              )}
            </div>

            {/* Order Summary */}
            <div className="bg-white rounded-lg shadow-md p-6">
              <h2 className="text-xl font-bold mb-4 text-gray-900">Order Summary</h2>
              <div className="space-y-3 mb-4 pb-4 border-b border-gray-200">
                {orderDetail.subtotal !== undefined && (
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-600">Subtotal:</span>
                    <span className="text-gray-900">${orderDetail.subtotal.toFixed(2)}</span>
                  </div>
                )}
                {orderDetail.shipping !== undefined && (
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-600">Shipping:</span>
                    <span className="text-gray-900">${orderDetail.shipping.toFixed(2)}</span>
                  </div>
                )}
                {orderDetail.tax !== undefined && (
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-600">Tax:</span>
                    <span className="text-gray-900">${orderDetail.tax.toFixed(2)}</span>
                  </div>
                )}
              </div>
              <div className="flex justify-between">
                <span className="font-bold text-gray-900">Total:</span>
                <span className="text-2xl font-bold text-blue-600">${orderDetail.total.toFixed(2)}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
