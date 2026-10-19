CREATE TABLE accounts (
    id         UUID           PRIMARY KEY,
    user_id    UUID           NOT NULL,
    balance    NUMERIC(19, 2) NOT NULL DEFAULT 0,
    version    BIGINT         NOT NULL DEFAULT 0,
    created_at TIMESTAMP      NOT NULL,
    updated_at TIMESTAMP      NOT NULL,
    CONSTRAINT uk_accounts_user_id UNIQUE (user_id),
    CONSTRAINT fk_accounts_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT chk_accounts_balance_non_negative CHECK (balance >= 0)
);
