import { useCallback } from 'react'
import { useOrderStore, type Order } from '../store/orderStore'

export interface UseOrderReturn {
  orders: Order[]
  currentOrder: Order | null
  totalOrders: number
  isLoading: boolean
  error: string | null
  currentPage: number
  fetchOrders: (page: number, size: number) => Promise<void>
  getOrderById: (orderId: string) => Promise<Order | null>
  createOrder: (orderData: unknown) => Promise<Order>
  cancelOrder: (orderId: string, reason: string) => Promise<void>
  subscribeToOrderUpdates: (orderId: string, callback: (order: Order) => void) => void
}

/**
 * Custom hook for order operations.
 * Wraps orderStore with convenience methods.
 */
export function useOrder(): UseOrderReturn {
  const {
    orders,
    currentOrder,
    totalOrders,
    isLoading,
    error,
    currentPage,
    fetchOrders,
    getOrderById,
    createOrder,
    cancelOrder,
    subscribeToOrderUpdates,
  } = useOrderStore()

  const fetchOrdersMemo = useCallback(fetchOrders, [fetchOrders])
  const getOrderByIdMemo = useCallback(getOrderById, [getOrderById])
  const createOrderMemo = useCallback(createOrder, [createOrder])
  const cancelOrderMemo = useCallback(cancelOrder, [cancelOrder])
  const subscribeToOrderUpdatesMemo = useCallback(subscribeToOrderUpdates, [subscribeToOrderUpdates])

  return {
    orders,
    currentOrder,
    totalOrders,
    isLoading,
    error,
    currentPage,
    fetchOrders: fetchOrdersMemo,
    getOrderById: getOrderByIdMemo,
    createOrder: createOrderMemo,
    cancelOrder: cancelOrderMemo,
    subscribeToOrderUpdates: subscribeToOrderUpdatesMemo,
  }
}
