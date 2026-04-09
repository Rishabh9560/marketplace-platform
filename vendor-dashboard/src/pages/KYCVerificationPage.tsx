import React, { useState } from 'react'
import { Card, Button, Badge, Input } from '@/components/common'
import { Upload, FileText, CheckCircle, AlertCircle, Clock, X } from 'lucide-react'

export const KYCVerificationPage: React.FC = () => {
  const [kycStatus] = useState<'PENDING' | 'SUBMITTED' | 'VERIFIED' | 'REJECTED'>(
    'PENDING'
  )
  const [documents, setDocuments] = useState<File[]>([])
  const [formData, setFormData] = useState({
    fullName: '',
    dateOfBirth: '',
    panNumber: '',
    businessRegistration: '',
    bankAccountNumber: '',
    bankIFSC: '',
    addressProof: '',
  })

  const documentTypes = [
    { type: 'PAN_CARD', label: 'PAN Card', required: true },
    { type: 'AADHAR', label: 'Aadhar Card', required: true },
    { type: 'BANK_STATEMENT', label: 'Bank Statement', required: true },
    { type: 'ADDRESS_PROOF', label: 'Address Proof', required: true },
    { type: 'BUSINESS_REG', label: 'Business Registration', required: false },
  ]

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target
    setFormData((prev) => ({ ...prev, [name]: value }))
  }

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>, docType: string) => {
    if (e.target.files?.[0]) {
      const newFile = {
        name: e.target.files[0].name,
        type: docType,
        file: e.target.files[0],
        url: URL.createObjectURL(e.target.files[0]),
      }
      setDocuments((prev) => [
        ...prev.filter((d) => d.type !== docType),
        newFile,
      ])
    }
  }

  const removeDocument = (docType: string) => {
    setDocuments((prev) => prev.filter((d) => d.type !== docType))
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    console.log('Submitting KYC:', { formData, documents })
    // Mock submission
    alert('KYC documents submitted for verification!')
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
      </Card>

      {/* Form */}
      {kycStatus !== 'VERIFIED' && (
        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Personal Information */}
          <Card className="card-shadow">
            <h3 className="text-lg font-bold text-gray-900 mb-6">Personal Information</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input
                label="Full Name"
                type="text"
                placeholder="John Doe"
                name="fullName"
                value={formData.fullName}
                onChange={handleInputChange}
                required
              />

              <Input
                label="Date of Birth"
                type="date"
                name="dateOfBirth"
                value={formData.dateOfBirth}
                onChange={handleInputChange}
                required
              />

              <Input
                label="PAN Number"
                type="text"
                placeholder="XXXXXXXXXX"
                name="panNumber"
                value={formData.panNumber}
                onChange={handleInputChange}
                required
              />

              <Input
                label="Business Name"
                type="text"
                placeholder="Your Business"
                name="businessRegistration"
                value={formData.businessRegistration}
                onChange={handleInputChange}
              />
            </div>
          </Card>

          {/* Bank Information */}
          <Card className="card-shadow">
            <h3 className="text-lg font-bold text-gray-900 mb-6">Bank Information</h3>
            <div className="space-y-4">
              <Input
                label="Bank Account Number"
                type="text"
                placeholder="1234567890123456"
                name="bankAccountNumber"
                value={formData.bankAccountNumber}
                onChange={handleInputChange}
                required
              />

              <Input
                label="Bank IFSC Code"
                type="text"
                placeholder="SBIN0000001"
                name="bankIFSC"
                value={formData.bankIFSC}
                onChange={handleInputChange}
                required
              />
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
                <div
                  key={doc.type}
                  className="border border-gray-200 rounded-lg p-4"
                >
                  <div className="flex justify-between items-center mb-3">
                    <label className="font-medium text-gray-900">
                      {doc.label}
                      {doc.required && <span className="text-red-600"> *</span>}
                    </label>
                    {documents.find((d) => d.type === doc.type) && (
                      <CheckCircle className="w-5 h-5 text-green-600" />
                    )}
                  </div>

                  {documents.find((d) => d.type === doc.type) ? (
                    <div className="flex items-center justify-between bg-green-50 border border-green-200 rounded p-3">
                      <div className="flex items-center gap-2">
                        <FileText className="w-4 h-4 text-green-600" />
                        <span className="text-sm text-green-800">
                          {documents.find((d) => d.type === doc.type)?.name}
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
                      <p className="text-sm text-gray-600">
                        Click to upload document
                      </p>
                      <p className="text-xs text-gray-500 mt-1">
                        PDF, JPG, PNG up to 10MB
                      </p>
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

          {/* Note */}
          <Card className="bg-blue-50 border border-blue-200 card-shadow">
            <div className="flex gap-3">
              <AlertCircle className="w-5 h-5 text-blue-600 flex-shrink-0 mt-0.5" />
              <div>
                <p className="text-sm font-medium text-blue-900 mb-1">
                  Important Information
                </p>
                <ul className="text-xs text-blue-800 space-y-1">
                  <li>• All documents must be clear and legible</li>
                  <li>• Ensure your name is visible on all documents</li>
                  <li>• Documents should not be outdated or expired</li>
                  <li>
                    • Verification process typically takes 2-3 business days
                  </li>
                </ul>
              </div>
            </div>
          </Card>

          {/* Submit Button */}
          <div className="flex gap-4 justify-end">
            <Button variant="outline" type="button">
              Save as Draft
            </Button>
            <Button type="submit" size="md">
              Submit for Verification
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
            <Button variant="primary">Go to Dashboard</Button>
          </div>
        </Card>
      )}
    </div>
  )
}
