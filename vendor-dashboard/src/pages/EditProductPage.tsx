import React, { useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { Card, Button, Input, Select, Dialog } from '@/components/common'
import { AlertCircle, Trash2, Save } from 'lucide-react'

export const EditProductPage: React.FC = () => {
  const { productId } = useParams<{ productId: string }>()
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [showDeleteDialog, setShowDeleteDialog] = useState(false)

  const [formData, setFormData] = useState({
    productName: 'Wireless Headphones',
    description: 'High-quality wireless headphones with noise cancellation',
    category: 'Electronics',
    sku: 'SKU-001',
    quantity: '45',
    originalPrice: '7999',
    vendorPrice: '5999',
    warranty: '12',
    shippingTime: '3-5',
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

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)

    try {
      setLoading(true)
      // Mock API call
      await new Promise((resolve) => setTimeout(resolve, 1500))
      navigate('/listings')
    } catch (err) {
      setError('Failed to update product. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  const handleDelete = async () => {
    try {
      setLoading(true)
      // Mock API call
      await new Promise((resolve) => setTimeout(resolve, 1500))
      navigate('/listings')
    } catch (err) {
      setError('Failed to delete product. Please try again.')
    } finally {
      setLoading(false)
      setShowDeleteDialog(false)
    }
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-start">
        <div>
          <h1 className="text-3xl font-bold text-gray-900 mb-2">Edit Product</h1>
          <p className="text-gray-600">Update product details and pricing</p>
        </div>
        <Button
          variant="danger"
          onClick={() => setShowDeleteDialog(true)}
          disabled={loading}
        >
          <Trash2 className="w-4 h-4 mr-2" />
          Delete Product
        </Button>
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
              label="Product Name"
              type="text"
              name="productName"
              value={formData.productName}
              onChange={handleInputChange}
              disabled={loading}
            />

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Description
              </label>
              <textarea
                name="description"
                value={formData.description}
                onChange={handleInputChange}
                disabled={loading}
                rows={4}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <Select
              label="Category"
              name="category"
              value={formData.category}
              onChange={handleInputChange}
              options={categories.map((cat) => ({ label: cat, value: cat }))}
              disabled={loading}
            />

            <Input
              label="SKU"
              type="text"
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
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <Input
              label="Quantity Available"
              type="number"
              name="quantity"
              value={formData.quantity}
              onChange={handleInputChange}
              disabled={loading}
            />

            <Input
              label="Original Price (MRP)"
              type="number"
              name="originalPrice"
              value={formData.originalPrice}
              onChange={handleInputChange}
              disabled={loading}
            />

            <Input
              label="Your Price"
              type="number"
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
                  {(
                    ((parseFloat(formData.originalPrice) - parseFloat(formData.vendorPrice)) /
                      parseFloat(formData.originalPrice)) *
                    100
                  ).toFixed(1)}%
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
              name="warranty"
              value={formData.warranty}
              onChange={handleInputChange}
              disabled={loading}
            />

            <Input
              label="Shipping Time (days)"
              type="text"
              name="shippingTime"
              value={formData.shippingTime}
              onChange={handleInputChange}
              disabled={loading}
            />
          </div>
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
            <Save className="w-4 h-4 mr-2" />
            {loading ? 'Saving Changes...' : 'Save Changes'}
          </Button>
        </div>
      </form>

      {/* Delete Dialog */}
      <Dialog
        open={showDeleteDialog}
        onOpenChange={setShowDeleteDialog}
        title="Delete Product"
        description="Are you sure you want to delete this product? This action cannot be undone."
        onConfirm={handleDelete}
        confirmText="Delete"
        confirmVariant="danger"
      />
    </div>
  )
}
