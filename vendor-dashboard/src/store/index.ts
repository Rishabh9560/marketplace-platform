import { create } from 'zustand'
import { VendorProfile, DashboardSummary } from '@/types'

interface AuthStore {
  isAuthenticated: boolean
  vendor: VendorProfile | null
  token: string | null
  login: (token: string, vendor: VendorProfile) => void
  logout: () => void
  setVendor: (vendor: VendorProfile) => void
}

export const useAuthStore = create<AuthStore>((set) => ({
  isAuthenticated: !!localStorage.getItem('auth_token'),
  vendor: null,
  token: localStorage.getItem('auth_token'),
  login: (token: string, vendor: VendorProfile) => {
    localStorage.setItem('auth_token', token)
    set({ isAuthenticated: true, token, vendor })
  },
  logout: () => {
    localStorage.removeItem('auth_token')
    set({ isAuthenticated: false, token: null, vendor: null })
  },
  setVendor: (vendor: VendorProfile) => {
    set({ vendor })
  },
}))

interface DashboardStore {
  summary: DashboardSummary | null
  loading: boolean
  error: string | null
  setSummary: (summary: DashboardSummary) => void
  setLoading: (loading: boolean) => void
  setError: (error: string | null) => void
}

export const useDashboardStore = create<DashboardStore>((set) => ({
  summary: null,
  loading: false,
  error: null,
  setSummary: (summary: DashboardSummary) => set({ summary }),
  setLoading: (loading: boolean) => set({ loading }),
  setError: (error: string | null) => set({ error }),
}))
