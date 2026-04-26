import React from 'react'
import { Link } from 'react-router-dom'
import { Star, Heart, ShoppingCart, Filter } from 'lucide-react'
import { useCartStore, useWishlistStore } from '@/store'
import { Product } from '@/types'

const ALL_PRODUCTS: Product[] = [
  // Electronics Category
  {
    id: '1',
    name: 'Apple iPhone 14 Pro',
    description: 'Latest iPhone with A16 Bionic chip and advanced camera system',
    price: 999.99,
    originalPrice: 1299.99,
    rating: 4.8,
    reviews: 3200,
    category: 'Electronics',
    images: ['https://images.unsplash.com/photo-1592286927505-1def25115558?w=500&h=500&fit=crop'],
    vendor: { id: 'v1', name: 'Tech Store', rating: 4.8 },
    inStock: true,
  },
  {
    id: '2',
    name: 'Sony WH-1000XM5 Headphones',
    description: 'Premium noise-cancelling headphones with crystal clear sound',
    price: 349.99,
    originalPrice: 449.99,
    rating: 4.9,
    reviews: 2840,
    category: 'Electronics',
    images: ['https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500&h=500&fit=crop'],
    vendor: { id: 'v1', name: 'Tech Store', rating: 4.8 },
    inStock: true,
  },
  {
    id: '3',
    name: 'MacBook Air M2',
    description: 'Ultra-thin laptop with M2 chip for professionals',
    price: 1199.99,
    originalPrice: 1499.99,
    rating: 4.9,
    reviews: 1950,
    category: 'Electronics',
    images: ['https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=500&h=500&fit=crop'],
    vendor: { id: 'v1', name: 'Tech Store', rating: 4.8 },
    inStock: true,
  },
  {
    id: '4',
    name: 'iPad Pro 12.9"',
    description: 'Powerful tablet with M2 chip for work and creativity',
    price: 799.99,
    originalPrice: 999.99,
    rating: 4.8,
    reviews: 1630,
    category: 'Electronics',
    images: ['https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=500&h=500&fit=crop'],
    vendor: { id: 'v1', name: 'Tech Store', rating: 4.8 },
    inStock: true,
  },
  {
    id: '5',
    name: 'AirPods Pro Max',
    description: 'Over-ear headphones with spatial audio',
    price: 549.99,
    originalPrice: 649.99,
    rating: 4.7,
    reviews: 1203,
    category: 'Electronics',
    images: ['https://images.unsplash.com/photo-1487215078519-e21cc028cb29?w=500&h=500&fit=crop'],
    vendor: { id: 'v1', name: 'Tech Store', rating: 4.8 },
    inStock: true,
  },
  {
    id: '6',
    name: '4K Webcam Pro',
    description: 'Professional 4K webcam for streaming and meetings',
    price: 199.99,
    originalPrice: 299.99,
    rating: 4.6,
    reviews: 534,
    category: 'Electronics',
    images: ['https://images.unsplash.com/photo-1612387803835-8ac9c8b87b10?w=500&h=500&fit=crop'],
    vendor: { id: 'v1', name: 'Tech Store', rating: 4.8 },
    inStock: true,
  },

  // Fashion Category
  {
    id: '7',
    name: 'Premium Denim Jeans',
    description: 'Classic blue denim with perfect fit and durability',
    price: 79.99,
    originalPrice: 129.99,
    rating: 4.6,
    reviews: 892,
    category: 'Fashion',
    images: ['https://images.unsplash.com/photo-1542272604-787c62d465d1?w=500&h=500&fit=crop'],
    vendor: { id: 'v2', name: 'Fashion Hub', rating: 4.6 },
    inStock: true,
  },
  {
    id: '8',
    name: 'Luxe Leather Handbag',
    description: 'Elegant leather bag perfect for any occasion',
    price: 249.99,
    originalPrice: 399.99,
    rating: 4.7,
    reviews: 1205,
    category: 'Fashion',
    images: ['https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=500&h=500&fit=crop'],
    vendor: { id: 'v2', name: 'Fashion Hub', rating: 4.6 },
    inStock: true,
  },
  {
    id: '9',
    name: 'Designer White Sneakers',
    description: 'Comfortable and stylish white shoes for casual wear',
    price: 129.99,
    originalPrice: 199.99,
    rating: 4.8,
    reviews: 1543,
    category: 'Fashion',
    images: ['https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500&h=500&fit=crop'],
    vendor: { id: 'v2', name: 'Fashion Hub', rating: 4.6 },
    inStock: true,
  },
  {
    id: '10',
    name: 'Silk Dress',
    description: 'Elegant silk evening dress for special occasions',
    price: 199.99,
    originalPrice: 349.99,
    rating: 4.9,
    reviews: 876,
    category: 'Fashion',
    images: ['https://images.unsplash.com/photo-1595521596521-995e29353cde?w=500&h=500&fit=crop'],
    vendor: { id: 'v2', name: 'Fashion Hub', rating: 4.6 },
    inStock: true,
  },
  {
    id: '11',
    name: 'Cozy Wool Sweater',
    description: 'Warm and comfortable sweater for winter',
    price: 89.99,
    originalPrice: 149.99,
    rating: 4.7,
    reviews: 654,
    category: 'Fashion',
    images: ['https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=500&h=500&fit=crop'],
    vendor: { id: 'v2', name: 'Fashion Hub', rating: 4.6 },
    inStock: true,
  },
  {
    id: '12',
    name: 'Designer Sunglasses',
    description: 'Premium UV protection with stylish frames',
    price: 149.99,
    originalPrice: 249.99,
    rating: 4.8,
    reviews: 1023,
    category: 'Fashion',
    images: ['https://images.unsplash.com/photo-1572635196237-14b3f281503f?w=500&h=500&fit=crop'],
    vendor: { id: 'v2', name: 'Fashion Hub', rating: 4.6 },
    inStock: true,
  },

  // Home Category
  {
    id: '13',
    name: 'Modern Coffee Table',
    description: 'Sleek wooden coffee table for contemporary living rooms',
    price: 299.99,
    originalPrice: 449.99,
    rating: 4.7,
    reviews: 543,
    category: 'Home',
    images: ['https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=500&h=500&fit=crop'],
    vendor: { id: 'v3', name: 'Home Essentials', rating: 4.9 },
    inStock: true,
  },
  {
    id: '14',
    name: 'Stainless Steel Cookware Set',
    description: 'Complete 10-piece kitchen cookware set',
    price: 199.99,
    originalPrice: 349.99,
    rating: 4.8,
    reviews: 876,
    category: 'Home',
    images: ['https://images.unsplash.com/photo-1584568694244-14fbbc83bd30?w=500&h=500&fit=crop'],
    vendor: { id: 'v3', name: 'Home Essentials', rating: 4.9 },
    inStock: true,
  },
  {
    id: '15',
    name: 'Smart LED Lighting Kit',
    description: 'Intelligent RGB lighting system with app control',
    price: 89.99,
    originalPrice: 149.99,
    rating: 4.6,
    reviews: 734,
    category: 'Home',
    images: ['https://images.unsplash.com/photo-1565636192335-14c46fa1120d?w=500&h=500&fit=crop'],
    vendor: { id: 'v3', name: 'Home Essentials', rating: 4.9 },
    inStock: true,
  },
  {
    id: '16',
    name: 'Memory Foam Pillow',
    description: 'Ergonomic pillow for better sleep and neck support',
    price: 79.99,
    originalPrice: 129.99,
    rating: 4.7,
    reviews: 1092,
    category: 'Home',
    images: ['https://images.unsplash.com/photo-1584622181563-430f63602d4b?w=500&h=500&fit=crop'],
    vendor: { id: 'v3', name: 'Home Essentials', rating: 4.9 },
    inStock: true,
  },
  {
    id: '17',
    name: 'Eco-Friendly Bed Sheets',
    description: 'Organic cotton sheets with premium quality',
    price: 129.99,
    originalPrice: 199.99,
    rating: 4.8,
    reviews: 823,
    category: 'Home',
    images: ['https://images.unsplash.com/photo-1582719471384-894fbb16e074?w=500&h=500&fit=crop'],
    vendor: { id: 'v3', name: 'Home Essentials', rating: 4.9 },
    inStock: true,
  },
  {
    id: '18',
    name: 'Stainless Steel Water Bottle',
    description: 'Keeps drinks hot/cold for 24 hours',
    price: 34.99,
    originalPrice: 59.99,
    rating: 4.9,
    reviews: 2103,
    category: 'Home',
    images: ['https://images.unsplash.com/photo-1602142905893-e988444fda75?w=500&h=500&fit=crop'],
    vendor: { id: 'v3', name: 'Home Essentials', rating: 4.9 },
    inStock: true,
  },

  // Sports Category
  {
    id: '19',
    name: 'Professional Running Shoes',
    description: 'High-performance shoes for serious runners',
    price: 149.99,
    originalPrice: 229.99,
    rating: 4.9,
    reviews: 1876,
    category: 'Sports',
    images: ['https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500&h=500&fit=crop'],
    vendor: { id: 'v4', name: 'Sports Central', rating: 4.7 },
    inStock: true,
  },
  {
    id: '20',
    name: 'Yoga Mat Premium',
    description: 'Non-slip eco-friendly mat for all exercises',
    price: 49.99,
    originalPrice: 79.99,
    rating: 4.8,
    reviews: 1567,
    category: 'Sports',
    images: ['https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=500&h=500&fit=crop'],
    vendor: { id: 'v4', name: 'Sports Central', rating: 4.7 },
    inStock: true,
  },
  {
    id: '21',
    name: 'Adjustable Dumbbell Set',
    description: '50 lb complete home gym equipment',
    price: 199.99,
    originalPrice: 349.99,
    rating: 4.7,
    reviews: 945,
    category: 'Sports',
    images: ['https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=500&h=500&fit=crop'],
    vendor: { id: 'v4', name: 'Sports Central', rating: 4.7 },
    inStock: true,
  },
  {
    id: '22',
    name: 'Resistance Band Set',
    description: 'Complete set for strength training',
    price: 39.99,
    originalPrice: 69.99,
    rating: 4.6,
    reviews: 812,
    category: 'Sports',
    images: ['https://images.unsplash.com/photo-1517836357463-d25ddfcbf042?w=500&h=500&fit=crop'],
    vendor: { id: 'v4', name: 'Sports Central', rating: 4.7 },
    inStock: true,
  },
  {
    id: '23',
    name: 'Smart Fitness Watch',
    description: 'Track workouts, heart rate, and sleep',
    price: 249.99,
    originalPrice: 399.99,
    rating: 4.8,
    reviews: 1534,
    category: 'Sports',
    images: ['https://images.unsplash.com/photo-1575311373937-040b3042dd35?w=500&h=500&fit=crop'],
    vendor: { id: 'v4', name: 'Sports Central', rating: 4.7 },
    inStock: true,
  },
  {
    id: '24',
    name: 'Professional Basketball',
    description: 'Regulation size and weight for serious players',
    price: 59.99,
    originalPrice: 99.99,
    rating: 4.7,
    reviews: 623,
    category: 'Sports',
    images: ['https://images.unsplash.com/photo-1461896836934-ffe607ba8211?w=500&h=500&fit=crop'],
    vendor: { id: 'v4', name: 'Sports Central', rating: 4.7 },
    inStock: true,
  },
]

