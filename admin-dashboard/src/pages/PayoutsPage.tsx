import React from 'react'
import { Check, Clock, AlertCircle } from 'lucide-react'

interface Payout {
  id: string
  vendor: string
  amount: number
  period: string
  status: 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED'
  date: string
}

export const PayoutsPage: React.FC = () => {
  const [payouts] = React.useState<Payout[]>([
    { id: 'PAY-001', vendor: 'Tech Store', amount: 2450.00, period: 'Jan 2024', status: 'COMPLETED', date: '2024-02-01' },
    { id: 'PAY-002', vendor: 'Fashion Hub', amount: 890.50, period: 'Jan 2024', status: 'COMPLETED', date: '2024-02-01' },
    { id: 'PAY-003', vendor: 'Home Essentials', amount: 1234.00, period: 'Jan 2024', status: 'PROCESSING', date: '2024-02-05' },
    { id: 'PAY-004', vendor: 'Electronics Plus', amount: 3456.75, period: 'Jan 2024', status: 'PENDING', date: 'TBD' },
  ])

  const getPayoutStatusIcon = (status: string) => {
    switch (status) {
      case 'COMPLETED':
        return <Check className="w-5 h-5 text-green-600" />
      case 'PROCESSING':
        return <Clock className="w-5 h-5 text-yellow-600" />
      case 'PENDING':
        return <Clock className="w-5 h-5 text-blue-600" />
      case 'FAILED':
        return <AlertCircle className="w-5 h-5 text-red-600" />
      default:
        return null
    }
  }

  const getPayoutStatusColor = (status: string) => {
    switch (status) {
      case 'COMPLETED':
        return 'bg-green-100 text-green-800'
      case 'PROCESSING':
        return 'bg-yellow-100 text-yellow-800'
      case 'PENDING':
        return 'bg-blue-100 text-blue-800'
      case 'FAILED':
        return 'bg-red-100 text-red-800'
      default:
        return 'bg-gray-100 text-gray-800'
    }
  }

  return (
    <div className="space-y-6">
      <h1 className="text-3xl font-bold">Payout Management</h1>

      {/* Summary Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-white rounded-lg shadow-md p-6">
          <p className="text-gray-600 text-sm">Total Paid</p>
          <p className="text-3xl font-bold mt-2">$3,574.50</p>
        </div>
        <div className="bg-white rounded-lg shadow-md p-6">
          <p className="text-gray-600 text-sm">Processing</p>
          <p className="text-3xl font-bold mt-2">$1,234.00</p>
        </div>
        <div className="bg-white rounded-lg shadow-md p-6">
          <p className="text-gray-600 text-sm">Pending</p>
          <p className="text-3xl font-bold mt-2">$3,456.75</p>
        </div>
      </div>

      {/* Payouts Table */}
      <div className="bg-white rounded-lg shadow-md overflow-hidden">
        <table className="w-full">
          <thead className="bg-gray-50 border-b border-gray-200">
            <tr>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Payout ID</th>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Vendor</th>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Amount</th>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Period</th>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Status</th>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Date</th>
            </tr>
          </thead>
          <tbody>
            {payouts.map((payout) => (
              <tr key={payout.id} className="border-b border-gray-100 hover:bg-gray-50">
                <td className="px-6 py-4 font-semibold">{payout.id}</td>
                <td className="px-6 py-4 text-gray-600">{payout.vendor}</td>
                <td className="px-6 py-4 font-semibold">${payout.amount.toFixed(2)}</td>
                <td className="px-6 py-4">{payout.period}</td>
                <td className="px-6 py-4">
                  <span className={`px-3 py-1 rounded-full text-xs font-semibold flex items-center gap-1 w-fit ${getPayoutStatusColor(payout.status)}`}>
                    {getPayoutStatusIcon(payout.status)}
                    {payout.status}
                  </span>
                </td>
                <td className="px-6 py-4 text-gray-600">{payout.date}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
