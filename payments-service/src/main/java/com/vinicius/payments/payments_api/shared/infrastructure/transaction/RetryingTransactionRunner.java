package com.vinicius.payments.payments_api.shared.infrastructure.transaction;

import com.vinicius.payments.payments_api.shared.application.port.TransactionRunner;
import com.vinicius.payments.payments_api.shared.exception.ConcurrentUpdateException;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

/**
 * Runs each unit of work in its own transaction and retries it, with a short jittered backoff,
 * when it fails with an optimistic or pessimistic locking conflict.
 */
public class RetryingTransactionRunner implements TransactionRunner {

    private final TransactionTemplate transactionTemplate;
    private final int maxAttempts;
    private final long backoffMillis;

    public RetryingTransactionRunner(PlatformTransactionManager transactionManager, int maxAttempts, long backoffMillis) {
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("maxAttempts must be at least 1");
        }
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.maxAttempts = maxAttempts;
        this.backoffMillis = backoffMillis;
    }

    @Override
    public <T> T run(Supplier<T> work) {
        for (int attempt = 1; ; attempt++) {
            try {
                return transactionTemplate.execute(status -> work.get());
            } catch (ConcurrencyFailureException ex) {
                if (attempt >= maxAttempts) {
                    throw new ConcurrentUpdateException("Resource was modified concurrently, please retry", ex);
                }
                backoff(attempt);
            }
        }
    }

    private void backoff(int attempt) {
        if (backoffMillis <= 0) {
            return;
        }
        long delay = backoffMillis * attempt + ThreadLocalRandom.current().nextLong(backoffMillis);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ConcurrentUpdateException("Interrupted while waiting to retry", ex);
        }
    }
}
