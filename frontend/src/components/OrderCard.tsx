import React from 'react'
import { ChevronRight, AlertCircle, CheckCircle, Clock, Truck } from 'lucide-react'
import { Link } from 'react-router-dom'

interface OrderItem {
  id: string
  productName: string
  productImage?: string
  quantity: number
  price: number
  vendorName: string
}

interface OrderCardProps {
  id: string
  orderNumber: string
  status: 'PENDING_PAYMENT' | 'PAID' | 'PROCESSING' | 'PACKED' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED' | 'REFUNDED'
  totalAmount: number
  itemCount: number
  items?: OrderItem[]
  createdAt: string
  estimatedDelivery?: string
  onViewDetails?: (orderId: string) => void
}

/**
 * OrderCard component - displays order summary with status and actions.
 */
export const OrderCard: React.FC<OrderCardProps> = ({
  id,
  orderNumber,
  status,
  totalAmount,
  itemCount,
  items = [],
  createdAt,
  estimatedDelivery,
  onViewDetails,
}) => {
  const statusConfig = {
    PENDING_PAYMENT: { label: 'Pending Payment', color: 'bg-yellow-100 text-yellow-800', icon: AlertCircle },
    PAID: { label: 'Confirmed', color: 'bg-blue-100 text-blue-800', icon: CheckCircle },
    PROCESSING: { label: 'Processing', color: 'bg-blue-100 text-blue-800', icon: Clock },
    PACKED: { label: 'Packed', color: 'bg-purple-100 text-purple-800', icon: Clock },
    SHIPPED: { label: 'Shipped', color: 'bg-indigo-100 text-indigo-800', icon: Truck },
    DELIVERED: { label: 'Delivered', color: 'bg-green-100 text-green-800', icon: CheckCircle },
    CANCELLED: { label: 'Cancelled', color: 'bg-red-100 text-red-800', icon: AlertCircle },
    REFUNDED: { label: 'Refunded', color: 'bg-gray-100 text-gray-800', icon: AlertCircle },
  }

  const config = statusConfig[status] || statusConfig.PENDING_PAYMENT
  const StatusIcon = config.icon

  const formattedDate = new Date(createdAt).toLocaleDateString('en-IN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  })

  return (
    <div className="bg-white rounded-lg border border-gray-200 hover:border-gray-300 transition-colors overflow-hidden">
      {/* Header */}
      <div className="p-4 border-b border-gray-100 flex items-start justify-between">
        <div>
          <h3 className="font-semibold text-gray-900">Order #{orderNumber}</h3>
          <p className="text-sm text-gray-600">Placed on {formattedDate}</p>
        </div>
        <div className={`px-3 py-1 rounded-full flex items-center gap-2 text-sm font-semibold ${config.color}`}>
          <StatusIcon size={16} />
          {config.label}
        </div>
      </div>

      {/* Items Preview */}
      {items.length > 0 && (
        <div className="p-4 border-b border-gray-100">
          <p className="text-sm font-semibold text-gray-700 mb-2">Items ({itemCount})</p>
          <div className="space-y-2">
            {items.slice(0, 2).map((item) => (
              <div key={item.id} className="flex items-center gap-3">
                {item.productImage && (
                  <img
                    src={item.productImage}
                    alt={item.productName}
                    className="w-10 h-10 rounded object-cover"
                  />
                )}
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-medium text-gray-900 truncate">{item.productName}</p>
                  <p className="text-xs text-gray-600">
                    {item.quantity} × ₹{item.price.toLocaleString()}
                  </p>
                </div>
              </div>
            ))}
            {itemCount > 2 && <p className="text-xs text-gray-500 pt-2">+{itemCount - 2} more items</p>}
          </div>
        </div>
      )}

      {/* Footer with Amount and Action */}
      <div className="p-4 flex items-center justify-between">
        <div>
          <p className="text-xs text-gray-600 mb-1">Total Amount</p>
          <p className="text-lg font-bold text-gray-900">₹{totalAmount.toLocaleString()}</p>
          {estimatedDelivery && (
            <p className="text-xs text-gray-600 mt-1">Est. delivery: {estimatedDelivery}</p>
          )}
        </div>
        <Link
          to={`/orders/${id}`}
          onClick={() => onViewDetails?.(id)}
          className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition-colors font-semibold"
        >
          View Details
          <ChevronRight size={18} />
        </Link>
      </div>
    </div>
  )
}
