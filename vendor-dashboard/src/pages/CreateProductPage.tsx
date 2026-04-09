import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Card, Button, Input, Select } from '@/components/common'
import { AlertCircle, Upload, X } from 'lucide-react'

export const CreateProductPage: React.FC = () => {
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [images, setImages] = useState<File[]>([])

  const [formData, setFormData] = useState({
    productName: '',
    description: '',
    category: '',
    sku: '',
    quantity: '',
    originalPrice: '',
    vendorPrice: '',
    discount: '',
    warranty: '',
    shippingTime: '',
  })

  const categories = [
    'Electronics',
    'Clothing',
    'Home & Garden',
    'Sports',
    'Books',
    'Toys',
    'Beauty',
    'Food & Beverages',
  ]

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    setFormData((prev) => ({ ...prev, [name]: value }))
  }

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files) {
      setImages(Array.from(e.target.files).slice(0, 5))
    }
  }

  const removeImage = (index: number) => {
    setImages((prev) => prev.filter((_, i) => i !== index))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)

    // Validation
    if (
      !formData.productName ||
      !formData.description ||
      !formData.category ||
      !formData.sku ||
      !formData.quantity ||
      !formData.originalPrice ||
      !formData.vendorPrice
    ) {
      setError('Please fill in all required fields')
      return
    }

    if (images.length === 0) {
      setError('Please upload at least one product image')
      return
    }

    const discount = ((parseFloat(formData.originalPrice) - parseFloat(formData.vendorPrice)) / parseFloat(formData.originalPrice)) * 100

    if (discount > 90) {
      setError('Discount cannot exceed 90%')
      return
    }

    try {
      setLoading(true)
      // Mock API call
      console.log('Creating product:', { ...formData, discount, images })
      await new Promise((resolve) => setTimeout(resolve, 1500))
      navigate('/listings')
    } catch (err) {
      setError('Failed to create product. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-3xl font-bold text-gray-900 mb-2">Add New Product</h1>
        <p className="text-gray-600">Fill in the details below to list a new product</p>
      </div>

      {error && (
        <div className="flex items-start gap-3 p-4 bg-red-50 border border-red-200 rounded-lg">
          <AlertCircle className="w-5 h-5 text-red-600 flex-shrink-0 mt-0.5" />
          <p className="text-sm text-red-800">{error}</p>
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-6">
        {/* Basic Information */}
        <Card className="card-shadow">
          <h3 className="text-lg font-bold text-gray-900 mb-6">Basic Information</h3>
          <div className="space-y-4">
            <Input
              label="Product Name *"
              type="text"
              placeholder="Enter product name"
              name="productName"
              value={formData.productName}
              onChange={handleInputChange}
              disabled={loading}
            />

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Description *
              </label>
              <textarea
                placeholder="Describe your product in detail..."
                name="description"
                value={formData.description}
                onChange={handleInputChange}
                disabled={loading}
                rows={4}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <Select
              label="Category *"
              name="category"
              value={formData.category}
              onChange={handleInputChange}
              options={categories.map((cat) => ({ label: cat, value: cat }))}
              disabled={loading}
            />

            <Input
              label="SKU (Stock Keeping Unit) *"
              type="text"
              placeholder="e.g., SKU-001"
              name="sku"
              value={formData.sku}
              onChange={handleInputChange}
              disabled={loading}
            />
          </div>
        </Card>

        {/* Pricing & Inventory */}
        <Card className="card-shadow">
          <h3 className="text-lg font-bold text-gray-900 mb-6">Pricing & Inventory</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
            <Input
              label="Quantity Available *"
              type="number"
              placeholder="0"
              name="quantity"
              value={formData.quantity}
              onChange={handleInputChange}
              disabled={loading}
            />

            <Input
              label="Original Price (MRP) *"
              type="number"
              placeholder="0.00"
              name="originalPrice"
              value={formData.originalPrice}
              onChange={handleInputChange}
              disabled={loading}
            />

            <Input
              label="Your Price *"
              type="number"
              placeholder="0.00"
              name="vendorPrice"
              value={formData.vendorPrice}
              onChange={handleInputChange}
              disabled={loading}
            />

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Discount (Auto-calculated)
              </label>
              <div className="px-4 py-2 border border-gray-300 rounded-lg bg-gray-50">
                <p className="text-gray-900 font-semibold">
                  {formData.originalPrice && formData.vendorPrice
                    ? (
                        ((parseFloat(formData.originalPrice) - parseFloat(formData.vendorPrice)) /
                          parseFloat(formData.originalPrice)) *
                        100
                      ).toFixed(1) + '%'
                    : '0%'}
                </p>
              </div>
            </div>
          </div>
        </Card>

        {/* Additional Details */}
        <Card className="card-shadow">
          <h3 className="text-lg font-bold text-gray-900 mb-6">Additional Details</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <Input
              label="Warranty (months)"
              type="number"
              placeholder="0"
              name="warranty"
              value={formData.warranty}
              onChange={handleInputChange}
              disabled={loading}
            />

            <Input
              label="Shipping Time (days)"
              type="number"
              placeholder="3-5"
              name="shippingTime"
              value={formData.shippingTime}
              onChange={handleInputChange}
              disabled={loading}
            />
          </div>
        </Card>

        {/* Product Images */}
        <Card className="card-shadow">
          <h3 className="text-lg font-bold text-gray-900 mb-6">Product Images *</h3>

          {/* Upload Area */}
          <div className="mb-6">
            <label className="border-2 border-dashed border-gray-300 rounded-lg p-8 text-center cursor-pointer hover:border-blue-500 hover:bg-blue-50 transition-colors block">
              <Upload className="w-8 h-8 text-gray-400 mx-auto mb-2" />
              <p className="text-gray-600 font-medium">Click to upload or drag images</p>
              <p className="text-xs text-gray-500 mt-1">
                PNG, JPG, GIF up to 5MB. Maximum 5 images.
              </p>
              <input
                type="file"
                multiple
                accept="image/*"
                onChange={handleImageChange}
                disabled={loading}
                className="hidden"
              />
            </label>
          </div>

          {/* Image Preview */}
          {images.length > 0 && (
            <div>
              <p className="text-sm font-medium text-gray-700 mb-3">
                Uploaded Images ({images.length}/5)
              </p>
              <div className="grid grid-cols-2 md:grid-cols-5 gap-4">
                {images.map((file, index) => (
                  <div key={index} className="relative group">
                    <img
                      src={URL.createObjectURL(file)}
                      alt={`Product ${index + 1}`}
                      className="w-full h-32 object-cover rounded-lg border border-gray-200"
                    />
                    <button
                      type="button"
                      onClick={() => removeImage(index)}
                      className="absolute top-1 right-1 bg-red-600 text-white rounded-full p-1 opacity-0 group-hover:opacity-100 transition-opacity"
                    >
                      <X className="w-4 h-4" />
                    </button>
                    <p className="text-xs text-gray-600 mt-1 truncate">{file.name}</p>
                  </div>
                ))}
              </div>
            </div>
          )}
        </Card>

        {/* Actions */}
        <div className="flex gap-4 justify-end">
          <Button
            type="button"
            variant="outline"
            onClick={() => navigate('/listings')}
            disabled={loading}
          >
            Cancel
          </Button>
          <Button type="submit" disabled={loading} size="md">
            {loading ? 'Creating Product...' : 'Create Product'}
          </Button>
        </div>
      </form>
    </div>
  )
}
