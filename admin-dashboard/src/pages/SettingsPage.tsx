import React, { useState } from 'react'
import { Save } from 'lucide-react'

export const SettingsPage: React.FC = () => {
  const [settings, setSettings] = useState({
    platformName: 'Marketplace Platform',
    platformEmail: 'admin@marketplace.com',
    vendorCommissionDefault: 5,
    maxOrderValue: 50000,
    enablePayments: true,
    enableNotifications: true,
    maintenanceMode: false,
  })

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value, type } = e.target
    setSettings({
      ...settings,
      [name]: type === 'checkbox' ? (e.target as HTMLInputElement).checked : value,
    })
  }

  const handleSave = () => {
    console.log('Settings saved:', settings)
    alert('Settings saved successfully!')
  }

  return (
    <div className="space-y-6">
      <h1 className="text-3xl font-bold">Platform Settings</h1>

      <div className="bg-white rounded-lg shadow-md p-8 max-w-2xl">
        <form className="space-y-6">
          {/* Platform Settings */}
          <div>
            <h2 className="text-xl font-bold mb-4 text-gray-800">Platform Configuration</h2>
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">Platform Name</label>
                <input
                  type="text"
                  name="platformName"
                  value={settings.platformName}
                  onChange={handleChange}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-600 focus:border-transparent"
                />
              </div>
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">Support Email</label>
                <input
                  type="email"
                  name="platformEmail"
                  value={settings.platformEmail}
                  onChange={handleChange}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-600 focus:border-transparent"
                />
              </div>
            </div>
          </div>

          <hr className="border-gray-200" />

          {/* Commission Settings */}
          <div>
            <h2 className="text-xl font-bold mb-4 text-gray-800">Commission & Fees</h2>
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">Default Vendor Commission (%)</label>
                <input
                  type="number"
                  name="vendorCommissionDefault"
                  value={settings.vendorCommissionDefault}
                  onChange={handleChange}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-600 focus:border-transparent"
                />
              </div>
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">Maximum Order Value ($)</label>
                <input
                  type="number"
                  name="maxOrderValue"
                  value={settings.maxOrderValue}
                  onChange={handleChange}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-600 focus:border-transparent"
                />
              </div>
            </div>
          </div>

          <hr className="border-gray-200" />

          {/* Feature Toggles */}
          <div>
            <h2 className="text-xl font-bold mb-4 text-gray-800">Feature Settings</h2>
            <div className="space-y-4">
              <label className="flex items-center">
                <input
                  type="checkbox"
                  name="enablePayments"
                  checked={settings.enablePayments}
                  onChange={handleChange}
                  className="w-5 h-5 text-red-600 rounded focus:ring-2 focus:ring-red-600"
                />
                <span className="ml-3 text-sm font-semibold text-gray-700">Enable Payments</span>
              </label>
              <label className="flex items-center">
                <input
                  type="checkbox"
                  name="enableNotifications"
                  checked={settings.enableNotifications}
                  onChange={handleChange}
                  className="w-5 h-5 text-red-600 rounded focus:ring-2 focus:ring-red-600"
                />
                <span className="ml-3 text-sm font-semibold text-gray-700">Enable Notifications</span>
              </label>
              <label className="flex items-center">
                <input
                  type="checkbox"
                  name="maintenanceMode"
                  checked={settings.maintenanceMode}
                  onChange={handleChange}
                  className="w-5 h-5 text-red-600 rounded focus:ring-2 focus:ring-red-600"
                />
                <span className="ml-3 text-sm font-semibold text-gray-700">Maintenance Mode</span>
              </label>
            </div>
          </div>

          <hr className="border-gray-200" />

          {/* Save Button */}
          <div className="flex gap-4">
            <button
              type="button"
              onClick={handleSave}
              className="flex items-center gap-2 bg-red-600 text-white px-6 py-3 rounded-lg hover:bg-red-700 transition font-semibold"
            >
              <Save className="w-5 h-5" />
              Save Settings
            </button>
            <button
              type="button"
              className="px-6 py-3 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition font-semibold"
            >
              Reset to Defaults
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
