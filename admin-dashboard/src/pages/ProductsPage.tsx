import React from 'react'
import { Edit, Trash2 } from 'lucide-react'

interface Product {
  id: string
  name: string
  vendor: string
  price: number
  category: string
  stock: number
  status: 'ACTIVE' | 'INACTIVE' | 'PENDING'
}

export const ProductsPage: React.FC = () => {
  const [products] = React.useState<Product[]>([
    { id: '1', name: 'Wireless Headphones', vendor: 'Tech Store', price: 129.99, category: 'Electronics', stock: 45, status: 'ACTIVE' },
    { id: '2', name: 'Cotton T-Shirt', vendor: 'Fashion Hub', price: 29.99, category: 'Fashion', stock: 120, status: 'ACTIVE' },
    { id: '3', name: 'Water Bottle', vendor: 'Home', price: 34.99, category: 'Home', stock: 0, status: 'INACTIVE' },
  ])

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold">Product Management</h1>
        <button className="bg-red-600 text-white px-6 py-2 rounded-lg hover:bg-red-700 transition">
          Add Product
        </button>
      </div>

      <div className="bg-white rounded-lg shadow-md overflow-hidden">
        <table className="w-full">
          <thead className="bg-gray-50 border-b border-gray-200">
            <tr>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Product Name</th>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Vendor</th>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Price</th>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Category</th>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Stock</th>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Status</th>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Actions</th>
            </tr>
          </thead>
          <tbody>
            {products.map((product) => (
              <tr key={product.id} className="border-b border-gray-100 hover:bg-gray-50">
                <td className="px-6 py-4 font-semibold">{product.name}</td>
                <td className="px-6 py-4 text-gray-600">{product.vendor}</td>
                <td className="px-6 py-4 font-semibold">${product.price}</td>
                <td className="px-6 py-4">{product.category}</td>
                <td className="px-6 py-4 font-semibold">{product.stock} units</td>
                <td className="px-6 py-4">
                  <span className={`px-3 py-1 rounded-full text-xs font-semibold ${product.status === 'ACTIVE' ? 'bg-green-100 text-green-800' : product.status === 'INACTIVE' ? 'bg-red-100 text-red-800' : 'bg-yellow-100 text-yellow-800'}`}>
                    {product.status}
                  </span>
                </td>
                <td className="px-6 py-4">
                  <div className="flex gap-2">
                    <button className="p-2 text-blue-600 hover:bg-blue-50 rounded-lg transition">
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
    </div>
  )
}
