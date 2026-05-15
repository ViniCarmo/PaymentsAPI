package com.vinicius.payments.payments_api.payment.domain.entity;

import com.vinicius.payments.payments_api.payment.domain.enums.StateMachine;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {

    private Integer id;
    private Integer sourceAccountId;
    private Integer destinationAccountId;
    private BigDecimal amount;
    private StateMachine status;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private String idempotenceKey;
    private String failureReason;

    public Payment(Integer id, Integer sourceAccountId, Integer destinationAccountId, BigDecimal amount, StateMachine status, LocalDateTime createdAt, LocalDateTime processedAt, String idempotenceKey, String failureReason) {
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

    public StateMachine getStatus() {
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
