/**
 * Authentication Service
 * Handles user registration and login validation
 */

export interface RegisteredUser {
  id: string
  name: string
  email: string
  password: string
  phone: string
  addresses: any[]
  wishlist: string[]
  registeredAt: string
}

const STORAGE_KEY = 'registered_users'

export const authService = {
  /**
   * Get all registered users
   */
  getRegisteredUsers: (): RegisteredUser[] => {
    try {
      return JSON.parse(localStorage.getItem(STORAGE_KEY) || '[]')
    } catch {
      return []
    }
  },

  /**
   * Check if email is already registered
   */
  isEmailRegistered: (email: string): boolean => {
    const users = authService.getRegisteredUsers()
    return users.some(u => u.email === email)
  },

  /**
   * Register a new user
   */
  registerUser: (userData: Omit<RegisteredUser, 'id' | 'registeredAt'>): RegisteredUser => {
    const users = authService.getRegisteredUsers()
    
    // Check if email already exists
    if (users.some(u => u.email === userData.email)) {
      throw new Error('Email already registered')
    }

    const newUser: RegisteredUser = {
      ...userData,
      id: Math.random().toString(36).substr(2, 9),
      registeredAt: new Date().toISOString(),
    }

    users.push(newUser)
    localStorage.setItem(STORAGE_KEY, JSON.stringify(users))
    return newUser
  },

  /**
   * Validate login credentials
   */
  validateLogin: (email: string, password: string): RegisteredUser | null => {
    const users = authService.getRegisteredUsers()
    const user = users.find(u => u.email === email)
    
    if (!user) {
      return null // Email not registered
    }
    
    if (user.password !== password) {
      return null // Wrong password
    }
    
    return user
  },

  /**
   * Get user by email
   */
  getUserByEmail: (email: string): RegisteredUser | undefined => {
    const users = authService.getRegisteredUsers()
    return users.find(u => u.email === email)
  },

  /**
   * Clear all registered users (dev/testing only)
   */
  clearAllUsers: (): void => {
    localStorage.removeItem(STORAGE_KEY)
  },
}
