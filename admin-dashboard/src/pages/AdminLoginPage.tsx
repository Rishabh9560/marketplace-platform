import React, { useState } from 'react'
import { Mail, Lock, Eye, EyeOff } from 'lucide-react'

export const AdminLoginPage: React.FC = () => {
  const [showPassword, setShowPassword] = useState(false)
  const [step, setStep] = useState<'login' | 'otp'>('login')
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    otp: '',
  })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [sentOtp, setSentOtp] = useState('')

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    })
  }

  const handleSubmitLogin = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')

    if (!formData.email || !formData.password) {
      setError('Email and password are required')
      return
    }

    if (!formData.email.endsWith('@admin.marketplace.com')) {
      setError('Only admin accounts can log in here')
      return
    }

    setLoading(true)
    try {
      // Simulate generating OTP
      await new Promise((resolve) => setTimeout(resolve, 1000))
      const otp = Math.random().toString().substring(2, 8)
      setSentOtp(otp)
      setStep('otp')
      alert(`OTP sent to ${formData.email}: ${otp}`)
    } finally {
      setLoading(false)
    }
  }

  const handleSubmitOtp = (e: React.FormEvent) => {
    e.preventDefault()
    setError('')

    if (!formData.otp) {
      setError('OTP is required')
      return
    }

    if (formData.otp === sentOtp) {
      localStorage.setItem('admin_token', 'admin-' + Date.now())
      window.location.href = '/admin'
    } else {
      setError('Invalid OTP. Please try again.')
    }
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 to-gray-800 flex items-center justify-center px-4">
      <div className="bg-white rounded-xl shadow-2xl p-8 max-w-md w-full">
        <div className="text-center mb-8">
          <div className="w-16 h-16 bg-red-600 rounded-full flex items-center justify-center mx-auto mb-4">
            <span className="text-2xl font-bold text-white">A</span>
          </div>
          <h1 className="text-3xl font-bold text-gray-800">Admin Portal</h1>
          <p className="text-gray-600 mt-2">Secure Administrator Access</p>
        </div>

        {step === 'login' ? (
          <form onSubmit={handleSubmitLogin} className="space-y-4">
            {error && <div className="bg-red-50 text-red-700 px-4 py-3 rounded-lg text-sm">{error}</div>}

            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-2">Admin Email</label>
              <div className="flex items-center">
                <Mail className="w-5 h-5 text-gray-400 ml-3 mr-2" />
                <input
                  type="email"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                  placeholder="admin@admin.marketplace.com"
                  className="flex-1 px-3 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-600 focus:border-transparent"
                />
              </div>
            </div>

            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-2">Password</label>
              <div className="flex items-center">
                <Lock className="w-5 h-5 text-gray-400 ml-3 mr-2" />
                <input
                  type={showPassword ? 'text' : 'password'}
                  name="password"
                  value={formData.password}
                  onChange={handleChange}
                  placeholder="••••••••"
                  className="flex-1 px-3 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-600 focus:border-transparent"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="mr-3 text-gray-400 hover:text-gray-600"
                >
                  {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                </button>
              </div>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-red-600 text-white py-3 rounded-lg font-semibold hover:bg-red-700 transition mt-6 disabled:bg-red-400"
            >
              {loading ? 'Sending OTP...' : 'Next - Send OTP'}
            </button>

            <p className="text-center text-gray-600 text-xs mt-4">
              Use: admin@admin.marketplace.com / admin123
            </p>
          </form>
        ) : (
          <form onSubmit={handleSubmitOtp} className="space-y-4">
            {error && <div className="bg-red-50 text-red-700 px-4 py-3 rounded-lg text-sm">{error}</div>}

            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-2">Email Verification</label>
              <p className="text-sm text-gray-600 mb-4">
                We've sent a 6-digit OTP to <span className="font-semibold">{formData.email}</span>
              </p>
              <input
                type="text"
                name="otp"
                value={formData.otp}
                onChange={handleChange}
                placeholder="Enter 6-digit OTP"
                maxLength={6}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg text-center text-2xl tracking-widest font-bold focus:ring-2 focus:ring-red-600 focus:border-transparent"
              />
            </div>

            <button
              type="submit"
              className="w-full bg-red-600 text-white py-3 rounded-lg font-semibold hover:bg-red-700 transition mt-6"
            >
              Verify & Login
            </button>

            <button
              type="button"
              onClick={() => {
                setStep('login')
                setFormData({ ...formData, otp: '' })
                setSentOtp('')
              }}
              className="w-full text-red-600 py-2 hover:text-red-700 font-semibold"
            >
              Back to Login
            </button>

            <p className="text-center text-gray-600 text-xs mt-4">OTP: {sentOtp}</p>
          </form>
        )}
      </div>
    </div>
  )
}
