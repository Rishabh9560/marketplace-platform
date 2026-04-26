import api from './authAPI'

export interface Order {
  id: string
  orderNumber: string
  customerId: string
  shippingAddressId: string
  status: 'PENDING_PAYMENT' | 'PAID' | 'PROCESSING' | 'PACKED' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED' | 'REFUNDED'
  paymentStatus: 'PENDING' | 'AUTHORIZED' | 'CAPTURED' | 'FAILED' | 'REFUNDED'
  subtotal: number
  discountAmount: number
  shippingAmount: number
  taxAmount: number
  totalAmount: number
  currency: string
  items: OrderItem[]
  notes?: string
  estimatedDeliveryDate?: string
  deliveredAt?: string
  createdAt: string
  updatedAt: string
}

export interface OrderItem {
  id: string
  productId: string
  variantId: string
  vendorId: string
  quantity: number
  unitPrice: number
  discountAmount: number
  subtotal: number
  itemStatus: string
  trackingNumber?: string
  courierName?: string
  vendorCommission: number
}

export interface CreateOrderRequest {
  cartItems: {
    productId: string
    variantId: string
    quantity: number
    price: number
    vendorId: string
  }[]
  shippingAddressId: string
  couponCode?: string
  notes?: string
}

export const orderAPI = {
  /**
   * Get all orders for current user
   */
  async getOrders(page = 0, size = 10): Promise<{ orders: Order[]; total: number }> {
    const response = await api.get<{ orders: Order[]; total: number }>(
      `/orders?page=${page}&size=${size}`
    )
    return response.data
  },

  /**
   * Get single order by ID
   */
  async getOrderById(orderId: string): Promise<Order> {
    const response = await api.get<Order>(`/orders/${orderId}`)
    return response.data
  },

  /**
   * Create new order
   */
  async createOrder(data: CreateOrderRequest): Promise<{ order: Order; clientSecret: string }> {
    const response = await api.post<{ order: Order; clientSecret: string }>('/orders', data)
    return response.data
  },

  /**
   * Confirm payment for order
   */
  async confirmPayment(
    orderId: string,
    paymentIntentId: string
  ): Promise<{ success: boolean; order: Order }> {
    const response = await api.post(`/orders/${orderId}/confirm-payment`, {
      paymentIntentId
    })
    return response.data
  },

  /**
   * Cancel order
   */
  async cancelOrder(orderId: string, reason: string): Promise<Order> {
    const response = await api.post<Order>(`/orders/${orderId}/cancel`, { reason })
    return response.data
  },

  /**
   * Get order tracking
   */
  async getOrderTracking(orderId: string): Promise<{
    status: string
    updates: Array<{
      status: string
      timestamp: string
      message: string
      location?: string
    }>
  }> {
    const response = await api.get(`/orders/${orderId}/tracking`)
    return response.data
  },

  /**
   * Request refund
   */
  async requestRefund(
    orderId: string,
    reason: string
  ): Promise<{ success: boolean; message: string }> {
    const response = await api.post(`/orders/${orderId}/refund-request`, { reason })
    return response.data
  },

  /**
   * Get order invoice
   */
  async getInvoice(orderId: string): Promise<Blob> {
    const response = await api.get(`/orders/${orderId}/invoice`, {
      responseType: 'blob'
    })
    return response.data
  }
}

export default orderAPI
