import React from 'react'
import { useWishlistStore, useCartStore } from '@/store'
import { Heart, ShoppingCart, X } from 'lucide-react'
import { Link } from 'react-router-dom'
import { formatCurrency } from '@/lib/utils'

// Mock products
const MOCK_PRODUCTS: any[] = [
  {
    id: '1',
    name: 'Premium Wireless Headphones',
    price: 129.99,
    images: ['/placeholder.jpg'],
    vendor: { id: 'v1', name: 'Tech Store' },
  },
  {
    id: '2',
    name: 'Organic Cotton T-Shirt',
    price: 29.99,
    images: ['/placeholder.jpg'],
    vendor: { id: 'v2', name: 'Fashion Hub' },
  },
]

export const WishlistPage: React.FC = () => {
  const { items, removeItem } = useWishlistStore()
  const { addToCart } = useCartStore()
  const [isAdding, setIsAdding] = React.useState(false)

  const wishlistProducts = MOCK_PRODUCTS.filter((p) => items.includes(p.id))

  const handleAddToCart = async (product: any) => {
    try {
      setIsAdding(true)
      const vendorId = product.vendor?.id || 'default-vendor'
      const variantId = product.id
      await addToCart(variantId, 1, vendorId)
      alert(`✅ ${product.name} added to cart!`)
    } catch (error) {
      console.error('Failed to add to cart:', error)
      alert('❌ Failed to add to cart. Please try again.')
    } finally {
      setIsAdding(false)
    }
  }

  if (wishlistProducts.length === 0) {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center">
        <div className="text-center">
          <Heart className="w-16 h-16 text-gray-300 mx-auto mb-4" />
          <h1 className="text-3xl font-bold text-gray-800 mb-4">Your Wishlist is Empty</h1>
          <p className="text-gray-600 mb-8">Add products to your wishlist to save them for later</p>
          <Link to="/products" className="inline-block bg-blue-600 text-white px-8 py-3 rounded-lg hover:bg-blue-700 transition">
            Continue Shopping
          </Link>
        </div>
      </div>
    )
  }

  return (
    <div className="max-w-7xl mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-8">My Wishlist</h1>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {wishlistProducts.map((product) => (
          <div key={product.id} className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition">
            <div className="relative bg-gray-200 h-48 flex items-center justify-center">
              <img src={product.images[0]} alt={product.name} className="w-full h-full object-cover" />
              <button onClick={() => removeItem(product.id)} className="absolute top-2 right-2 bg-white p-2 rounded-full shadow-md hover:bg-gray-100">
                <X className="w-5 h-5 text-gray-600" />
              </button>
            </div>
            <div className="p-4">
              <Link to={`/product/${product.id}`} className="font-semibold text-lg hover:text-blue-600 line-clamp-2">
                {product.name}
              </Link>
              <p className="text-gray-600 text-sm mb-3">{product.vendor.name}</p>
              <div className="flex items-baseline gap-2 mb-4">
                <span className="text-2xl font-bold text-blue-600">{formatCurrency(product.price)}</span>
              </div>
              <button
                onClick={() => handleAddToCart(product)}
                disabled={isAdding}
                className="w-full bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition flex items-center justify-center gap-2"
              >
                <ShoppingCart className="w-4 h-4" />
                {isAdding ? 'Adding...' : 'Add to Cart'}
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
