import { create } from 'zustand'
import { vendorAPI, Vendor } from '@/services/vendorAPI'

interface VendorDashboard {
  totalProducts: number
  totalOrders: number
  totalRevenue: number
  pendingOrders: number
  lowStockItems: number
  revenueThisMonth: number
}

interface VendorStore {
  vendor: Vendor | null
  vendorDashboard: VendorDashboard | null
  vendorProducts: any[]
  vendorOrders: any[]
  payouts: any[]
  isLoading: boolean
  error: string | null

  // Actions
  registerVendor: (vendorData: any) => Promise<void>
  getVendorProfile: () => Promise<void>
  getVendorDashboard: () => Promise<void>
  getVendorProducts: (page?: number, size?: number) => Promise<void>
  getVendorOrders: (page?: number, size?: number) => Promise<void>
  requestPayout: (amount: number) => Promise<void>
  updateVendorProfile: (data: any) => Promise<void>
  setError: (error: string | null) => void
  clearVendor: () => void
}

export const useVendorStore = create<VendorStore>((set, get) => ({
  vendor: null,
  vendorDashboard: null,
  vendorProducts: [],
  vendorOrders: [],
  payouts: [],
  isLoading: false,
  error: null,

  registerVendor: async (vendorData: any) => {
    try {
      set({ isLoading: true, error: null })
      const vendor = await vendorAPI.registerVendor(vendorData)
      set({ vendor })
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Vendor registration failed'
      set({ error: errorMessage })
      throw error
    } finally {
      set({ isLoading: false })
    }
  },

  getVendorProfile: async () => {
    try {
      set({ isLoading: true, error: null })
      const vendor = await vendorAPI.getVendorProfile()
      set({ vendor })
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Failed to fetch vendor profile'
      set({ error: errorMessage })
    } finally {
      set({ isLoading: false })
    }
  },

  getVendorDashboard: async () => {
    try {
      set({ isLoading: true, error: null })
      const dashboard = await vendorAPI.getVendorDashboard()
      set({ vendorDashboard: dashboard })
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Failed to fetch dashboard'
      set({ error: errorMessage })
    } finally {
      set({ isLoading: false })
    }
  },

  getVendorProducts: async (page = 0, size = 20) => {
    try {
      set({ isLoading: true, error: null })
      const products = await vendorAPI.getVendorProducts(page, size)
      set({ vendorProducts: products })
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Failed to fetch vendor products'
      set({ error: errorMessage })
    } finally {
      set({ isLoading: false })
    }
  },

  getVendorOrders: async (page = 0, size = 20) => {
    try {
      set({ isLoading: true, error: null })
      const orders = await vendorAPI.getVendorOrders(page, size)
      set({ vendorOrders: orders })
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Failed to fetch vendor orders'
      set({ error: errorMessage })
    } finally {
      set({ isLoading: false })
    }
  },

  requestPayout: async (amount: number) => {
    try {
      set({ isLoading: true, error: null })
      const payout = await vendorAPI.requestPayout(amount)
      set({ payouts: [payout, ...get().payouts] })
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Failed to request payout'
      set({ error: errorMessage })
      throw error
    } finally {
      set({ isLoading: false })
    }
  },

  updateVendorProfile: async (data: any) => {
    try {
      set({ isLoading: true, error: null })
      const vendor = await vendorAPI.updateVendorProfile(data)
      set({ vendor })
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Failed to update profile'
      set({ error: errorMessage })
      throw error
    } finally {
      set({ isLoading: false })
    }
  },

  setError: (error: string | null) => set({ error }),
  
  clearVendor: () => {
    set({
      vendor: null,
      vendorDashboard: null,
      vendorProducts: [],
      vendorOrders: [],
      payouts: []
    })
  }
}))
