import { create } from 'zustand'
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
    } finally {
      set({ loading: false })
    }
  },

  setError: (error: string | null) => set({ error }),
  setLoading: (loading: boolean) => set({ loading })
}))
