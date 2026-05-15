package com.vinicius.payments.payments_api.payment.domain.enums;

public enum StateMachine {
    PENDING,
    AUTHORIZED,
    COMPLETED,
    FAILED,
    REVERSED
}
