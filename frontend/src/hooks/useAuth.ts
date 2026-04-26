import { useCallback } from 'react'
import { authStore, type AuthState } from '../stores/authStore'
import { authAPI } from '../api/authAPI'

export interface UseAuthReturn {
  user: AuthState['user']
  isAuthenticated: boolean
  isLoading: boolean
  error: string | null
  login: (email: string, password: string) => Promise<void>
  signup: (email: string, password: string, fullName: string, phone: string) => Promise<void>
  logout: () => void
  refreshToken: () => Promise<void>
  verifyEmail: (token: string) => Promise<void>
  forgotPassword: (email: string) => Promise<void>
  resetPassword: (token: string, newPassword: string) => Promise<void>
}

/**
 * Custom hook for authentication operations.
 * Wraps authStore with convenience methods.
 */
export function useAuth(): UseAuthReturn {
  const { user, isAuthenticated, isLoading, error, login: storeLogin, signup: storeSignup, logout: storeLogout } = authStore()

  const login = useCallback(async (email: string, password: string) => {
    try {
      const response = await authAPI.login(email, password)
      storeLogin(response.access_token, response.refresh_token, response.user)
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Login failed'
      authStore.setState({ error: message })
      throw err
    }
  }, [])

  const signup = useCallback(async (email: string, password: string, fullName: string, phone: string) => {
    try {
      const response = await authAPI.signup(email, password, fullName, phone)
      storeSignup(response.access_token, response.refresh_token, response.user)
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Signup failed'
      authStore.setState({ error: message })
      throw err
    }
  }, [])

  const logout = useCallback(() => {
    storeLogout()
  }, [])

  const refreshToken = useCallback(async () => {
    try {
      const refreshTokenValue = localStorage.getItem('refreshToken')
      if (!refreshTokenValue) throw new Error('No refresh token found')
      
      const response = await authAPI.refreshToken(refreshTokenValue)
      authStore.setState({
        accessToken: response.access_token,
        refreshToken: response.refresh_token,
      })
    } catch (err) {
      storeLogout()
      throw err
    }
  }, [])

  const verifyEmail = useCallback(async (token: string) => {
    try {
      await authAPI.verifyEmail(token)
      // Update user state to mark email as verified
      if (user) {
        authStore.setState({ user: { ...user, emailVerified: true } })
      }
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Email verification failed'
      authStore.setState({ error: message })
      throw err
    }
  }, [user])

  const forgotPassword = useCallback(async (email: string) => {
    try {
      await authAPI.forgotPassword(email)
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Forgot password request failed'
      authStore.setState({ error: message })
      throw err
    }
  }, [])

  const resetPassword = useCallback(async (token: string, newPassword: string) => {
    try {
      await authAPI.resetPassword(token, newPassword)
      // Clear any auth state after password reset
      storeLogout()
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Password reset failed'
      authStore.setState({ error: message })
      throw err
    }
  }, [])

  return {
    user,
    isAuthenticated,
    isLoading,
    error,
    login,
    signup,
    logout,
    refreshToken,
    verifyEmail,
    forgotPassword,
    resetPassword,
  }
}
