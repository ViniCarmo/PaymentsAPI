package com.vinicius.payments.payments_api.shared.infrastructure.transaction;

import com.vinicius.payments.payments_api.shared.application.port.TransactionRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class TransactionConfig {

    @Bean
    public TransactionRunner transactionRunner(PlatformTransactionManager transactionManager,
                                               @Value("${payments.transaction.max-attempts:3}") int maxAttempts,
                                               @Value("${payments.transaction.backoff-millis:20}") long backoffMillis) {
        return new RetryingTransactionRunner(transactionManager, maxAttempts, backoffMillis);
    }
}
