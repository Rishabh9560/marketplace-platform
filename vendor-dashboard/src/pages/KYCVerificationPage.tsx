import React, { useState, useEffect } from 'react'
import { Card, Button, Badge } from '@/components/common'
import { Upload, FileText, CheckCircle, AlertCircle, Clock, X } from 'lucide-react'
import { useAuthStore } from '@/store'
import { apiClient } from '@/lib/apiClient'

interface DocumentUpload {
  type: 'PAN' | 'AADHAR' | 'BUSINESS_LICENSE' | 'BANK_STATEMENT'
  file: File | null
  preview?: string
  uploaded?: boolean
}

export const KYCVerificationPage: React.FC = () => {
  const { vendor } = useAuthStore()
  const [loading, setLoading] = useState(false)
  const [kycStatus, setKycStatus] = useState<'PENDING' | 'SUBMITTED' | 'VERIFIED' | 'REJECTED'>('PENDING')
  const [rejectionReason, setRejectionReason] = useState('')
  const [documents, setDocuments] = useState<Record<string, DocumentUpload>>({
    PAN: { type: 'PAN', file: null },
    AADHAR: { type: 'AADHAR', file: null },
    BUSINESS_LICENSE: { type: 'BUSINESS_LICENSE', file: null },
    BANK_STATEMENT: { type: 'BANK_STATEMENT', file: null },
  })
  const [formData, setFormData] = useState({
    businessName: '',
    taxId: '',
    businessLicenseNumber: '',
    bankAccountNumber: '',
    bankRoutingNumber: '',
    bankName: '',
  })

  const documentTypes = [
    { type: 'PAN' as const, label: 'PAN Card', required: true },
    { type: 'AADHAR' as const, label: 'Aadhar Card', required: true },
    { type: 'BUSINESS_LICENSE' as const, label: 'Business License', required: true },
    { type: 'BANK_STATEMENT' as const, label: 'Bank Statement', required: true },
  ]

  // Fetch KYC status on mount
  useEffect(() => {
    if (vendor?.id) {
      fetchKYCStatus()
    }
  }, [vendor?.id])

  const fetchKYCStatus = async () => {
    try {
      const response = await apiClient.get(`/kyc/${vendor?.id}/status`)
      const kycData = response?.data as any || {}
      if (kycData?.verified) {
        setKycStatus('VERIFIED')
      } else if (kycData?.status === 'SUBMITTED') {
        setKycStatus('SUBMITTED')
        setRejectionReason(kycData?.rejectionReason || '')
      } else if (kycData?.status === 'REJECTED') {
        setKycStatus('REJECTED')
        setRejectionReason(kycData?.rejectionReason || '')
      } else {
        setKycStatus('PENDING')
      }
    } catch (error) {
      console.error('Error fetching KYC status:', error)
    }
  }

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target
    setFormData((prev) => ({ ...prev, [name]: value }))
  }

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>, docType: 'PAN' | 'AADHAR' | 'BUSINESS_LICENSE' | 'BANK_STATEMENT') => {
    if (e.target.files?.[0]) {
      const file = e.target.files[0]
      const preview = URL.createObjectURL(file)
      setDocuments((prev) => ({
        ...prev,
        [docType]: {
          type: docType,
          file,
          preview,
        },
      }))
    }
  }

  const removeDocument = (docType: string) => {
    setDocuments((prev) => ({
      ...prev,
      [docType]: {
        type: docType as any,
        file: null,
        preview: undefined,
      },
    }))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)

    try {
      // Validate all required documents are uploaded
      const missingDocs = documentTypes
        .filter((doc) => doc.required && !documents[doc.type].file)
        .map((doc) => doc.label)

      if (missingDocs.length > 0) {
        alert(`Please upload required documents: ${missingDocs.join(', ')}`)
        setLoading(false)
        return
      }

      // Upload documents
      const uploadedDocs: any[] = []
      for (const [type, doc] of Object.entries(documents)) {
        if (doc.file) {
          const uploadFormData = new FormData()
          uploadFormData.append('file', doc.file)
          uploadFormData.append('documentType', type)

          try {
            const uploadResponse = await fetch('/api/v1/kyc/documents/upload', {
              method: 'POST',
              headers: {
                'Authorization': `Bearer ${localStorage.getItem('auth_token')}`,
              },
              body: uploadFormData,
            })

            let responseData: any = null
            try {
              responseData = await uploadResponse.json()
            } catch (parseError) {
              console.error(`Error parsing response for ${type}:`, parseError)
              throw new Error(`Invalid response format from server`)
            }

            console.log(`Upload response for ${type}:`, { status: uploadResponse.status, data: responseData })

            if (!uploadResponse.ok) {
              const errorMsg = responseData?.message || `HTTP ${uploadResponse.status}: ${uploadResponse.statusText}`
              throw new Error(errorMsg)
            }

            // Handle nested ApiResponse structure
            const uploadUrl = responseData?.data?.url || responseData?.url
            if (!uploadUrl) {
              console.error(`No URL in response for ${type}:`, responseData)
              throw new Error(`No document URL returned from server`)
            }
            
            uploadedDocs.push({
              type,
              url: uploadUrl,
            })
            console.log(`Successfully uploaded ${type} with URL: ${uploadUrl}`)
          } catch (uploadError) {
            console.error(`Error uploading ${type}:`, uploadError)
            const errorMsg = uploadError instanceof Error ? uploadError.message : String(uploadError)
            alert(`Failed to upload ${type}. Error: ${errorMsg}`)
            setLoading(false)
            return
          }
        }
      }

      // Submit KYC
      const submitResponse = await apiClient.post('/kyc/submit', {
        vendorId: vendor?.id,
        ...formData,
        documents: uploadedDocs,
      })

      if (submitResponse.success || submitResponse.statusCode === 201) {
        setKycStatus('SUBMITTED')
        alert('KYC documents submitted successfully! Awaiting admin verification.')
      } else {
        throw new Error(submitResponse?.message || 'Failed to submit KYC')
      }
    } catch (error: any) {
      console.error('Error submitting KYC:', error)
      const errorMessage = error?.response?.data?.message || error?.message || 'Error submitting KYC. Please try again.'
      alert(errorMessage)
    } finally {
      setLoading(false)
    }
  }

  const getStatusIcon = () => {
    switch (kycStatus) {
      case 'VERIFIED':
        return <CheckCircle className="w-6 h-6 text-green-600" />
      case 'REJECTED':
        return <X className="w-6 h-6 text-red-600" />
      case 'SUBMITTED':
        return <Clock className="w-6 h-6 text-yellow-600" />
      default:
        return <AlertCircle className="w-6 h-6 text-gray-600" />
    }
  }

  const getStatusColor = () => {
    switch (kycStatus) {
      case 'VERIFIED':
        return 'bg-green-50 border-green-200'
      case 'REJECTED':
        return 'bg-red-50 border-red-200'
      case 'SUBMITTED':
        return 'bg-yellow-50 border-yellow-200'
      default:
        return 'bg-gray-50 border-gray-200'
    }
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-3xl font-bold text-gray-900 mb-2">KYC Verification</h1>
        <p className="text-gray-600">
          Complete your identity verification to unlock all features
        </p>
      </div>

      {/* Status Card */}
      <Card className={`card-shadow border-2 ${getStatusColor()}`}>
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-4">
            {getStatusIcon()}
            <div>
              <h3 className="text-lg font-bold text-gray-900 mb-1">
                KYC Status: {kycStatus}
              </h3>
              <p className="text-sm text-gray-600">
                {kycStatus === 'VERIFIED' &&
                  'Your identity has been verified successfully.'}
                {kycStatus === 'SUBMITTED' &&
                  'Your documents are under review. This usually takes 2-3 business days.'}
                {kycStatus === 'PENDING' &&
                  'Please complete your KYC verification to increase selling limits.'}
                {kycStatus === 'REJECTED' &&
                  'Your submission was rejected. Please resubmit with correct documents.'}
              </p>
            </div>
          </div>
          <Badge
            className={
              kycStatus === 'VERIFIED'
                ? 'bg-green-100 text-green-800'
                : kycStatus === 'REJECTED'
                  ? 'bg-red-100 text-red-800'
                  : 'bg-yellow-100 text-yellow-800'
            }
          >
            {kycStatus}
          </Badge>
        </div>
        
        {/* Rejection Reason */}
        {kycStatus === 'REJECTED' && rejectionReason && (
          <div className="mt-4 p-3 bg-red-100 border border-red-300 rounded text-red-800 text-sm">
            <strong>Reason:</strong> {rejectionReason}
          </div>
        )}
      </Card>

      {/* Form */}
      {kycStatus !== 'VERIFIED' && (
        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Business Information */}
          <Card className="card-shadow">
            <h3 className="text-lg font-bold text-gray-900 mb-6">Business Information</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Business Name *
                </label>
                <input
                  type="text"
                  placeholder="Your Business Name"
                  name="businessName"
                  value={formData.businessName}
                  onChange={handleInputChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Tax ID / TIN *
                </label>
                <input
                  type="text"
                  placeholder="Tax ID"
                  name="taxId"
                  value={formData.taxId}
                  onChange={handleInputChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Business License Number *
                </label>
                <input
                  type="text"
                  placeholder="License Number"
                  name="businessLicenseNumber"
                  value={formData.businessLicenseNumber}
                  onChange={handleInputChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
                />
              </div>
            </div>
          </Card>

          {/* Bank Information */}
          <Card className="card-shadow">
            <h3 className="text-lg font-bold text-gray-900 mb-6">Bank Information</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Bank Account Number *
                </label>
                <input
                  type="text"
                  placeholder="1234567890123456"
                  name="bankAccountNumber"
                  value={formData.bankAccountNumber}
                  onChange={handleInputChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Bank Routing Number *
                </label>
                <input
                  type="text"
                  placeholder="Routing Number"
                  name="bankRoutingNumber"
                  value={formData.bankRoutingNumber}
                  onChange={handleInputChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
                />
              </div>

              <div className="md:col-span-2">
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Bank Name *
                </label>
                <input
                  type="text"
                  placeholder="Bank Name"
                  name="bankName"
                  value={formData.bankName}
                  onChange={handleInputChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
                />
              </div>
            </div>
          </Card>

          {/* Document Upload */}
          <Card className="card-shadow">
            <h3 className="text-lg font-bold text-gray-900 mb-6 flex items-center gap-2">
              <FileText className="w-5 h-5" />
              Document Upload
            </h3>

            <div className="space-y-6">
              {documentTypes.map((doc) => (
                <div key={doc.type} className="border border-gray-200 rounded-lg p-4">
                  <div className="flex justify-between items-center mb-3">
                    <label className="font-medium text-gray-900">
                      {doc.label}
                      {doc.required && <span className="text-red-600"> *</span>}
                    </label>
                    {documents[doc.type]?.file && (
                      <CheckCircle className="w-5 h-5 text-green-600" />
                    )}
                  </div>

                  {documents[doc.type]?.file ? (
                    <div className="flex items-center justify-between bg-green-50 border border-green-200 rounded p-3">
                      <div className="flex items-center gap-2">
                        <FileText className="w-4 h-4 text-green-600" />
                        <span className="text-sm text-green-800">
                          {documents[doc.type].file?.name}
                        </span>
                      </div>
                      <button
                        type="button"
                        onClick={() => removeDocument(doc.type)}
                        className="text-red-600 hover:text-red-700"
                      >
                        <X className="w-4 h-4" />
                      </button>
                    </div>
                  ) : (
                    <label className="border-2 border-dashed border-gray-300 rounded-lg p-6 text-center cursor-pointer hover:border-blue-500 hover:bg-blue-50 transition-colors block">
                      <Upload className="w-6 h-6 text-gray-400 mx-auto mb-2" />
                      <p className="text-sm text-gray-600">Click to upload document</p>
                      <p className="text-xs text-gray-500 mt-1">PDF, JPG, PNG up to 10MB</p>
                      <input
                        type="file"
                        onChange={(e) => handleFileChange(e, doc.type)}
                        accept=".pdf,.jpg,.jpeg,.png"
                        className="hidden"
                      />
                    </label>
                  )}
                </div>
              ))}
            </div>
          </Card>

          {/* Important Note */}
          <Card className="bg-blue-50 border border-blue-200 card-shadow">
            <div className="flex gap-3">
              <AlertCircle className="w-5 h-5 text-blue-600 flex-shrink-0 mt-0.5" />
              <div>
                <p className="text-sm font-medium text-blue-900 mb-1">Important Information</p>
                <ul className="text-xs text-blue-800 space-y-1">
                  <li>• All documents must be clear and legible</li>
                  <li>• Ensure your name is visible on all documents</li>
                  <li>• Documents should not be outdated or expired</li>
                  <li>• Verification process typically takes 2-3 business days</li>
                </ul>
              </div>
            </div>
          </Card>

          {/* Submit Button */}
          <div className="flex gap-4 justify-end">
            <Button 
              variant="outline" 
              type="button"
              disabled={loading}
            >
              Cancel
            </Button>
            <Button 
              type="submit" 
              disabled={loading}
            >
              {loading ? 'Submitting...' : 'Submit for Verification'}
            </Button>
          </div>
        </form>
      )}

      {/* Verified State */}
      {kycStatus === 'VERIFIED' && (
        <Card className="card-shadow border-2 border-green-200">
          <div className="text-center py-12">
            <CheckCircle className="w-16 h-16 text-green-600 mx-auto mb-4" />
            <h3 className="text-2xl font-bold text-gray-900 mb-2">
              Verification Complete!
            </h3>
            <p className="text-gray-600 mb-6">
              Your identity has been verified successfully. You now have access to all
              features and can start selling without limits.
            </p>
            <a href="/dashboard">
              <Button variant="primary">Go to Dashboard</Button>
            </a>
          </div>
        </Card>
      )}
    </div>
  )
}
