import { create } from 'zustand'
import { productAPI, Product } from '@/services/productAPI'

interface ProductFilters {
  query?: string
  categoryId?: string
  brand?: string
  minPrice?: number
  maxPrice?: number
  minRating?: number
  vendorId?: string
  sortBy?: string
  page: number
  size: number
}

interface ProductStore {
  products: Product[]
  currentProduct: Product | null
  isLoading: boolean
  error: string | null
  totalProducts: number
  filters: ProductFilters
  facets: Record<string, Record<string, number>>

  // Actions
  searchProducts: (filters: Partial<ProductFilters>) => Promise<void>
  getProductById: (productId: string) => Promise<void>
  getSimilarProducts: (productId: string) => Promise<Product[]>
  autocompleteSearch: (query: string) => Promise<any[]>
  setFilters: (filters: Partial<ProductFilters>) => void
  clearFilters: () => void
  setError: (error: string | null) => void
  clearCurrentProduct: () => void
}

export const useProductStore = create<ProductStore>((set, get) => ({
  products: [],
  currentProduct: null,
  isLoading: false,
  error: null,
  totalProducts: 0,
  filters: {
    query: '',
    page: 0,
    size: 20
  },
  facets: {},

  searchProducts: async (filters: Partial<ProductFilters>) => {
    try {
      set({ isLoading: true, error: null })
      const currentFilters = { ...get().filters, ...filters, page: filters.page || 0 }
      
      const response = await productAPI.searchProducts(currentFilters)
      
      set({
        products: response.products,
        totalProducts: response.total,
        filters: currentFilters,
        facets: response.facets || {}
      })
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Search failed'
      set({ error: errorMessage })
    } finally {
      set({ isLoading: false })
    }
  },

  getProductById: async (productId: string) => {
    try {
      set({ isLoading: true, error: null })
      const product = await productAPI.getProductById(productId)
      set({ currentProduct: product })
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Failed to fetch product'
      set({ error: errorMessage })
    } finally {
      set({ isLoading: false })
    }
  },

  getSimilarProducts: async (productId: string) => {
    try {
      const products = await productAPI.getSimilarProducts(productId)
      return products
    } catch (error) {
      console.error('Failed to fetch similar products:', error)
      return []
    }
  },

  autocompleteSearch: async (query: string) => {
    try {
      if (query.length < 2) return []
      const results = await productAPI.autocomplete(query)
      return results
    } catch (error) {
      console.error('Autocomplete error:', error)
      return []
    }
  },

  setFilters: (filters: Partial<ProductFilters>) => {
    const currentFilters = { ...get().filters, ...filters, page: 0 }
    set({ filters: currentFilters })
  },

  clearFilters: () => {
    set({
      filters: {
        query: '',
        page: 0,
        size: 20
      },
      products: [],
      totalProducts: 0,
      facets: {}
    })
  },

  setError: (error: string | null) => set({ error }),
  
  clearCurrentProduct: () => set({ currentProduct: null })
}))
