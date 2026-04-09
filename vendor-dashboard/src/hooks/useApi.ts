import { useState, useCallback } from 'react'
import { apiClient, ApiResponse } from '@/lib/apiClient'

interface UseApiState<T> {
  data: T | null
  loading: boolean
  error: Error | null
}

export function useApi<T>(initialData: T | null = null) {
  const [state, setState] = useState<UseApiState<T>>({
    data: initialData,
    loading: false,
    error: null,
  })

  const execute = useCallback(
    async (promise: Promise<ApiResponse<T>>): Promise<T | null> => {
      setState({ data: null, loading: true, error: null })
      try {
        const response = await promise
        if (response.success && response.data) {
          setState({ data: response.data, loading: false, error: null })
          return response.data
        } else {
          const error = new Error(response.message || 'API Error')
          setState({ data: null, loading: false, error })
          return null
        }
      } catch (err) {
        const error = err instanceof Error ? err : new Error('Unknown error occurred')
        setState({ data: null, loading: false, error })
        throw error
      }
    },
    []
  )

  return { ...state, execute }
}

// Vendor Profile Hooks
export function useVendorProfile() {
  const api = useApi<any>(null)

  const fetchVendor = useCallback(
    async (vendorId: string) => {
      return api.execute(apiClient.get(`/vendors/${vendorId}`))
    },
    [api]
  )

  const updateVendor = useCallback(
    async (vendorId: string, data: any) => {
      return api.execute(apiClient.put(`/vendors/${vendorId}`, data))
    },
    [api]
  )

  return { ...api, fetchVendor, updateVendor }
}

// Product Listing Hooks
export function useProductListings() {
  const api = useApi<any>(null)

  const fetchListings = useCallback(
    async (vendorId: string, page = 0, size = 20) => {
      return api.execute(apiClient.get(`/listings/vendor/${vendorId}?page=${page}&size=${size}`))
    },
    [api]
  )

  const createListing = useCallback(
    async (listingData: any) => {
      return api.execute(apiClient.post('/listings', listingData))
    },
    [api]
  )

  const updateListing = useCallback(
    async (listingId: string, listingData: any) => {
      return api.execute(apiClient.put(`/listings/${listingId}`, listingData))
    },
    [api]
  )

  const publishListing = useCallback(
    async (listingId: string) => {
      return api.execute(apiClient.post(`/listings/${listingId}/publish`, {}))
    },
    [api]
  )

  return { ...api, fetchListings, createListing, updateListing, publishListing }
}

// Payout Hooks
export function usePayouts() {
  const api = useApi<any>(null)

  const fetchPayouts = useCallback(
    async (vendorId: string, page = 0, size = 20) => {
      return api.execute(apiClient.get(`/payouts/vendor/${vendorId}?page=${page}&size=${size}`))
    },
    [api]
  )

  const getPayoutSummary = useCallback(
    async (vendorId: string) => {
      return api.execute(apiClient.get(`/payouts/vendor/${vendorId}/summary`))
    },
    [api]
  )

  return { ...api, fetchPayouts, getPayoutSummary }
}

// KYC Hooks
export function useKYC() {
  const api = useApi<any>(null)

  const submitKYC = useCallback(
    async (kycData: any) => {
      return api.execute(apiClient.post('/kyc/submit', kycData))
    },
    [api]
  )

  const getKYCStatus = useCallback(
    async (vendorId: string) => {
      return api.execute(apiClient.get(`/kyc/${vendorId}/status`))
    },
    [api]
  )

  return { ...api, submitKYC, getKYCStatus }
}

// Statistics Hooks
export function useStatistics() {
  const api = useApi<any>(null)

  const getVendorStats = useCallback(
    async (vendorId: string) => {
      return api.execute(apiClient.get(`/statistics/vendor/${vendorId}`))
    },
    [api]
  )

  const getPerformanceScore = useCallback(
    async (vendorId: string) => {
      return api.execute(apiClient.get(`/statistics/vendor/${vendorId}/performance-score`))
    },
    [api]
  )

  return { ...api, getVendorStats, getPerformanceScore }
}
