import React, { useState } from 'react'
import { Edit, Trash2, CheckCircle, XCircle } from 'lucide-react'

interface Vendor {
  id: string
  name: string
  email: string
  kycStatus: 'PENDING' | 'VERIFIED' | 'REJECTED'
  commission: number
  totalEarnings: number
  status: 'ACTIVE' | 'SUSPENDED'
}

export const VendorsPage: React.FC = () => {
  const [vendors] = useState<Vendor[]>([
    { id: '1', name: 'Tech Store', email: 'tech@store.com', kycStatus: 'VERIFIED', commission: 5, totalEarnings: 45000, status: 'ACTIVE' },
    { id: '2', name: 'Fashion Hub', email: 'fashion@hub.com', kycStatus: 'PENDING', commission: 8, totalEarnings: 12000, status: 'ACTIVE' },
    { id: '3', name: 'Home Essentials', email: 'home@essentials.com', kycStatus: 'VERIFIED', commission: 6, totalEarnings: 28000, status: 'SUSPENDED' },
  ])

  const getKYCBadge = (status: string) => {
    switch (status) {
      case 'VERIFIED':
        return <span className="bg-green-100 text-green-800 px-3 py-1 rounded-full text-xs font-semibold flex items-center gap-1"><CheckCircle className="w-4 h-4" /> Verified</span>
      case 'PENDING':
        return <span className="bg-yellow-100 text-yellow-800 px-3 py-1 rounded-full text-xs font-semibold">Pending</span>
      case 'REJECTED':
        return <span className="bg-red-100 text-red-800 px-3 py-1 rounded-full text-xs font-semibold flex items-center gap-1"><XCircle className="w-4 h-4" /> Rejected</span>
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold">Vendor Management</h1>
        <button className="bg-red-600 text-white px-6 py-2 rounded-lg hover:bg-red-700 transition">
          Add Vendor
        </button>
      </div>

      <div className="bg-white rounded-lg shadow-md overflow-hidden">
        <table className="w-full">
          <thead className="bg-gray-50 border-b border-gray-200">
            <tr>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Vendor Name</th>
              <th className="px-6 py-ρ text-left text-gray-600 font-semibold">Email</th>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">KYC Status</th>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Commission</th>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Total Earnings</th>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Status</th>
              <th className="px-6 py-3 text-left text-gray-600 font-semibold">Actions</th>
            </tr>
          </thead>
          <tbody>
            {vendors.map((vendor) => (
              <tr key={vendor.id} className="border-b border-gray-100 hover:bg-gray-50">
                <td className="px-6 py-4 font-semibold">{vendor.name}</td>
                <td className="px-6 py-4 text-gray-600">{vendor.email}</td>
                <td className="px-6 py-4">{getKYCBadge(vendor.kycStatus)}</td>
                <td className="px-6 py-4 font-semibold">{vendor.commission}%</td>
                <td className="px-6 py-4 font-semibold">${vendor.totalEarnings.toLocaleString()}</td>
                <td className="px-6 py-4">
                  <span className={`px-3 py-1 rounded-full text-xs font-semibold ${vendor.status === 'ACTIVE' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>
                    {vendor.status}
                  </span>
                </td>
                <td className="px-6 py-4">
                  <div className="flex gap-2">
                    <button className="p-2 text-blue-600 hover:bg-blue-50 rounded-lg transition">
                      <Edit className="w-5 h-5" />
                    </button>
                    <button className="p-2 text-red-600 hover:bg-red-50 rounded-lg transition">
                      <Trash2 className="w-5 h-5" />
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
