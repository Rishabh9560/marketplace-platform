import api from './authAPI'

export interface Product {
  id: string
  name: string
  slug: string
  description: string
  brand?: string
  price: number
  compareAtPrice?: number
  imageUrl: string
  vendorId: string
  vendorName: string
  categoryId: string
  categoryName: string
  averageRating: number
  reviewCount: number
  status: 'DRAFT' | 'PENDING_APPROVAL' | 'APPROVED' | 'REJECTED' | 'ARCHIVED'
  isFeatured: boolean
  variants: ProductVariant[]
  tags: string[]
  attributes?: Record<string, string>
}

export interface ProductVariant {
  id: string
  productId: string
  sku: string
  name?: string
  price: number
  compareAtPrice?: number
  costPrice?: number
  stockQuantity: number
  lowStockThreshold: number
  imageUrls: string[]
  attributes: Record<string, string>
  isActive: boolean
}

export interface SearchResponse {
  products: Product[]
  total: number
  page: number
  size: number
  totalPages: number
  facets?: Record<string, Record<string, number>>
}

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

export const productAPI = {
  /**
   * Search products with filters
   */
  async searchProducts(filters: {
    query?: string
    categoryId?: string
    brand?: string
    minPrice?: number
    maxPrice?: number
    minRating?: number
    vendorId?: string
    sortBy?: string
    page?: number
    size?: number
  }): Promise<SearchResponse> {
    const params = new URLSearchParams()

    if (filters.query) params.append('q', filters.query)
    if (filters.categoryId) params.append('category', filters.categoryId)
    if (filters.brand) params.append('brand', filters.brand)
    if (filters.minPrice !== undefined) params.append('minPrice', filters.minPrice.toString())
    if (filters.maxPrice !== undefined) params.append('maxPrice', filters.maxPrice.toString())
    if (filters.minRating !== undefined) params.append('minRating', filters.minRating.toString())
    if (filters.vendorId) params.append('vendorId', filters.vendorId)
    if (filters.sortBy) params.append('sort', filters.sortBy)
    params.append('page', (filters.page || 0).toString())
    params.append('size', (filters.size || 20).toString())

    const response = await api.get<SearchResponse>(`/search/products?${params}`)
    return response.data
  },

  /**
   * Get single product by ID
   */
  async getProductById(productId: string): Promise<Product> {
    const response = await api.get<Product>(`/products/${productId}`)
    return response.data
  },

  /**
   * Get product variants
   */
  async getProductVariants(productId: string): Promise<ProductVariant[]> {
    const response = await api.get<ProductVariant[]>(`/products/${productId}/variants`)
    return response.data
  },

  /**
   * Get similar products
   */
  async getSimilarProducts(productId: string, limit = 8): Promise<Product[]> {
    const response = await api.get<{ products: Product[] }>(
      `/search/products/${productId}/similar?limit=${limit}`
    )
    return response.data.products
  },

  /**
   * Autocomplete search
   */
  async autocomplete(
    query: string,
    limit = 5
  ): Promise<Array<{ id: string; name: string; imageUrl: string; price: number }>> {
    const response = await api.get(
      `/search/autocomplete?q=${encodeURIComponent(query)}&limit=${limit}`
    )
    return response.data
  },

  /**
   * Get featured products
   */
  async getFeaturedProducts(limit = 10): Promise<Product[]> {
    const response = await api.get<{ products: Product[] }>(`/products/featured?limit=${limit}`)
    return response.data.products
  },

  /**
   * Get recently viewed products
   */
  getRcentlyViewedProducts(): Product[] {
    const viewed = localStorage.getItem('recentlyViewed')
    return viewed ? JSON.parse(viewed) : []
  },

  /**
   * Add to recently viewed
   */
  addToRecentlyViewed(product: Product): void {
    const viewed = JSON.parse(localStorage.getItem('recentlyViewed') || '[]') as Product[]
    const filtered = viewed.filter((p) => p.id !== product.id)
    const updated = [product, ...filtered].slice(0, 20)
    localStorage.setItem('recentlyViewed', JSON.stringify(updated))
  },

  /**
   * Create product (vendor only)
   */
  async createProduct(data: Partial<Product>): Promise<Product> {
    const response = await api.post<Product>('/products', data)
    return response.data
  },

  /**
   * Update product (vendor only)
   */
  async updateProduct(productId: string, data: Partial<Product>): Promise<Product> {
    const response = await api.put<Product>(`/products/${productId}`, data)
    return response.data
  },

  /**
   * Delete product (vendor only)
   */
  async deleteProduct(productId: string): Promise<void> {
    await api.delete(`/products/${productId}`)
  },

  /**
   * Upload product images
   */
  async uploadProductImages(
    productId: string,
    files: File[]
  ): Promise<{ urls: string[]; imageIds: string[] }> {
    const formData = new FormData()
    files.forEach((file) => formData.append('files', file))

    const response = await api.post(
      `/products/${productId}/images`,
      formData,
      {
        headers: { 'Content-Type': 'multipart/form-data' }
      }
    )
    return response.data
  }
}

export default productAPI
