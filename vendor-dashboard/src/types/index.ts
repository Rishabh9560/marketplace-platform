// Vendor Profile Types
export interface VendorProfile {
  id: string
  userId: string
  businessName: string
  businessEmail: string
  businessPhone: string
  taxId?: string
  businessLicenseNumber?: string
  bankAccountNumber?: string
  commissionRate: number
  kycStatus: 'PENDING' | 'SUBMITTED' | 'VERIFIED' | 'REJECTED' | 'SUSPENDED'
  isActive?: boolean
  isSuspended?: boolean
  totalEarnings: number
  availableBalance?: number
  averageRating: number
  totalReviews: number
  totalSales?: number
  accountStatus?: 'ACTIVE' | 'INACTIVE' | 'SUSPENDED'
  createdAt: string
  updatedAt: string
}

// Product Listing Types
export interface ProductListing {
  id: string
  vendorId: string
  productId: string
  productName: string
  sku: string
  description: string
  vendorPrice: number
  discountPercentage: number
  shippingCost: number
  quantityAvailable: number
  quantityReserved: number
  reorderLevel: number
  status: 'DRAFT' | 'ACTIVE' | 'INACTIVE' | 'DELISTED' | 'SUSPENDED'
  viewCount: number
  listedAt?: string
  delistedAt?: string
  createdAt: string
  updatedAt: string
}

// Payout Types
export interface VendorPayoutRecord {
  id: string
  vendorId: string
  payoutPeriod: string
  totalSalesAmount: number
  commissionRate: number
  commissionDeducted: number
  netPayoutAmount: number
  status: 'PENDING' | 'SCHEDULED' | 'PROCESSING' | 'COMPLETED' | 'FAILED' | 'CANCELLED' | 'ON_HOLD'
  transactionId?: string
  scheduledPayoutDate?: string
  actualPayoutDate?: string
  retryCount: number
  createdAt: string
  updatedAt: string
}

// Statistics Types
export interface VendorStatistics {
  vendorId: string
  totalSales: number
  totalEarnings: number
  averageRating: number
  totalReviews: number
  activeListings: number
  performanceScore: number
  isPremium: boolean
  lastUpdated: string
}

// KYC Types
export interface KYCSubmissionRequest {
  vendorId?: string
  businessLicenseNumber: string
  taxId: string
  bankAccountNumber: string
  documents?: string[]
}

// Inventory Update Types
export interface InventoryUpdateDTO {
  quantityAvailable: number
  reorderLevel: number
  reorderQuantity: number
}

// Price Update Types
export interface PriceUpdateDTO {
  vendorPrice: number
  discountPercentage: number
  shippingCost: number
}

// Auth Types
export interface AuthResponse {
  token: string
  vendor: VendorProfile
}

// Dashboard Summary Types
export interface DashboardSummary {
  totalSales: number
  totalEarnings: number
  activeListings: number
  pendingPayouts: number
  kycStatus: string
  performanceScore: number
  averageRating: number
  totalOrders?: number
  averageOrderValue?: number
  customerSatisfaction?: number
  fulfillmentRate?: number
  returnRate?: number
}
