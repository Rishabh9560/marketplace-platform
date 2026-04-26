import React from 'react'
import { MapPin, Star, ShoppingCart } from 'lucide-react'
import { useNavigate } from 'react-router-dom'

interface Vendor {
  id: string
  name: string
  rating: number
  reviews: number
  location: string
  products: number
  featured: boolean
  image: string
}

export const VendorsPage: React.FC = () => {
  const navigate = useNavigate()
  const [vendors] = React.useState<Vendor[]>([
    { id: 'v1', name: 'Tech Store Pro', rating: 4.8, reviews: 2540, location: 'San Francisco, CA', products: 245, featured: true, image: 'https://images.pexels.com/photos/18105/pexels-photo.jpg?auto=compress&cs=tinysrgb&w=600' },
    { id: 'v2', name: 'Fashion Hub', rating: 4.6, reviews: 1890, location: 'New York, NY', products: 342, featured: true, image: 'https://images.pexels.com/photos/3962286/pexels-photo-3962286.jpeg?auto=compress&cs=tinysrgb&w=600' },
    { id: 'v3', name: 'Home Essentials', rating: 4.9, reviews: 3120, location: 'Austin, TX', products: 156, featured: true, image: 'https://images.pexels.com/photos/279746/pexels-photo-279746.jpeg?auto=compress&cs=tinysrgb&w=600' },
    { id: 'v4', name: 'Sports Central', rating: 4.7, reviews: 892, location: 'Denver, CO', products: 178, featured: false, image: 'https://images.pexels.com/photos/3945683/pexels-photo-3945683.jpeg?auto=compress&cs=tinysrgb&w=600' },
    { id: 'v5', name: 'Beauty & Wellness', rating: 4.5, reviews: 641, location: 'Los Angeles, CA', products: 289, featured: false, image: 'https://images.pexels.com/photos/3807517/pexels-photo-3807517.jpeg?auto=compress&cs=tinysrgb&w=600' },
    { id: 'v6', name: 'Book Bazaar', rating: 4.8, reviews: 1203, location: 'Seattle, WA', products: 567, featured: false, image: 'https://images.pexels.com/photos/1756617/pexels-photo-1756617.jpeg?auto=compress&cs=tinysrgb&w=600' },
  ])

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 py-8">
        <h1 className="text-4xl font-bold mb-2">Explore Vendors</h1>
        <p className="text-gray-600 mb-8">Browse and shop from trusted sellers on our marketplace</p>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {vendors.map((vendor) => (
            <div key={vendor.id} className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition transform hover:-translate-y-1">
              <div className="h-32 bg-gray-200 flex items-center justify-center overflow-hidden">
                <img 
                  src={vendor.image} 
                  alt={vendor.name}
                  className="w-full h-full object-cover hover:scale-105 transition-transform"
                  onError={(e) => {
                    e.currentTarget.src = 'data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 100 100%22%3E%3Crect fill=%22%233b82f6%22 width=%22100%22 height=%22100%22/%3E%3Ctext x=%2250%22 y=%2250%22 font-size=%2240%22 fill=%22white%22 text-anchor=%22middle%22 dy=%22.3em%22%3E%F0%9F%8F%AA%3C/text%3E%3C/svg%3E'
                  }}
                />
              </div>

              <div className="p-6">
                {vendor.featured && <span className="inline-block bg-yellow-100 text-yellow-800 px-3 py-1 rounded-full text-xs font-bold mb-3">Featured</span>}

                <h3 className="text-xl font-bold mb-2">{vendor.name}</h3>

                <div className="flex items-center gap-2 mb-3">
                  <div className="flex items-center gap-1">
                    <Star className="w-4 h-4 text-yellow-400 fill-yellow-400" />
                    <span className="font-bold">{vendor.rating}</span>
                  </div>
                  <span className="text-gray-600 text-sm">({vendor.reviews} reviews)</span>
                </div>

                <div className="space-y-2 mb-4 text-sm text-gray-600">
                  <div className="flex items-center gap-2">
                    <MapPin className="w-4 h-4" />
                    {vendor.location}
                  </div>
                  <div className="flex items-center gap-2">
                    <ShoppingCart className="w-4 h-4" />
                    {vendor.products} Products
                  </div>
                </div>

                <button 
                  onClick={() => navigate(`/store/${vendor.id}`)}
                  className="w-full bg-blue-600 text-white py-2 rounded-lg font-semibold hover:bg-blue-700 transition"
                >
                  View Store
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
