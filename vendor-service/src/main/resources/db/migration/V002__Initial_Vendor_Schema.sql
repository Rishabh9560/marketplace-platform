-- V002__Initial_Vendor_Schema.sql
-- Vendor-specific schema additions for vendor management, KYC, and additional analytics

CREATE TABLE IF NOT EXISTS kyc_documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vendor_id UUID NOT NULL REFERENCES vendors(id) ON DELETE CASCADE,
    document_type VARCHAR(50) NOT NULL,
    document_url TEXT NOT NULL,
    uploaded_at TIMESTAMPTZ DEFAULT now() NOT NULL,
    verified_at TIMESTAMPTZ,
    verification_notes TEXT
);

CREATE INDEX idx_kyc_documents_vendor_id ON kyc_documents(vendor_id);
CREATE INDEX idx_kyc_documents_verified_at ON kyc_documents(verified_at);

-- Vendor settings table
CREATE TABLE IF NOT EXISTS vendor_settings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vendor_id UUID UNIQUE NOT NULL REFERENCES vendors(id) ON DELETE CASCADE,
    logo_url TEXT,
    banner_url TEXT,
    description TEXT,
    support_email VARCHAR(255),
    support_phone VARCHAR(20),
    return_policy TEXT,
    cancellation_policy TEXT,
    auto_accept_returns BOOLEAN DEFAULT false,
    return_window_days INTEGER DEFAULT 30,
    created_at TIMESTAMPTZ DEFAULT now() NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT now() NOT NULL
);

CREATE INDEX idx_vendor_settings_vendor_id ON vendor_settings(vendor_id);

-- Vendor analytics table
CREATE TABLE IF NOT EXISTS vendor_analytics (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vendor_id UUID NOT NULL REFERENCES vendors(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    total_orders INTEGER DEFAULT 0,
    total_revenue NUMERIC(12, 2) DEFAULT 0,
    total_items_sold INTEGER DEFAULT 0,
    avg_order_value NUMERIC(10, 2) DEFAULT 0,
    return_rate NUMERIC(5, 2) DEFAULT 0,
    created_at TIMESTAMPTZ DEFAULT now() NOT NULL
);

CREATE INDEX idx_vendor_analytics_vendor_id ON vendor_analytics(vendor_id);
CREATE INDEX idx_vendor_analytics_date ON vendor_analytics(date);
CREATE UNIQUE INDEX idx_vendor_analytics_vendor_date ON vendor_analytics(vendor_id, date);
