import React, { useState } from 'react'
import { Star, Heart, ShoppingCart, Zap, Truck, Shield, ChevronRight, ArrowRight } from 'lucide-react'
import { Link } from 'react-router-dom'
import { useCartStore, useWishlistStore } from '@/store'
import { Product } from '@/types'

const CATEGORIES = [
  { 
    id: '1', 
    name: 'Electronics', 
    image: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&h=600&fit=crop', 
    color: 'from-blue-500 to-blue-600' 
  },
  { 
    id: '2', 
    name: 'Fashion', 
    image: 'https://images.unsplash.com/photo-1509631179647-0177331693ae?w=600&h=600&fit=crop', 
    color: 'from-purple-500 to-purple-600' 
  },
  { 
    id: '3', 
    name: 'Home', 
    image: 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=600&h=600&fit=crop', 
    color: 'from-orange-500 to-orange-600' 
  },
  { 
    id: '4', 
    name: 'Sports', 
    image: 'https://images.unsplash.com/photo-1461896836934-ffe607ba8211?w=600&h=600&fit=crop', 
    color: 'from-green-500 to-green-600' 
  },
  { 
    id: '5', 
    name: 'Books', 
    image: 'https://images.unsplash.com/photo-1512820790803-83ca734da794?w=600&h=600&fit=crop', 
    color: 'from-yellow-500 to-yellow-600' 
  },
  { 
    id: '6', 
    name: 'Toys', 
    image: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=600&h=600&fit=crop', 
    color: 'from-pink-500 to-pink-600' 
  },
]

