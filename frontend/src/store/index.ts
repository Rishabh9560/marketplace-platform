import { create } from 'zustand'
import { Customer } from '@/types'
import { cartAPI, Cart } from '@/services/cartAPI'

interface CartStore {
  cart: Cart | null
  loading: boolean
  error: string | null
  itemCount: number

  // Actions
  fetchCart: () => Promise<void>
  addToCart: (variantId: string, quantity: number, vendorId: string) => Promise<void>
  updateQuantity: (itemId: string, quantity: number) => Promise<void>
  removeFromCart: (itemId: string) => Promise<void>
  clearCart: () => Promise<void>
  setError: (error: string | null) => void
  setLoading: (loading: boolean) => void
}

interface Order {
  id: string
  date: string
  total: number
  status: 'PENDING' | 'CONFIRMED' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED'
  items: number
  trackingId?: string
  items_detail?: { name: string; quantity: number; price: number }[]
}

interface OrderStore {
  orders: Order[]
  addOrder: (order: Order) => void
  getOrders: () => Order[]
}

export const useOrderStore = create<OrderStore>((set, get) => ({
  orders: JSON.parse(localStorage.getItem('orders') || '[]'),
  addOrder: (order) => {
    const orders = get().orders
    const updated = [...orders, order]
    localStorage.setItem('orders', JSON.stringify(updated))
    set({ orders: updated })
  },
  getOrders: () => get().orders,
}))

export const useCartStore = create<CartStore>((set) => ({
  cart: null,
  loading: false,
  error: null,
  itemCount: 0,

  fetchCart: async () => {
    try {
      set({ loading: true, error: null })
      const cart = await cartAPI.getCart()
      set({ cart, itemCount: cart.itemCount })
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Failed to fetch cart'
      set({ error: errorMessage, cart: null, itemCount: 0 })
    } finally {
      set({ loading: false })
    }
  },

  addToCart: async (variantId: string, quantity: number, vendorId: string) => {
    try {
      set({ loading: true, error: null })
      const cart = await cartAPI.addToCart({
        variantId,
        quantity,
        vendorId
      })
      set({ cart, itemCount: cart.itemCount })
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Failed to add to cart'
      set({ error: errorMessage })
      throw error
    } finally {
      set({ loading: false })
    }
  },

  updateQuantity: async (itemId: string, quantity: number) => {
    try {
      set({ loading: true, error: null })
      const cart = await cartAPI.updateQuantity({ itemId, quantity })
      set({ cart, itemCount: cart.itemCount })
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Failed to update quantity'
      set({ error: errorMessage })
      throw error
    } finally {
      set({ loading: false })
    }
  },

  removeFromCart: async (itemId: string) => {
    try {
      set({ loading: true, error: null })
      const cart = await cartAPI.removeFromCart(itemId)
      set({ cart, itemCount: cart.itemCount })
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Failed to remove item'
      set({ error: errorMessage })
      throw error
    } finally {
      set({ loading: false })
    }
  },

  clearCart: async () => {
    try {
      set({ loading: true, error: null })
      await cartAPI.clearCart()
      set({ cart: null, itemCount: 0 })
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Failed to clear cart'
      set({ error: errorMessage })
      throw error
    } finally {
      set({ loading: false })
    }
  },

  setError: (error: string | null) => set({ error }),
  setLoading: (loading: boolean) => set({ loading })
}))

interface AuthStore {
  isAuthenticated: boolean
  customer: Customer | null
  token: string | null
  login: (token: string, customer: Customer) => void
  logout: () => void
}

export const useAuthStore = create<AuthStore>((set) => ({
  isAuthenticated: !!localStorage.getItem('customer_token'),
  customer: null,
  token: localStorage.getItem('customer_token'),
  login: (token, customer) => {
    localStorage.setItem('customer_token', token)
    set({ isAuthenticated: true, token, customer })
  },
  logout: () => {
    localStorage.removeItem('customer_token')
    set({ isAuthenticated: false, token: null, customer: null })
  },
}))

interface WishlistStore {
  items: string[]
  addItem: (productId: string) => void
  removeItem: (productId: string) => void
  isInWishlist: (productId: string) => boolean
}

export const useWishlistStore = create<WishlistStore>((set, get) => ({
  items: JSON.parse(localStorage.getItem('wishlist') || '[]'),
  addItem: (productId) => {
    const items = get().items
    if (!items.includes(productId)) {
      const updated = [...items, productId]
      localStorage.setItem('wishlist', JSON.stringify(updated))
      set({ items: updated })
    }
  },
  removeItem: (productId) => {
    const updated = get().items.filter((id) => id !== productId)
    localStorage.setItem('wishlist', JSON.stringify(updated))
    set({ items: updated })
  },
  isInWishlist: (productId) => get().items.includes(productId),
}))
