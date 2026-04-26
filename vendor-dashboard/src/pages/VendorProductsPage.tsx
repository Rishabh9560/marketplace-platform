import React, { useState, useEffect } from 'react'
import { Edit, Trash2, Eye, Package } from 'lucide-react'
import { useProductListings } from '@/hooks/useApi'
import { isDemoVendor } from '@/lib/vendorUtils'
import { useAuthStore } from '@/store'

interface VendorProduct {
  id: string
  name?: string
  productName?: string
  price?: number
  vendorPrice?: number
  stock?: number
  quantityAvailable?: number
  status: 'ACTIVE' | 'INACTIVE' | 'OUT_OF_STOCK'
  orders?: number
  salesCount?: number
  revenue?: number
}

export const VendorProductsPage: React.FC = () => {
  const { vendor } = useAuthStore()
  const { data: listings, loading, error, fetchListings } = useProductListings()
  const isDemo = isDemoVendor(vendor)
  const [products, setProducts] = useState<VendorProduct[]>([])

  // Fetch vendor products only for real vendors
  useEffect(() => {
    if (vendor?.id && !isDemo) {
      fetchListings(vendor.id, 0, 100)
    }
  }, [vendor?.id, isDemo, fetchListings])

  // Update products state when listings data changes
  useEffect(() => {
    if (listings && Array.isArray(listings)) {
      setProducts(listings as VendorProduct[])
    }
  }, [listings])

  // Fallback mock data if no listings are available
  const mockProducts: VendorProduct[] = [
    {
      id: '1',
      name: 'Premium Wireless Headphones',
      price: 129.99,
      stock: 45,
      status: 'ACTIVE',
      orders: 234,
      revenue: 30436.66,
    },
    {
      id: '2',
      name: 'Smartphone Case Pro',
      price: 19.99,
      stock: 120,
      status: 'ACTIVE',
      orders: 478,
      revenue: 9552.22,
    },
    {
      id: '3',
      name: 'Tech Accessory Bundle',
      price: 89.99,
      stock: 0,
      status: 'INACTIVE',
      orders: 92,
      revenue: 8279.08,
    },
  ]

  // Use real products if available, otherwise show mock
  const displayProducts = products.length > 0 ? products : mockProducts

  const getProductName = (product: VendorProduct) => product.productName || product.name || 'N/A'
  const getPrice = (product: VendorProduct) => product.vendorPrice || product.price || 0
  const getStock = (product: VendorProduct) => product.quantityAvailable ?? product.stock ?? 0
  const getOrders = (product: VendorProduct) => product.salesCount || product.orders || 0
  const getRevenue = (product: VendorProduct) => product.revenue || 0

  if (loading) {
    return (
      <div className="space-y-6">
        <h1 className="text-3xl font-bold">Your Products</h1>
        <div className="text-center py-12">
          <p className="text-gray-500">Loading your products...</p>
        </div>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold">Your Products</h1>
        <button className="bg-green-600 text-white px-6 py-2 rounded-lg hover:bg-green-700 transition font-semibold">
          Add New Product
        </button>
      </div>

      {displayProducts.length === 0 ? (
        <div className="bg-white rounded-lg shadow-md p-12 text-center">
          <Package className="w-12 h-12 text-gray-400 mx-auto mb-4" />
          <p className="text-gray-600 text-lg">No products found. Create your first product listing!</p>
        </div>
      ) : (
        <>
          {/* Stats */}
          <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
            <div className="bg-white rounded-lg shadow-md p-6">
              <p className="text-gray-600 text-sm">Total Products</p>
              <p className="text-3xl font-bold mt-2">{displayProducts.length}</p>
            </div>
            <div className="bg-white rounded-lg shadow-md p-6">
              <p className="text-gray-600 text-sm">Active Products</p>
              <p className="text-3xl font-bold mt-2">{displayProducts.filter((p) => p.status === 'ACTIVE').length}</p>
            </div>
            <div className="bg-white rounded-lg shadow-md p-6">
              <p className="text-gray-600 text-sm">Total Orders</p>
              <p className="text-3xl font-bold mt-2">{displayProducts.reduce((sum, p) => sum + getOrders(p), 0)}</p>
            </div>
            <div className="bg-white rounded-lg shadow-md p-6">
              <p className="text-gray-600 text-sm">Total Revenue</p>
              <p className="text-3xl font-bold mt-2 text-green-600">
                ${displayProducts.reduce((sum, p) => sum + getRevenue(p), 0).toFixed(2)}
              </p>
            </div>
          </div>

          {/* Products Table */}
          <div className="bg-white rounded-lg shadow-md overflow-hidden">
            <table className="w-full">
              <thead className="bg-gray-50 border-b border-gray-200">
                <tr>
                  <th className="px-6 py-3 text-left text-gray-600 font-semibold">Product Name</th>
                  <th className="px-6 py-3 text-left text-gray-600 font-semibold">Price</th>
                  <th className="px-6 py-3 text-left text-gray-600 font-semibold">Stock</th>
                  <th className="px-6 py-3 text-left text-gray-600 font-semibold">Orders</th>
                  <th className="px-6 py-3 text-left text-gray-600 font-semibold">Revenue</th>
                  <th className="px-6 py-3 text-left text-gray-600 font-semibold">Status</th>
                  <th className="px-6 py-3 text-left text-gray-600 font-semibold">Actions</th>
                </tr>
              </thead>
              <tbody>
                {displayProducts.map((product) => (
                  <tr key={product.id} className="border-b border-gray-100 hover:bg-gray-50">
                    <td className="px-6 py-4 font-semibold">{getProductName(product)}</td>
                    <td className="px-6 py-4">${getPrice(product)}</td>
                    <td className="px-6 py-4 font-semibold">{getStock(product)} units</td>
                    <td className="px-6 py-4 font-semibold">{getOrders(product)}</td>
                    <td className="px-6 py-4 font-semibold text-green-600">${getRevenue(product).toFixed(2)}</td>
                    <td className="px-6 py-4">
                      <span
                        className={`px-3 py-1 rounded-full text-xs font-semibold ${
                          product.status === 'ACTIVE'
                            ? 'bg-green-100 text-green-800'
                            : product.status === 'OUT_OF_STOCK'
                            ? 'bg-yellow-100 text-yellow-800'
                            : 'bg-red-100 text-red-800'
                        }`}
                      >
                        {product.status}
                      </span>
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex gap-2">
                        <button className="p-2 text-blue-600 hover:bg-blue-50 rounded-lg transition">
                          <Eye className="w-5 h-5" />
                        </button>
                        <button className="p-2 text-green-600 hover:bg-green-50 rounded-lg transition">
                          <Edit className="w-5 h-5" />
                        </button>
                        <button className="p-2 text-red-600 hover:bg-red-50 rounded-lg transition">
                          <Trash2 className="w-5 h-5" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </>
      )}
    </div>
  )
}
