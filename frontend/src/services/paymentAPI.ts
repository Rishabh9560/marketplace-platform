import api from './authAPI'

export interface Payment {
  id: string
  orderId: string
  paymentIntentId: string
  paymentMethod: 'CARD' | 'UPI' | 'NET_BANKING' | 'WALLET' | 'COD'
  status: 'PENDING' | 'PROCESSING' | 'SUCCEEDED' | 'FAILED' | 'REFUNDED'
  amount: number
  currency: string
  errorCode?: string
  errorMessage?: string
  processedAt?: string
  createdAt: string
}

export interface PaymentIntent {
  clientSecret: string
  publishableKey: string
  amount: number
  currency: string
  orderId: string
}

export const paymentAPI = {
  /**
   * Create payment intent for order
   */
  async createPaymentIntent(data: {
    orderId: string
    amount: number
    currency?: string
  }): Promise<PaymentIntent> {
    const response = await api.post<PaymentIntent>('/payments/create-intent', data)
    return response.data
  },

  /**
   * Confirm payment (after Stripe confirmation)
   */
  async confirmPayment(data: {
    orderId: string
    paymentIntentId: string
    paymentMethod: string
  }): Promise<{ success: boolean; payment: Payment; order: any }> {
    const response = await api.post('/payments/confirm', data)
    return response.data
  },

  /**
   * Get payment details
   */
  async getPayment(paymentId: string): Promise<Payment> {
    const response = await api.get<Payment>(`/payments/${paymentId}`)
    return response.data
  },

  /**
   * Get order payment status
   */
  async getPaymentStatus(orderId: string): Promise<Payment> {
    const response = await api.get<Payment>(`/payments/order/${orderId}`)
    return response.data
  },

  /**
   * Refund payment
   */
  async refundPayment(paymentId: string, amount?: number): Promise<{ success: boolean; message: string }> {
    const response = await api.post(`/payments/${paymentId}/refund`, { amount })
    return response.data
  },

  /**
   * Get payment methods
   */
  async getPaymentMethods(): Promise<Array<{
    id: string
    type: string
    brand?: string
    lastFourDigits?: string
  }>> {
    const response = await api.get('/payments/methods')
    return response.data
  },

  /**
   * Add payment method
   */
  async addPaymentMethod(data: {
    type: string
    token: string
    isDefault?: boolean
  }): Promise<{ id: string; type: string }> {
    const response = await api.post('/payments/methods', data)
    return response.data
  },

  /**
   * Delete payment method
   */
  async deletePaymentMethod(methodId: string): Promise<{ success: boolean }> {
    const response = await api.delete(`/payments/methods/${methodId}`)
    return response.data
  },

  /**
   * Get payment history
   */
  async getPaymentHistory(page = 0, size = 20): Promise<{
    payments: Payment[]
    total: number
  }> {
    const response = await api.get(`/payments/history?page=${page}&size=${size}`)
    return response.data
  },

  /**
   * Validate coupon code
   */
  async validateCoupon(code: string, cartTotal: number): Promise<{
    valid: boolean
    discount: number
    discountType: string
    message?: string
  }> {
    const response = await api.post('/payments/validate-coupon', {
      code,
      cartTotal
    })
    return response.data
  }
}

export default paymentAPI
