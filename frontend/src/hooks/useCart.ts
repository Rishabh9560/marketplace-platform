import { useCallback } from 'react'
import { useCartStore } from '../store/cartStore'

export interface UseCartReturn {
  cart: ReturnType<typeof useCartStore>['cart']
  loading: boolean
  error: string | null
  itemCount: number
  fetchCart: () => Promise<void>
  addToCart: (variantId: string, quantity: number, vendorId: string) => Promise<void>
  updateQuantity: (itemId: string, quantity: number) => Promise<void>
  removeFromCart: (itemId: string) => Promise<void>
  clearCart: () => Promise<void>
}

/**
 * Custom hook for cart operations.
 * Wraps cartStore with convenience methods.
 */
export function useCart(): UseCartReturn {
  const { cart, loading, error, itemCount, fetchCart, addToCart, updateQuantity, removeFromCart, clearCart } =
    useCartStore()

  const fetchCartMemo = useCallback(fetchCart, [fetchCart])
  const addToCartMemo = useCallback(addToCart, [addToCart])
  const updateQuantityMemo = useCallback(updateQuantity, [updateQuantity])
  const removeFromCartMemo = useCallback(removeFromCart, [removeFromCart])
  const clearCartMemo = useCallback(clearCart, [clearCart])

  return {
    cart,
    loading,
    error,
    itemCount,
    fetchCart: fetchCartMemo,
    addToCart: addToCartMemo,
    updateQuantity: updateQuantityMemo,
    removeFromCart: removeFromCartMemo,
    clearCart: clearCartMemo,
  }
}
