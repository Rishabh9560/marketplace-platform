import React from 'react'
import { Facebook, Twitter, Instagram, Mail } from 'lucide-react'

export const Footer: React.FC = () => {
  return (
    <footer className="bg-gray-800 text-white mt-16">
      <div className="max-w-7xl mx-auto px-4 py-12">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
          {/* About */}
          <div>
            <h3 className="font-bold text-lg mb-4">About Marketplace</h3>
            <p className="text-gray-400 text-sm leading-relaxed">
              Your trusted platform for shopping from multiple vendors. Quality products, competitive prices, and excellent customer service.
            </p>
          </div>

          {/* Links */}
          <div>
            <h3 className="font-bold text-lg mb-4">Quick Links</h3>
            <ul className="space-y-2 text-sm">
              <li><a href="/" className="text-gray-400 hover:text-white transition">Home</a></li>
              <li><a href="/products" className="text-gray-400 hover:text-white transition">Products</a></li>
              <li><a href="/about" className="text-gray-400 hover:text-white transition">About Us</a></li>
              <li><a href="/contact" className="text-gray-400 hover:text-white transition">Contact</a></li>
            </ul>
          </div>

          {/* Support */}
          <div>
            <h3 className="font-bold text-lg mb-4">Support</h3>
            <ul className="space-y-2 text-sm">
              <li><a href="/help" className="text-gray-400 hover:text-white transition">Help Center</a></li>
              <li><a href="/returns" className="text-gray-400 hover:text-white transition">Returns</a></li>
              <li><a href="/faq" className="text-gray-400 hover:text-white transition">FAQ</a></li>
              <li><a href="/shipping" className="text-gray-400 hover:text-white transition">Shipping Info</a></li>
            </ul>
          </div>

          {/* Social */}
          <div>
            <h3 className="font-bold text-lg mb-4">Follow Us</h3>
            <div className="flex gap-4">
              <a href="#" className="text-gray-400 hover:text-blue-400 transition"><Facebook className="w-6 h-6" /></a>
              <a href="#" className="text-gray-400 hover:text-blue-400 transition"><Twitter className="w-6 h-6" /></a>
              <a href="#" className="text-gray-400 hover:text-pink-400 transition"><Instagram className="w-6 h-6" /></a>
              <a href="#" className="text-gray-400 hover:text-red-400 transition"><Mail className="w-6 h-6" /></a>
            </div>
          </div>
        </div>

        <div className="border-t border-gray-700 mt-8 pt-8 flex flex-col md:flex-row justify-between items-center text-gray-400 text-sm">
          <p>&copy; 2024 Marketplace. All rights reserved.</p>
          <div className="flex gap-6 mt-4 md:mt-0">
            <a href="/privacy" className="hover:text-white transition">Privacy Policy</a>
            <a href="/terms" className="hover:text-white transition">Terms & Conditions</a>
          </div>
        </div>
      </div>
    </footer>
  )
}
