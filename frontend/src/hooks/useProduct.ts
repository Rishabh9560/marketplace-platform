import { useCallback } from 'react'
import { useProductStore, type Product } from '../store/productStore'

export interface UseProductReturn {
  products: Product[]
  currentProduct: Product | null
  totalProducts: number
  isLoading: boolean
  error: string | null
  filters: Record<string, unknown>
  facets: Record<string, unknown>
  searchProducts: (filters: Record<string, unknown>) => Promise<void>
  getProductById: (productId: string) => Promise<Product | null>
  getSimilarProducts: (productId: string, limit?: number) => Promise<Product[]>
  autocompleteSearch: (query: string, limit?: number) => Promise<Array<{ id: string; name: string; price: number }>>
  setFilters: (filters: Record<string, unknown>) => void
  clearFilters: () => void
}

/**
 * Custom hook for product search and browsing.
 * Wraps productStore with convenience methods.
 */
export function useProduct(): UseProductReturn {
  const {
    products,
    currentProduct,
    totalProducts,
    isLoading,
    error,
    filters,
    facets,
    searchProducts,
    getProductById,
    getSimilarProducts,
    autocompleteSearch,
    setFilters,
    clearFilters,
  } = useProductStore()

  const searchProductsMemo = useCallback(searchProducts, [searchProducts])
  const getProductByIdMemo = useCallback(getProductById, [getProductById])
  const getSimilarProductsMemo = useCallback(getSimilarProducts, [getSimilarProducts])
  const autocompleteSearchMemo = useCallback(autocompleteSearch, [autocompleteSearch])
  const setFiltersMemo = useCallback(setFilters, [setFilters])
  const clearFiltersMemo = useCallback(clearFilters, [clearFilters])

  return {
    products,
    currentProduct,
    totalProducts,
    isLoading,
    error,
    filters,
    facets,
    searchProducts: searchProductsMemo,
    getProductById: getProductByIdMemo,
    getSimilarProducts: getSimilarProductsMemo,
    autocompleteSearch: autocompleteSearchMemo,
    setFilters: setFiltersMemo,
    clearFilters: clearFiltersMemo,
  }
}
