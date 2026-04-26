import React from 'react'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { Sidebar, TopBar } from '@/components/Sidebar'
import { AdminDashboard } from '@/pages/AdminDashboard'
import { VendorsPage } from '@/pages/VendorsPage'
import { ProductsPage } from '@/pages/ProductsPage'
import { OrdersPage } from '@/pages/OrdersPage'
import { PayoutsPage } from '@/pages/PayoutsPage'
import { AnalyticsPage } from '@/pages/AnalyticsPage'
import { SettingsPage } from '@/pages/SettingsPage'
import { AdminLoginPage } from '@/pages/AdminLoginPage'
import { AdminProfilePage } from '@/pages/AdminProfilePage'
import { KYCReviewPage } from '@/pages/KYCReviewPage'

export const App: React.FC = () => {
  const isAdminAuthenticated = localStorage.getItem('admin_token') !== null

  return (
    <BrowserRouter>
      {/* Login Page - Full Width */}
      <Routes>
        <Route path="/admin-login" element={<AdminLoginPage />} />
      </Routes>

      {/* Admin Pages with Sidebar */}
      {isAdminAuthenticated && (
        <div className="flex h-screen bg-gray-100">
          <Sidebar />
          <div className="flex-1 flex flex-col overflow-hidden">
            <TopBar />
            <main className="flex-1 overflow-y-auto p-6">
              <Routes>
                <Route path="/admin" element={<AdminDashboard />} />
                <Route path="/admin/vendors" element={<VendorsPage />} />
                <Route path="/admin/kyc-review" element={<KYCReviewPage />} />
                <Route path="/admin/products" element={<ProductsPage />} />
                <Route path="/admin/orders" element={<OrdersPage />} />
                <Route path="/admin/payouts" element={<PayoutsPage />} />
                <Route path="/admin/analytics" element={<AnalyticsPage />} />
                <Route path="/admin/settings" element={<SettingsPage />} />
                <Route path="/admin/profile" element={<AdminProfilePage />} />
                <Route path="*" element={<Navigate to="/admin" replace />} />
              </Routes>
            </main>
          </div>
        </div>
      )}

      {/* Redirect unauthenticated users to login */}
      {!isAdminAuthenticated && (
        <Routes>
          <Route path="*" element={<Navigate to="/admin-login" replace />} />
        </Routes>
      )}
    </BrowserRouter>
  )
}
