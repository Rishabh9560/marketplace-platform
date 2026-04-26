import React, { useEffect } from 'react'
import { Heart, Search, ShoppingCart, User, LogOut, Menu, X, Package, Home } from 'lucide-react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuthStore, useWishlistStore } from '@/store'
import { useCartStore } from '@/store/cartStore'
import { NotificationBell } from './NotificationBell'
import { useState } from 'react'

export const Navigation: React.FC = () => {
  const navigate = useNavigate()
  const { isAuthenticated, customer, logout } = useAuthStore()
  const { itemCount, fetchCart } = useCartStore()
  const { items: wishlistItems } = useWishlistStore()
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false)
  const [searchQuery, setSearchQuery] = useState('')
  const [profileMenuOpen, setProfileMenuOpen] = useState(false)

  // Fetch cart on component mount if authenticated
  useEffect(() => {
    if (isAuthenticated) {
      fetchCart()
    }
  }, [isAuthenticated])

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault()
    if (searchQuery.trim()) {
      navigate(`/search?q=${encodeURIComponent(searchQuery)}`)
      setSearchQuery('')
    }
  }

  const handleLogout = () => {
    logout()
    navigate('/login')
    setProfileMenuOpen(false)
  }

  return (
    <nav className="sticky top-0 z-40 bg-white border-b border-gray-200 shadow-sm">
      <div className="max-w-7xl mx-auto px-4">
        <div className="flex justify-between items-center h-16">
          {/* Logo */}
          <Link to="/" className="flex items-center gap-2 font-bold text-xl text-red-600 flex-shrink-0">
            <div className="w-8 h-8 bg-gradient-to-br from-red-600 to-red-800 rounded flex items-center justify-center text-white text-sm font-bold">M</div>
            Marketplace
          </Link>

          {/* Navigation Links - Hidden on Mobile */}
          <div className="hidden md:flex items-center gap-8 flex-1 ml-12">
            <Link to="/" className="text-gray-700 hover:text-red-600 font-medium text-sm transition flex items-center gap-1">
              <Home className="w-4 h-4" />
              Home
            </Link>
            <Link to="/products" className="text-gray-700 hover:text-red-600 font-medium text-sm transition">
              Shop
            </Link>
            <Link to="/vendors" className="text-gray-700 hover:text-red-600 font-medium text-sm transition">
              Sellers
            </Link>
            {isAuthenticated && (
              <Link to="/orders" className="text-gray-700 hover:text-blue-600 font-medium text-sm transition flex items-center gap-1 bg-blue-50 px-3 py-1 rounded-lg">
                <Package className="w-4 h-4" />
                My Orders
              </Link>
            )}
          </div>

          {/* Search Bar - Hidden on Mobile */}
          <form onSubmit={handleSearch} className="hidden md:flex flex-1 max-w-md mx-4">
            <div className="w-full relative">
              <input
                type="text"
                placeholder="Search products..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
              <button type="submit" className="absolute right-3 top-2.5">
                <Search className="w-5 h-5 text-gray-400" />
              </button>
            </div>
          </form>

          {/* Right Menu */}
          <div className="flex items-center gap-4">
            {/* Wishlist */}
            <Link to="/wishlist" className="relative p-2 hover:bg-gray-100 rounded-lg transition">
              <Heart className="w-6 h-6 text-gray-600" />
              {wishlistItems.length > 0 && (
                <span className="absolute top-1 right-1 bg-red-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">
                  {wishlistItems.length}
                </span>
              )}
            </Link>

            {/* Notifications */}
            {isAuthenticated && <NotificationBell />}

            {/* Cart */}
            <Link to="/cart" className="relative p-2 hover:bg-gray-100 rounded-lg transition">
              <ShoppingCart className="w-6 h-6 text-gray-600" />
              {itemCount > 0 && (
                <span className="absolute top-1 right-1 bg-red-600 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center font-semibold">
                  {itemCount > 99 ? '99+' : itemCount}
                </span>
              )}
            </Link>

            {/* Auth - Profile or Login */}
            {isAuthenticated ? (
              <div className="relative">
                <button
                  onClick={() => setProfileMenuOpen(!profileMenuOpen)}
                  className="flex items-center gap-2 p-2 hover:bg-gray-100 rounded-lg transition"
                >
                  <div className="w-8 h-8 bg-gradient-to-br from-blue-400 to-blue-600 rounded-full flex items-center justify-center text-white text-sm font-bold">
                    {customer?.name?.charAt(0) || 'U'}
                  </div>
                </button>

                {/* Profile Dropdown */}
                {profileMenuOpen && (
                  <div className="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg border border-gray-200 z-50">
                    <div className="px-4 py-3 border-b border-gray-200">
                      <p className="font-semibold text-sm">{customer?.name}</p>
                      <p className="text-xs text-gray-600">{customer?.email}</p>
                    </div>
                    <Link
                      to="/profile"
                      onClick={() => setProfileMenuOpen(false)}
                      className="flex items-center gap-3 px-4 py-2 hover:bg-gray-50 text-sm"
                    >
                      <User className="w-4 h-4" />
                      My Profile
                    </Link>
                    <Link
                      to="/orders"
                      onClick={() => setProfileMenuOpen(false)}
                      className="flex items-center gap-3 px-4 py-2 hover:bg-gray-50 text-sm"
                    >
                      <Package className="w-4 h-4" />
                      My Orders
                    </Link>
                    <Link
                      to="/wishlist"
                      onClick={() => setProfileMenuOpen(false)}
                      className="flex items-center gap-3 px-4 py-2 hover:bg-gray-50 text-sm"
                    >
                      <Heart className="w-4 h-4" />
                      My Wishlist
                    </Link>
                    <button
                      onClick={handleLogout}
                      className="w-full flex items-center gap-3 px-4 py-2 hover:bg-red-50 text-red-600 text-sm border-t border-gray-200"
                    >
                      <LogOut className="w-4 h-4" />
                      Logout
                    </button>
                  </div>
                )}
              </div>
            ) : (
              <Link to="/login" className="p-2 hover:bg-gray-100 rounded-lg transition">
                <User className="w-6 h-6 text-gray-600" />
              </Link>
            )}

            {/* Mobile Menu Toggle */}
            <button onClick={() => setMobileMenuOpen(!mobileMenuOpen)} className="md:hidden">
              {mobileMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
            </button>
          </div>
        </div>

        {/* Mobile Menu */}
        {mobileMenuOpen && (
          <div className="md:hidden pb-4 space-y-2">
            <form onSubmit={handleSearch} className="mb-4">
              <input
                type="text"
                placeholder="Search products..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </form>
            <Link to="/products" className="block px-4 py-2 hover:bg-gray-50 rounded-lg text-sm font-semibold">
              All Products
            </Link>
            <Link to="/vendors" className="block px-4 py-2 hover:bg-gray-50 rounded-lg text-sm font-semibold">
              Find Vendors
            </Link>
            {!isAuthenticated && (
              <>
                <Link to="/login" className="block px-4 py-2 hover:bg-gray-50 rounded-lg text-sm font-semibold">
                  Login
                </Link>
                <Link to="/register" className="block px-4 py-2 bg-blue-600 text-white rounded-lg text-sm font-semibold">
                  Sign Up
                </Link>
              </>
            )}
          </div>
        )}
      </div>
    </nav>
  )
}
