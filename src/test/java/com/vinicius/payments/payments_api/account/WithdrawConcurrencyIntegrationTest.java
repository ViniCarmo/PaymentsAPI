package com.vinicius.payments.payments_api.account;

import com.vinicius.payments.payments_api.TestcontainersConfiguration;
import com.vinicius.payments.payments_api.account.application.useCases.CreateAccountUseCase;
import com.vinicius.payments.payments_api.account.application.useCases.DepositUseCase;
import com.vinicius.payments.payments_api.account.application.useCases.FindAccountByIdUseCase;
import com.vinicius.payments.payments_api.account.domain.entity.Account;
import com.vinicius.payments.payments_api.account.domain.repository.AccountRepository;
import com.vinicius.payments.payments_api.account.interfaces.dto.AmountRequestDto;
import com.vinicius.payments.payments_api.shared.application.port.TransactionRunner;
import com.vinicius.payments.payments_api.shared.exception.ConcurrentUpdateException;
import com.vinicius.payments.payments_api.user.application.useCases.CreateUserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class WithdrawConcurrencyIntegrationTest {

    private static final int WITHDRAWALS = 100;
    private static final BigDecimal WITHDRAWAL_AMOUNT = new BigDecimal("10.00");
    private static final BigDecimal INITIAL_BALANCE = new BigDecimal("1000.00");

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private CreateUserUseCase createUserUseCase;
    @Autowired
    private CreateAccountUseCase createAccountUseCase;
    @Autowired
    private DepositUseCase depositUseCase;
    @Autowired
    private FindAccountByIdUseCase findAccountByIdUseCase;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRunner transactionRunner;

    private UUID accountId;

    @BeforeEach
    void createFundedAccount() {
        String unique = UUID.randomUUID().toString();
        var user = createUserUseCase.execute("Concurrent User", unique + "@mail.com", unique, "secret123");
        accountId = createAccountUseCase.execute(user.getId()).getId();
        depositUseCase.execute(accountId, INITIAL_BALANCE);
    }

    @Test
    void concurrentWithdrawalsNeverLoseAnUpdate() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(32);
        CountDownLatch startGate = new CountDownLatch(1);
        List<Future<HttpStatusCode>> results = new ArrayList<>();

        try {
            for (int i = 0; i < WITHDRAWALS; i++) {
                results.add(executor.submit(() -> {
                    startGate.await();
                    return restTemplate.postForEntity("/api/v1/accounts/{id}/withdraw",
                            new AmountRequestDto(WITHDRAWAL_AMOUNT), String.class, accountId).getStatusCode();
                }));
            }
            startGate.countDown();

            List<HttpStatusCode> statuses = new ArrayList<>();
            for (Future<HttpStatusCode> result : results) {
                statuses.add(result.get(60, TimeUnit.SECONDS));
            }

            assertThat(statuses).hasSize(WITHDRAWALS).allMatch(status -> status.equals(HttpStatus.OK));
        } finally {
            executor.shutdownNow();
        }

        Account account = findAccountByIdUseCase.execute(accountId);
        assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        // insert (0) + initial deposit (1) + one increment per successful withdrawal
        assertThat(account.getVersion()).isEqualTo(1L + WITHDRAWALS);

        var overdraft = restTemplate.postForEntity("/api/v1/accounts/{id}/withdraw",
                new AmountRequestDto(WITHDRAWAL_AMOUNT), String.class, accountId);
        assertThat(overdraft.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    void staleWriteIsRejectedByOptimisticLock() {
        Account stale = findAccountByIdUseCase.execute(accountId);
        depositUseCase.execute(accountId, BigDecimal.ONE);

        stale.withdraw(INITIAL_BALANCE);

        assertThatThrownBy(() -> transactionRunner.run(() -> accountRepository.save(stale)))
                .isInstanceOf(ConcurrentUpdateException.class);
        assertThat(findAccountByIdUseCase.execute(accountId).getBalance()).isEqualByComparingTo("1001.00");
    }
}
