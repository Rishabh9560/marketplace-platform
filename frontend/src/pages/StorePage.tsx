import React, { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { Star, MapPin, ShoppingCart, ArrowLeft, Share2, Heart } from 'lucide-react'

interface Product {
  id: string
  name: string
  price: number
  rating: number
  reviews: number
  image: string
  category: string
  stock: number
}

interface StoreInfo {
  id: string
  name: string
  rating: number
  reviews: number
  location: string
  products: number
  description: string
  image: string
  featured: boolean
}

export const StorePage: React.FC = () => {
  const { storeId } = useParams<{ storeId: string }>()
  const navigate = useNavigate()
  const [store, setStore] = useState<StoreInfo | null>(null)
  const [products, setProducts] = useState<Product[]>([])
  const [loading, setLoading] = useState(true)
  const [wishlist, setWishlist] = useState<Set<string>>(new Set())

  // Mock store data
  const storeData: Record<string, StoreInfo> = {
    v1: {
      id: 'v1',
      name: 'Tech Store Pro',
      rating: 4.8,
      reviews: 2540,
      location: 'San Francisco, CA',
      products: 245,
      description: 'Your one-stop shop for the latest tech gadgets, electronics, and accessories. We offer premium quality products with fast shipping and excellent customer service.',
      image: 'https://images.pexels.com/photos/18105/pexels-photo.jpg?auto=compress&cs=tinysrgb&w=600',
      featured: true,
    },
    v2: {
      id: 'v2',
      name: 'Fashion Hub',
      rating: 4.6,
      reviews: 1890,
      location: 'New York, NY',
      products: 342,
      description: 'Latest fashion trends and styles from around the world. We curate the best clothing, accessories, and footwear for every occasion.',
      image: 'https://images.pexels.com/photos/3962286/pexels-photo-3962286.jpeg?auto=compress&cs=tinysrgb&w=600',
      featured: true,
    },
    v3: {
      id: 'v3',
      name: 'Home Essentials',
      rating: 4.9,
      reviews: 3120,
      location: 'Austin, TX',
      products: 156,
      description: 'Everything you need to make your house a home. From furniture to decor, we have quality products for every room.',
      image: 'https://images.pexels.com/photos/279746/pexels-photo-279746.jpeg?auto=compress&cs=tinysrgb&w=600',
      featured: true,
    },
    v4: {
      id: 'v4',
      name: 'Sports Central',
      rating: 4.7,
      reviews: 892,
      location: 'Denver, CO',
      products: 178,
      description: 'Premium sports equipment and athletic wear for all your fitness needs.',
      image: 'https://images.pexels.com/photos/3945683/pexels-photo-3945683.jpeg?auto=compress&cs=tinysrgb&w=600',
      featured: false,
    },
    v5: {
      id: 'v5',
      name: 'Beauty & Wellness',
      rating: 4.5,
      reviews: 641,
      location: 'Los Angeles, CA',
      products: 289,
      description: 'Natural and premium beauty products to help you look and feel your best.',
      image: 'https://images.pexels.com/photos/3807517/pexels-photo-3807517.jpeg?auto=compress&cs=tinysrgb&w=600',
      featured: false,
    },
    v6: {
      id: 'v6',
      name: 'Book Bazaar',
      rating: 4.8,
      reviews: 1203,
      location: 'Seattle, WA',
      products: 567,
      description: 'Discover thousands of books across all genres. From bestsellers to rare editions, find your next great read.',
      image: 'https://images.pexels.com/photos/1756617/pexels-photo-1756617.jpeg?auto=compress&cs=tinysrgb&w=600',
      featured: false,
    },
  }

  // Mock products data
  const productsData: Record<string, Product[]> = {
    v1: [
      { id: 'p1', name: 'Wireless Headphones', price: 129.99, rating: 4.8, reviews: 512, image: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500&h=500&fit=crop', category: 'Audio', stock: 45 },
      { id: 'p2', name: 'Smart Watch', price: 249.99, rating: 4.7, reviews: 328, image: 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500&h=500&fit=crop', category: 'Wearables', stock: 23 },
      { id: 'p3', name: 'USB-C Cable', price: 19.99, rating: 4.9, reviews: 856, image: 'https://images.unsplash.com/photo-1625948515291-69613efd103f?w=500&h=500&fit=crop', category: 'Cables', stock: 120 },
      { id: 'p4', name: 'Power Bank', price: 49.99, rating: 4.7, reviews: 445, image: 'https://images.unsplash.com/photo-1609091839311-d5365f9ff1c5?w=500&h=500&fit=crop', category: 'Chargers', stock: 67 },
    ],
    v2: [
      { id: 'p5', name: 'Summer Dress', price: 49.99, rating: 4.6, reviews: 234, image: 'https://images.unsplash.com/photo-1595777707802-41d339d60280?w=500&h=500&fit=crop', category: 'Dresses', stock: 45 },
      { id: 'p6', name: 'Jeans', price: 59.99, rating: 4.7, reviews: 189, image: 'https://images.unsplash.com/photo-1542272604-787c62d465d1?w=500&h=500&fit=crop', category: 'Pants', stock: 89 },
    ],
    v3: [
      { id: 'p7', name: 'Sofa', price: 399.99, rating: 4.8, reviews: 234, image: 'https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=500&h=500&fit=crop', category: 'Furniture', stock: 12 },
      { id: 'p8', name: 'Bookshelf', price: 149.99, rating: 4.5, reviews: 145, image: 'https://images.unsplash.com/photo-1594938298603-c8148c4dae35?w=500&h=500&fit=crop', category: 'Furniture', stock: 28 },
    ],
    v4: [
      { id: 'p9', name: 'Running Shoes', price: 99.99, rating: 4.7, reviews: 312, image: 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500&h=500&fit=crop', category: 'Footwear', stock: 56 },
      { id: 'p10', name: 'Yoga Mat', price: 24.99, rating: 4.6, reviews: 423, image: 'https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=500&h=500&fit=crop', category: 'Equipment', stock: 100 },
    ],
    v5: [
      { id: 'p11', name: 'Face Serum', price: 39.99, rating: 4.8, reviews: 567, image: 'https://images.unsplash.com/photo-1556228578-8c89e6adf883?w=500&h=500&fit=crop', category: 'Skincare', stock: 78 },
      { id: 'p12', name: 'Moisturizer', price: 34.99, rating: 4.6, reviews: 234, image: 'https://images.unsplash.com/photo-1556228578-8c89e6adf883?w=500&h=500&fit=crop', category: 'Skincare', stock: 92 },
    ],
    v6: [
      { id: 'p13', name: 'The Great Gatsby', price: 14.99, rating: 4.9, reviews: 1203, image: 'https://images.unsplash.com/photo-1543002588-d83cea6bafff?w=500&h=500&fit=crop', category: 'Fiction', stock: 45 },
      { id: 'p14', name: 'Clean Code', price: 34.99, rating: 4.8, reviews: 567, image: 'https://images.unsplash.com/photo-1507842217343-583f20270319?w=500&h=500&fit=crop', category: 'Tech', stock: 32 },
    ],
  }

  useEffect(() => {
    setLoading(true)
    const timer = setTimeout(() => {
      if (storeId && storeData[storeId]) {
        setStore(storeData[storeId])
        setProducts(productsData[storeId] || [])
      }
      setLoading(false)
    }, 500)
    return () => clearTimeout(timer)
  }, [storeId])

  const toggleWishlist = (productId: string) => {
    const newWishlist = new Set(wishlist)
    if (newWishlist.has(productId)) {
      newWishlist.delete(productId)
    } else {
      newWishlist.add(productId)
    }
    setWishlist(newWishlist)
  }

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-600 mx-auto mb-4"></div>
          <p className="text-gray-600">Loading store...</p>
        </div>
      </div>
    )
  }

  if (!store) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <p className="text-gray-600 mb-4">Store not found</p>
          <button
            onClick={() => navigate('/sellers')}
            className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700"
          >
            Back to Sellers
          </button>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Back Button */}
      <div className="max-w-7xl mx-auto px-4 py-4">
        <button
          onClick={() => navigate('/sellers')}
          className="flex items-center gap-2 text-blue-600 hover:text-blue-700 font-semibold mb-6"
        >
          <ArrowLeft className="w-5 h-5" />
          Back to Sellers
        </button>
      </div>

      {/* Store Header */}
      <div className="bg-white shadow">
        <div className="max-w-7xl mx-auto px-4 py-8">
          <div className="flex gap-6 items-start">
            {/* Store Image */}
            <div className="w-32 h-32 bg-gray-200 rounded-lg overflow-hidden flex-shrink-0">
              <img
                src={store.image}
                alt={store.name}
                className="w-full h-full object-cover"
                onError={(e) => {
                  e.currentTarget.src = 'data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 100 100%22%3E%3Crect fill=%22%233b82f6%22 width=%22100%22 height=%22100%22/%3E%3Ctext x=%2250%22 y=%2250%22 font-size=%2240%22 fill=%22white%22 text-anchor=%22middle%22 dy=%22.3em%22%3E%F0%9F%8F%AA%3C/text%3E%3C/svg%3E'
                }}
              />
            </div>

            {/* Store Info */}
            <div className="flex-1">
              <div className="flex items-start justify-between mb-4">
                <div>
                  <h1 className="text-3xl font-bold text-gray-900">{store.name}</h1>
                  <p className="text-gray-600 mt-2">{store.description}</p>
                </div>
                {store.featured && (
                  <span className="bg-yellow-100 text-yellow-800 px-3 py-1 rounded-full text-sm font-bold">
                    Featured
                  </span>
                )}
              </div>

              <div className="grid grid-cols-4 gap-4">
                <div>
                  <p className="text-gray-600 text-sm">Rating</p>
                  <div className="flex items-center gap-1 mt-1">
                    <Star className="w-5 h-5 text-yellow-400 fill-yellow-400" />
                    <span className="font-bold text-lg">{store.rating}</span>
                  </div>
                  <p className="text-xs text-gray-500">({store.reviews} reviews)</p>
                </div>

                <div>
                  <p className="text-gray-600 text-sm">Products</p>
                  <p className="font-bold text-lg mt-1">{store.products}</p>
                </div>

                <div>
                  <p className="text-gray-600 text-sm">Location</p>
                  <div className="flex items-center gap-1 mt-1 font-semibold">
                    <MapPin className="w-4 h-4" />
                    <span className="text-sm">{store.location}</span>
                  </div>
                </div>

                <div className="flex gap-2">
                  <button className="flex items-center justify-center w-10 h-10 bg-gray-100 hover:bg-gray-200 rounded-lg">
                    <Share2 className="w-5 h-5" />
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Products */}
      <div className="max-w-7xl mx-auto px-4 py-12">
        <h2 className="text-2xl font-bold text-gray-900 mb-8">Featured Products</h2>

        {products.length === 0 ? (
          <div className="bg-white rounded-lg p-12 text-center">
            <p className="text-gray-600">No products available from this store yet.</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            {products.map((product) => (
              <div key={product.id} className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition">
                {/* Product Image */}
                <div className="relative h-48 bg-gray-200 overflow-hidden">
                  <img
                    src={product.image}
                    alt={product.name}
                    className="w-full h-full object-cover hover:scale-105 transition-transform"
                    onError={(e) => {
                      e.currentTarget.src = 'https://via.placeholder.com/300x300?text=Product'
                    }}
                  />
                  <button
                    onClick={() => toggleWishlist(product.id)}
                    className={`absolute top-3 right-3 p-2 rounded-full ${
                      wishlist.has(product.id)
                        ? 'bg-red-100 text-red-600'
                        : 'bg-white text-gray-600 hover:bg-gray-100'
                    }`}
                  >
                    <Heart className={`w-5 h-5 ${wishlist.has(product.id) ? 'fill-current' : ''}`} />
                  </button>
                </div>

                {/* Product Info */}
                <div className="p-4">
                  <p className="text-xs text-gray-500 uppercase font-bold mb-1">{product.category}</p>
                  <h3 className="font-bold text-gray-900 line-clamp-2 mb-2">{product.name}</h3>

                  <div className="flex items-center gap-2 mb-3">
                    <Star className="w-4 h-4 text-yellow-400 fill-yellow-400" />
                    <span className="text-sm font-medium">{product.rating}</span>
                    <span className="text-xs text-gray-500">({product.reviews})</span>
                  </div>

                  <div className="flex items-center justify-between">
                    <span className="text-xl font-bold text-gray-900">${product.price}</span>
                    <button className="flex items-center gap-1 bg-blue-600 text-white px-3 py-2 rounded hover:bg-blue-700 transition text-sm">
                      <ShoppingCart className="w-4 h-4" />
                      Add
                    </button>
                  </div>

                  {product.stock < 10 && product.stock > 0 && (
                    <p className="text-xs text-orange-600 mt-2">Only {product.stock} left!</p>
                  )}
                  {product.stock === 0 && (
                    <p className="text-xs text-red-600 mt-2">Out of stock</p>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Footer Info */}
      <div className="bg-white mt-12 py-8 border-t">
        <div className="max-w-7xl mx-auto px-4">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div>
              <h3 className="font-bold text-gray-900 mb-3">About This Store</h3>
              <p className="text-sm text-gray-600">We are committed to providing high-quality products and excellent customer service.</p>
            </div>
            <div>
              <h3 className="font-bold text-gray-900 mb-3">Customer Support</h3>
              <ul className="space-y-2 text-sm text-gray-600">
                <li>📧 support@store.com</li>
                <li>📞 1-800-SUPPORT</li>
              </ul>
            </div>
            <div>
              <h3 className="font-bold text-gray-900 mb-3">Policies</h3>
              <ul className="space-y-2 text-sm text-gray-600">
                <li>Free Shipping on orders over $50</li>
                <li>30-day money back guarantee</li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
