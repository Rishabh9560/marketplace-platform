import React, { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuthStore } from '@/store'
import { validateEmail } from '@/lib/utils'
import { Button, Input, Card } from '@/components/common'
import { AlertCircle, Mail, Lock } from 'lucide-react'
import { VendorProfile } from '@/types'

export const LoginPage: React.FC = () => {
  const navigate = useNavigate()
  const { login } = useAuthStore()

  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [rememberMe, setRememberMe] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)

    // Validation
    if (!email || !password) {
      setError('Please fill in all fields')
      return
    }

    if (!validateEmail(email)) {
      setError('Please enter a valid email address')
      return
    }

    if (password.length < 6) {
      setError('Password must be at least 6 characters')
      return
    }

    try {
      setLoading(true)

      // Determine if this is a demo vendor based on email
      const isDemoEmail = email.toLowerCase().includes('demo') || email.toLowerCase().includes('test')
      
      // Generate unique vendor ID based on email hash
      const emailHash = email
        .split('')
        .reduce((acc, char) => ((acc << 5) - acc) + char.charCodeAt(0), 0)
        .toString()
        .substring(0, 8)
      
      const vendorId = isDemoEmail ? '1' : `vendor-${emailHash}`

      // Mock API call for authentication
      // In real app, this would call the backend
      const response = {
        success: true,
        data: {
          token: `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...${Date.now()}`,
          vendor: {
            id: vendorId,
            userId: `user-${emailHash}`,
            businessName: isDemoEmail ? 'Sample Vendor Store' : `${email.split('@')[0]}'s Store`,
            businessEmail: email,
            businessPhone: '+1234567890',
            kycStatus: 'VERIFIED',
            commissionRate: 5,
            totalEarnings: isDemoEmail ? 15000 : 0,
            availableBalance: isDemoEmail ? 3500 : 0,
            averageRating: isDemoEmail ? 4.5 : 0,
            totalReviews: isDemoEmail ? 124 : 0,
            accountStatus: 'ACTIVE',
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString(),
          },
        },
      }

      if (response.success) {
        const vendor = response.data.vendor as any
        login(response.data.token, vendor as VendorProfile)
        if (rememberMe) {
          localStorage.setItem('rememberMe', email)
        }
        navigate('/dashboard')
      }
    } catch (err) {
      setError('Invalid email or password. Please try again.')
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

        <Input
          label="Email Address"
          type="email"
          placeholder="vendor@example.com"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          icon={<Mail className="w-4 h-4" />}
          disabled={loading}
          error={email && !validateEmail(email) ? 'Invalid email' : ''}
        />

        <Input
          label="Password"
          type="password"
          placeholder="••••••••"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          icon={<Lock className="w-4 h-4" />}
          disabled={loading}
          error={password && password.length < 6 ? 'Minimum 6 characters' : ''}
        />

        <div className="flex items-center justify-between">
          <label className="flex items-center gap-2 cursor-pointer">
            <input
              type="checkbox"
              checked={rememberMe}
              onChange={(e) => setRememberMe(e.target.checked)}
              className="w-4 h-4 rounded border-gray-300"
              disabled={loading}
            />
            <span className="text-sm text-gray-600">Remember me</span>
          </label>
          <a href="#" className="text-sm text-blue-600 hover:text-blue-700">
            Forgot password?
          </a>
        </div>

        <Button
          type="submit"
          disabled={loading}
          className="w-full"
          size="md"
        >
          {loading ? 'Signing in...' : 'Sign In'}
        </Button>

        <p className="text-center text-sm text-gray-600">
          Don't have an account?{' '}
          <Link to="/register" className="text-blue-600 hover:text-blue-700 font-medium">
            Register here
          </Link>
        </p>

        {/* Demo Credentials */}
        <Card className="bg-blue-50 border border-blue-200 p-4">
          <p className="text-xs font-semibold text-blue-900 mb-2">Demo Credentials:</p>
          <p className="text-xs text-blue-800">
            <span className="font-mono">demo@vendor.com</span> / <span className="font-mono">password123</span>
          </p>
        </Card>
      </form>
    </div>
  )
}
