package com.vinicius.payments.payments_api.payment.domain.entity;

import com.vinicius.payments.payments_api.payment.domain.enums.PaymentStatus;
import com.vinicius.payments.payments_api.payment.domain.exceptions.InvalidPaymentStatusTransitionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;

class PaymentTest {

    private static Payment paymentIn(PaymentStatus status) {
        return new Payment(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("10.00"),
                status, LocalDateTime.now(), null, "idempotency-key", null);
    }

    @Test
    void authorizeMovesPendingToAuthorized() {
        Payment payment = paymentIn(PaymentStatus.PENDING);

        payment.authorize();

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.AUTHORIZED);
        assertThat(payment.getProcessedAt()).isNull();
    }

    @ParameterizedTest
    @EnumSource(value = PaymentStatus.class, names = "PENDING", mode = EXCLUDE)
    void authorizeIsRejectedFromAnyOtherStatus(PaymentStatus status) {
        Payment payment = paymentIn(status);

        assertThatThrownBy(payment::authorize)
                .isInstanceOf(InvalidPaymentStatusTransitionException.class)
                .hasMessage("Cannot transition payment from " + status + " to AUTHORIZED");
        assertThat(payment.getStatus()).isEqualTo(status);
    }

    @Test
    void completeMovesAuthorizedToCompletedAndSetsProcessedAt() {
        Payment payment = paymentIn(PaymentStatus.AUTHORIZED);

        payment.complete();

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(payment.getProcessedAt()).isNotNull();
    }

    @ParameterizedTest
    @EnumSource(value = PaymentStatus.class, names = "AUTHORIZED", mode = EXCLUDE)
    void completeIsRejectedFromAnyOtherStatus(PaymentStatus status) {
        Payment payment = paymentIn(status);

        assertThatThrownBy(payment::complete)
                .isInstanceOf(InvalidPaymentStatusTransitionException.class);
        assertThat(payment.getStatus()).isEqualTo(status);
        assertThat(payment.getProcessedAt()).isNull();
    }

    @ParameterizedTest
    @EnumSource(value = PaymentStatus.class, names = {"PENDING", "AUTHORIZED"})
    void failMovesPendingOrAuthorizedToFailedWithReason(PaymentStatus status) {
        Payment payment = paymentIn(status);

        payment.fail("card declined");

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
        assertThat(payment.getFailureReason()).isEqualTo("card declined");
    }

    @ParameterizedTest
    @EnumSource(value = PaymentStatus.class, names = {"PENDING", "AUTHORIZED"}, mode = EXCLUDE)
    void failIsRejectedFromAnyOtherStatus(PaymentStatus status) {
        Payment payment = paymentIn(status);

        assertThatThrownBy(() -> payment.fail("card declined"))
                .isInstanceOf(InvalidPaymentStatusTransitionException.class);
        assertThat(payment.getStatus()).isEqualTo(status);
        assertThat(payment.getFailureReason()).isNull();
    }

    @Test
    void reverseMovesCompletedToReversed() {
        Payment payment = paymentIn(PaymentStatus.COMPLETED);

        payment.reverse();

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.REVERSED);
    }

    @ParameterizedTest
    @EnumSource(value = PaymentStatus.class, names = "COMPLETED", mode = EXCLUDE)
    void reverseIsRejectedFromAnyOtherStatus(PaymentStatus status) {
        Payment payment = paymentIn(status);

        assertThatThrownBy(payment::reverse)
                .isInstanceOf(InvalidPaymentStatusTransitionException.class);
        assertThat(payment.getStatus()).isEqualTo(status);
    }

    @Test
    void fullLifecyclePendingToReversed() {
        Payment payment = paymentIn(PaymentStatus.PENDING);

        payment.authorize();
        payment.complete();
        payment.reverse();

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.REVERSED);
        assertThat(payment.getIdempotencyKey()).isEqualTo("idempotency-key");
    }
}
