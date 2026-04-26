import React from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { ArrowLeft, Package, MapPin, Truck, CheckCircle, Clock, AlertCircle } from 'lucide-react'
import { useAuthStore } from '@/store'

interface TrackingEvent {
  timestamp: string
  status: string
  location: string
  description: string
  completed: boolean
  icon: 'check' | 'truck' | 'location' | 'clock'
}

interface TrackingData {
  orderId: string
  trackingId: string
  currentStatus: 'CONFIRMED' | 'SHIPPED' | 'IN_TRANSIT' | 'OUT_FOR_DELIVERY' | 'DELIVERED'
  currentLocation: string
  estimatedDelivery: string
  carrier: string
  events: TrackingEvent[]
}

// Mock tracking data
const MOCK_TRACKING: { [key: string]: TrackingData } = {
  'TRACK-123456789': {
    orderId: 'ORD-001',
    trackingId: 'TRACK-123456789',
    currentStatus: 'DELIVERED',
    currentLocation: 'Delivered at your doorstep',
    estimatedDelivery: '2024-01-18',
    carrier: 'FastShip Express',
    events: [
      {
        timestamp: '2024-01-15 10:30',
        status: 'Order Placed',
        location: 'Online Store',
        description: 'Your order has been received and confirmed',
        completed: true,
        icon: 'check',
      },
      {
        timestamp: '2024-01-15 14:45',
        status: 'Processing',
        location: 'Warehouse - Mumbai',
        description: 'Your order is being prepared for shipment',
        completed: true,
        icon: 'clock',
      },
      {
        timestamp: '2024-01-16 08:20',
        status: 'Shipped',
        location: 'Delhi Distribution Center',
        description: 'Package picked up by FastShip Express',
        completed: true,
        icon: 'truck',
      },
      {
        timestamp: '2024-01-17 15:30',
        status: 'In Transit',
        location: 'Bangalore Hub',
        description: 'Package in transit to delivery address',
        completed: true,
        icon: 'location',
      },
      {
        timestamp: '2024-01-18 09:15',
        status: 'Out for Delivery',
        location: 'Local Delivery Station',
        description: 'Package out for delivery today',
        completed: true,
        icon: 'truck',
      },
      {
        timestamp: '2024-01-18 17:45',
        status: 'Delivered',
        location: 'Your Location',
        description: 'Package delivered successfully',
        completed: true,
        icon: 'check',
      },
    ],
  },
  'TRACK-987654321': {
    orderId: 'ORD-002',
    trackingId: 'TRACK-987654321',
    currentStatus: 'IN_TRANSIT',
    currentLocation: 'Bangalore - In Transit',
    estimatedDelivery: '2024-01-22',
    carrier: 'QuickDeliver',
    events: [
      {
        timestamp: '2024-01-18 11:00',
        status: 'Order Placed',
        location: 'Online Store',
        description: 'Your order has been received and confirmed',
        completed: true,
        icon: 'check',
      },
      {
        timestamp: '2024-01-18 15:30',
        status: 'Processing',
        location: 'Warehouse - Delhi',
        description: 'Your order is being prepared for shipment',
        completed: true,
        icon: 'clock',
      },
      {
        timestamp: '2024-01-19 09:00',
        status: 'Shipped',
        location: 'Bangalore Distribution Center',
        description: 'Package picked up by QuickDeliver',
        completed: true,
        icon: 'truck',
      },
      {
        timestamp: '2024-01-20 14:20',
        status: 'In Transit',
        location: 'Bangalore - In Transit',
        description: 'Package in transit to delivery address',
        completed: true,
        icon: 'location',
      },
      {
        timestamp: '2024-01-22 (Expected)',
        status: 'Out for Delivery',
        location: 'Local Delivery Station',
        description: 'Package will be out for delivery',
        completed: false,
        icon: 'truck',
      },
      {
        timestamp: '2024-01-22 (Expected)',
        status: 'Delivered',
        location: 'Your Location',
        description: 'Expected delivery today',
        completed: false,
        icon: 'check',
      },
    ],
  },
}

