import React, { useState, useEffect } from 'react'
import { Eye, Download, CheckCircle, XCircle, FileText, MessageSquare } from 'lucide-react'
import axios from 'axios'

interface KYCVendor {
  id: string
  name: string
  email: string
  businessName: string
  kycStatus: 'PENDING' | 'SUBMITTED' | 'VERIFIED' | 'REJECTED'
  submittedAt: string
  businessLicense: string
  taxId: string
  bankDetails: string
  documents: Array<{
    type: string
    url: string
    uploadedAt: string
  }>
  rejectionReason?: string
}

export const KYCReviewPage: React.FC = () => {
  const [loading, setLoading] = useState(false)
  const [vendors, setVendors] = useState<KYCVendor[]>([])
  const [selectedVendor, setSelectedVendor] = useState<KYCVendor | null>(null)
  const [showReviewModal, setShowReviewModal] = useState(false)
  const [rejectionReason, setRejectionReason] = useState('')
  const [actionLoading, setActionLoading] = useState(false)
  const [filterStatus, setFilterStatus] = useState<string>('SUBMITTED')
  const [searchTerm, setSearchTerm] = useState('')
  const [currentPage, setCurrentPage] = useState(1)
  const [itemsPerPage] = useState(5)

  // Fetch KYC vendors on mount
  useEffect(() => {
    fetchKYCVendors()
  }, [filterStatus])

  const fetchKYCVendors = async () => {
    setLoading(true)
    try {
      let url = `/api/v1/kyc`
      if (filterStatus === 'SUBMITTED') {
        url += `/pending`
      } else if (filterStatus === 'REJECTED') {
        url += `/rejected`
      } else if (filterStatus === 'VERIFIED') {
        url += `/verified`
      }

      const response = await axios.get(url, {
        params: { page: 0, size: 50 },
      })

      const vendorList = response.data?.data?.content || response.data?.data || []
      setVendors(vendorList)
    } catch (error) {
      console.error('Error fetching KYC vendors:', error)
      // Mock data for demo
      setVendors([
        {
          id: '1',
          name: 'John Doe',
          email: 'john@store.com',
          businessName: 'Tech Store Ltd',
          kycStatus: 'SUBMITTED',
          submittedAt: '2024-04-20T10:30:00Z',
          businessLicense: 'BL-2024-001',
          taxId: 'TAX-123456',
          bankDetails: 'HDFC Bank, Acc: ****1234',
          documents: [
            { type: 'PAN', url: '#', uploadedAt: '2024-04-20T10:30:00Z' },
            { type: 'AADHAR', url: '#', uploadedAt: '2024-04-20T10:30:00Z' },
            { type: 'BUSINESS_LICENSE', url: '#', uploadedAt: '2024-04-20T10:30:00Z' },
            { type: 'BANK_STATEMENT', url: '#', uploadedAt: '2024-04-20T10:30:00Z' },
          ],
        },
        {
          id: '2',
          name: 'Jane Smith',
          email: 'jane@fashion.com',
          businessName: 'Fashion Hub',
          kycStatus: 'SUBMITTED',
          submittedAt: '2024-04-19T15:45:00Z',
          businessLicense: 'BL-2024-002',
          taxId: 'TAX-234567',
          bankDetails: 'ICICI Bank, Acc: ****5678',
          documents: [
            { type: 'PAN', url: '#', uploadedAt: '2024-04-19T15:45:00Z' },
            { type: 'AADHAR', url: '#', uploadedAt: '2024-04-19T15:45:00Z' },
          ],
        },
      ])
    } finally {
      setLoading(false)
    }
  }

  const handleApprove = async () => {
    if (!selectedVendor) return

    setActionLoading(true)
    try {
      await axios.post(`/api/v1/kyc/${selectedVendor.id}/verify`)
      alert('KYC approved successfully!')
      setShowReviewModal(false)
      setSelectedVendor(null)
      fetchKYCVendors()
    } catch (error: any) {
      console.error('Error approving KYC:', error)
      alert(error.response?.data?.message || 'Error approving KYC')
    } finally {
      setActionLoading(false)
    }
  }

  const handleReject = async () => {
    if (!selectedVendor || !rejectionReason.trim()) {
      alert('Please enter rejection reason')
      return
    }

    setActionLoading(true)
    try {
      await axios.post(
        `/api/v1/kyc/${selectedVendor.id}/reject`,
        {},
        { params: { reason: rejectionReason } }
      )
      alert('KYC rejected successfully!')
      setShowReviewModal(false)
      setSelectedVendor(null)
      setRejectionReason('')
      fetchKYCVendors()
    } catch (error: any) {
      console.error('Error rejecting KYC:', error)
      alert(error.response?.data?.message || 'Error rejecting KYC')
    } finally {
      setActionLoading(false)
    }
  }

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'VERIFIED':
        return <span className="bg-green-100 text-green-800 px-3 py-1 rounded-full text-xs font-semibold flex items-center gap-1"><CheckCircle className="w-4 h-4" /> Verified</span>
      case 'REJECTED':
        return <span className="bg-red-100 text-red-800 px-3 py-1 rounded-full text-xs font-semibold flex items-center gap-1"><XCircle className="w-4 h-4" /> Rejected</span>
      case 'SUBMITTED':
        return <span className="bg-yellow-100 text-yellow-800 px-3 py-1 rounded-full text-xs font-semibold">Pending Review</span>
      default:
        return <span className="bg-gray-100 text-gray-800 px-3 py-1 rounded-full text-xs font-semibold">{status}</span>
    }
  }

  const filteredVendors = vendors.filter((vendor) => {
    const matchesSearch =
      vendor.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      vendor.email.toLowerCase().includes(searchTerm.toLowerCase())
    return matchesSearch
  })

  const paginatedVendors = filteredVendors.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  )

  const totalPages = Math.ceil(filteredVendors.length / itemsPerPage)

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-3xl font-bold text-gray-900 mb-2">KYC Review & Approval</h1>
        <p className="text-gray-600">Review and approve vendor KYC submissions</p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex justify-between items-center">
            <div>
              <p className="text-gray-600 text-sm mb-1">Pending Review</p>
              <p className="text-2xl font-bold">
                {vendors.filter((v) => v.kycStatus === 'SUBMITTED').length}
              </p>
            </div>
            <MessageSquare className="w-8 h-8 text-yellow-500 opacity-50" />
          </div>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex justify-between items-center">
            <div>
              <p className="text-gray-600 text-sm mb-1">Verified</p>
              <p className="text-2xl font-bold">
                {vendors.filter((v) => v.kycStatus === 'VERIFIED').length}
              </p>
            </div>
            <CheckCircle className="w-8 h-8 text-green-500 opacity-50" />
          </div>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex justify-between items-center">
            <div>
              <p className="text-gray-600 text-sm mb-1">Rejected</p>
              <p className="text-2xl font-bold">
                {vendors.filter((v) => v.kycStatus === 'REJECTED').length}
              </p>
            </div>
            <XCircle className="w-8 h-8 text-red-500 opacity-50" />
          </div>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex justify-between items-center">
            <div>
              <p className="text-gray-600 text-sm mb-1">Total</p>
              <p className="text-2xl font-bold">{vendors.length}</p>
            </div>
            <FileText className="w-8 h-8 text-blue-500 opacity-50" />
          </div>
        </div>
      </div>

      {/* Filters */}
      <div className="bg-white rounded-lg shadow p-6">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Filter by Status
            </label>
            <select
              value={filterStatus}
              onChange={(e) => {
                setFilterStatus(e.target.value)
                setCurrentPage(1)
              }}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="SUBMITTED">Pending Review</option>
              <option value="VERIFIED">Verified</option>
              <option value="REJECTED">Rejected</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Search
            </label>
            <input
              type="text"
              placeholder="Search by name or email..."
              value={searchTerm}
              onChange={(e) => {
                setSearchTerm(e.target.value)
                setCurrentPage(1)
              }}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <div className="flex items-end gap-2">
            <button 
              onClick={fetchKYCVendors} 
              disabled={loading}
              className="w-full px-4 py-2 bg-gray-600 text-white rounded-lg hover:bg-gray-700 disabled:opacity-50 transition"
            >
              {loading ? 'Loading...' : 'Refresh'}
            </button>
          </div>
        </div>
      </div>

      {/* Vendors Table */}
      <div className="bg-white rounded-lg shadow overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gray-50 border-b border-gray-200">
              <tr>
                <th className="px-6 py-3 text-left text-gray-600 font-semibold text-sm">
                  Vendor Name
                </th>
                <th className="px-6 py-3 text-left text-gray-600 font-semibold text-sm">
                  Email
                </th>
                <th className="px-6 py-3 text-left text-gray-600 font-semibold text-sm">
                  Business
                </th>
                <th className="px-6 py-3 text-left text-gray-600 font-semibold text-sm">
                  Submitted
                </th>
                <th className="px-6 py-3 text-left text-gray-600 font-semibold text-sm">
                  Status
                </th>
                <th className="px-6 py-3 text-left text-gray-600 font-semibold text-sm">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody>
              {paginatedVendors.map((vendor) => (
                <tr key={vendor.id} className="border-b border-gray-100 hover:bg-gray-50 transition">
                  <td className="px-6 py-4 font-medium text-gray-900">{vendor.name}</td>
                  <td className="px-6 py-4 text-gray-600 text-sm">{vendor.email}</td>
                  <td className="px-6 py-4 text-gray-600 text-sm">{vendor.businessName}</td>
                  <td className="px-6 py-4 text-gray-600 text-sm">
                    {new Date(vendor.submittedAt).toLocaleDateString()}
                  </td>
                  <td className="px-6 py-4">{getStatusBadge(vendor.kycStatus)}</td>
                  <td className="px-6 py-4">
                    <button
                      onClick={() => {
                        setSelectedVendor(vendor)
                        setShowReviewModal(true)
                      }}
                      className="inline-flex items-center gap-1 px-3 py-2 bg-blue-50 text-blue-700 rounded-lg hover:bg-blue-100 transition text-sm font-medium"
                    >
                      <Eye className="w-4 h-4" />
                      Review
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        {totalPages > 1 && (
          <div className="px-6 py-4 border-t border-gray-200 flex justify-between items-center">
            <div className="text-sm text-gray-600">
              Page {currentPage} of {totalPages}
            </div>
            <div className="flex gap-2">
              <button
                onClick={() => setCurrentPage((p) => Math.max(1, p - 1))}
                disabled={currentPage === 1}
                className="px-3 py-1 text-sm border border-gray-300 rounded hover:bg-gray-50 disabled:opacity-50"
              >
                Previous
              </button>
              <button
                onClick={() => setCurrentPage((p) => Math.min(totalPages, p + 1))}
                disabled={currentPage === totalPages}
                className="px-3 py-1 text-sm border border-gray-300 rounded hover:bg-gray-50 disabled:opacity-50"
              >
                Next
              </button>
            </div>
          </div>
        )}
      </div>

      {/* Review Modal */}
      {showReviewModal && selectedVendor && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="w-full max-w-2xl max-h-[90vh] overflow-y-auto bg-white rounded-lg shadow-xl">
            <div className="flex justify-between items-center mb-6 p-6 border-b border-gray-200">
              <h2 className="text-2xl font-bold text-gray-900">KYC Review</h2>
              <button
                onClick={() => {
                  setShowReviewModal(false)
                  setSelectedVendor(null)
                  setRejectionReason('')
                }}
                className="text-gray-400 hover:text-gray-600"
              >
                ✕
              </button>
            </div>

            <div className="p-6 space-y-6">
              {/* Vendor Info */}
              <div className="bg-gray-50 rounded-lg p-4 space-y-3">
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <p className="text-sm text-gray-600">Vendor Name</p>
                    <p className="font-semibold">{selectedVendor.name}</p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-600">Email</p>
                    <p className="font-semibold">{selectedVendor.email}</p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-600">Business Name</p>
                    <p className="font-semibold">{selectedVendor.businessName}</p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-600">Tax ID</p>
                    <p className="font-semibold">{selectedVendor.taxId}</p>
                  </div>
                </div>
              </div>

              {/* Documents */}
              <div>
                <h3 className="font-semibold text-gray-900 mb-3 flex items-center gap-2">
                  <FileText className="w-5 h-5" />
                  Submitted Documents
                </h3>
                <div className="space-y-2">
                  {selectedVendor.documents.map((doc, idx) => (
                    <div key={idx} className="flex items-center justify-between bg-gray-50 p-3 rounded-lg">
                      <div className="flex items-center gap-3">
                        <FileText className="w-5 h-5 text-blue-600" />
                        <div>
                          <p className="font-medium text-sm text-gray-900">{doc.type}</p>
                          <p className="text-xs text-gray-600">
                            {new Date(doc.uploadedAt).toLocaleDateString()}
                          </p>
                        </div>
                      </div>
                      <a
                        href={doc.url}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="inline-flex items-center gap-1 px-2 py-1 text-blue-600 hover:bg-blue-50 rounded text-sm"
                      >
                        <Download className="w-4 h-4" />
                        View
                      </a>
                    </div>
                  ))}
                </div>
              </div>

              {/* Rejection Reason (if rejecting) */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Rejection Reason (if rejecting)
                </label>
                <textarea
                  value={rejectionReason}
                  onChange={(e) => setRejectionReason(e.target.value)}
                  placeholder="Enter reason for rejection (if applicable)"
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500 resize-none"
                  rows={4}
                />
              </div>
            </div>

            {/* Action Buttons */}
            <div className="flex gap-3 justify-end p-6 border-t border-gray-200 bg-gray-50">
              <button
                onClick={() => {
                  setShowReviewModal(false)
                  setSelectedVendor(null)
                  setRejectionReason('')
                }}
                disabled={actionLoading}
                className="px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-100 disabled:opacity-50 transition"
              >
                Close
              </button>
              <button
                onClick={handleReject}
                disabled={actionLoading || !rejectionReason.trim()}
                className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 disabled:opacity-50 disabled:cursor-not-allowed transition"
              >
                {actionLoading ? 'Processing...' : 'Reject'}
              </button>
              <button
                onClick={handleApprove}
                disabled={actionLoading}
                className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 transition"
              >
                {actionLoading ? 'Processing...' : 'Approve'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
