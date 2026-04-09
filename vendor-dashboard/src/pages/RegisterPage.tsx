import React, { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuthStore } from '@/store'
import { validateEmail, validatePhone } from '@/lib/utils'
import { Button, Input, Card } from '@/components/common'
import { AlertCircle, Mail, Lock, Phone, Building2, User } from 'lucide-react'

export const RegisterPage: React.FC = () => {
  const navigate = useNavigate()
  const { login } = useAuthStore()

  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    businessName: '',
    password: '',
    confirmPassword: '',
  })

  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [agreeTerms, setAgreeTerms] = useState(false)

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target
    setFormData((prev) => ({ ...prev, [name]: value }))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)

    // Validation
    if (
      !formData.firstName ||
      !formData.lastName ||
      !formData.email ||
      !formData.phone ||
      !formData.businessName ||
      !formData.password ||
      !formData.confirmPassword
    ) {
      setError('Please fill in all fields')
      return
    }

    if (!validateEmail(formData.email)) {
      setError('Please enter a valid email address')
      return
    }

    if (!validatePhone(formData.phone)) {
      setError('Please enter a valid phone number')
      return
    }

    if (formData.password.length < 8) {
      setError('Password must be at least 8 characters')
      return
    }

    if (formData.password !== formData.confirmPassword) {
      setError('Passwords do not match')
      return
    }

    if (!agreeTerms) {
      setError('You must agree to the terms and conditions')
      return
    }

    try {
      setLoading(true)

      // Mock API call for registration
      const response = {
        success: true,
        data: {
          token: `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...${Date.now()}`,
          vendor: {
            id: '1',
            userId: 'user-123',
            businessName: formData.businessName,
            businessEmail: formData.email,
            businessPhone: formData.phone,
            kycStatus: 'PENDING',
            commissionRate: 5,
            totalEarnings: 0,
            availableBalance: 0,
            averageRating: 0,
            totalReviews: 0,
            accountStatus: 'PENDING',
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString(),
          },
        },
      }

      if (response.success) {
        login(response.data.token, response.data.vendor)
        navigate('/kyc')
      }
    } catch (err) {
      setError('Registration failed. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="w-full">
      <form onSubmit={handleSubmit} className="space-y-6">
        {error && (
          <div className="flex items-start gap-3 p-4 bg-red-50 border border-red-200 rounded-lg animate-slideUp">
            <AlertCircle className="w-5 h-5 text-red-600 flex-shrink-0 mt-0.5" />
            <p className="text-sm text-red-800">{error}</p>
          </div>
        )}

        <div className="grid grid-cols-2 gap-4">
          <Input
            label="First Name"
            type="text"
            placeholder="John"
            name="firstName"
            value={formData.firstName}
            onChange={handleChange}
            disabled={loading}
            icon={<User className="w-4 h-4" />}
          />
          <Input
            label="Last Name"
            type="text"
            placeholder="Doe"
            name="lastName"
            value={formData.lastName}
            onChange={handleChange}
            disabled={loading}
            icon={<User className="w-4 h-4" />}
          />
        </div>

        <Input
          label="Email Address"
          type="email"
          placeholder="vendor@example.com"
          name="email"
          value={formData.email}
          onChange={handleChange}
          disabled={loading}
          icon={<Mail className="w-4 h-4" />}
          error={
            formData.email && !validateEmail(formData.email)
              ? 'Invalid email'
              : ''
          }
        />

        <Input
          label="Phone Number"
          type="tel"
          placeholder="+1 (555) 000-0000"
          name="phone"
          value={formData.phone}
          onChange={handleChange}
          disabled={loading}
          icon={<Phone className="w-4 h-4" />}
          error={
            formData.phone && !validatePhone(formData.phone)
              ? 'Invalid phone'
              : ''
          }
        />

        <Input
          label="Business Name"
          type="text"
          placeholder="Your Store Name"
          name="businessName"
          value={formData.businessName}
          onChange={handleChange}
          disabled={loading}
          icon={<Building2 className="w-4 h-4" />}
        />

        <Input
          label="Password"
          type="password"
          placeholder="••••••••"
          name="password"
          value={formData.password}
          onChange={handleChange}
          disabled={loading}
          icon={<Lock className="w-4 h-4" />}
          error={
            formData.password && formData.password.length < 8
              ? 'Minimum 8 characters'
              : ''
          }
        />

        <Input
          label="Confirm Password"
          type="password"
          placeholder="••••••••"
          name="confirmPassword"
          value={formData.confirmPassword}
          onChange={handleChange}
          disabled={loading}
          icon={<Lock className="w-4 h-4" />}
          error={
            formData.confirmPassword &&
            formData.password !== formData.confirmPassword
              ? 'Passwords do not match'
              : ''
          }
        />

        <label className="flex items-start gap-3 cursor-pointer">
          <input
            type="checkbox"
            checked={agreeTerms}
            onChange={(e) => setAgreeTerms(e.target.checked)}
            className="w-4 h-4 rounded border-gray-300 mt-1"
            disabled={loading}
          />
          <span className="text-sm text-gray-600">
            I agree to the{' '}
            <a href="#" className="text-blue-600 hover:underline">
              Terms of Service
            </a>{' '}
            and{' '}
            <a href="#" className="text-blue-600 hover:underline">
              Privacy Policy
            </a>
          </span>
        </label>

        <Button
          type="submit"
          disabled={loading}
          className="w-full"
          size="md"
        >
          {loading ? 'Creating Account...' : 'Create Account'}
        </Button>

        <p className="text-center text-sm text-gray-600">
          Already have an account?{' '}
          <Link to="/login" className="text-blue-600 hover:text-blue-700 font-medium">
            Sign in here
          </Link>
        </p>
      </form>
    </div>
  )
}
