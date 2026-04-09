-- Payment Service Schema Migration
-- Created: 2026-04-08

CREATE TABLE transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    stripe_payment_method_id VARCHAR(100) NOT NULL UNIQUE,
    stripe_charge_id VARCHAR(100) NOT NULL UNIQUE,
    payment_method VARCHAR(50) NOT NULL,
    amount NUMERIC(10, 2) NOT NULL,
    currency VARCHAR(10),
    status VARCHAR(50) NOT NULL,
    failure_reason VARCHAR(255),
    is_refunded BOOLEAN,
    refund_amount NUMERIC(10, 2),
    refund_transaction_id VARCHAR(100),
    
    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP,
    refunded_at TIMESTAMP
);

-- Indexes for Transactions Table
CREATE INDEX idx_transactions_order ON transactions(order_id);
CREATE INDEX idx_transactions_customer ON transactions(customer_id);
CREATE INDEX idx_transactions_status ON transactions(status);
CREATE INDEX idx_transactions_created ON transactions(created_at);

-- Add updated_at trigger
CREATE OR REPLACE FUNCTION update_transactions_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_transactions_updated_at BEFORE UPDATE ON transactions
FOR EACH ROW EXECUTE FUNCTION update_transactions_updated_at_column();
