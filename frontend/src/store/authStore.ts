import { create } from 'zustand'
import { authAPI, User, AuthResponse } from '@/services/authAPI'

interface AuthStore {
  user: User | null
  accessToken: string | null
  refreshToken: string | null
  isAuthenticated: boolean
  isLoading: boolean
  error: string | null

  // Actions
  login: (email: string, password: string) => Promise<void>
  signup: (email: string, password: string, fullName: string, phone?: string) => Promise<void>
  logout: () => Promise<void>
  refreshAuthToken: () => Promise<void>
  setUser: (user: User | null) => void
  clearAuth: () => void
  setError: (error: string | null) => void
  verifyEmail: (token: string) => Promise<void>
  forgotPassword: (email: string) => Promise<void>
  resetPassword: (token: string, newPassword: string) => Promise<void>
}

export const useAuthStore = create<AuthStore>((set, get) => ({
  user: null,
  accessToken: localStorage.getItem('accessToken'),
  refreshToken: localStorage.getItem('refreshToken'),
  isAuthenticated: !!localStorage.getItem('accessToken'),
  isLoading: false,
  error: null,

  login: async (email: string, password: string) => {
    try {
      set({ isLoading: true, error: null })
      const response = await authAPI.login({ email, password })
      
      // Store tokens in localStorage
      localStorage.setItem('accessToken', response.access_token)
      localStorage.setItem('refreshToken', response.refresh_token)
      
      set({
        user: {
          id: response.userId,
          email: response.email,
          fullName: response.fullName,
          phone: response.phone,
          role: response.role,
          emailVerified: response.emailVerified
        },
        accessToken: response.access_token,
        refreshToken: response.refresh_token,
        isAuthenticated: true
      })
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Login failed'
      set({ error: errorMessage, isAuthenticated: false })
      throw error
    } finally {
      set({ isLoading: false })
    }
  },

  signup: async (email: string, password: string, fullName: string, phone?: string) => {
    try {
      set({ isLoading: true, error: null })
      const response = await authAPI.signup({ email, password, fullName, phone })
      
      // Store tokens
      localStorage.setItem('accessToken', response.access_token)
      localStorage.setItem('refreshToken', response.refresh_token)
      
      set({
        user: {
          id: response.userId,
          email: response.email,
          fullName: response.fullName,
          phone: response.phone,
          role: response.role,
          emailVerified: response.emailVerified
        },
        accessToken: response.access_token,
        refreshToken: response.refresh_token,
        isAuthenticated: true
      })
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Signup failed'
      set({ error: errorMessage })
      throw error
    } finally {
      set({ isLoading: false })
    }
  },

  logout: async () => {
    try {
      set({ isLoading: true })
      await authAPI.logout()
      
      // Clear localStorage
      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
      
      set({
        user: null,
        accessToken: null,
        refreshToken: null,
        isAuthenticated: false
      })
    } catch (error) {
      console.error('Logout error:', error)
    } finally {
      set({ isLoading: false })
    }
  },

  refreshAuthToken: async () => {
    try {
      const { refreshToken } = get()
      if (!refreshToken) throw new Error('No refresh token available')
      
      const response = await authAPI.refreshToken({ refreshToken })
      
      // Update tokens
      localStorage.setItem('accessToken', response.access_token)
      localStorage.setItem('refreshToken', response.refresh_token)
      
      set({
        accessToken: response.access_token,
        refreshToken: response.refresh_token
      })
    } catch (error) {
      console.error('Token refresh failed:', error)
      get().clearAuth()
      throw error
    }
  },

  setUser: (user: User | null) => set({ user }),
  
  clearAuth: () => {
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    set({
      user: null,
      accessToken: null,
      refreshToken: null,
      isAuthenticated: false
    })
  },

  setError: (error: string | null) => set({ error }),

  verifyEmail: async (token: string) => {
    try {
      set({ isLoading: true, error: null })
      await authAPI.verifyEmail(token)
      set({ user: get().user ? { ...get().user!, emailVerified: true } : null })
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Email verification failed'
      set({ error: errorMessage })
      throw error
    } finally {
      set({ isLoading: false })
    }
  },

  forgotPassword: async (email: string) => {
    try {
      set({ isLoading: true, error: null })
      await authAPI.forgotPassword({ email })
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Password reset request failed'
      set({ error: errorMessage })
      throw error
    } finally {
      set({ isLoading: false })
    }
  },

  resetPassword: async (token: string, newPassword: string) => {
    try {
      set({ isLoading: true, error: null })
      await authAPI.resetPassword({ token, newPassword })
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Password reset failed'
      set({ error: errorMessage })
      throw error
    } finally {
      set({ isLoading: false })
    }
  }
}))
