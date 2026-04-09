import React from 'react'

interface AuthLayoutProps {
  children: React.ReactNode
  title?: string
  subtitle?: string
}

export const AuthLayout: React.FC<AuthLayoutProps> = ({
  children,
  title = 'Welcome',
  subtitle = 'Vendor Management Platform',
}) => {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-600 to-blue-800 px-4">
      <div className="w-full max-w-md">
        {/* Logo & Header */}
        <div className="text-center mb-8">
          <div className="w-16 h-16 bg-white rounded-lg flex items-center justify-center mx-auto mb-4 shadow-lg">
            <span className="text-2xl font-bold text-blue-600">V</span>
          </div>
          <h1 className="text-3xl font-bold text-white mb-2">{title}</h1>
          <p className="text-blue-100">{subtitle}</p>
        </div>

        {/* Content */}
        <div className="bg-white rounded-lg shadow-xl p-8">
          {children}
        </div>

        {/* Footer */}
        <div className="mt-8 text-center text-blue-100 text-sm">
          <p>
            © 2024 Vendor Hub. All rights reserved.{' '}
            <a href="#" className="text-white hover:underline">
              Privacy Policy
            </a>{' '}
            •{' '}
            <a href="#" className="text-white hover:underline">
              Terms
            </a>
          </p>
        </div>
      </div>
    </div>
  )
}
