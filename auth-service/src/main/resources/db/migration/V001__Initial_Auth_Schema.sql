-- V001__Initial_Auth_Schema.sql
-- Initial database schema for auth service

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Users table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(50) NOT NULL DEFAULT 'CUSTOMER' CHECK (role IN ('CUSTOMER', 'VENDOR', 'ADMIN', 'SUPPORT')),
    email_verified BOOLEAN DEFAULT false NOT NULL,
    is_active BOOLEAN DEFAULT true NOT NULL,
    oauth_provider VARCHAR(50),
    oauth_subject VARCHAR(255),
    two_factor_enabled BOOLEAN DEFAULT false NOT NULL,
    two_factor_secret VARCHAR(255),
    created_at TIMESTAMPTZ DEFAULT now() NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT now() NOT NULL
);

-- Indexes for users table
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_created_at ON users(created_at DESC);

-- Refresh tokens table
CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(255) UNIQUE NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked BOOLEAN DEFAULT false NOT NULL,
    device_info TEXT,
    created_at TIMESTAMPTZ DEFAULT now() NOT NULL
);

-- Indexes for refresh_tokens table
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);
CREATE INDEX idx_refresh_tokens_revoked ON refresh_tokens(revoked);

-- Vendors table
CREATE TABLE vendors (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    business_name VARCHAR(255) NOT NULL,
    gstin VARCHAR(15),
    pan VARCHAR(10),
    bank_account VARCHAR(50),
    ifsc_code VARCHAR(11),
    kyc_status VARCHAR(50) NOT NULL DEFAULT 'PENDING' CHECK (kyc_status IN ('PENDING', 'UNDER_REVIEW', 'APPROVED', 'REJECTED')),
    kyc_rejection_reason TEXT,
    commission_rate NUMERIC(5, 2) DEFAULT 10.00 NOT NULL,
    total_revenue NUMERIC(15, 2) DEFAULT 0 NOT NULL,
    is_active BOOLEAN DEFAULT true NOT NULL,
    approved_at TIMESTAMPTZ,
    approved_by UUID REFERENCES users(id),
    created_at TIMESTAMPTZ DEFAULT now() NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT now() NOT NULL
);

-- Indexes for vendors table
CREATE INDEX idx_vendors_user_id ON vendors(user_id);
CREATE INDEX idx_vendors_kyc_status ON vendors(kyc_status);
CREATE INDEX idx_vendors_is_active ON vendors(is_active);
