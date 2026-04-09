import React, { useEffect } from 'react'
import {
  BrowserRouter,
  Routes,
  Route,
  Navigate,
  useLocation,
} from 'react-router-dom'
import { useAuthStore } from '@/store'
import { MainLayout } from '@/layouts'
import { AuthLayout } from '@/layouts'

// Pages
import { LoginPage } from '@/pages/LoginPage'
import { RegisterPage } from '@/pages/RegisterPage'
import { DashboardPage } from '@/pages/DashboardPage'
import { ProductListingsPage } from '@/pages/ProductListingsPage'
import { CreateProductPage } from '@/pages/CreateProductPage'
import { EditProductPage } from '@/pages/EditProductPage'
import { PayoutHistoryPage } from '@/pages/PayoutHistoryPage'
import { KYCVerificationPage } from '@/pages/KYCVerificationPage'
import { ProfilePage } from '@/pages/ProfilePage'

// Protected Route Component
interface ProtectedRouteProps {
  component: React.ReactNode
  isAuthenticated: boolean
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
  component,
  isAuthenticated,
}) => {
  return isAuthenticated ? (
    <MainLayout>{component}</MainLayout>
  ) : (
    <Navigate to="/login" replace />
  )
}

// Public Route Component
interface PublicRouteProps {
  component: React.ReactNode
  isAuthenticated: boolean
}

const PublicRoute: React.FC<PublicRouteProps> = ({
  component,
  isAuthenticated,
}) => {
  return isAuthenticated ? (
    <Navigate to="/dashboard" replace />
  ) : (
    <AuthLayout>{component}</AuthLayout>
  )
}

// Scroll to Top Component
const ScrollToTop: React.FC = () => {
  const { pathname } = useLocation()

  useEffect(() => {
    window.scrollTo(0, 0)
  }, [pathname])

  return null
}

// Main App Component
const AppContent: React.FC = () => {
  const { isAuthenticated } = useAuthStore()

  return (
    <>
      <ScrollToTop />
      <Routes>
        {/* Auth Routes */}
        <Route
          path="/login"
          element={
            <PublicRoute component={<LoginPage />} isAuthenticated={isAuthenticated} />
          }
        />
        <Route
          path="/register"
          element={
            <PublicRoute
              component={<RegisterPage />}
              isAuthenticated={isAuthenticated}
            />
          }
        />

        {/* Protected Routes */}
        <Route
          path="/dashboard"
          element={
            <ProtectedRoute
              component={<DashboardPage />}
              isAuthenticated={isAuthenticated}
            />
          }
        />

        {/* Product Listings */}
        <Route
          path="/listings"
          element={
            <ProtectedRoute
              component={<ProductListingsPage />}
              isAuthenticated={isAuthenticated}
            />
          }
        />
        <Route
          path="/listings/create"
          element={
            <ProtectedRoute
              component={<CreateProductPage />}
              isAuthenticated={isAuthenticated}
            />
          }
        />
        <Route
          path="/listings/:productId/edit"
          element={
            <ProtectedRoute
              component={<EditProductPage />}
              isAuthenticated={isAuthenticated}
            />
          }
        />

        {/* Payouts */}
        <Route
          path="/payouts"
          element={
            <ProtectedRoute
              component={<PayoutHistoryPage />}
              isAuthenticated={isAuthenticated}
            />
          }
        />

        {/* KYC */}
        <Route
          path="/kyc"
          element={
            <ProtectedRoute
              component={<KYCVerificationPage />}
              isAuthenticated={isAuthenticated}
            />
          }
        />

        {/* Profile */}
        <Route
          path="/profile"
          element={
            <ProtectedRoute
              component={<ProfilePage />}
              isAuthenticated={isAuthenticated}
            />
          }
        />

        {/* Fallback */}
        <Route
          path="/"
          element={
            <Navigate to={isAuthenticated ? '/dashboard' : '/login'} replace />
          }
        />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </>
  )
}

export const App: React.FC = () => {
  return (
    <BrowserRouter>
      <AppContent />
    </BrowserRouter>
  )
}
