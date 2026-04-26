import React from 'react'
import { Star, Truck, Shield } from 'lucide-react'
import { useParams, useNavigate } from 'react-router-dom'
import { useCartStore, useWishlistStore } from '@/store'
import { formatCurrency } from '@/lib/utils'

export const ProductDetailPage: React.FC = () => {
  const { productId } = useParams()
  const navigate = useNavigate()
  const { addToCart } = useCartStore()
  const { isInWishlist, addItem: addToWishlist, removeItem: removeFromWishlist } = useWishlistStore()
  const [quantity, setQuantity] = React.useState(1)
  const [isAdding, setIsAdding] = React.useState(false)

  const handleAddToCart = async () => {
    try {
      setIsAdding(true)
      // Extract vendor ID - default to vendor.id or 'default-vendor'
      const vendorId = product.vendor.id || 'default-vendor'
      // Extract variant ID - default to product ID
      const variantId = product.id
      
      await addToCart(variantId, quantity, vendorId)
      
      // Show success message
      alert(`✅ Added ${quantity} item(s) to cart!`)
      
      // Reset quantity
      setQuantity(1)
      
      // Ask user if they want to go to cart
      setTimeout(() => {
        const goToCart = confirm('Would you like to view your cart now?')
        if (goToCart) {
          navigate('/cart')
        }
      }, 500)
    } catch (error) {
      console.error('Failed to add to cart:', error)
      alert('❌ Failed to add to cart. Please try again.')
    } finally {
      setIsAdding(false)
    }
  }

  // Mock product
  const product = {
    id: productId || '1',
    name: 'Premium Wireless Headphones',
    description: 'High-quality audio with active noise cancellation, 30-hour battery life, and premium comfort.',
    price: 129.99,
    originalPrice: 199.99,
    rating: 4.5,
    reviews: 2540,
    category: 'Electronics',
    images: [
      'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&h=800&fit=crop',
      'https://images.unsplash.com/photo-1487215078519-e21cc028cb29?w=800&h=800&fit=crop',
    ],
    vendor: { id: 'v1', name: 'Tech Store', rating: 4.8, reviews: 5240 },
    inStock: true,
    specifications: {
      'Driver Size': '40mm',
      'Frequency Response': '20Hz - 20kHz',
      'Impedance': '32 Ohms',
      'Battery Life': '30 hours',
      'Charging Time': '2 hours',
      'Connectivity': 'Bluetooth 5.0',
    },
  }

  return (
    <div className="max-w-7xl mx-auto px-4 py-8">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        {/* Images */}
        <div>
          <div className="bg-gray-200 rounded-lg h-96 flex items-center justify-center mb-4">
            <img src={product.images[0]} alt={product.name} className="w-full h-full object-cover rounded-lg" />
          </div>
          <div className="flex gap-2">
            {product.images.map((img, idx) => (
              <div key={idx} className="bg-gray-200 rounded-lg w-20 h-20 flex items-center justify-center cursor-pointer hover:ring-2 ring-blue-600">
                <img src={img} alt={`View ${idx + 1}`} className="w-full h-full object-cover rounded-lg" />
              </div>
            ))}
          </div>
        </div>

        {/* Details */}
        <div>
          <div className="mb-4">
            <div className="text-sm text-gray-600 mb-2">{product.category}</div>
            <h1 className="text-3xl font-bold mb-4">{product.name}</h1>
            <p className="text-gray-600 mb-4">{product.description}</p>
          </div>

          {/* Rating */}
          <div className="flex items-center gap-2 mb-6 pb-6 border-b border-gray-200">
            <div className="flex items-center">
              {Array.from({ length: 5 }).map((_, i) => (
                <Star key={i} className={`w-5 h-5 ${i < Math.floor(product.rating) ? 'fill-yellow-400 text-yellow-400' : 'text-gray-300'}`} />
              ))}
            </div>
            <span className="font-semibold">{product.rating}</span>
            <span className="text-gray-600">({product.reviews.toLocaleString()} reviews)</span>
          </div>

          {/* Vendor */}
          <div className="mb-6 pb-6 border-b border-gray-200">
            <p className="text-sm text-gray-600 mb-2">Sold by</p>
            <div className="flex items-center justify-between">
              <div>
                <p className="font-semibold text-lg">{product.vendor.name}</p>
                <div className="flex items-center gap-2 text-sm">
                  <span className="text-yellow-500 font-semibold">{product.vendor.rating}★</span>
                  <span className="text-gray-600">({product.vendor.reviews.toLocaleString()} reviews)</span>
                </div>
              </div>
            </div>
          </div>

          {/* Price & Stock */}
          <div className="mb-6 pb-6 border-b border-gray-200">
            <div className="flex items-baseline gap-3 mb-4">
              <span className="text-4xl font-bold text-blue-600">{formatCurrency(product.price)}</span>
              <span className="text-2xl text-gray-400 line-through">{formatCurrency(product.originalPrice)}</span>
              <span className="text-lg font-semibold text-red-600">Save {Math.round(((product.originalPrice - product.price) / product.originalPrice) * 100)}%</span>
            </div>
            <div className={`inline-block px-4 py-2 rounded-lg font-semibold ${product.inStock ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>
              {product.inStock ? 'In Stock' : 'Out of Stock'}
            </div>
          </div>

          {/* Quantity & Add to Cart */}
          <div className="mb-6">
            <div className="flex items-center gap-4 mb-4">
              <span className="font-semibold">Quantity:</span>
              <div className="flex items-center border border-gray-300 rounded-lg">
                <button onClick={() => setQuantity(Math.max(1, quantity - 1))} className="px-3 py-2 text-lg">-</button>
                <span className="px-4 py-2 font-semibold">{quantity}</span>
                <button onClick={() => setQuantity(quantity + 1)} className="px-3 py-2 text-lg">+</button>
              </div>
            </div>
            <button
              onClick={handleAddToCart}
              disabled={!product.inStock || isAdding}
              className="w-full bg-blue-600 text-white py-3 rounded-lg font-semibold hover:bg-blue-700 transition disabled:opacity-50 flex items-center justify-center gap-2"
            >
              {isAdding ? (
                <>
                  <div className="animate-spin h-5 w-5 border-2 border-white border-t-transparent rounded-full"></div>
                  Adding...
                </>
              ) : (
                '🛒 Add to Cart'
              )}
            </button>
            <button
              onClick={() => isInWishlist(product.id) ? removeFromWishlist(product.id) : addToWishlist(product.id)}
              className={`w-full mt-2 py-3 rounded-lg font-semibold transition border-2 ${isInWishlist(product.id) ? 'bg-red-50 border-red-600 text-red-600' : 'border-gray-300 text-gray-700 hover:bg-gray-50'}`}
            >
              {isInWishlist(product.id) ? '❤ Saved to Wishlist' : 'Add to Wishlist'}
            </button>
          </div>

          {/* Features */}
          <div className="bg-blue-50 rounded-lg p-4 space-y-3">
            <div className="flex items-start gap-3">
              <Truck className="w-5 h-5 text-blue-600 mt-1 flex-shrink-0" />
              <div>
                <p className="font-semibold">Free Shipping</p>
                <p className="text-sm text-gray-600">On orders over $50</p>
              </div>
            </div>
            <div className="flex items-start gap-3">
              <Shield className="w-5 h-5 text-blue-600 mt-1 flex-shrink-0" />
              <div>
                <p className="font-semibold">30-Day Guarantee</p>
                <p className="text-sm text-gray-600">Not satisfied? Return it for free</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Specifications */}
      <div className="mt-12 border-t border-gray-200 pt-8">
        <h2 className="text-2xl font-bold mb-6">Specifications</h2>
        <div className="bg-white rounded-lg overflow-hidden">
          {Object.entries(product.specifications).map(([key, value], idx) => (
            <div key={idx} className={`flex py-4 px-6 ${idx % 2 === 0 ? 'bg-gray-50' : 'bg-white'}`}>
              <span className="font-semibold text-gray-700 w-1/3">{key}</span>
              <span className="text-gray-600 w-2/3">{value}</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
