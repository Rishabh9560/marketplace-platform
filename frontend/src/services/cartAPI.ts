import axios from 'axios'

const API_BASE_URL = 'http://localhost:8000/api/v1'
const STORAGE_KEY = 'marketplace_cart'

interface CartItem {
  itemId: string
  variantId: string
  productId: string
  productName: string
  sku: string
  quantity: number
  price: number
  totalPrice: number
  vendorId: string
  vendorName: string
  addedAt: number
}

interface Cart {
  customerId: string
  items: CartItem[]
  itemCount: number
  subtotal: number
  tax: number
  shipping: number
  total: number
  estimatedDelivery: string
  updatedAt: number
}

interface AddToCartRequest {
  variantId: string
  quantity: number
  vendorId: string
}

interface UpdateQuantityRequest {
  itemId: string
  quantity: number
}

// Get mock product data
const PRODUCTS: any = {
  '1': { id: '1', name: 'Premium Wireless Headphones', price: 129.99, vendor: { id: 'tech-store', name: 'Tech Store' } },
  '2': { id: '2', name: 'Organic Cotton T-Shirt', price: 29.99, vendor: { id: 'fashion-hub', name: 'Fashion Hub' } },
  '3': { id: '3', name: 'Stainless Steel Water Bottle', price: 34.99, vendor: { id: 'home-essentials', name: 'Home Essentials' } },
}

const getAuthHeader = () => {
  const token = localStorage.getItem('accessToken') || localStorage.getItem('auth_token')
  if (!token) {
    throw new Error('No authentication token found. Please login first.')
  }
  return {
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  }
}

/**
 * Local storage cart implementation
 */
const getLocalCart = (): Cart => {
  try {
    const stored = localStorage.getItem(STORAGE_KEY)
    if (stored) {
      return JSON.parse(stored)
    }
  } catch (e) {
    console.warn('Failed to parse cart from storage')
  }

  return {
    customerId: localStorage.getItem('customerId') || 'user-1',
    items: [],
    itemCount: 0,
    subtotal: 0,
    tax: 0,
    shipping: 0,
    total: 0,
    estimatedDelivery: new Date(Date.now() + 5 * 24 * 60 * 60 * 1000).toISOString(),
    updatedAt: Date.now(),
  }
}

const saveLocalCart = (cart: Cart) => {
  cart.updatedAt = Date.now()
  localStorage.setItem(STORAGE_KEY, JSON.stringify(cart))
}

const calculateCartTotals = (cart: Cart): Cart => {
  const subtotal = cart.items.reduce((sum, item) => sum + item.totalPrice, 0)
  const tax = subtotal * 0.08 // 8% tax
  const shipping = cart.items.length > 0 ? 10 : 0
  const total = subtotal + tax + shipping

  return {
    ...cart,
    itemCount: cart.items.length,
    subtotal: Math.round(subtotal * 100) / 100,
    tax: Math.round(tax * 100) / 100,
    shipping,
    total: Math.round(total * 100) / 100,
  }
}

export const cartAPI = {
  /**
   * Get user's cart
   */
  getCart: async (): Promise<Cart> => {
    try {
      // Try backend first
      const response = await axios.get(`${API_BASE_URL}/cart`, getAuthHeader())
      return response.data.data || response.data
    } catch (error) {
      // Fallback to local cart
      console.log('Backend cart unavailable, using local cart')
      const localCart = getLocalCart()
      return calculateCartTotals(localCart)
    }
  },

  /**
   * Add item to cart
   */
  addToCart: async (request: AddToCartRequest): Promise<Cart> => {
    try {
      // Try backend first
      const response = await axios.post(`${API_BASE_URL}/cart/items`, request, getAuthHeader())
      // Sync to local storage
      const remoteCart = response.data.data || response.data
      saveLocalCart(remoteCart)
      return remoteCart
    } catch (error) {
      // Fallback to local implementation
      console.log('Backend cart unavailable, adding to local cart')
      
      const cart = getLocalCart()
      const product = PRODUCTS[request.variantId]
      
      if (!product) {
        throw new Error('Product not found')
      }

      // Check if item already in cart
      const existingIndex = cart.items.findIndex(
        item => item.variantId === request.variantId && item.vendorId === request.vendorId
      )

      if (existingIndex >= 0) {
        // Update quantity
        cart.items[existingIndex].quantity += request.quantity
        cart.items[existingIndex].totalPrice = 
          Math.round(cart.items[existingIndex].quantity * cart.items[existingIndex].price * 100) / 100
      } else {
        // Add new item
        const newItem: CartItem = {
          itemId: `item-${Math.random().toString(36).substr(2, 9)}`,
          variantId: request.variantId,
          productId: product.id,
          productName: product.name,
          sku: `SKU-${request.variantId}`,
          quantity: request.quantity,
          price: product.price,
          totalPrice: Math.round(product.price * request.quantity * 100) / 100,
          vendorId: request.vendorId,
          vendorName: product.vendor?.name || request.vendorId,
          addedAt: Date.now(),
        }
        cart.items.push(newItem)
      }

      const updated = calculateCartTotals(cart)
      saveLocalCart(updated)
      return updated
    }
  },

  /**
   * Update item quantity
   */
  updateQuantity: async (request: UpdateQuantityRequest): Promise<Cart> => {
    try {
      const response = await axios.put(`${API_BASE_URL}/cart/items`, request, getAuthHeader())
      const remoteCart = response.data.data || response.data
      saveLocalCart(remoteCart)
      return remoteCart
    } catch (error) {
      // Fallback to local implementation
      console.log('Backend cart unavailable, updating local cart')
      
      const cart = getLocalCart()
      const itemIndex = cart.items.findIndex(item => item.itemId === request.itemId)
      
      if (itemIndex < 0) {
        throw new Error('Item not found in cart')
      }

      if (request.quantity <= 0) {
        cart.items.splice(itemIndex, 1)
      } else {
        cart.items[itemIndex].quantity = request.quantity
        cart.items[itemIndex].totalPrice = 
          Math.round(cart.items[itemIndex].quantity * cart.items[itemIndex].price * 100) / 100
      }

      const updated = calculateCartTotals(cart)
      saveLocalCart(updated)
      return updated
    }
  },

  /**
   * Remove item from cart
   */
  removeFromCart: async (itemId: string): Promise<Cart> => {
    try {
      const response = await axios.delete(`${API_BASE_URL}/cart/items/${itemId}`, getAuthHeader())
      const remoteCart = response.data.data || response.data
      saveLocalCart(remoteCart)
      return remoteCart
    } catch (error) {
      // Fallback to local implementation
      console.log('Backend cart unavailable, removing from local cart')
      
      const cart = getLocalCart()
      const itemIndex = cart.items.findIndex(item => item.itemId === itemId)
      
      if (itemIndex < 0) {
        throw new Error('Item not found in cart')
      }

      cart.items.splice(itemIndex, 1)
      const updated = calculateCartTotals(cart)
      saveLocalCart(updated)
      return updated
    }
  },

  /**
   * Clear entire cart
   */
  clearCart: async (): Promise<void> => {
    try {
      await axios.delete(`${API_BASE_URL}/cart`, getAuthHeader())
    } catch (error) {
      // Fallback to local implementation
      console.log('Backend cart unavailable, clearing local cart')
    }
    localStorage.removeItem(STORAGE_KEY)
  },
}

export type { Cart, CartItem, AddToCartRequest, UpdateQuantityRequest }
