package com.vinicius.payments.payments_api.payment.domain.exceptions;

import com.vinicius.payments.payments_api.payment.domain.enums.PaymentStatus;

public class InvalidPaymentStatusTransitionException extends RuntimeException  {
    public InvalidPaymentStatusTransitionException(PaymentStatus from, PaymentStatus to) {
        super("Cannot transition payment from " + from + " to " + to);
    }
}
