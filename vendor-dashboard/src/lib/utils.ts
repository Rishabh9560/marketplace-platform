import { format, parseISO, differenceInDays } from 'date-fns'

export const formatCurrency = (value: number): string => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
  }).format(value)
}

export const formatNumber = (value: number): string => {
  return new Intl.NumberFormat('en-US').format(value)
}

export const formatDate = (dateString: string | Date, formatStr = 'MMM dd, yyyy'): string => {
  try {
    const date = typeof dateString === 'string' ? parseISO(dateString) : dateString
    return format(date, formatStr)
  } catch {
    return 'Invalid Date'
  }
}

export const formatDateTime = (dateString: string | Date): string => {
  return formatDate(dateString, 'MMM dd, yyyy HH:mm')
}

export const formatPercentage = (value: number): string => {
  return `${value.toFixed(2)}%`
}

export const getStatusColor = (status: string): string => {
  const colors: Record<string, string> = {
    ACTIVE: 'bg-green-100 text-green-800',
    DRAFT: 'bg-yellow-100 text-yellow-800',
    PENDING: 'bg-blue-100 text-blue-800',
    VERIFIED: 'bg-green-100 text-green-800',
    REJECTED: 'bg-red-100 text-red-800',
    COMPLETED: 'bg-green-100 text-green-800',
    FAILED: 'bg-red-100 text-red-800',
    SCHEDULED: 'bg-purple-100 text-purple-800',
    DELISTED: 'bg-gray-100 text-gray-800',
    SUSPENDED: 'bg-orange-100 text-orange-800',
  }
  return colors[status] || 'bg-gray-100 text-gray-800'
}

export const getStatusBadge = (status: string): string => {
  const badges: Record<string, string> = {
    ACTIVE: '✓',
    DRAFT: '✎',
    PENDING: '⟳',
    VERIFIED: '✓',
    REJECTED: '✗',
    COMPLETED: '✓',
    FAILED: '✗',
    SCHEDULED: '⏱',
    DELISTED: '⊘',
    SUSPENDED: '⚠',
  }
  return badges[status] || ''
}

export const truncateText = (text: string, maxLength: number): string => {
  if (text.length <= maxLength) return text
  return text.substring(0, maxLength) + '...'
}

export const validateEmail = (email: string): boolean => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(email)
}

export const validatePhone = (phone: string): boolean => {
  const phoneRegex = /^\+?[\d\s\-()]+$/
  return phoneRegex.test(phone) && phone.replace(/\D/g, '').length >= 10
}

export const getInitials = (name: string): string => {
  return name
    .split(' ')
    .map((n) => n[0])
    .join('')
    .toUpperCase()
    .substring(0, 2)
}

export const formatNumberWithCommas = (num: number): string => {
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

export const calculateDaysAgo = (dateString: string): string => {
  try {
    const date = parseISO(dateString)
    const days = differenceInDays(new Date(), date)
    if (days === 0) return 'Today'
    if (days === 1) return 'Yesterday'
    if (days < 7) return `${days} days ago`
    if (days < 30) return `${Math.floor(days / 7)} weeks ago`
    return `${Math.floor(days / 30)} months ago`
  } catch {
    return 'Unknown'
  }
}

export const getProgressColor = (progress: number): string => {
  if (progress >= 80) return 'bg-green-500'
  if (progress >= 60) return 'bg-blue-500'
  if (progress >= 40) return 'bg-yellow-500'
  return 'bg-red-500'
}
