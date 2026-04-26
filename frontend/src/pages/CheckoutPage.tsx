import React from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuthStore, useCartStore, useOrderStore } from '@/store'
import { Check, AlertCircle } from 'lucide-react'

export const CheckoutPage: React.FC = () => {
  const navigate = useNavigate()
  const { isAuthenticated, customer } = useAuthStore()
  const { cart, clearCart } = useCartStore()
  const { addOrder } = useOrderStore()
  const [step, setStep] = React.useState(1)
  const [orderConfirmed, setOrderConfirmed] = React.useState(false)
  const [paymentProcessing, setPaymentProcessing] = React.useState(false)
  const [error, setError] = React.useState<string | null>(null)
  const [orderId, setOrderId] = React.useState<string>('')

  // Get cart items safely
  const items = cart?.items || []
  const getTotalPrice = () => cart?.total || 0

  const [formData, setFormData] = React.useState({
    fullName: '',
    street: '',
    city: '',
    state: '',
    zipCode: '',
    phone: '',
    cardNumber: '',
    cardExp: '',
    cardCVC: '',
  })

  const total = getTotalPrice()
  const shipping = 10
  const tax = (total + shipping) * 0.1
  const grandTotal = total + shipping + tax

  if (!isAuthenticated) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-3xl font-bold mb-4">Please Login to Checkout</h1>
          <button onClick={() => navigate('/login')} className="bg-blue-600 text-white px-8 py-3 rounded-lg">
            Go to Login
          </button>
        </div>
      </div>
    )
  }

  if (items.length === 0 && !orderConfirmed) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-3xl font-bold mb-4">Your cart is empty</h1>
          <button onClick={() => navigate('/products')} className="bg-blue-600 text-white px-8 py-3 rounded-lg">
            Continue Shopping
          </button>
        </div>
      </div>
    )
  }

  if (orderConfirmed) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-green-50 to-emerald-50">
        <div className="text-center bg-white rounded-xl shadow-2xl p-12">
          <div className="w-20 h-20 bg-green-100 text-green-600 rounded-full flex items-center justify-center mx-auto mb-6">
            <Check className="w-10 h-10" />
          </div>
          <h1 className="text-4xl font-bold mb-4 text-green-600">Order Placed Successfully!</h1>
          <p className="text-gray-600 mb-2">Your order has been confirmed</p>
          <p className="text-2xl font-bold text-gray-800 mb-4">Order ID: <span className="text-blue-600">{orderId}</span></p>
          <p className="text-gray-600 mb-8">A confirmation email has been sent to <span className="font-semibold">{customer?.email}</span></p>
          
          <div className="bg-gray-50 rounded-lg p-4 mb-8 text-left">
            <h3 className="font-bold mb-3">Order Details:</h3>
            <div className="space-y-2 text-sm">
              <div className="flex justify-between"><span>Subtotal:</span><span>${total.toFixed(2)}</span></div>
              <div className="flex justify-between"><span>Shipping:</span><span>${shipping.toFixed(2)}</span></div>
              <div className="flex justify-between"><span>Tax:</span><span>${tax.toFixed(2)}</span></div>
              <div className="flex justify-between font-bold border-t border-gray-300 pt-2"><span>Total:</span><span>${grandTotal.toFixed(2)}</span></div>
            </div>
          </div>

          <div className="flex gap-4">
            <button onClick={() => navigate('/orders')} className="flex-1 bg-blue-600 text-white px-8 py-3 rounded-lg font-semibold hover:bg-blue-700 transition">
              Track Order
            </button>
            <button onClick={() => navigate('/')} className="flex-1 bg-gray-200 text-gray-800 px-8 py-3 rounded-lg font-semibold hover:bg-gray-300 transition">
              Continue Shopping
            </button>
          </div>
        </div>
      </div>
    )
  }

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    })
  }

  const validateShippingForm = () => {
    if (!formData.fullName) return 'Full name is required'
    if (!formData.street) return 'Street address is required'
    if (!formData.city) return 'City is required'
    if (!formData.state) return 'State is required'
    if (!formData.zipCode) return 'Zip code is required'
    if (!formData.phone) return 'Phone number is required'
    return null
  }

  const validatePaymentForm = () => {
    if (!formData.cardNumber) return 'Card number is required'
    if (formData.cardNumber.length < 13) return 'Card number must be at least 13 characters'
    if (!formData.cardExp) return 'Expiration date is required'
    if (!formData.cardCVC) return 'CVC is required'
    if (formData.cardCVC.length !== 3) return 'CVC must be 3 digits'
    return null
  }

  const handlePlaceOrder = async () => {
    setError(null)

    // Validate shipping
    const shippingError = validateShippingForm()
    if (shippingError) {
      setError(shippingError)
      setStep(1)
      return
    }

    // Validate payment
    const paymentError = validatePaymentForm()
    if (paymentError) {
      setError(paymentError)
      setStep(2)
      return
    }

    // Simulate payment processing
    setPaymentProcessing(true)
    try {
      // Simulate API call to process payment
      await new Promise((resolve) => setTimeout(resolve, 2000))

      // Mock successful payment (90% success rate for demo)
      const isSuccess = Math.random() > 0.1

      if (isSuccess) {
        const newOrderId = `ORD-${Math.random().toString(36).substring(7).toUpperCase()}`
        const trackingId = `TRACK-${Math.random().toString(36).substring(7).toUpperCase().padEnd(15, '0')}`
        
        // Save order to store
        addOrder({
          id: newOrderId,
          date: new Date().toISOString().split('T')[0],
          total: grandTotal,
          status: 'CONFIRMED',
          items: items.length,
          trackingId: trackingId,
          items_detail: items.map((item: any) => ({
            name: item.productName || item.name,
            quantity: item.quantity,
            price: item.price
          }))
        })
        
        setOrderId(newOrderId)
        clearCart()
        setOrderConfirmed(true)
      } else {
        setError('Payment failed. Please check your card details and try again.')
        setStep(2)
      }
    } catch (err) {
      setError('An error occurred during payment processing. Please try again.')
      setStep(2)
    } finally {
      setPaymentProcessing(false)
    }
  }

  return (
    <div className="max-w-4xl mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-8">Checkout</h1>

      {error && (
        <div className="mb-6 bg-red-50 border border-red-200 rounded-lg p-4 flex gap-3">
          <AlertCircle className="w-5 h-5 text-red-600 flex-shrink-0 mt-0.5" />
          <div>
            <h3 className="font-semibold text-red-800">Error</h3>
            <p className="text-red-700 text-sm">{error}</p>
          </div>
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Checkout Form */}
        <div className="lg:col-span-2">
          {/* Shipping Address */}
          <div className={`bg-white rounded-lg shadow-md p-6 mb-6 ${step !== 1 && 'opacity-50 pointer-events-none'}`}>
            <h2 className="text-xl font-bold mb-4 flex items-center gap-2">
              <span className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold ${step >= 1 ? 'bg-blue-600 text-white' : 'bg-gray-300 text-white'}`}>1</span>
              Shipping Address
            </h2>
            <div className="space-y-4">
              <input
                type="text"
                name="fullName"
                placeholder="Full Name"
                value={formData.fullName}
                onChange={handleInputChange}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-600"
              />
              <input
                type="text"
                name="street"
                placeholder="Street Address"
                value={formData.street}
                onChange={handleInputChange}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-600"
              />
              <div className="grid grid-cols-2 gap-4">
                <input
                  type="text"
                  name="city"
                  placeholder="City"
                  value={formData.city}
                  onChange={handleInputChange}
                  className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-600"
                />
                <input
                  type="text"
                  name="state"
                  placeholder="State"
                  value={formData.state}
                  onChange={handleInputChange}
                  className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-600"
                />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <input
                  type="text"
                  name="zipCode"
                  placeholder="Zip Code"
                  value={formData.zipCode}
                  onChange={handleInputChange}
                  className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-600"
                />
                <input
                  type="tel"
                  name="phone"
                  placeholder="Phone Number"
                  value={formData.phone}
                  onChange={handleInputChange}
                  className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-600"
                />
              </div>
            </div>
          </div>

          {/* Payment Method */}
          <div className={`bg-white rounded-lg shadow-md p-6 mb-6 ${step !== 2 && 'opacity-50 pointer-events-none'}`}>
            <h2 className="text-xl font-bold mb-4 flex items-center gap-2">
              <span className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold ${step >= 2 ? 'bg-blue-600 text-white' : 'bg-gray-300 text-white'}`}>2</span>
              Payment Method
            </h2>
            <div className="space-y-4">
              <div className="space-y-4 pl-0">
                <input
                  type="text"
                  name="cardNumber"
                  placeholder="Card Number (e.g., 4532015112830366)"
                  value={formData.cardNumber}
                  onChange={handleInputChange}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-600"
                />
                <div className="grid grid-cols-2 gap-4">
                  <input
                    type="text"
                    name="cardExp"
                    placeholder="MM/YY"
                    value={formData.cardExp}
                    onChange={handleInputChange}
                    className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-600"
                  />
                  <input
                    type="text"
                    name="cardCVC"
                    placeholder="CVC"
                    value={formData.cardCVC}
                    onChange={handleInputChange}
                    className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-600"
                  />
                </div>
              </div>
            </div>
          </div>

          {/* Order Review */}
          <div className="bg-white rounded-lg shadow-md p-6">
            <h2 className="text-xl font-bold mb-4 flex items-center gap-2">
              <span className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold ${step >= 3 ? 'bg-blue-600 text-white' : 'bg-gray-300 text-white'}`}>3</span>
              Order Review
            </h2>
            <div className="space-y-3 border-b border-gray-200 pb-4 mb-4">
              {items.map((item: any) => (
                <div key={item.id} className="flex justify-between text-sm">
                  <span>{item.productName} x {item.quantity}</span>
                  <span>${(item.price * item.quantity).toFixed(2)}</span>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Order Summary */}
        <div className="bg-white rounded-lg shadow-md p-6 h-fit sticky top-8">
          <h2 className="font-bold text-lg mb-4">Order Summary</h2>
          <div className="space-y-3 border-b border-gray-200 pb-4 mb-4 text-sm">
            <div className="flex justify-between">
              <span>Subtotal</span>
              <span>${total.toFixed(2)}</span>
            </div>
            <div className="flex justify-between">
              <span>Shipping</span>
              <span>${shipping.toFixed(2)}</span>
            </div>
            <div className="flex justify-between">
              <span>Tax</span>
              <span>${tax.toFixed(2)}</span>
            </div>
          </div>
          <div className="flex justify-between font-bold text-lg mb-6">
            <span>Total</span>
            <span className="text-blue-600">${grandTotal.toFixed(2)}</span>
          </div>
          <button
            onClick={handlePlaceOrder}
            disabled={paymentProcessing}
            className="w-full bg-blue-600 text-white py-3 rounded-lg font-semibold hover:bg-blue-700 transition disabled:bg-blue-400 disabled:cursor-not-allowed flex items-center justify-center gap-2"
          >
            {paymentProcessing ? (
              <>
                <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                Processing...
              </>
            ) : (
              'Place Order & Pay'
            )}
          </button>
          <p className="text-xs text-gray-600 mt-4 text-center">For demo: Use card 4532015112830366, any future date, any 3-digit CVC</p>
        </div>
      </div>
    </div>
  )
}
