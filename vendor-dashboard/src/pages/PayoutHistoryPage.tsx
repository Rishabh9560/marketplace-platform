import React, { useState } from 'react'
import { Card, Badge, Button } from '@/components/common'
import { formatCurrency, formatDate, getStatusColor } from '@/lib/utils'
import { Download, Filter, Search, ChevronLeft, ChevronRight, DollarSign } from 'lucide-react'

export const PayoutHistoryPage: React.FC = () => {
  const [searchTerm, setSearchTerm] = useState('')
  const [statusFilter, setStatusFilter] = useState<string | null>(null)
  const [currentPage, setCurrentPage] = useState(1)

  // Mock payout data
  const mockPayouts = [
    {
      id: '1',
      payoutId: 'PAYOUT-2024-001',
      period: '2024-03-01 to 2024-03-31',
      totalSalesAmount: 54321,
      commissionDeducted: 2716.05,
      netPayoutAmount: 51604.95,
      status: 'COMPLETED',
      transactionId: 'TXN-2024-001',
      payoutDate: '2024-04-05T10:30:00Z',
      method: 'Bank Transfer',
    },
    {
      id: '2',
      payoutId: 'PAYOUT-2024-002',
      period: '2024-02-01 to 2024-02-29',
      totalSalesAmount: 43210,
      commissionDeducted: 2160.5,
      netPayoutAmount: 41049.5,
      status: 'COMPLETED',
      transactionId: 'TXN-2024-002',
      payoutDate: '2024-03-05T14:20:00Z',
      method: 'Bank Transfer',
    },
    {
      id: '3',
      payoutId: 'PAYOUT-2024-003',
      period: '2024-01-01 to 2024-01-31',
      totalSalesAmount: 38500,
      commissionDeducted: 1925,
      netPayoutAmount: 36575,
      status: 'PENDING',
      transactionId: 'TXN-2024-003',
      payoutDate: null,
      method: 'Bank Transfer',
    },
  ]

  const filteredPayouts = mockPayouts.filter((payout) => {
    const matchesSearch =
      payout.payoutId.toLowerCase().includes(searchTerm.toLowerCase()) ||
      payout.transactionId.toLowerCase().includes(searchTerm.toLowerCase())
    const matchesStatus = statusFilter ? payout.status === statusFilter : true
    return matchesSearch && matchesStatus
  })

  const itemsPerPage = 10
  const paginatedPayouts = filteredPayouts.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  )
  const totalPages = Math.ceil(filteredPayouts.length / itemsPerPage)

  // Calculate totals
  const totalSalesAmount = filteredPayouts.reduce(
    (sum, p) => sum + p.totalSalesAmount,
    0
  )
  const totalCommission = filteredPayouts.reduce(
    (sum, p) => sum + p.commissionDeducted,
    0
  )
  const totalNetPayout = filteredPayouts.reduce(
    (sum, p) => sum + p.netPayoutAmount,
    0
  )

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-3xl font-bold text-gray-900 mb-2">Payout History</h1>
        <p className="text-gray-600">Track all your monthly payouts and settlements</p>
      </div>

      {/* Summary Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <Card className="card-shadow">
          <div className="flex items-start justify-between">
            <div>
              <p className="text-gray-600 text-sm font-medium mb-1">Total Sales</p>
              <p className="text-2xl font-bold text-gray-900">
                {formatCurrency(totalSalesAmount)}
              </p>
            </div>
            <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
              <DollarSign className="w-6 h-6 text-blue-600" />
            </div>
          </div>
        </Card>

        <Card className="card-shadow">
          <div className="flex items-start justify-between">
            <div>
              <p className="text-gray-600 text-sm font-medium mb-1">Commission Deducted</p>
              <p className="text-2xl font-bold text-gray-900">
                {formatCurrency(totalCommission)}
              </p>
            </div>
            <div className="w-12 h-12 bg-red-100 rounded-lg flex items-center justify-center">
              <span className="text-lg">💰</span>
            </div>
          </div>
        </Card>

        <Card className="card-shadow">
          <div className="flex items-start justify-between">
            <div>
              <p className="text-gray-600 text-sm font-medium mb-1">Net Payout</p>
              <p className="text-2xl font-bold text-green-600">
                {formatCurrency(totalNetPayout)}
              </p>
            </div>
            <div className="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center">
              <span className="text-lg">✅</span>
            </div>
          </div>
        </Card>
      </div>

      {/* Filters & Search */}
      <Card className="card-shadow">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 items-end">
          <div>
            <label className="text-sm font-medium text-gray-700 block mb-2">
              <Search className="w-4 h-4 inline mr-2" />
              Search Payouts
            </label>
            <input
              type="text"
              placeholder="Search by Payout ID or Transaction ID..."
              value={searchTerm}
              onChange={(e) => {
                setSearchTerm(e.target.value)
                setCurrentPage(1)
              }}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <div>
            <label className="text-sm font-medium text-gray-700 block mb-2">
              <Filter className="w-4 h-4 inline mr-2" />
              Status
            </label>
            <select
              value={statusFilter || ''}
              onChange={(e) => {
                setStatusFilter(e.target.value || null)
                setCurrentPage(1)
              }}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">All Status</option>
              <option value="COMPLETED">Completed</option>
              <option value="PENDING">Pending</option>
              <option value="FAILED">Failed</option>
            </select>
          </div>

          <Button variant="outline" fullWidth>
            Download Report
            <Download className="w-4 h-4 ml-2" />
          </Button>
        </div>
      </Card>

      {/* Payouts Table */}
      <Card className="card-shadow overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gray-50 border-b">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-700 uppercase">
                  Payout ID
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-700 uppercase">
                  Period
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-700 uppercase">
                  Sales Amount
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-700 uppercase">
                  Commission
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-700 uppercase">
                  Net Payout
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-700 uppercase">
                  Status
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-700 uppercase">
                  Date
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-700 uppercase">
                  Action
                </th>
              </tr>
            </thead>
            <tbody>
              {paginatedPayouts.map((payout) => (
                <tr
                  key={payout.id}
                  className="border-b hover:bg-gray-50 transition-colors"
                >
                  <td className="px-6 py-4 font-mono text-sm text-gray-900">
                    {payout.payoutId}
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-600">
                    {payout.period}
                  </td>
                  <td className="px-6 py-4 font-medium text-gray-900">
                    {formatCurrency(payout.totalSalesAmount)}
                  </td>
                  <td className="px-6 py-4 text-gray-800">
                    {formatCurrency(payout.commissionDeducted)}
                    <p className="text-xs text-gray-500">
                      (
                      {(
                        (payout.commissionDeducted / payout.totalSalesAmount) *
                        100
                      ).toFixed(1)}
                      %)
                    </p>
                  </td>
                  <td className="px-6 py-4 font-bold text-green-600">
                    {formatCurrency(payout.netPayoutAmount)}
                  </td>
                  <td className="px-6 py-4">
                    <Badge className={getStatusColor(payout.status)}>
                      {payout.status}
                    </Badge>
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-600">
                    {payout.payoutDate
                      ? formatDate(payout.payoutDate)
                      : 'Pending'}
                  </td>
                  <td className="px-6 py-4">
                    <button className="text-blue-600 hover:text-blue-700 text-sm font-medium">
                      View Details
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        <div className="flex items-center justify-between p-6 border-t bg-gray-50">
          <p className="text-sm text-gray-600">
            Showing {(currentPage - 1) * itemsPerPage + 1} to{' '}
            {Math.min(currentPage * itemsPerPage, filteredPayouts.length)} of{' '}
            {filteredPayouts.length} payouts
          </p>
          <div className="flex gap-2">
            <button
              onClick={() => setCurrentPage(Math.max(1, currentPage - 1))}
              disabled={currentPage === 1}
              className="p-2 rounded-lg border border-gray-300 hover:bg-gray-100 disabled:opacity-50"
            >
              <ChevronLeft className="w-4 h-4" />
            </button>
            <div className="flex items-center gap-1 px-3 py-2">
              {Array.from({ length: totalPages }, (_, i) => i + 1).map((page) => (
                <button
                  key={page}
                  onClick={() => setCurrentPage(page)}
                  className={`w-8 h-8 rounded-lg ${
                    page === currentPage
                      ? 'bg-blue-600 text-white'
                      : 'border border-gray-300 hover:bg-gray-100'
                  }`}
                >
                  {page}
                </button>
              ))}
            </div>
            <button
              onClick={() => setCurrentPage(Math.min(totalPages, currentPage + 1))}
              disabled={currentPage === totalPages}
              className="p-2 rounded-lg border border-gray-300 hover:bg-gray-100 disabled:opacity-50"
            >
              <ChevronRight className="w-4 h-4" />
            </button>
          </div>
        </div>
      </Card>
    </div>
  )
}
