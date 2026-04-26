import React, { useState } from 'react'
import { Card, Button, Input, Badge } from '@/components/common'
import { useAuthStore } from '@/store'
import { formatCurrency, formatDate } from '@/lib/utils'
import { User, Building2, Phone, MapPin, Clock, Award, AlertCircle, Save } from 'lucide-react'

export const ProfilePage: React.FC = () => {
  const { vendor } = useAuthStore()
  const [isEditing, setIsEditing] = useState(false)
  const [loading, setLoading] = useState(false)
  const [showPasswordModal, setShowPasswordModal] = useState(false)
  const [passwordLoading, setPasswordLoading] = useState(false)
  const [passwordData, setPasswordData] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  })
  const [passwordError, setPasswordError] = useState('')
  const [passwordSuccess, setPasswordSuccess] = useState('')

  const [formData, setFormData] = useState({
    businessName: vendor?.businessName || '',
    businessEmail: vendor?.businessEmail || '',
    businessPhone: vendor?.businessPhone || '',
    businessAddress: vendor?.businessAddress || '',
    businessCity: vendor?.businessCity || '',
    businessState: vendor?.businessState || '',
    businessPincode: vendor?.businessPostalCode || '',
    businessDescription: vendor?.businessDescription || '',
    taxId: vendor?.taxId || '',
  })

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target
    setFormData((prev) => ({ ...prev, [name]: value }))
  }

  const handleSave = async () => {
    try {
      setLoading(true)
      // Mock API call
      await new Promise((resolve) => setTimeout(resolve, 1500))
      setIsEditing(false)
      alert('Profile updated successfully!')
    } catch (err) {
      alert('Failed to update profile')
    } finally {
      setLoading(false)
    }
  }

  const handlePasswordChange = async () => {
    // Validate password inputs
    if (!passwordData.currentPassword || !passwordData.newPassword || !passwordData.confirmPassword) {
      setPasswordError('All fields are required')
      return
    }

    if (passwordData.newPassword !== passwordData.confirmPassword) {
      setPasswordError('New passwords do not match')
      return
    }

    if (passwordData.newPassword.length < 8) {
      setPasswordError('New password must be at least 8 characters')
      return
    }

    if (passwordData.currentPassword === passwordData.newPassword) {
      setPasswordError('New password must be different from current password')
      return
    }

    try {
      setPasswordLoading(true)
      setPasswordError('')
      setPasswordSuccess('')

      // Mock API call to change password
      await new Promise((resolve) => setTimeout(resolve, 1500))

      setPasswordSuccess('Password changed successfully!')
      setPasswordData({ currentPassword: '', newPassword: '', confirmPassword: '' })

      // Close modal after 2 seconds
      setTimeout(() => {
        setShowPasswordModal(false)
        setPasswordSuccess('')
      }, 2000)
    } catch (err) {
      setPasswordError('Failed to change password. Please try again.')
    } finally {
      setPasswordLoading(false)
    }
  }

  const handlePasswordInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target
    setPasswordData((prev) => ({ ...prev, [name]: value }))
    setPasswordError('')
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-start">
        <div>
          <h1 className="text-3xl font-bold text-gray-900 mb-2">Business Profile</h1>
          <p className="text-gray-600">Manage your vendor account and business information</p>
        </div>
        <Button
          variant={isEditing ? 'outline' : 'primary'}
          onClick={() => (isEditing ? handleSave() : setIsEditing(true))}
          disabled={loading}
        >
          {isEditing ? (
            <>
              <Save className="w-4 h-4 mr-2" />
              Save Changes
            </>
          ) : (
            'Edit Profile'
          )}
        </Button>
      </div>

      {/* Profile Header Card */}
      <Card className="card-shadow">
        <div className="flex items-start justify-between">
          <div className="flex items-center gap-6">
            <div className="w-20 h-20 bg-gradient-to-br from-blue-600 to-blue-800 rounded-lg flex items-center justify-center">
              <span className="text-3xl font-bold text-white">
                {vendor?.businessName.charAt(0)}
              </span>
            </div>
            <div>
              <h2 className="text-2xl font-bold text-gray-900">
                {vendor?.businessName}
              </h2>
              <p className="text-gray-600 text-sm mt-1 flex items-center gap-2">
                <Award className="w-4 h-4" />
                {vendor?.kycStatus === 'VERIFIED'
                  ? 'Verified Seller'
                  : 'Pending Verification'}
              </p>
              <div className="flex gap-2 mt-3">
                <Badge
                  className={
                    vendor?.accountStatus === 'ACTIVE'
                      ? 'bg-green-100 text-green-800'
                      : 'bg-yellow-100 text-yellow-800'
                  }
                >
                  {vendor?.accountStatus}
                </Badge>
                <Badge className="bg-blue-100 text-blue-800">
                  Seller since {new Date(vendor?.createdAt || '').getFullYear()}
                </Badge>
              </div>
            </div>
          </div>

          <div className="text-right">
            <p className="text-sm text-gray-600 mb-2">Rating</p>
            <div className="flex items-center justify-end gap-2">
              <span className="text-3xl font-bold text-gray-900">
                {vendor?.averageRating || 0}
              </span>
              <span className="text-2xl text-yellow-500">★</span>
            </div>
            <p className="text-xs text-gray-500 mt-1">
              Based on {vendor?.totalReviews || 0} reviews
            </p>
          </div>
        </div>
      </Card>

      {/* Business Statistics */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card className="card-shadow">
          <p className="text-gray-600 text-sm mb-2">Total Earnings</p>
          <p className="text-2xl font-bold text-green-600">
            {formatCurrency(vendor?.totalEarnings || 0)}
          </p>
        </Card>

        <Card className="card-shadow">
          <p className="text-gray-600 text-sm mb-2">Available Balance</p>
          <p className="text-2xl font-bold text-blue-600">
            {formatCurrency(vendor?.availableBalance || 0)}
          </p>
        </Card>

        <Card className="card-shadow">
          <p className="text-gray-600 text-sm mb-2">Commission Rate</p>
          <p className="text-2xl font-bold text-gray-900">
            {vendor?.commissionRate || 0}%
          </p>
        </Card>

        <Card className="card-shadow">
          <p className="text-gray-600 text-sm mb-2">Account Status</p>
          <p className="text-2xl font-bold text-gray-900">
            {vendor?.kycStatus || 'PENDING'}
          </p>
        </Card>
      </div>

      {/* Business Information */}
      <Card className="card-shadow">
        <div className="flex justify-between items-center mb-6">
          <h3 className="text-lg font-bold text-gray-900">Business Information</h3>
          {isEditing && (
            <Button
              variant="ghost"
              onClick={() => setIsEditing(false)}
              size="sm"
            >
              Cancel
            </Button>
          )}
        </div>

        <div className="space-y-4">
          {isEditing ? (
            <>
              <Input
                label="Business Name"
                type="text"
                name="businessName"
                value={formData.businessName}
                onChange={handleInputChange}
              />

              <Input
                label="Business Email"
                type="email"
                name="businessEmail"
                value={formData.businessEmail}
                onChange={handleInputChange}
              />

              <Input
                label="Business Phone"
                type="tel"
                name="businessPhone"
                value={formData.businessPhone}
                onChange={handleInputChange}
              />

              <Input
                label="Business Address"
                type="text"
                name="businessAddress"
                value={formData.businessAddress}
                onChange={handleInputChange}
              />

              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <Input
                  label="City"
                  type="text"
                  name="businessCity"
                  value={formData.businessCity}
                  onChange={handleInputChange}
                />
                <Input
                  label="State"
                  type="text"
                  name="businessState"
                  value={formData.businessState}
                  onChange={handleInputChange}
                />
                <Input
                  label="Pincode"
                  type="text"
                  name="businessPincode"
                  value={formData.businessPincode}
                  onChange={handleInputChange}
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Business Description
                </label>
                <textarea
                  name="businessDescription"
                  value={formData.businessDescription}
                  onChange={handleInputChange}
                  rows={3}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>

              <Input
                label="Tax ID / GST Number"
                type="text"
                name="taxId"
                value={formData.taxId}
                onChange={handleInputChange}
              />
            </>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <p className="text-gray-600 text-sm mb-1 flex items-center gap-2">
                  <Building2 className="w-4 h-4" />
                  Business Email
                </p>
                <p className="text-gray-900 font-medium">{formData.businessEmail}</p>
              </div>

              <div>
                <p className="text-gray-600 text-sm mb-1 flex items-center gap-2">
                  <Phone className="w-4 h-4" />
                  Business Phone
                </p>
                <p className="text-gray-900 font-medium">{formData.businessPhone}</p>
              </div>

              <div>
                <p className="text-gray-600 text-sm mb-1 flex items-center gap-2">
                  <MapPin className="w-4 h-4" />
                  Address
                </p>
                <p className="text-gray-900 font-medium">
                  {formData.businessAddress}, {formData.businessCity},{' '}
                  {formData.businessState} {formData.businessPincode}
                </p>
              </div>

              <div>
                <p className="text-gray-600 text-sm mb-1">Tax ID / GST</p>
                <p className="text-gray-900 font-medium">{formData.taxId}</p>
              </div>
            </div>
          )}
        </div>
      </Card>

      {/* Account Details */}
      <Card className="card-shadow">
        <h3 className="text-lg font-bold text-gray-900 mb-6">Account Details</h3>

        <div className="space-y-4">
          <div className="flex justify-between items-center p-4 bg-gray-50 rounded-lg">
            <span className="text-gray-700 flex items-center gap-2">
              <User className="w-4 h-4" />
              User ID
            </span>
            <span className="font-mono text-gray-900">{vendor?.id}</span>
          </div>

          <div className="flex justify-between items-center p-4 bg-gray-50 rounded-lg">
            <span className="text-gray-700 flex items-center gap-2">
              <Clock className="w-4 h-4" />
              Account Created
            </span>
            <span className="text-gray-900">
              {formatDate(vendor?.createdAt || '')}
            </span>
          </div>

          <div className="flex justify-between items-center p-4 bg-gray-50 rounded-lg">
            <span className="text-gray-700">Commission Rate</span>
            <span className="font-bold text-gray-900">{vendor?.commissionRate || 0}%</span>
          </div>

          <div className="flex justify-between items-center p-4 bg-gray-50 rounded-lg">
            <span className="text-gray-700">KYC Status</span>
            <Badge className="bg-blue-100 text-blue-800">{vendor?.kycStatus}</Badge>
          </div>
        </div>
      </Card>

      {/* Security Section */}
      <Card className="card-shadow">
        <h3 className="text-lg font-bold text-gray-900 mb-6">Security</h3>

        <div className="space-y-4">
          <div className="flex justify-between items-center p-4 border border-gray-200 rounded-lg">
            <div>
              <p className="font-medium text-gray-900">Change Password</p>
              <p className="text-sm text-gray-600">Update your password regularly</p>
            </div>
            <Button 
              variant="outline" 
              size="sm"
              onClick={() => setShowPasswordModal(true)}
            >
              Change
            </Button>
          </div>

          <div className="flex justify-between items-center p-4 border border-gray-200 rounded-lg">
            <div>
              <p className="font-medium text-gray-900">Two-Factor Authentication</p>
              <p className="text-sm text-gray-600">Add an extra layer of security</p>
            </div>
            <Button variant="outline" size="sm">
              Enable
            </Button>
          </div>

          <div className="flex justify-between items-center p-4 border border-gray-200 rounded-lg">
            <div>
              <p className="font-medium text-gray-900">Active Sessions</p>
              <p className="text-sm text-gray-600">Manage your login sessions</p>
            </div>
            <Button variant="outline" size="sm">
              View
            </Button>
          </div>
        </div>
      </Card>

      {/* Support Note */}
      <Card className="bg-blue-50 border border-blue-200 card-shadow">
        <div className="flex gap-3">
          <AlertCircle className="w-5 h-5 text-blue-600 flex-shrink-0 mt-0.5" />
          <div>
            <p className="text-sm font-medium text-blue-900 mb-1">Need Help?</p>
            <p className="text-sm text-blue-800">
              Contact our support team at{' '}
              <a href="mailto:support@vendorhub.com" className="font-semibold hover:underline">
                support@vendorhub.com
              </a>{' '}
              or call{' '}
              <a href="tel:+1234567890" className="font-semibold hover:underline">
                +1-234-567-890
              </a>
            </p>
          </div>
        </div>
      </Card>

      {/* Change Password Modal */}
      {showPasswordModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <Card className="w-full max-w-md mx-4">
            <div className="flex justify-between items-center mb-6">
              <h3 className="text-lg font-bold text-gray-900">Change Password</h3>
              <button
                onClick={() => {
                  setShowPasswordModal(false)
                  setPasswordError('')
                  setPasswordData({ currentPassword: '', newPassword: '', confirmPassword: '' })
                }}
                className="text-gray-500 hover:text-gray-700"
              >
                ✕
              </button>
            </div>

            {passwordError && (
              <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg flex gap-2">
                <AlertCircle className="w-5 h-5 text-red-600 flex-shrink-0" />
                <p className="text-sm text-red-800">{passwordError}</p>
              </div>
            )}

            {passwordSuccess && (
              <div className="mb-4 p-3 bg-green-50 border border-green-200 rounded-lg">
                <p className="text-sm text-green-800">✓ {passwordSuccess}</p>
              </div>
            )}

            <div className="space-y-4 mb-6">
              <Input
                label="Current Password"
                type="password"
                name="currentPassword"
                value={passwordData.currentPassword}
                onChange={handlePasswordInputChange}
                placeholder="Enter your current password"
                disabled={passwordLoading}
              />

              <Input
                label="New Password"
                type="password"
                name="newPassword"
                value={passwordData.newPassword}
                onChange={handlePasswordInputChange}
                placeholder="Enter your new password (min 8 characters)"
                disabled={passwordLoading}
              />

              <Input
                label="Confirm Password"
                type="password"
                name="confirmPassword"
                value={passwordData.confirmPassword}
                onChange={handlePasswordInputChange}
                placeholder="Confirm your new password"
                disabled={passwordLoading}
              />
            </div>

            <div className="flex gap-3">
              <Button
                variant="outline"
                onClick={() => {
                  setShowPasswordModal(false)
                  setPasswordError('')
                  setPasswordData({ currentPassword: '', newPassword: '', confirmPassword: '' })
                }}
                disabled={passwordLoading}
                className="flex-1"
              >
                Cancel
              </Button>
              <Button
                variant="primary"
                onClick={handlePasswordChange}
                disabled={passwordLoading}
                className="flex-1"
              >
                {passwordLoading ? 'Changing...' : 'Change Password'}
              </Button>
            </div>
          </Card>
        </div>
      )}
    </div>
  )
}
