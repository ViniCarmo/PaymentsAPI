package com.vinicius.payments.payments_api.payment.domain.entity;

import com.vinicius.payments.payments_api.payment.domain.enums.PaymentStatus;
import com.vinicius.payments.payments_api.payment.domain.exceptions.InvalidPaymentStatusTransitionException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {

    private Integer id;
    private Integer sourceAccountId;
    private Integer destinationAccountId;
    private BigDecimal amount;
    private PaymentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private String idempotenceKey;
    private String failureReason;

    public Payment(Integer id, Integer sourceAccountId, Integer destinationAccountId, BigDecimal amount, PaymentStatus status, LocalDateTime createdAt, LocalDateTime processedAt, String idempotenceKey, String failureReason) {
        this.id = id;
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
        this.idempotenceKey = idempotenceKey;
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

    public Integer getId() {
        return id;
    }

    public Integer getSourceAccountId() {
        return sourceAccountId;
    }

    public Integer getDestinationAccountId() {
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

    public String getIdempotenceKey() {
        return idempotenceKey;
    }

    public String getFailureReason() {
        return failureReason;
    }
}
