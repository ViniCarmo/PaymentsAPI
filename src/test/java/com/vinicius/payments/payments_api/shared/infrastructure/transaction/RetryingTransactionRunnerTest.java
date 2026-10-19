package com.vinicius.payments.payments_api.shared.infrastructure.transaction;

import com.vinicius.payments.payments_api.shared.exception.ConcurrentUpdateException;
import org.junit.jupiter.api.Test;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.SimpleTransactionStatus;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RetryingTransactionRunnerTest {

    private final PlatformTransactionManager transactionManager = mock(PlatformTransactionManager.class);
    private final RetryingTransactionRunner runner = new RetryingTransactionRunner(transactionManager, 3, 0);

    RetryingTransactionRunnerTest() {
        when(transactionManager.getTransaction(any())).thenAnswer(invocation -> new SimpleTransactionStatus());
    }

    @Test
    void returnsResultWithinATransaction() {
        assertThat(runner.run(() -> "done")).isEqualTo("done");

        verify(transactionManager).commit(any());
    }

    @Test
    void retriesOptimisticLockFailuresUntilSuccess() {
        AtomicInteger calls = new AtomicInteger();

        String result = runner.run(() -> {
            if (calls.incrementAndGet() < 3) {
                throw new ObjectOptimisticLockingFailureException(Object.class, "id");
            }
            return "done";
        });

        assertThat(result).isEqualTo("done");
        assertThat(calls).hasValue(3);
        verify(transactionManager, times(2)).rollback(any());
    }

    @Test
    void givesUpAfterMaxAttemptsWithConcurrentUpdateException() {
        AtomicInteger calls = new AtomicInteger();

        assertThatThrownBy(() -> runner.run(() -> {
            calls.incrementAndGet();
            throw new ObjectOptimisticLockingFailureException(Object.class, "id");
        })).isInstanceOf(ConcurrentUpdateException.class)
                .hasCauseInstanceOf(ObjectOptimisticLockingFailureException.class);

        assertThat(calls).hasValue(3);
    }

    @Test
    void doesNotRetryOtherExceptions() {
        AtomicInteger calls = new AtomicInteger();

        assertThatThrownBy(() -> runner.run(() -> {
            calls.incrementAndGet();
            throw new IllegalStateException("boom");
        })).isInstanceOf(IllegalStateException.class);

        assertThat(calls).hasValue(1);
    }
}
