import React, { useEffect, useState } from 'react'
import { Card, Badge, Button } from '@/components/common'
import { useAuthStore } from '@/store'
import { formatCurrency, formatNumber } from '@/lib/utils'
import { Star, Share2, Download, ShoppingBag, TrendingUp, Clock, Award, ExternalLink } from 'lucide-react'

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

export const ShopPage: React.FC = () => {
  const { vendor } = useAuthStore()
  const [products, setProducts] = useState<Product[]>([])
  const [loading, setLoading] = useState(true)
  const [copySuccess, setCopySuccess] = useState(false)

  // Updated products data with real product images
  const mockProducts: Product[] = [
    {
      id: '1',
      name: 'Premium Wireless Headphones Pro',
      price: 129.99,
      rating: 4.8,
      reviews: 512,
      image: 'https://images.pexels.com/photos/3394650/pexels-photo-3394650.jpeg?auto=compress&cs=tinysrgb&w=500&h=500&fit=crop',
      category: 'Audio',
      stock: 67,
    },
    {
      id: '2',
      name: 'Smart Fitness Watch',
      price: 249.99,
      rating: 4.7,
      reviews: 328,
      image: 'https://images.pexels.com/photos/437037/pexels-photo-437037.jpeg?auto=compress&cs=tinysrgb&w=500&h=500&fit=crop',
      category: 'Wearables',
      stock: 45,
    },
    {
      id: '3',
      name: 'Ultra-Fast USB-C Cable 3m',
      price: 19.99,
      rating: 4.9,
      reviews: 856,
      image: 'https://images.pexels.com/photos/3808517/pexels-photo-3808517.jpeg?auto=compress&cs=tinysrgb&w=500&h=500&fit=crop',
      category: 'Cables & Adapters',
      stock: 180,
    },
    {
      id: '4',
      name: 'Aluminum Phone Stand',
      price: 34.99,
      rating: 4.6,
      reviews: 234,
      image: 'https://images.pexels.com/photos/788946/pexels-photo-788946.jpeg?auto=compress&cs=tinysrgb&w=500&h=500&fit=crop',
      category: 'Phone Accessories',
      stock: 92,
    },
    {
      id: '5',
      name: '20000mAh Power Bank Fast Charge',
      price: 49.99,
      rating: 4.7,
      reviews: 445,
      image: 'https://images.pexels.com/photos/788946/pexels-photo-788946.jpeg?auto=compress&cs=tinysrgb&w=500&h=500&fit=crop',
      category: 'Chargers',
      stock: 134,
    },
    {
      id: '6',
      name: 'Tempered Glass Screen Protector Pack',
      price: 14.99,
      rating: 4.8,
      reviews: 689,
      image: 'https://images.pexels.com/photos/3587478/pexels-photo-3587478.jpeg?auto=compress&cs=tinysrgb&w=500&h=500&fit=crop',
      category: 'Screen Protection',
      stock: 250,
    },
    {
      id: '7',
      name: '4K Webcam with Auto Focus',
      price: 89.99,
      rating: 4.5,
      reviews: 167,
      image: 'https://images.pexels.com/photos/3962285/pexels-photo-3962285.jpeg?auto=compress&cs=tinysrgb&w=500&h=500&fit=crop',
      category: 'Cameras',
      stock: 38,
    },
    {
      id: '8',
      name: 'Wireless Keyboard & Mouse Set',
      price: 59.99,
      rating: 4.6,
      reviews: 302,
      image: 'https://images.pexels.com/photos/18105/pexels-photo.jpg?auto=compress&cs=tinysrgb&w=500&h=500&fit=crop',
      category: 'Peripherals',
      stock: 75,
    },
  ]

  useEffect(() => {
    // Simulate loading products
    setLoading(true)
    const timer = setTimeout(() => {
      setProducts(mockProducts)
      setLoading(false)
    }, 1000)
    return () => clearTimeout(timer)
  }, [])

  const shopUrl = `${window.location.origin}/shop/${vendor?.id}`

  const handleCopyLink = () => {
    navigator.clipboard.writeText(shopUrl)
    setCopySuccess(true)
    setTimeout(() => setCopySuccess(false), 2000)
  }

  const handleShare = () => {
    if (navigator.share) {
      navigator.share({
        title: `Visit ${vendor?.businessName} on Marketplace`,
        text: `Check out our great products!`,
        url: shopUrl,
      })
    }
  }

  const handleDownloadProfile = () => {
    alert('Shop profile PDF would be downloaded here')
  }

  const openShopInNewTab = () => {
    window.open(shopUrl, '_blank')
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-600 mx-auto mb-4"></div>
          <p className="text-gray-600">Loading your shop...</p>
        </div>
      </div>
    )
  }

  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="flex justify-between items-start">
        <div>
          <h1 className="text-3xl font-bold text-gray-900 mb-2">Your Shop</h1>
          <p className="text-gray-600">Preview how your shop appears to customers</p>
        </div>
        <Button
          variant="primary"
          onClick={openShopInNewTab}
          className="flex items-center gap-2"
        >
          <ExternalLink className="w-4 h-4" />
          Visit Shop
        </Button>
      </div>

      {/* Shop Header Card */}
      <Card className="card-shadow">
        <div className="flex items-start justify-between mb-6">
          <div className="flex items-center gap-6">
            <div className="w-24 h-24 bg-gradient-to-br from-blue-600 to-blue-800 rounded-lg flex items-center justify-center">
              <span className="text-4xl font-bold text-white">
                {vendor?.businessName.charAt(0)}
              </span>
            </div>
            <div>
              <h2 className="text-2xl font-bold text-gray-900">
                {vendor?.businessName}
              </h2>
              <p className="text-gray-600 text-sm mt-1 flex items-center gap-2">
                <Award className="w-4 h-4" />
                {vendor?.kycStatus === 'VERIFIED'
                  ? 'Verified Seller'
                  : 'Pending Verification'}
              </p>
              <div className="flex gap-2 mt-3">
                <Badge className="bg-green-100 text-green-800">
                  {formatNumber(products.length)} Products
                </Badge>
                <Badge className="bg-blue-100 text-blue-800 flex items-center gap-1">
                  <Star className="w-3 h-3" />
                  {vendor?.averageRating || 0} Rating
                </Badge>
              </div>
            </div>
          </div>

          <div className="text-right">
            <p className="text-sm text-gray-600 mb-2">Customer Reviews</p>
            <div className="flex items-center justify-end gap-2">
              <span className="text-3xl font-bold text-gray-900">
                {vendor?.totalReviews || 0}
              </span>
              <span className="text-2xl text-yellow-500">★</span>
            </div>
          </div>
        </div>

        {/* Shop Stats */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 pt-6 border-t">
          <div>
            <p className="text-gray-600 text-sm mb-2 flex items-center gap-2">
              <ShoppingBag className="w-4 h-4" />
              Total Orders
            </p>
            <p className="text-2xl font-bold text-gray-900">
              {formatNumber((vendor as any)?.totalOrders || 0)}
            </p>
          </div>
          <div>
            <p className="text-gray-600 text-sm mb-2 flex items-center gap-2">
              <TrendingUp className="w-4 h-4" />
              Performance Score
            </p>
            <p className="text-2xl font-bold text-blue-600">
              {(vendor as any)?.performanceScore || 95}%
            </p>
          </div>
          <div>
            <p className="text-gray-600 text-sm mb-2 flex items-center gap-2">
              <Clock className="w-4 h-4" />
              Member Since
            </p>
            <p className="text-2xl font-bold text-gray-900">
              {new Date(vendor?.createdAt || '').getFullYear()}
            </p>
          </div>
        </div>
      </Card>

      {/* Shop URL & Share */}
      <Card className="card-shadow">
        <h3 className="text-lg font-bold text-gray-900 mb-4">Share Your Shop</h3>

        <div className="space-y-4">
          <div className="flex items-center gap-3">
            <input
              type="text"
              value={shopUrl}
              readOnly
              className="flex-1 px-4 py-2 border border-gray-300 rounded-lg bg-gray-50 text-gray-900 text-sm font-mono"
            />
            <Button
              variant={copySuccess ? 'primary' : 'outline'}
              onClick={handleCopyLink}
              size="sm"
            >
              {copySuccess ? '✓ Copied' : 'Copy Link'}
            </Button>
          </div>

          <div className="flex gap-3">
            <Button
              variant="outline"
              onClick={handleShare}
              className="flex-1 flex items-center justify-center gap-2"
            >
              <Share2 className="w-4 h-4" />
              Share Shop
            </Button>
            <Button
              variant="outline"
              onClick={handleDownloadProfile}
              className="flex-1 flex items-center justify-center gap-2"
            >
              <Download className="w-4 h-4" />
              Download Profile
            </Button>
          </div>
        </div>
      </Card>

      {/* Featured Products */}
      <div>
        <h3 className="text-lg font-bold text-gray-900 mb-4">Featured Products</h3>

        {products.length === 0 ? (
          <Card className="py-12">
            <p className="text-center text-gray-600">
              No products listed yet. Create listings to display them here.
            </p>
          </Card>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {products.map((product) => (
              <Card key={product.id} className="card-shadow hover:shadow-lg transition-shadow overflow-hidden">
                {/* Product Image */}
                <div className="w-full h-48 bg-gray-200 rounded-lg mb-4 overflow-hidden">
                  <img
                    src={product.image}
                    alt={product.name}
                    className="w-full h-full object-cover hover:scale-105 transition-transform"
                    onError={(e) => {
                      e.currentTarget.src = 'data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 500 500%22%3E%3Crect fill=%22%23e5e7eb%22 width=%22500%22 height=%22500%22/%3E%3Ctext x=%22250%22 y=%22250%22 font-size=%22100%22 fill=%22%239ca3af%22 text-anchor=%22middle%22 dy=%22.3em%22 font-family=%22Arial%22%3EImage%3C/text%3E%3C/svg%3E'
                    }}
                  />
                </div>

                <div className="flex items-start justify-between mb-4">
                  <Badge className="bg-blue-100 text-blue-800 text-xs">
                    {product.category}
                  </Badge>
                </div>

                <h4 className="font-bold text-gray-900 mb-2 line-clamp-2">
                  {product.name}
                </h4>

                <div className="mb-4">
                  <p className="text-2xl font-bold text-gray-900">
                    {formatCurrency(product.price)}
                  </p>
                  <div className="flex items-center gap-2 text-sm text-gray-600 mt-1">
                    <Star className="w-4 h-4 text-yellow-500" />
                    <span className="font-medium">{product.rating}</span>
                    <span>({formatNumber(product.reviews)} reviews)</span>
                  </div>
                </div>

                <div className="flex items-center justify-between text-sm text-gray-600 pt-4 border-t">
                  <span>Stock: {product.stock}</span>
                  <Button variant="ghost" size="sm">
                    View
                  </Button>
                </div>
              </Card>
            ))}
          </div>
        )}
      </div>

      {/* Shop Preview Info */}
      <Card className="bg-blue-50 border border-blue-200 card-shadow">
        <h3 className="font-bold text-blue-900 mb-3">Shop Preview Tips</h3>
        <ul className="space-y-2 text-sm text-blue-800">
          <li className="flex gap-2">
            <span className="font-bold">•</span>
            <span>Keep your shop name professional and memorable</span>
          </li>
          <li className="flex gap-2">
            <span className="font-bold">•</span>
            <span>High-quality product images attract more customers</span>
          </li>
          <li className="flex gap-2">
            <span className="font-bold">•</span>
            <span>Maintain excellent seller ratings for better visibility</span>
          </li>
          <li className="flex gap-2">
            <span className="font-bold">•</span>
            <span>Respond to customer inquiries promptly</span>
          </li>
        </ul>
      </Card>
    </div>
  )
}
