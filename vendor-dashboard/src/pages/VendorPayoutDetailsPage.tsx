import React from 'react'
import { Eye, Download } from 'lucide-react'

interface PayoutDetail {
  id: string
  week: string
  totalOrders: number
  totalRevenue: number
  commission: number
  netAmount: number
  status: 'PENDING' | 'PROCESSING' | 'COMPLETED'
}

export const VendorPayoutDetailsPage: React.FC = () => {
  const [activeDetail, setActiveDetail] = React.useState<string | null>(null)
  const [details] = React.useState<PayoutDetail[]>([
    {
      id: '1',
      week: 'Jan 15-21, 2024',
      totalOrders: 45,
      totalRevenue: 5234.50,
      commission: 261.73,
      netAmount: 4972.77,
      status: 'COMPLETED',
    },
    {
      id: '2',
      week: 'Jan 22-28, 2024',
      totalOrders: 52,
      totalRevenue: 6123.75,
      commission: 306.19,
      netAmount: 5817.56,
      status: 'COMPLETED',
    },
    {
      id: '3',
      week: 'Jan 29 - Feb 4, 2024',
      totalOrders: 38,
      totalRevenue: 4567.20,
      commission: 228.36,
      netAmount: 4338.84,
      status: 'PROCESSING',
    },
  ])

  return (
    <div className="space-y-6">
      <h1 className="text-3xl font-bold">Payout Details</h1>

      {/* Summary */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <div className="bg-white rounded-lg shadow-md p-6">
          <p className="text-gray-600 text-sm">Total Revenue</p>
          <p className="text-3xl font-bold mt-2">$ {details.reduce((sum, d) => sum + d.totalRevenue, 0).toFixed(2)}</p>
        </div>
        <div className="bg-white rounded-lg shadow-md p-6">
          <p className="text-gray-600 text-sm">Total Commission</p>
          <p className="text-3xl font-bold mt-2">$ {details.reduce((sum, d) => sum + d.commission, 0).toFixed(2)}</p>
        </div>
        <div className="bg-white rounded-lg shadow-md p-6">
          <p className="text-gray-600 text-sm">Net Payable</p>
          <p className="text-3xl font-bold mt-2 text-green-600">$ {details.reduce((sum, d) => sum + d.netAmount, 0).toFixed(2)}</p>
        </div>
        <div className="bg-white rounded-lg shadow-md p-6">
          <p className="text-gray-600 text-sm">Commission Rate</p>
          <p className="text-3xl font-bold mt-2">5%</p>
        </div>
      </div>

      {/* Details Table */}
      <div className="bg-white rounded-lg shadow-md overflow-hidden">
        <table className="w-full">
          <thead className="bg-gray-50 border-b border-gray-200">
            <tr>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Period</th>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Orders</th>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Revenue</th>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Commission (5%)</th>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Net Payout</th>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Status</th>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Actions</th>
            </tr>
          </thead>
          <tbody>
            {details.map((detail) => (
              <tr key={detail.id} className="border-b border-gray-100 hover:bg-gray-50">
                <td className="px-6 py-4 font-semibold">{detail.week}</td>
                <td className="px-6 py-4">{detail.totalOrders}</td>
                <td className="px-6 py-4 font-semibold">${detail.totalRevenue.toFixed(2)}</td>
                <td className="px-6 py-4">${detail.commission.toFixed(2)}</td>
                <td className="px-6 py-4 font-bold text-green-600">${detail.netAmount.toFixed(2)}</td>
                <td className="px-6 py-4">
                  <span
                    className={`px-3 py-1 rounded-full text-xs font-semibold ${
                      detail.status === 'COMPLETED'
                        ? 'bg-green-100 text-green-800'
                        : detail.status === 'PROCESSING'
                          ? 'bg-yellow-100 text-yellow-800'
                          : 'bg-gray-100 text-gray-800'
                    }`}
                  >
                    {detail.status}
                  </span>
                </td>
                <td className="px-6 py-4">
                  <div className="flex gap-2">
                    <button
                      onClick={() => setActiveDetail(activeDetail === detail.id ? null : detail.id)}
                      className="p-2 text-blue-600 hover:bg-blue-50 rounded-lg transition"
                      title="View Details"
                    >
                      <Eye className="w-5 h-5" />
                    </button>
                    <button className="p-2 text-green-600 hover:bg-green-50 rounded-lg transition" title="Download Invoice">
                      <Download className="w-5 h-5" />
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Details Accordion */}
      {activeDetail && (
        <div className="bg-white rounded-lg shadow-md p-6">
          <h3 className="text-lg font-bold mb-4">
            Detailed Breakdown - {details.find((d) => d.id === activeDetail)?.week}
          </h3>
          <div className="space-y-3">
            <div className="flex justify-between pb-3 border-b border-gray-200">
              <span className="text-gray-600">Total Orders</span>
              <span className="font-semibold">{details.find((d) => d.id === activeDetail)?.totalOrders}</span>
            </div>
            <div className="flex justify-between pb-3 border-b border-gray-200">
              <span className="text-gray-600">Total Revenue</span>
              <span className="font-semibold">${details.find((d) => d.id === activeDetail)?.totalRevenue.toFixed(2)}</span>
            </div>
            <div className="flex justify-between pb-3 border-b border-gray-200">
              <span className="text-gray-600">Commission Rate</span>
              <span className="font-semibold">5%</span>
            </div>
            <div className="flex justify-between pb-3 border-b border-gray-200">
              <span className="text-gray-600">Commission Charged</span>
              <span className="font-semibold text-red-600">
                -${details.find((d) => d.id === activeDetail)?.commission.toFixed(2)}
              </span>
            </div>
            <div className="flex justify-between pt-3 bg-green-50 px-4 py-3 rounded-lg">
              <span className="font-bold text-lg">Net Payout</span>
              <span className="font-bold text-green-600 text-lg">
                ${details.find((d) => d.id === activeDetail)?.netAmount.toFixed(2)}
              </span>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
