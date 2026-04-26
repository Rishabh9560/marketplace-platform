import axios from 'axios'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

export interface User {
  id: string
  email: string
  fullName: string
  phone?: string
  role: 'CUSTOMER' | 'VENDOR' | 'ADMIN' | 'SUPPORT'
  emailVerified: boolean
}

export interface AuthResponse {
  access_token: string
  refresh_token: string
  userId: string
  email: string
  fullName: string
  phone?: string
  role: string
  emailVerified: boolean
  expires_in: number
}

const api = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Request interceptor - add token to headers
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// Response interceptor - handle token refresh
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true

      try {
        const refreshToken = localStorage.getItem('refreshToken')
        if (!refreshToken) throw new Error('No refresh token')

        const response = await axios.post(`${API_BASE_URL}/auth/refresh`, {
          refreshToken
        })

        localStorage.setItem('accessToken', response.data.access_token)
        localStorage.setItem('refreshToken', response.data.refresh_token)

        api.defaults.headers.common['Authorization'] = `Bearer ${response.data.access_token}`
        originalRequest.headers['Authorization'] = `Bearer ${response.data.access_token}`

        return api(originalRequest)
      } catch (refreshError) {
        localStorage.removeItem('accessToken')
        localStorage.removeItem('refreshToken')
        window.location.href = '/login'
        return Promise.reject(refreshError)
      }
    }

    return Promise.reject(error)
  }
)

export const authAPI = {
  /**
   * User login
   */
  async login(credentials: { email: string; password: string }): Promise<AuthResponse> {
    const response = await api.post<AuthResponse>('/auth/login', credentials)
    return response.data
  },

  /**
   * User signup (registration)
   */
  async signup(data: {
    email: string
    password: string
    fullName: string
    phone?: string
  }): Promise<AuthResponse> {
    const response = await api.post<AuthResponse>('/auth/signup', data)
    return response.data
  },

  /**
   * Refresh access token
   */
  async refreshToken(data: { refreshToken: string }): Promise<AuthResponse> {
    const response = await api.post<AuthResponse>('/auth/refresh', data)
    return response.data
  },

  /**
   * Logout (revoke refresh token)
   */
  async logout(): Promise<void> {
    await api.post('/auth/logout')
  },

  /**
   * Get current user details
   */
  async getCurrentUser(): Promise<User> {
    const response = await api.get<User>('/auth/me')
    return response.data
  },

  /**
   * Verify email address
   */
  async verifyEmail(token: string): Promise<void> {
    await api.get(`/auth/verify-email?token=${token}`)
  },

  /**
   * Request password reset
   */
  async forgotPassword(data: { email: string }): Promise<void> {
    await api.post('/auth/forgot-password', data)
  },

  /**
   * Reset password with token
   */
  async resetPassword(data: { token: string; newPassword: string }): Promise<void> {
    await api.post('/auth/reset-password', data)
  }
}

export default api
