export interface Product {
  id: string
  name: string
  description: string
  price: number
  originalPrice?: number
  rating: number
  reviews: number
  category: string
  images: string[]
  vendor: {
    id: string
    name: string
    rating: number
  }
  inStock: boolean
  quantity?: number
}

export interface CartItem {
  product: Product
  quantity: number
  selectedVariant?: string
}

export interface Order {
  id: string
  items: CartItem[]
  total: number
  status: 'PENDING' | 'CONFIRMED' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED'
  createdAt: string
  estimatedDelivery: string
}

export interface Customer {
  id: string
  email: string
  name: string
  phone: string
  avatar?: string
  addresses: Address[]
  wishlist: string[]
}

export interface Address {
  id: string
  type: 'home' | 'office' | 'other'
  street: string
  city: string
  state: string
  zipCode: string
  country: string
  isDefault: boolean
}
