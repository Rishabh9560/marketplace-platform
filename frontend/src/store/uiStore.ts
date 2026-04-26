import { create } from 'zustand'

export interface Toast {
  id: string
  title: string
  description?: string
  type: 'success' | 'error' | 'info' | 'warning'
  duration?: number
}

interface UIStore {
  // Modal states
  isCartOpen: boolean
  isSearchOpen: boolean
  isAuthModalOpen: boolean
  isCheckoutOpen: boolean
  isFilterOpen: boolean

  // Toast notifications
  toasts: Toast[]

  // Modals
  toggleCart: () => void
  toggleSearch: () => void
  toggleAuthModal: () => void
  toggleCheckout: () => void
  toggleFilter: () => void
  openCart: () => void
  closeCart: () => void
  openSearch: () => void
  closeSearch: () => void
  closeAuthModal: () => void
  openAuthModal: () => void

  // Toast methods
  addToast: (toast: Omit<Toast, 'id'>) => void
  removeToast: (id: string) => void
  clearToasts: () => void

  // Success/Error helpers
  showSuccess: (title: string, description?: string) => void
  showError: (title: string, description?: string) => void
  showInfo: (title: string, description?: string) => void
  showWarning: (title: string, description?: string) => void
}

export const useUIStore = create<UIStore>((set) => ({
  isCartOpen: false,
  isSearchOpen: false,
  isAuthModalOpen: false,
  isCheckoutOpen: false,
  isFilterOpen: false,
  toasts: [],

  // Modal toggles
  toggleCart: () => set((state) => ({ isCartOpen: !state.isCartOpen })),
  toggleSearch: () => set((state) => ({ isSearchOpen: !state.isSearchOpen })),
  toggleAuthModal: () => set((state) => ({ isAuthModalOpen: !state.isAuthModalOpen })),
  toggleCheckout: () => set((state) => ({ isCheckoutOpen: !state.isCheckoutOpen })),
  toggleFilter: () => set((state) => ({ isFilterOpen: !state.isFilterOpen })),
  
  openCart: () => set({ isCartOpen: true }),
  closeCart: () => set({ isCartOpen: false }),
  openSearch: () => set({ isSearchOpen: true }),
  closeSearch: () => set({ isSearchOpen: false }),
  openAuthModal: () => set({ isAuthModalOpen: true }),
  closeAuthModal: () => set({ isAuthModalOpen: false }),

  // Toast methods
  addToast: (toast: Omit<Toast, 'id'>) => {
    const id = Math.random().toString(36).substr(2, 9)
    set((state) => ({ toasts: [...state.toasts, { ...toast, id }] }))

    // Auto-remove after duration (default 3s)
    if (toast.duration !== -1) {
      setTimeout(
        () => set((state) => ({ toasts: state.toasts.filter((t) => t.id !== id) })),
        toast.duration || 3000
      )
    }
  },

  removeToast: (id: string) => {
    set((state) => ({ toasts: state.toasts.filter((t) => t.id !== id) }))
  },

  clearToasts: () => set({ toasts: [] }),

  // Helper methods
  showSuccess: (title: string, description?: string) => {
    set((state) => ({
      toasts: [
        ...state.toasts,
        {
          id: Math.random().toString(36).substr(2, 9),
          title,
          description,
          type: 'success',
          duration: 3000
        }
      ]
    }))
  },

  showError: (title: string, description?: string) => {
    set((state) => ({
      toasts: [
        ...state.toasts,
        {
          id: Math.random().toString(36).substr(2, 9),
          title,
          description,
          type: 'error',
          duration: 5000
        }
      ]
    }))
  },

  showInfo: (title: string, description?: string) => {
    set((state) => ({
      toasts: [
        ...state.toasts,
        {
          id: Math.random().toString(36).substr(2, 9),
          title,
          description,
          type: 'info',
          duration: 3000
        }
      ]
    }))
  },

  showWarning: (title: string, description?: string) => {
    set((state) => ({
      toasts: [
        ...state.toasts,
        {
          id: Math.random().toString(36).substr(2, 9),
          title,
          description,
          type: 'warning',
          duration: 4000
        }
      ]
    }))
  }
}))