export const OrderTrackingPage: React.FC = () => {
  const { trackingId } = useParams<{ trackingId: string }>()
  const navigate = useNavigate()
  const { isAuthenticated } = useAuthStore()

  const tracking = trackingId ? MOCK_TRACKING[trackingId] : null

  if (!isAuthenticated) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <button onClick={() => navigate('/login')} className="bg-blue-600 text-white px-8 py-3 rounded-lg">
          Login to Track Orders
        </button>
      </div>
    )
  }

  if (!tracking) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <AlertCircle className="w-16 h-16 text-red-500 mx-auto mb-4" />
          <p className="text-xl font-semibold text-gray-800 mb-4">Tracking Not Found</p>
          <button onClick={() => navigate('/orders')} className="bg-blue-600 text-white px-8 py-3 rounded-lg">
            Back to Orders
          </button>
        </div>
      </div>
    )
  }

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'DELIVERED':
        return 'text-green-600 bg-green-50'
      case 'OUT_FOR_DELIVERY':
        return 'text-blue-600 bg-blue-50'
      case 'IN_TRANSIT':
        return 'text-orange-600 bg-orange-50'
      case 'SHIPPED':
        return 'text-yellow-600 bg-yellow-50'
      case 'CONFIRMED':
        return 'text-purple-600 bg-purple-50'
      default:
        return 'text-gray-600 bg-gray-50'
    }
  }

  const getStatusIcon = (icon: string, completed: boolean) => {
    const iconClass = completed ? 'w-6 h-6' : 'w-6 h-6'
    const color = completed ? 'text-green-600' : 'text-gray-400'

    switch (icon) {
      case 'check':
        return <CheckCircle className={`${iconClass} ${color}`} />
      case 'truck':
        return <Truck className={`${iconClass} ${color}`} />
      case 'location':
        return <MapPin className={`${iconClass} ${color}`} />
      case 'clock':
        return <Clock className={`${iconClass} ${color}`} />
      default:
        return <Package className={`${iconClass} ${color}`} />
    }
  }

  const lastCompletedIndex = tracking.events.findIndex((e) => !e.completed)

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
        <div className="bg-white rounded-lg shadow-md p-6 mb-8">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
            <div>
              <p className="text-sm text-gray-600 mb-1">Order ID</p>
              <p className="text-xl font-bold text-gray-900">{tracking.orderId}</p>
            </div>
            <div>
              <p className="text-sm text-gray-600 mb-1">Tracking ID</p>
              <p className="text-lg font-mono font-semibold text-gray-800">{tracking.trackingId}</p>
            </div>
            <div>
              <p className="text-sm text-gray-600 mb-1">Carrier</p>
              <p className="text-lg font-semibold text-gray-800">{tracking.carrier}</p>
            </div>
            <div>
              <p className="text-sm text-gray-600 mb-1">Est. Delivery</p>
              <p className="text-lg font-semibold text-green-600">{tracking.estimatedDelivery}</p>
            </div>
          </div>
        </div>

        {/* Current Status Card */}
        <div className={`${getStatusColor(tracking.currentStatus)} rounded-lg p-6 mb-8 border-l-4 border-current`}>
          <div className="flex items-start justify-between">
            <div>
              <p className="text-sm font-semibold opacity-75">Current Status</p>
              <h2 className="text-3xl font-bold mt-1">{tracking.currentStatus.replace('_', ' ')}</h2>
              <p className="text-lg mt-2 opacity-90">{tracking.currentLocation}</p>
            </div>
            <Package className="w-12 h-12 opacity-20" />
          </div>
        </div>

        {/* Tracking Timeline */}
        <div className="bg-white rounded-lg shadow-md p-8">
          <h3 className="text-2xl font-bold mb-8 text-gray-900">Tracking Timeline</h3>

          <div className="space-y-0">
            {tracking.events.map((event, index) => (
              <div key={index} className="flex gap-6 pb-8 relative">
                {/* Timeline line */}
                {index !== tracking.events.length - 1 && (
                  <div className={`absolute left-3 top-12 w-0.5 h-20 ${event.completed ? 'bg-green-500' : 'bg-gray-300'}`}></div>
                )}

                {/* Event icon */}
                <div className="flex-shrink-0 relative z-10">
                  <div
                    className={`w-12 h-12 rounded-full flex items-center justify-center ${
                      event.completed ? 'bg-green-100' : 'bg-gray-100'
                    }`}
                  >
                    {getStatusIcon(event.icon, event.completed)}
                  </div>
                </div>

                {/* Event content */}
                <div className="flex-grow pt-1">
                  <div className="flex items-start justify-between">
                    <div>
                      <h4 className={`text-lg font-bold ${event.completed ? 'text-gray-900' : 'text-gray-600'}`}>
                        {event.status}
                      </h4>
                      <p className={`text-sm mt-1 ${event.completed ? 'text-gray-700' : 'text-gray-500'}`}>
                        {event.timestamp}
                      </p>
                    </div>
                    {event.completed && (
                      <span className="bg-green-100 text-green-800 text-xs font-semibold px-3 py-1 rounded-full">
                        Completed
                      </span>
                    )}
                    {!event.completed && index === lastCompletedIndex && (
                      <span className="bg-blue-100 text-blue-800 text-xs font-semibold px-3 py-1 rounded-full animate-pulse">
                        Upcoming
                      </span>
                    )}
                  </div>

                  <div className="mt-3 p-4 bg-gray-50 rounded-lg border border-gray-200">
                    <p className="text-sm text-gray-700">{event.description}</p>
                    <p className="text-xs text-gray-600 mt-2 flex items-center gap-1">
                      <MapPin className="w-3 h-3" />
                      {event.location}
                    </p>
                  </div>
                </div>
              </div>
            ))}
          </div>

          {/* Delivery Status Summary */}
          <div className="mt-10 pt-8 border-t border-gray-200">
            <h4 className="text-lg font-bold mb-4 text-gray-900">Delivery Status Summary</h4>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="bg-blue-50 p-4 rounded-lg border border-blue-200">
                <p className="text-sm text-blue-700 font-semibold">Items in Package</p>
                <p className="text-2xl font-bold text-blue-900 mt-1">1 Item</p>
              </div>
              <div className="bg-purple-50 p-4 rounded-lg border border-purple-200">
                <p className="text-sm text-purple-700 font-semibold">Weight</p>
                <p className="text-2xl font-bold text-purple-900 mt-1">1.5 kg</p>
              </div>
              <div className="bg-orange-50 p-4 rounded-lg border border-orange-200">
                <p className="text-sm text-orange-700 font-semibold">Last Update</p>
                <p className="text-2xl font-bold text-orange-900 mt-1">{tracking.events[tracking.events.length - 1].timestamp}</p>
              </div>
              <div className="bg-green-50 p-4 rounded-lg border border-green-200">
                <p className="text-sm text-green-700 font-semibold">Next Update</p>
                <p className="text-2xl font-bold text-green-900 mt-1">Auto-refresh</p>
              </div>
            </div>
          </div>

          {/* Help Section */}
          <div className="mt-8 p-4 bg-yellow-50 border border-yellow-200 rounded-lg">
            <p className="text-sm text-yellow-800">
              <span className="font-semibold">💡 Tip:</span> This page automatically updates. Bookmark this link to check your delivery status anytime.
            </p>
          </div>
        </div>
      </div>
    </div>
  )
}
