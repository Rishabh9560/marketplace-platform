import React from 'react'
import { Trash2, Plus, Minus } from 'lucide-react'

interface CartItemProps {
  id: string
  productId: string
  productName: string
  productImage: string
  variantName?: string
  price: number
  quantity: number
  maxQuantity: number
  vendorName: string
  onUpdateQuantity?: (itemId: string, quantity: number) => Promise<void>
  onRemove?: (itemId: string) => Promise<void>
}

/**
 * CartItem component - displays individual cart item with quantity controls.
 */
export const CartItem: React.FC<CartItemProps> = ({
  id,
  productId,
  productName,
  productImage,
  variantName,
  price,
  quantity,
  maxQuantity,
  vendorName,
  onUpdateQuantity,
  onRemove,
}) => {
  const [isUpdating, setIsUpdating] = React.useState(false)
  const [isRemoving, setIsRemoving] = React.useState(false)

  const handleQuantityChange = async (newQuantity: number) => {
    if (newQuantity < 1 || newQuantity > maxQuantity) return
    if (!onUpdateQuantity) return

    try {
      setIsUpdating(true)
      await onUpdateQuantity(id, newQuantity)
    } finally {
      setIsUpdating(false)
    }
  }

  const handleRemove = async () => {
    if (!onRemove) return
    try {
      setIsRemoving(true)
      await onRemove(id)
    } finally {
      setIsRemoving(false)
    }
  }

  const subtotal = price * quantity

  return (
    <div className="flex gap-4 p-4 bg-white rounded-lg border border-gray-200 hover:border-gray-300 transition-colors">
      {/* Product Image */}
      <div className="w-24 h-24 flex-shrink-0 bg-gray-100 rounded overflow-hidden">
        <img src={productImage} alt={productName} className="w-full h-full object-cover" />
      </div>

      {/* Product Details */}
      <div className="flex-1">
        <h3 className="font-semibold text-gray-900">{productName}</h3>
        {variantName && <p className="text-sm text-gray-600">{variantName}</p>}
        <p className="text-sm text-gray-500">Vendor: {vendorName}</p>
        <p className="mt-2 font-semibold text-gray-900">₹{price.toLocaleString()}</p>
      </div>

      {/* Quantity Controls */}
      <div className="flex flex-col items-end gap-3">
        {/* Quantity Selector */}
        <div className="flex items-center border border-gray-300 rounded">
          <button
            onClick={() => handleQuantityChange(quantity - 1)}
            disabled={quantity <= 1 || isUpdating}
            className="p-1 hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <Minus size={16} />
          </button>
          <input
            type="number"
            value={quantity}
            onChange={(e) => handleQuantityChange(parseInt(e.target.value) || 1)}
            disabled={isUpdating}
            className="w-12 text-center border-l border-r border-gray-300 py-1 disabled:bg-gray-50"
          />
          <button
            onClick={() => handleQuantityChange(quantity + 1)}
            disabled={quantity >= maxQuantity || isUpdating}
            className="p-1 hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <Plus size={16} />
          </button>
        </div>

        {/* Subtotal and Remove */}
        <div className="text-right">
          <p className="text-lg font-bold text-gray-900">₹{subtotal.toLocaleString()}</p>
          <button
            onClick={handleRemove}
            disabled={isRemoving}
            className="text-red-600 hover:text-red-700 mt-1 flex items-center gap-1 text-sm disabled:opacity-50"
          >
            <Trash2 size={16} />
            {isRemoving ? 'Removing...' : 'Remove'}
          </button>
        </div>
      </div>
    </div>
  )
}
