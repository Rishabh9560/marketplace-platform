import { VendorProfile } from '@/types'

/**
 * Check if a vendor is a demo/test vendor
 * Demo vendors are identified by:
 * - Email containing 'demo' or 'test'
 * - Vendor ID starting with '1' (legacy demo account)
 */
export const isDemoVendor = (vendor: VendorProfile | null): boolean => {
  if (!vendor) return false

  // Check if email is a demo email
  const email = vendor.businessEmail?.toLowerCase() || ''
  if (email.includes('demo@') || email.includes('test@') || email.includes('.demo') || email.includes('.test')) {
    return true
  }

  // Legacy check: vendor ID '1' is demo
  if (vendor.id === '1') {
    return true
  }

  // Check if business name suggests it's a demo vendor
  if (
    vendor.businessName?.toLowerCase().includes('sample vendor store') ||
    vendor.businessName?.toLowerCase().includes('demo') ||
    vendor.businessName?.toLowerCase() === 'test'
  ) {
    return true
  }

  return false
}
