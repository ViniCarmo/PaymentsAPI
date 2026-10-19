package com.vinicius.payments.payments_api.payment.domain.entity;

import com.vinicius.payments.payments_api.payment.domain.enums.PaymentStatus;
import com.vinicius.payments.payments_api.payment.domain.exceptions.InvalidPaymentStatusTransitionException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Payment {

    private UUID id;
    private UUID sourceAccountId;
    private UUID destinationAccountId;
    private BigDecimal amount;
    private PaymentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private String idempotencyKey;
    private String failureReason;

    public Payment(UUID id, UUID sourceAccountId, UUID destinationAccountId, BigDecimal amount, PaymentStatus status, LocalDateTime createdAt, LocalDateTime processedAt, String idempotencyKey, String failureReason) {
        this.id = id;
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
        this.idempotencyKey = idempotencyKey;
        this.failureReason = failureReason;
    }

    public void authorize() {
        if (this.status != PaymentStatus.PENDING) {
            throw new InvalidPaymentStatusTransitionException(
                    this.status, PaymentStatus.AUTHORIZED
            );
        }
        this.status = PaymentStatus.AUTHORIZED;
    }

    public void complete() {
        if (this.status != PaymentStatus.AUTHORIZED) {
            throw new InvalidPaymentStatusTransitionException(
                    this.status, PaymentStatus.COMPLETED
            );
        }
        this.status = PaymentStatus.COMPLETED;
        this.processedAt = LocalDateTime.now();
    }

    public void fail(String reason) {
        if (this.status != PaymentStatus.PENDING &&
                this.status != PaymentStatus.AUTHORIZED) {
            throw new InvalidPaymentStatusTransitionException(
                    this.status, PaymentStatus.FAILED
            );
        }
        this.status = PaymentStatus.FAILED;
        this.failureReason = reason;
    }

    public void reverse() {
        if (this.status != PaymentStatus.COMPLETED) {
            throw new InvalidPaymentStatusTransitionException(
                    this.status, PaymentStatus.REVERSED
            );
        }
        this.status = PaymentStatus.REVERSED;
    }

    public UUID getId() {
        return id;
    }

    public UUID getSourceAccountId() {
        return sourceAccountId;
    }

    public UUID getDestinationAccountId() {
        return destinationAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public String getFailureReason() {
        return failureReason;
    }
}