export const ProductsPage: React.FC = () => {
  const { addToCart } = useCartStore()
  const { isInWishlist, addItem: addToWishlist, removeItem: removeFromWishlist } = useWishlistStore()
  const [filteredProducts, setFilteredProducts] = React.useState(ALL_PRODUCTS)
  const [selectedCategory, setSelectedCategory] = React.useState('All')
  const [isAdding, setIsAdding] = React.useState(false)

  const categories = ['All', 'Electronics', 'Fashion', 'Home', 'Sports']

  const handleCategoryFilter = (category: string) => {
    setSelectedCategory(category)
    if (category === 'All') {
      setFilteredProducts(ALL_PRODUCTS)
    } else {
      setFilteredProducts(ALL_PRODUCTS.filter((p) => p.category === category))
    }
  }

  const handleAddToCart = async (product: Product) => {
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

  const handleWishlist = (e: React.MouseEvent, productId: string) => {
    e.preventDefault()
    if (isInWishlist(productId)) {
      removeFromWishlist(productId)
    } else {
      addToWishlist(productId)
    }
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 py-8">
        <h1 className="text-4xl font-bold mb-2">All Products</h1>
        <p className="text-gray-600 mb-8">Browse our complete catalog of products</p>

        {/* Filters */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-8">
          <div className="flex items-center gap-2 mb-4">
            <Filter className="w-5 h-5" />
            <h3 className="font-bold text-lg">Category</h3>
          </div>
          <div className="flex flex-wrap gap-3">
            {categories.map((cat) => (
              <button
                key={cat}
                onClick={() => handleCategoryFilter(cat)}
                className={`px-4 py-2 rounded-full font-semibold transition ${
                  selectedCategory === cat ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-800 hover:bg-gray-300'
                }`}
              >
                {cat}
              </button>
            ))}
          </div>
        </div>

        {/* Products Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredProducts.map((product) => (
            <Link key={product.id} to={`/product/${product.id}`}>
              <div className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition transform hover:-translate-y-1">
                {/* Image */}
                <div className="h-48 bg-gray-200 flex items-center justify-center relative overflow-hidden">
                  <img 
                    src={product.images[0]} 
                    alt={product.name}
                    className="w-full h-full object-cover hover:scale-110 transition-transform duration-300"
                  />
                  <button
                    onClick={(e) => handleWishlist(e, product.id)}
                    className="absolute top-3 right-3 p-2 bg-white rounded-full shadow-md hover:bg-red-50 transition z-10"
                  >
                    <Heart className={`w-5 h-5 ${isInWishlist(product.id) ? 'text-red-600 fill-red-600' : 'text-gray-400'}`} />
                  </button>
                </div>

                {/* Content */}
                <div className="p-4">
                  <h3 className="font-bold text-lg mb-1 line-clamp-2">{product.name}</h3>

                  <div className="flex items-center gap-2 mb-2">
                    <Star className="w-4 h-4 text-yellow-400 fill-yellow-400" />
                    <span className="font-semibold text-sm">{product.rating}</span>
                    <span className="text-gray-600 text-xs">({product.reviews})</span>
                  </div>

                  <p className="text-sm text-gray-600 mb-3">{product.vendor.name}</p>

                  <div className="flex items-baseline gap-2 mb-4">
                    <span className="text-2xl font-bold text-blue-600">${product.price}</span>
                    <span className="text-gray-500 line-through text-sm">${product.originalPrice}</span>
                  </div>

                  <button
                    onClick={(e) => {
                      e.preventDefault()
                      handleAddToCart(product)
                    }}
                    disabled={isAdding}
                    className="w-full bg-blue-600 text-white py-2 rounded-lg font-semibold hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition flex items-center justify-center gap-2"
                  >
                    <ShoppingCart className="w-4 h-4" />
                    {isAdding ? 'Adding...' : 'Add to Cart'}
                  </button>
                </div>
              </div>
            </Link>
          ))}
        </div>
      </div>
    </div>
  )
}
