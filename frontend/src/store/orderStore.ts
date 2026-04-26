import { create } from 'zustand'
import { orderAPI, Order } from '@/services/orderAPI'

interface OrderStore {
  orders: Order[]
  currentOrder: Order | null
  isLoading: boolean
  error: string | null
  totalOrders: number
  currentPage: number

  // Actions
  fetchOrders: (page?: number, size?: number) => Promise<void>
  getOrderById: (orderId: string) => Promise<void>
  createOrder: (orderData: any) => Promise<string>
  cancelOrder: (orderId: string, reason: string) => Promise<void>
  setError: (error: string | null) => void
  clearCurrentOrder: () => void
  subscribeToOrderUpdates: (orderId: string, callback: (order: Order) => void) => void
}

export const useOrderStore = create<OrderStore>((set, get) => ({
  orders: [],
  currentOrder: null,
  isLoading: false,
  error: null,
  totalOrders: 0,
  currentPage: 0,

  fetchOrders: async (page = 0, size = 10) => {
    try {
      set({ isLoading: true, error: null })
      const response = await orderAPI.getOrders(page, size)
      set({
        orders: response.orders,
        totalOrders: response.total,
        currentPage: page
      })
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Failed to fetch orders'
      set({ error: errorMessage })
    } finally {
      set({ isLoading: false })
    }
  },

  getOrderById: async (orderId: string) => {
    try {
      set({ isLoading: true, error: null })
      const order = await orderAPI.getOrderById(orderId)
      set({ currentOrder: order })
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Failed to fetch order'
      set({ error: errorMessage })
    } finally {
      set({ isLoading: false })
    }
  },

  createOrder: async (orderData: any) => {
    try {
      set({ isLoading: true, error: null })
      const response = await orderAPI.createOrder(orderData)
      set({ currentOrder: response.order })
      return response.order.id
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Failed to create order'
      set({ error: errorMessage })
      throw error
    } finally {
      set({ isLoading: false })
    }
  },

  cancelOrder: async (orderId: string, reason: string) => {
    try {
      set({ isLoading: true, error: null })
      await orderAPI.cancelOrder(orderId, reason)
      
      // Update current order if it matches
      const { currentOrder } = get()
      if (currentOrder?.id === orderId) {
        set({ currentOrder: { ...currentOrder, status: 'CANCELLED' } })
      }
      
      // Refresh orders list
      get().fetchOrders()
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Failed to cancel order'
      set({ error: errorMessage })
      throw error
    } finally {
      set({ isLoading: false })
    }
  },

  setError: (error: string | null) => set({ error }),
  
  clearCurrentOrder: () => set({ currentOrder: null }),

  subscribeToOrderUpdates: (orderId: string, callback: (order: Order) => void) => {
    // WebSocket connection for real-time updates
    const ws = new WebSocket(
      `${import.meta.env.VITE_WS_URL}/ws/orders/${orderId}?token=${localStorage.getItem('accessToken')}`
    )
    
    ws.onmessage = (event) => {
      const data = JSON.parse(event.data)
      callback(data)
      set({ currentOrder: data })
    }
    
    ws.onerror = (error) => {
      console.error('WebSocket error:', error)
      set({ error: 'Failed to connect to order updates' })
    }
    
    return () => ws.close()
  }
}))
