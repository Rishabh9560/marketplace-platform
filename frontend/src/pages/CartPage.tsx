import React, { useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { Trash2, Plus, Minus, ShoppingCart, ArrowLeft } from 'lucide-react'
import { useCartStore } from '@/store/cartStore'
import { useAuthStore } from '@/store/index'

const CartPage: React.FC = () => {
  const navigate = useNavigate()
  const { isAuthenticated } = useAuthStore()
  const { cart, loading, error, fetchCart, updateQuantity, removeFromCart, clearCart } =
    useCartStore()

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login')
      return
    }
    // Fetch cart on component mount
    fetchCart()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  if (!isAuthenticated) {
    return null
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-red-600 mx-auto mb-4"></div>
          <p className="text-gray-600">Loading cart...</p>
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 py-12">
        <div className="max-w-6xl mx-auto px-4">
          <div className="bg-red-50 border border-red-200 rounded-lg p-4 text-red-700">
            <p className="font-semibold mb-2">Error loading cart</p>
            <p>{error}</p>
          </div>
        </div>
      </div>
    )
  }

  if (!cart || cart.items.length === 0) {
    return (
      <div className="min-h-screen bg-gray-50 py-12">
        <div className="max-w-6xl mx-auto px-4">
          <button
            onClick={() => navigate(-1)}
            className="flex items-center gap-2 text-gray-600 hover:text-gray-900 mb-8"
          >
            <ArrowLeft className="w-5 h-5" />
            Continue Shopping
          </button>

          <div className="bg-white rounded-lg shadow p-12 text-center">
            <ShoppingCart className="w-16 h-16 text-gray-300 mx-auto mb-4" />
            <h1 className="text-2xl font-bold text-gray-900 mb-2">Your cart is empty</h1>
            <p className="text-gray-600 mb-8">Add some products to get started!</p>
            <button
              onClick={() => navigate('/products')}
              className="bg-red-600 text-white px-6 py-2 rounded-lg hover:bg-red-700 transition"
            >
              Start Shopping
            </button>
          </div>
        </div>
      </div>
    )
  }

  const handleQuantityChange = async (itemId: string, newQuantity: number) => {
    if (newQuantity < 1) {
      await removeFromCart(itemId)
    } else {
      await updateQuantity(itemId, newQuantity)
    }
  }

  return (
    <div className="min-h-screen bg-gray-50 py-12">
      <div className="max-w-6xl mx-auto px-4">
        {/* Header */}
        <button
          onClick={() => navigate(-1)}
          className="flex items-center gap-2 text-gray-600 hover:text-gray-900 mb-8"
        >
          <ArrowLeft className="w-5 h-5" />
          Continue Shopping
        </button>

        <h1 className="text-3xl font-bold text-gray-900 mb-8">Shopping Cart</h1>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Cart Items */}
          <div className="lg:col-span-2">
            <div className="bg-white rounded-lg shadow overflow-hidden">
              <div className="p-6 border-b border-gray-200">
                <p className="text-gray-600">
                  {cart.items.length} item{cart.items.length !== 1 ? 's' : ''} in cart
                </p>
              </div>

              <div className="divide-y divide-gray-200">
                {cart.items.map((item) => (
                  <div key={item.itemId} className="p-6 hover:bg-gray-50 transition">
                    <div className="flex gap-4">
                      {/* Product Image Placeholder */}
                      <div className="w-24 h-24 bg-gray-200 rounded-lg flex-shrink-0" />

                      {/* Product Details */}
                      <div className="flex-1">
                        <div className="flex justify-between mb-2">
                          <div>
                            <h3 className="font-semibold text-gray-900">{item.productName}</h3>
                            <p className="text-sm text-gray-600 mt-1">SKU: {item.sku}</p>
                            <p className="text-sm text-gray-600">Vendor: {item.vendorName}</p>
                          </div>
                          <button
                            onClick={() => removeFromCart(item.itemId)}
                            className="text-red-600 hover:text-red-700 transition"
                            title="Remove from cart"
                          >
                            <Trash2 className="w-5 h-5" />
                          </button>
                        </div>

                        {/* Quantity and Price */}
                        <div className="flex items-center justify-between mt-4">
                          <div className="flex items-center gap-3 bg-gray-100 rounded-lg p-1">
                            <button
                              onClick={() =>
                                handleQuantityChange(item.itemId, item.quantity - 1)
                              }
                              className="p-1 hover:bg-gray-200 rounded transition"
                              disabled={loading}
                            >
                              <Minus className="w-4 h-4 text-gray-600" />
                            </button>
                            <span className="w-8 text-center font-semibold text-gray-900">
                              {item.quantity}
                            </span>
                            <button
                              onClick={() =>
                                handleQuantityChange(item.itemId, item.quantity + 1)
                              }
                              className="p-1 hover:bg-gray-200 rounded transition"
                              disabled={loading}
                            >
                              <Plus className="w-4 h-4 text-gray-600" />
                            </button>
                          </div>

                          <div className="text-right">
                            <p className="text-sm text-gray-600">
                              ₹{item.price.toLocaleString('en-IN', { minimumFractionDigits: 2 })}
                              × {item.quantity}
                            </p>
                            <p className="font-semibold text-gray-900 text-lg">
                              ₹
                              {item.totalPrice.toLocaleString('en-IN', { minimumFractionDigits: 2 })}
                            </p>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* Order Summary */}
          <div className="lg:col-span-1">
            <div className="bg-white rounded-lg shadow p-6 sticky top-4">
              <h2 className="text-xl font-bold text-gray-900 mb-6">Order Summary</h2>

              <div className="space-y-4 mb-6 pb-6 border-b border-gray-200">
                <div className="flex justify-between text-gray-600">
                  <span>Subtotal</span>
                  <span>
                    ₹{cart.subtotal.toLocaleString('en-IN', { minimumFractionDigits: 2 })}
                  </span>
                </div>
                <div className="flex justify-between text-gray-600">
                  <span>Tax (GST)</span>
                  <span>
                    ₹{cart.tax.toLocaleString('en-IN', { minimumFractionDigits: 2 })}
                  </span>
                </div>
                <div className="flex justify-between text-gray-600">
                  <span>Shipping</span>
                  <span className="text-green-600 font-semibold">FREE</span>
                </div>
              </div>

              <div className="flex justify-between items-center mb-6 pb-6 border-b border-gray-200">
                <span className="font-bold text-gray-900">Total</span>
                <span className="text-2xl font-bold text-red-600">
                  ₹{cart.total.toLocaleString('en-IN', { minimumFractionDigits: 2 })}
                </span>
              </div>

              {cart.estimatedDelivery && (
                <p className="text-sm text-gray-600 mb-6">
                  Estimated Delivery: <span className="font-semibold">{cart.estimatedDelivery}</span>
                </p>
              )}

              <button
                onClick={() => navigate('/checkout')}
                className="w-full bg-red-600 text-white py-3 rounded-lg font-semibold hover:bg-red-700 transition mb-2"
              >
                Proceed to Checkout
              </button>

              <button
                onClick={() => navigate('/products')}
                className="w-full border-2 border-red-600 text-red-600 py-2 rounded-lg font-semibold hover:bg-red-50 transition"
              >
                Continue Shopping
              </button>

              <button
                onClick={() => {
                  if (confirm('Are you sure you want to clear the cart?')) {
                    clearCart()
                  }
                }}
                className="w-full text-red-600 py-2 mt-4 hover:bg-red-50 rounded-lg transition text-sm"
              >
                Clear Cart
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default CartPage
export { CartPage }