const FEATURED_PRODUCTS: Product[] = [
  {
    id: '1',
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
    id: '2',
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
    id: '3',
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
    id: '4',
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
    id: '5',
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
    id: '6',
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
]

export const HomePage: React.FC = () => {
  const { addToCart } = useCartStore()
  const { isInWishlist, addItem: addToWishlist, removeItem: removeFromWishlist } = useWishlistStore()
  const [isAdding, setIsAdding] = useState(false)

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
    <div className="min-h-screen bg-white">
      {/* Hero Banner with Background */}
      <div className="relative bg-gradient-to-b from-gray-50 to-white overflow-hidden">
        {/* Background decoration */}
        <div className="absolute top-0 right-0 w-96 h-96 bg-blue-100 rounded-full -mr-48 -mt-48 opacity-30"></div>
        <div className="absolute bottom-0 left-0 w-80 h-80 bg-blue-100 rounded-full -ml-40 -mb-40 opacity-30"></div>
        
        <div className="relative max-w-7xl mx-auto px-4 py-20">
          <div className="text-center mb-16">
            <p className="text-blue-600 font-semibold text-sm tracking-wide mb-2">DISCOVER YOUR FAVORITES</p>
            <h1 className="text-5xl md:text-6xl font-bold text-gray-900 mb-6">Welcome to Marketplace</h1>
            <p className="text-xl text-gray-600 max-w-2xl mx-auto mb-8">Shop from thousands of products curated by trusted sellers. Find everything you love in one place.</p>
            <div className="flex justify-center gap-4">
              <Link to="/products" className="bg-blue-600 text-white px-8 py-3 rounded-lg font-semibold hover:bg-blue-700 transition inline-flex items-center gap-2">
                Shop Now
                <ArrowRight className="w-5 h-5" />
              </Link>
              <Link to="/vendors" className="border-2 border-gray-300 text-gray-900 px-8 py-3 rounded-lg font-semibold hover:border-blue-600 hover:text-blue-600 transition">
                Explore Sellers
              </Link>
            </div>
          </div>
        </div>
      </div>

      {/* Categories Section */}
      <div className="max-w-7xl mx-auto px-4 py-20">
        <h2 className="text-3xl font-bold mb-12 text-gray-900">Shop by Category</h2>
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
          {CATEGORIES.map((category) => (
            <Link key={category.id} to={`/products?category=${category.name}`} className="group cursor-pointer">
              <div className={`rounded-xl overflow-hidden h-48 relative shadow-lg hover:shadow-2xl transition-all duration-300 bg-gradient-to-br ${category.color}`}>
                {/* Background Image */}
                <img 
                  src={category.image} 
                  alt={category.name}
                  className="absolute inset-0 w-full h-full object-cover group-hover:scale-110 transition-transform duration-300"
                  loading="lazy"
                />
                
                {/* Dark overlay for text readability */}
                <div className="absolute inset-0 bg-black/30 group-hover:bg-black/50 transition-colors duration-300"></div>
                
                {/* Text overlay */}
                <div className="absolute inset-0 flex items-end">
                  <div className="w-full p-4 bg-gradient-to-t from-black/70 via-black/40 to-transparent">
                    <h3 className="font-bold text-xl text-white group-hover:text-yellow-300 transition">{category.name}</h3>
                  </div>
                </div>
              </div>
            </Link>
          ))}
        </div>
      </div>

      {/* Features */}
      <div className="bg-gray-50 border-y border-gray-200">
        <div className="max-w-7xl mx-auto px-4 py-16">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="flex items-start gap-4">
              <div className="bg-blue-100 p-3 rounded-lg flex-shrink-0">
                <Zap className="w-6 h-6 text-blue-600" />
              </div>
              <div>
                <h3 className="font-semibold text-lg mb-1 text-gray-900">Fast Shipping</h3>
                <p className="text-gray-600 text-sm">Quick delivery to your doorstep</p>
              </div>
            </div>
            <div className="flex items-start gap-4">
              <div className="bg-blue-100 p-3 rounded-lg flex-shrink-0">
                <Shield className="w-6 h-6 text-blue-600" />
              </div>
              <div>
                <h3 className="font-semibold text-lg mb-1 text-gray-900">Secure Payment</h3>
                <p className="text-gray-600 text-sm">100% safe and encrypted transactions</p>
              </div>
            </div>
            <div className="flex items-start gap-4">
              <div className="bg-blue-100 p-3 rounded-lg flex-shrink-0">
                <Truck className="w-6 h-6 text-blue-600" />
              </div>
              <div>
                <h3 className="font-semibold text-lg mb-1 text-gray-900">Easy Returns</h3>
                <p className="text-gray-600 text-sm">Hassle-free return policy</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Featured Products */}
      <div className="max-w-7xl mx-auto px-4 py-20">
        <div className="flex items-center justify-between mb-12">
          <div>
            <p className="text-blue-600 font-semibold text-sm tracking-wide mb-2">NEW & TRENDING</p>
            <h2 className="text-4xl font-bold text-gray-900">Featured Products</h2>
          </div>
          <Link to="/products" className="text-blue-600 font-semibold flex items-center gap-2 hover:gap-3 transition">
            See All
            <ChevronRight className="w-5 h-5" />
          </Link>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {FEATURED_PRODUCTS.map((product) => (
            <Link key={product.id} to={`/product/${product.id}`} className="group bg-white rounded-2xl shadow-sm hover:shadow-xl overflow-hidden transition-all duration-300">
              {/* Product Image */}
              <div className="relative bg-gray-100 h-64 flex items-center justify-center overflow-hidden">
                <img src={product.images[0]} alt={product.name} className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300" />
                {product.originalPrice && (
                  <div className="absolute top-4 right-4 bg-red-500 text-white px-3 py-1 rounded-full text-sm font-bold">
                    -{Math.round(((product.originalPrice - product.price) / product.originalPrice) * 100)}%
                  </div>
                )}
              </div>

              {/* Product Info */}
              <div className="p-6">
                <div className="text-sm text-blue-600 font-semibold mb-1 uppercase tracking-wide">{product.category}</div>
                <h3 className="font-bold text-xl mb-2 line-clamp-2 text-gray-900 group-hover:text-blue-600 transition">{product.name}</h3>
                <p className="text-gray-600 text-sm mb-4 line-clamp-1">{product.description}</p>

                {/* Rating */}
                <div className="flex items-center gap-2 mb-4">
                  <div className="flex items-center">
                    {Array.from({ length: 5 }).map((_, i) => (
                      <Star key={i} className={`w-4 h-4 ${i < Math.floor(product.rating) ? 'fill-yellow-400 text-yellow-400' : 'text-gray-300'}`} />
                    ))}
                  </div>
                  <span className="text-xs text-gray-600">({product.reviews})</span>
                </div>

                {/* Price */}
                <div className="flex items-baseline gap-2 mb-4">
                  <span className="text-3xl font-bold text-blue-600">${product.price.toFixed(2)}</span>
                  {product.originalPrice && (
                    <span className="text-lg text-gray-400 line-through">${product.originalPrice.toFixed(2)}</span>
                  )}
                </div>

                {/* Vendor Info */}
                <div className="text-xs text-gray-600 mb-4 flex items-center gap-1">
                  <span>Sold by</span>
                  <span className="font-semibold text-gray-900">{product.vendor.name}</span>
                  <span className="text-yellow-500">★</span>
                  <span>{product.vendor.rating}</span>
                </div>

                {/* Actions */}
                <div className="flex gap-2">
                  <button onClick={(e) => { e.preventDefault(); handleAddToCart(product) }} disabled={isAdding} className="flex-1 bg-blue-600 text-white py-3 rounded-lg hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition font-semibold flex items-center justify-center gap-2 group/btn">
                    <ShoppingCart className="w-4 h-4" />
                    <span className="hidden group-hover/btn:inline">{isAdding ? 'Adding...' : 'Add to Cart'}</span>
                  </button>
                  <button onClick={(e) => handleWishlist(e, product.id)} className={`px-3 py-3 rounded-lg transition font-semibold ${isInWishlist(product.id) ? 'bg-red-100 text-red-600' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'}`}>
                    <Heart className={`w-5 h-5 ${isInWishlist(product.id) ? 'fill-red-500' : ''}`} />
                  </button>
                </div>
              </div>
            </Link>
          ))}
        </div>
      </div>

      {/* Promotional Section */}
      <div className="bg-gradient-to-r from-blue-600 to-blue-700 text-white py-16 mt-8">
        <div className="max-w-7xl mx-auto px-4 text-center">
          <h2 className="text-3xl md:text-4xl font-bold mb-4">Become a Seller</h2>
          <p className="text-lg text-blue-100 mb-8 max-w-2xl mx-auto">Join thousands of successful sellers and start earning today. Get access to millions of customers.</p>
          <Link to="/vendors" className="bg-white text-blue-600 px-8 py-3 rounded-lg font-semibold hover:bg-gray-100 transition inline-flex items-center gap-2">
            Join Now
            <ArrowRight className="w-5 h-5" />
          </Link>
        </div>
      </div>
    </div>
  )
}
