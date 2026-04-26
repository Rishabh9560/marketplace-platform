import React, { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { Card, Button, Badge } from '@/components/common'
import { formatCurrency, getStatusColor } from '@/lib/utils'
import { isDemoVendor } from '@/lib/vendorUtils'
import { useProductListings } from '@/hooks/useApi'
import { useAuthStore } from '@/store'
import { Search, Plus, Edit2, Eye, Archive, Filter, ChevronLeft, ChevronRight, TrendingUp } from 'lucide-react'

export const ProductListingsPage: React.FC = () => {
  const { vendor } = useAuthStore()
  const { data: listings, loading, error, fetchListings } = useProductListings()
  const isDemo = isDemoVendor(vendor)

  const [searchTerm, setSearchTerm] = useState('')
  const [statusFilter, setStatusFilter] = useState<string | null>(null)
  const [currentPage, setCurrentPage] = useState(1)
  const [itemsPerPage] = useState(10)

  // Fetch vendor's product listings only for real vendors
  useEffect(() => {
    if (vendor?.id && !isDemo) {
      fetchListings(vendor.id, 0, 100)
    }
  }, [vendor?.id, isDemo, fetchListings])

  // Fallback mock product data
  const mockListings = [
    {
      id: '1',
      productId: 'PROD-001',
      sku: 'SKU-001',
      productName: 'Wireless Headphones',
      vendorPrice: 5999,
      originalPrice: 7999,
      discount: 25,
      quantityAvailable: 45,
      status: 'ACTIVE',
      viewCount: 1234,
      salesCount: 45,
      createdAt: '2024-03-15T10:30:00Z',
      avgRating: 4.5,
      reviews: 23,
    },
    {
      id: '2',
      productId: 'PROD-002',
      sku: 'SKU-002',
      productName: 'USB-C Cable 2m',
      vendorPrice: 299,
      originalPrice: 499,
      discount: 40,
      quantityAvailable: 120,
      status: 'ACTIVE',
      viewCount: 892,
      salesCount: 67,
      createdAt: '2024-03-14T15:45:00Z',
      avgRating: 4.8,
      reviews: 56,
    },
    {
      id: '3',
      productId: 'PROD-003',
      sku: 'SKU-003',
      productName: 'Phone Stand',
      vendorPrice: 799,
      originalPrice: 1299,
      discount: 38,
      quantityAvailable: 0,
      status: 'OUT_OF_STOCK',
      viewCount: 567,
      salesCount: 34,
      createdAt: '2024-03-12T09:20:00Z',
      avgRating: 4.2,
      reviews: 12,
    },
  ]

  // For demo vendors: show mock data
  // For real vendors: show ONLY real API data (even if empty)
  const displayListings = isDemo 
    ? mockListings 
    : (listings && Array.isArray(listings) ? listings : [])

  const filteredListings = displayListings.filter((item) => {
    const matchesSearch = item.productName
      .toLowerCase()
      .includes(searchTerm.toLowerCase())
    const matchesStatus = statusFilter ? item.status === statusFilter : true
    return matchesSearch && matchesStatus
  })

  const paginatedListings = filteredListings.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  )

  const totalPages = Math.ceil(filteredListings.length / itemsPerPage)

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-start">
        <div>
          <h1 className="text-3xl font-bold text-gray-900 mb-2">Product Listings</h1>
          <p className="text-gray-600">Manage and monitor your product inventory</p>
        </div>
        <Link to="/listings/create">
          <Button variant="primary" size="md">
            <Plus className="w-4 h-4 mr-2" />
            Add Product
          </Button>
        </Link>
      </div>

      {/* Error Message */}
      {error && (
        <Card className="card-shadow bg-red-50 border border-red-200">
          <div className="flex items-start gap-3">
            <div className="text-red-600 text-lg">⚠️</div>
            <div>
              <p className="font-semibold text-red-900">Error loading products</p>
              <p className="text-sm text-red-700 mt-1">{(error as any)?.message || 'Failed to fetch your product listings. Please try again.'}</p>
            </div>
          </div>
        </Card>
      )}

      {/* Stats Bar */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card className="card-shadow">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-600 text-sm mb-1">Total Listings</p>
              <p className="text-2xl font-bold">{displayListings.length}</p>
            </div>
            <div className="text-3xl">📦</div>
          </div>
        </Card>

        <Card className="card-shadow">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-600 text-sm mb-1">Active</p>
              <p className="text-2xl font-bold">
                {displayListings.filter((p) => p.status === 'ACTIVE').length}
              </p>
            </div>
            <div className="text-3xl">✅</div>
          </div>
        </Card>

        <Card className="card-shadow">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-600 text-sm mb-1">Out of Stock</p>
              <p className="text-2xl font-bold">
                {displayListings.filter((p) => p.status === 'OUT_OF_STOCK').length}
              </p>
            </div>
            <div className="text-3xl">⚠️</div>
          </div>
        </Card>

        <Card className="card-shadow">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-600 text-sm mb-1">Total Views</p>
              <p className="text-2xl font-bold">
                {displayListings.reduce((sum, p) => sum + p.viewCount, 0).toLocaleString()}
              </p>
            </div>
            <div className="text-3xl">👁️</div>
          </div>
        </Card>
      </div>

      {/* Filters & Search */}
      <Card className="card-shadow">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 items-end">
          <div>
            <label className="text-sm font-medium text-gray-700 block mb-2">
              <Search className="w-4 h-4 inline mr-2" />
              Search Products
            </label>
            <input
              type="text"
              placeholder="Search by product name..."
              value={searchTerm}
              onChange={(e) => {
                setSearchTerm(e.target.value)
                setCurrentPage(1)
              }}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <div>
            <label className="text-sm font-medium text-gray-700 block mb-2">
              <Filter className="w-4 h-4 inline mr-2" />
              Status Filter
            </label>
            <select
              value={statusFilter || ''}
              onChange={(e) => {
                setStatusFilter(e.target.value || null)
                setCurrentPage(1)
              }}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">All Status</option>
              <option value="ACTIVE">Active</option>
              <option value="OUT_OF_STOCK">Out of Stock</option>
              <option value="ARCHIVED">Archived</option>
            </select>
          </div>

          <Button
            variant="outline"
            onClick={() => {
              setSearchTerm('')
              setStatusFilter(null)
              setCurrentPage(1)
            }}
          >
            Clear Filters
          </Button>
        </div>
      </Card>

      {/* Products Table */}
      <Card className="card-shadow overflow-hidden">
        {paginatedListings.length > 0 ? (
          <>
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-gray-50 border-b">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-700 uppercase">
                      Product
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-700 uppercase">
                      Price
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-700 uppercase">
                      Stock
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-700 uppercase">
                      Views
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-700 uppercase">
                      Rating
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-700 uppercase">
                      Status
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-700 uppercase">
                      Actions
                    </th>
                  </tr>
                </thead>
                <tbody>
                  {paginatedListings.map((item) => (
                    <tr
                      key={item.id}
                      className="border-b hover:bg-gray-50 transition-colors"
                    >
                      <td className="px-6 py-4">
                        <div>
                          <p className="font-medium text-gray-900 text-sm">
                            {item.productName}
                          </p>
                          <p className="text-xs text-gray-500">{item.sku}</p>
                        </div>
                      </td>
                      <td className="px-6 py-4">
                        <div>
                          <p className="font-bold text-gray-900">
                            {formatCurrency(item.vendorPrice)}
                          </p>
                          <p className="text-xs text-gray-500 line-through">
                            {formatCurrency(item.originalPrice)}
                          </p>
                          <Badge className="bg-red-100 text-red-800 text-xs mt-1">
                            {item.discount}% OFF
                          </Badge>
                        </div>
                      </td>
                      <td className="px-6 py-4">
                        <p
                          className={`font-medium ${
                            item.quantityAvailable > 0
                              ? 'text-green-600'
                              : 'text-red-600'
                          }`}
                        >
                          {item.quantityAvailable} units
                        </p>
                      </td>
                      <td className="px-6 py-4">
                        <div className="flex items-center gap-1">
                          <Eye className="w-4 h-4 text-gray-400" />
                          <span className="text-gray-900 font-medium">
                            {item.viewCount.toLocaleString()}
                          </span>
                        </div>
                      </td>
                      <td className="px-6 py-4">
                        <div className="flex items-center gap-1">
                          <span className="font-medium text-gray-900">
                            {item.avgRating.toFixed(1)}
                          </span>
                          <span className="text-yellow-500">★</span>
                          <span className="text-xs text-gray-500">
                            ({item.reviews})
                          </span>
                        </div>
                      </td>
                      <td className="px-6 py-4">
                        <Badge className={getStatusColor(item.status)}>
                          {item.status === 'OUT_OF_STOCK' ? 'Out of Stock' : item.status}
                        </Badge>
                      </td>
                      <td className="px-6 py-4">
                        <div className="flex gap-2">
                          <Link to={`/listings/${item.id}/edit`}>
                            <button className="p-2 rounded-lg text-blue-600 hover:bg-blue-50 transition-colors">
                              <Edit2 className="w-4 h-4" />
                            </button>
                          </Link>
                          <button className="p-2 rounded-lg text-orange-600 hover:bg-orange-50 transition-colors">
                            <Archive className="w-4 h-4" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {/* Pagination */}
            <div className="flex items-center justify-between p-6 border-t bg-gray-50">
              <p className="text-sm text-gray-600">
                Showing {(currentPage - 1) * itemsPerPage + 1} to{' '}
                {Math.min(currentPage * itemsPerPage, filteredListings.length)} of{' '}
                {filteredListings.length} listings
              </p>
              <div className="flex gap-2">
                <button
                  onClick={() => setCurrentPage(Math.max(1, currentPage - 1))}
                  disabled={currentPage === 1}
                  className="p-2 rounded-lg border border-gray-300 hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  <ChevronLeft className="w-4 h-4" />
                </button>
                <div className="flex items-center gap-1 px-3 py-2">
                  {Array.from({ length: totalPages }, (_, i) => i + 1).map((page) => (
                    <button
                      key={page}
                      onClick={() => setCurrentPage(page)}
                      className={`w-8 h-8 rounded-lg ${
                        page === currentPage
                          ? 'bg-blue-600 text-white'
                          : 'border border-gray-300 hover:bg-gray-100'
                      }`}
                    >
                      {page}
                    </button>
                  ))}
                </div>
                <button
                  onClick={() => setCurrentPage(Math.min(totalPages, currentPage + 1))}
                  disabled={currentPage === totalPages}
                  className="p-2 rounded-lg border border-gray-300 hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  <ChevronRight className="w-4 h-4" />
                </button>
              </div>
            </div>
          </>
        ) : (
          <div className="flex flex-col items-center justify-center h-96 text-center">
            <TrendingUp className="w-12 h-12 text-gray-300 mb-4" />
            <h3 className="text-lg font-semibold text-gray-900 mb-1">No listings found</h3>
            <p className="text-gray-600 mb-6">Start by adding your first product.</p>
            <Link to="/listings/create">
              <Button variant="primary">
                <Plus className="w-4 h-4 mr-2" />
                Create First Listing
              </Button>
            </Link>
          </div>
        )}
      </Card>
    </div>
  )
}
