CREATE TABLE IF NOT EXISTS bank_stocks (
    stock_name VARCHAR(255) PRIMARY KEY,
    quantity INTEGER NOT NULL CHECK (quantity >= 0)
);

CREATE TABLE IF NOT EXISTS wallets (id VARCHAR(255) PRIMARY KEY);

CREATE TABLE IF NOT EXISTS wallet_stocks (
    wallet_id VARCHAR(255) NOT NULL REFERENCES wallets (id) ON DELETE CASCADE,
    stock_name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity >= 0),
    PRIMARY KEY (wallet_id, stock_name)
);

CREATE TABLE IF NOT EXISTS audit_log (
    id BIGSERIAL PRIMARY KEY,
    operation_type VARCHAR(4) NOT NULL CHECK (
        operation_type IN ('buy', 'sell')
    ),
    wallet_id VARCHAR(255) NOT NULL,
    stock_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_audit_log_wallet_id ON audit_log (wallet_id);

CREATE INDEX IF NOT EXISTS idx_audit_log_created_at ON audit_log (created_at);