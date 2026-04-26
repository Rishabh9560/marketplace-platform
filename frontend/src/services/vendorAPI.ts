import api from './authAPI'

export interface Vendor {
  id: string
  userId: string
  businessName: string
  gstin?: string
  pan?: string
  bankAccount?: string
  ifscCode?: string
  kycStatus: 'PENDING' | 'UNDER_REVIEW' | 'APPROVED' | 'REJECTED'
  kycRejectionReason?: string
  commissionRate: number
  totalRevenue: number
  isActive: boolean
  approvedAt?: string
  approvedBy?: string
  createdAt: string
  updatedAt: string
}

export interface VendorDashboard {
  totalProducts: number
  totalOrders: number
  totalRevenue: number
  pendingOrders: number
  lowStockItems: number
  revenueThisMonth: number
  averageOrderValue: number
  customerRating: number
}

export interface VendorProduct {
  id: string
  name: string
  sku: string
  price: number
  stock: number
  status: string
  soldCount: number
  rating: number
}

export interface VendorOrder {
  id: string
  orderNumber: string
  customerName: string
  totalAmount: number
  status: string
  createdAt: string
}

export interface VendorPayout {
  id: string
  amount: number
  status: 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED'
  periodStart: string
  periodEnd: string
  transactionReference?: string
  processedAt?: string
  createdAt: string
}

export const vendorAPI = {
  /**
   * Register as vendor
   */
  async registerVendor(data: {
    businessName: string
    gstin?: string
    pan?: string
    bankAccount?: string
    ifscCode?: string
  }): Promise<Vendor> {
    const response = await api.post<Vendor>('/vendors/register', data)
    return response.data
  },

  /**
   * Get vendor profile
   */
  async getVendorProfile(): Promise<Vendor> {
    const response = await api.get<Vendor>('/vendors/profile')
    return response.data
  },

  /**
   * Update vendor profile
   */
  async updateVendorProfile(data: Partial<Vendor>): Promise<Vendor> {
    const response = await api.put<Vendor>('/vendors/profile', data)
    return response.data
  },

  /**
   * Get vendor dashboard metrics
   */
  async getVendorDashboard(): Promise<VendorDashboard> {
    const response = await api.get<VendorDashboard>('/vendors/dashboard')
    return response.data
  },

  /**
   * Get vendor products
   */
  async getVendorProducts(page = 0, size = 20): Promise<{
    products: VendorProduct[]
    total: number
  }> {
    const response = await api.get(`/vendors/products?page=${page}&size=${size}`)
    return response.data
  },

  /**
   * Get vendor orders
   */
  async getVendorOrders(page = 0, size = 20, status?: string): Promise<{
    orders: VendorOrder[]
    total: number
  }> {
    let url = `/vendors/orders?page=${page}&size=${size}`
    if (status) url += `&status=${status}`
    const response = await api.get(url)
    return response.data
  },

  /**
   * Get vendor statistics
   */
  async getVendorStatistics(period = '30d'): Promise<{
    revenue: number[]
    orders: number[]
    sales: number[]
    dates: string[]
  }> {
    const response = await api.get(`/vendors/statistics?period=${period}`)
    return response.data
  },

  /**
   * Get payout history
   */
  async getPayoutHistory(page = 0, size = 20): Promise<{
    payouts: VendorPayout[]
    total: number
  }> {
    const response = await api.get(`/vendors/payouts?page=${page}&size=${size}`)
    return response.data
  },

  /**
   * Request payout
   */
  async requestPayout(amount: number): Promise<VendorPayout> {
    const response = await api.post<VendorPayout>('/vendors/payouts/request', {
      amount
    })
    return response.data
  },

  /**
   * Get KYC status
   */
  async getKycStatus(): Promise<{
    status: string
    rejectionReason?: string
    documents?: Array<{ type: string; status: string; url: string }>
  }> {
    const response = await api.get('/vendors/kyc/status')
    return response.data
  },

  /**
   * Upload KYC documents
   */
  async uploadKycDocuments(
    documents: Array<{ type: string; file: File }>
  ): Promise<{ success: boolean; message: string }> {
    const formData = new FormData()
    documents.forEach(({ type, file }) => {
      formData.append('documents', file)
      formData.append('types', type)
    })

    const response = await api.post('/vendors/kyc/documents', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    return response.data
  },

  /**
   * Submit KYC for approval
   */
  async submitKycForApproval(): Promise<{ success: boolean; message: string }> {
    const response = await api.post('/vendors/kyc/submit')
    return response.data
  },

  /**
   * Get available commission rates
   */
  async getCommissionRates(): Promise<Array<{
    category: string
    rate: number
    description: string
  }>> {
    const response = await api.get('/vendors/commission-rates')
    return response.data
  },

  /**
   * Get vendor settlement report
   */
  async getSettlementReport(startDate: string, endDate: string): Promise<{
    totalSales: number
    totalCommission: number
    totalRefunds: number
    netAmount: number
    breakdown: Array<{ category: string; sales: number; commission: number }>
  }> {
    const response = await api.get(
      `/vendors/settlement-report?startDate=${startDate}&endDate=${endDate}`
    )
    return response.data
  }
}

export default vendorAPI
