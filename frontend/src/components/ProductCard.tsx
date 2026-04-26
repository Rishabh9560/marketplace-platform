import React from 'react'
import { Heart, ShoppingCart, Star } from 'lucide-react'
import { Link } from 'react-router-dom'

interface Variant {
  id: string
  name: string
  price: number
  stock: number
}

interface ProductCardProps {
  id: string
  name: string
  description: string
  price: number
  rating: number
  reviewCount: number
  imageUrl: string
  vendor: {
    id: string
    name: string
  }
  variants?: Variant[]
  isFeatured?: boolean
  onAddToCart?: (variantId: string, quantity: number) => void
  onToggleWishlist?: (productId: string) => void
  isInWishlist?: boolean
}

/**
 * ProductCard component - displays product information with add-to-cart functionality.
 */
export const ProductCard: React.FC<ProductCardProps> = ({
  id,
  name,
  description,
  price,
  rating,
  reviewCount,
  imageUrl,
  vendor,
  variants = [],
  isFeatured = false,
  onAddToCart,
  onToggleWishlist,
  isInWishlist = false,
}) => {
  const [selectedVariant, setSelectedVariant] = React.useState<Variant | null>(variants[0] || null)
  const [quantity, setQuantity] = React.useState(1)
  const [isAdding, setIsAdding] = React.useState(false)

  const handleAddToCart = async () => {
    if (!selectedVariant || !onAddToCart) return
    try {
      setIsAdding(true)
      await onAddToCart(selectedVariant.id, quantity)
      setQuantity(1)
    } finally {
      setIsAdding(false)
    }
  }

  const inStock = selectedVariant ? selectedVariant.stock > 0 : true

  return (
    <div className="bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow overflow-hidden">
      {/* Image Container */}
      <div className="relative w-full h-48 bg-gray-100 overflow-hidden">
        <img
          src={imageUrl}
          alt={name}
          className="w-full h-full object-cover hover:scale-105 transition-transform"
        />
        {isFeatured && <div className="absolute top-2 right-2 bg-red-500 text-white px-3 py-1 rounded text-sm font-semibold">Featured</div>}
        <button
          onClick={() => onToggleWishlist?.(id)}
          className="absolute top-2 left-2 bg-white rounded-full p-2 hover:bg-gray-100 transition-colors"
        >
          <Heart
            size={20}
            className={isInWishlist ? 'fill-red-500 text-red-500' : 'text-gray-400'}
          />
        </button>
      </div>

      {/* Content Container */}
      <div className="p-4">
        {/* Product Name and Link */}
        <Link to={`/products/${id}`}>
          <h3 className="text-lg font-semibold text-gray-900 hover:text-blue-600 transition-colors line-clamp-2">
            {name}
          </h3>
        </Link>

        {/* Description */}
        <p className="text-sm text-gray-600 mt-1 line-clamp-2">{description}</p>

        {/* Rating and Reviews */}
        <div className="flex items-center gap-2 mt-2">
          <div className="flex items-center">
            {[...Array(5)].map((_, i) => (
              <Star
                key={i}
                size={14}
                className={i < Math.round(rating) ? 'fill-yellow-400 text-yellow-400' : 'text-gray-300'}
              />
            ))}
          </div>
          <span className="text-sm text-gray-600">({reviewCount} reviews)</span>
        </div>

        {/* Vendor */}
        <p className="text-sm text-gray-500 mt-2">By {vendor.name}</p>

        {/* Price */}
        <div className="mt-3 mb-4">
          <p className="text-2xl font-bold text-gray-900">₹{price.toLocaleString()}</p>
        </div>

        {/* Variants Selector */}
        {variants.length > 1 && (
          <div className="mb-3">
            <label className="text-xs font-semibold text-gray-700 block mb-1">Select Variant</label>
            <select
              value={selectedVariant?.id || ''}
              onChange={(e) => {
                const variant = variants.find((v) => v.id === e.target.value)
                setSelectedVariant(variant || null)
              }}
              className="w-full border border-gray-300 rounded px-2 py-1 text-sm"
            >
              {variants.map((variant) => (
                <option key={variant.id} value={variant.id}>
                  {variant.name} - ₹{variant.price.toLocaleString()}
                </option>
              ))}
            </select>
          </div>
        )}

        {/* Quantity Selector */}
        <div className="flex items-center gap-2 mb-4">
          <label className="text-xs font-semibold text-gray-700">Qty:</label>
          <button
            onClick={() => setQuantity(Math.max(1, quantity - 1))}
            className="border border-gray-300 rounded px-2 py-1 text-sm hover:bg-gray-100"
          >
            −
          </button>
          <input
            type="number"
            min="1"
            max={selectedVariant?.stock || 999}
            value={quantity}
            onChange={(e) => setQuantity(Math.max(1, parseInt(e.target.value) || 1))}
            className="w-10 text-center border border-gray-300 rounded px-1 py-1 text-sm"
          />
          <button
            onClick={() => setQuantity(Math.min(selectedVariant?.stock || 999, quantity + 1))}
            className="border border-gray-300 rounded px-2 py-1 text-sm hover:bg-gray-100"
          >
            +
          </button>
        </div>

        {/* Add to Cart Button */}
        <button
          onClick={handleAddToCart}
          disabled={!inStock || isAdding}
          className={`w-full flex items-center justify-center gap-2 py-2 rounded font-semibold transition-colors ${
            inStock
              ? 'bg-blue-600 text-white hover:bg-blue-700 active:scale-95'
              : 'bg-gray-300 text-gray-500 cursor-not-allowed'
          }`}
        >
          <ShoppingCart size={18} />
          {isAdding ? 'Adding...' : inStock ? 'Add to Cart' : 'Out of Stock'}
        </button>
      </div>
    </div>
  )
}
