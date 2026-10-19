package com.vinicius.payments.payments_api.account.application.useCases;

import com.vinicius.payments.payments_api.account.domain.entity.Account;
import com.vinicius.payments.payments_api.account.domain.exception.AccountNotFoundException;
import com.vinicius.payments.payments_api.account.domain.exception.InsufficientBalanceException;
import com.vinicius.payments.payments_api.account.domain.exception.InvalidAmountException;
import com.vinicius.payments.payments_api.account.domain.exception.UserAlreadyHasAnAccountException;
import com.vinicius.payments.payments_api.account.domain.repository.AccountRepository;
import com.vinicius.payments.payments_api.shared.application.port.TransactionRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountUseCasesTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRunner transactionRunner;

    private final UUID accountId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    private Account accountWithBalance(String balance) {
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        return new Account(accountId, userId, new BigDecimal(balance), 3L, createdAt, createdAt);
    }

    private void saveReturnsArgument() {
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private void transactionRunnerRunsWork() {
        when(transactionRunner.run(any())).thenAnswer(invocation -> invocation.<Supplier<?>>getArgument(0).get());
    }

    @Nested
    class CreateAccount {

        private CreateAccountUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new CreateAccountUseCase(accountRepository);
        }

        @Test
        void createsAccountWithZeroBalance() {
            when(accountRepository.findByUserId(userId)).thenReturn(Optional.empty());
            saveReturnsArgument();

            Account created = useCase.execute(userId);

            assertThat(created.getUserId()).isEqualTo(userId);
            assertThat(created.getBalance()).isEqualByComparingTo("0");
            verify(accountRepository).save(created);
        }

        @Test
        void rejectsSecondAccountForSameUser() {
            when(accountRepository.findByUserId(userId)).thenReturn(Optional.of(accountWithBalance("0")));

            assertThatThrownBy(() -> useCase.execute(userId)).isInstanceOf(UserAlreadyHasAnAccountException.class);

            verify(accountRepository, never()).save(any());
        }
    }

    @Nested
    class DeleteAccount {

        private DeleteAccountUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new DeleteAccountUseCase(accountRepository);
        }

        @Test
        void deletesExistingAccount() {
            when(accountRepository.findById(accountId)).thenReturn(Optional.of(accountWithBalance("0")));

            useCase.execute(accountId);

            verify(accountRepository).deleteById(accountId);
        }

        @Test
        void throwsWhenAccountDoesNotExist() {
            when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.execute(accountId)).isInstanceOf(AccountNotFoundException.class);

            verify(accountRepository, never()).deleteById(any());
        }
    }

    @Nested
    class FindAccountById {

        private FindAccountByIdUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new FindAccountByIdUseCase(accountRepository);
        }

        @Test
        void returnsAccount() {
            Account account = accountWithBalance("10.00");
            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

            assertThat(useCase.execute(accountId)).isSameAs(account);
        }

        @Test
        void throwsWhenAccountDoesNotExist() {
            when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.execute(accountId)).isInstanceOf(AccountNotFoundException.class);
        }
    }

    @Nested
    class FindByUserId {

        private FindByUserIdUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new FindByUserIdUseCase(accountRepository);
        }

        @Test
        void returnsAccountOfUser() {
            Account account = accountWithBalance("10.00");
            when(accountRepository.findByUserId(userId)).thenReturn(Optional.of(account));

            assertThat(useCase.execute(userId)).isSameAs(account);
        }

        @Test
        void throwsWhenUserHasNoAccount() {
            when(accountRepository.findByUserId(userId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.execute(userId)).isInstanceOf(AccountNotFoundException.class);
        }
    }

    @Nested
    class Deposit {

        private DepositUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new DepositUseCase(accountRepository, transactionRunner);
            transactionRunnerRunsWork();
        }

        @Test
        void addsAmountToLockedAccountInsideTransaction() {
            when(accountRepository.findByIdForUpdate(accountId)).thenReturn(Optional.of(accountWithBalance("100.00")));
            saveReturnsArgument();

            Account result = useCase.execute(accountId, new BigDecimal("50.00"));

            assertThat(result.getBalance()).isEqualByComparingTo("150.00");
            assertThat(result.getVersion()).isEqualTo(3L);
            verify(transactionRunner).run(any());
            verify(accountRepository, never()).findById(any());
        }

        @Test
        void throwsWhenAccountDoesNotExist() {
            when(accountRepository.findByIdForUpdate(accountId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.execute(accountId, BigDecimal.TEN))
                    .isInstanceOf(AccountNotFoundException.class);

            verify(accountRepository, never()).save(any());
        }

        @Test
        void propagatesInvalidAmountWithoutSaving() {
            when(accountRepository.findByIdForUpdate(accountId)).thenReturn(Optional.of(accountWithBalance("100.00")));

            assertThatThrownBy(() -> useCase.execute(accountId, BigDecimal.ZERO))
                    .isInstanceOf(InvalidAmountException.class);

            verify(accountRepository, never()).save(any());
        }
    }

    @Nested
    class Withdraw {

        private WithdrawUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new WithdrawUseCase(accountRepository, transactionRunner);
            transactionRunnerRunsWork();
        }

        @Test
        void subtractsAmountFromLockedAccountInsideTransaction() {
            when(accountRepository.findByIdForUpdate(accountId)).thenReturn(Optional.of(accountWithBalance("100.00")));
            saveReturnsArgument();

            Account result = useCase.execute(accountId, new BigDecimal("30.00"));

            assertThat(result.getBalance()).isEqualByComparingTo("70.00");
            verify(transactionRunner).run(any());
            verify(accountRepository, never()).findById(any());
        }

        @Test
        void rejectsWithdrawalAboveBalanceWithoutSaving() {
            when(accountRepository.findByIdForUpdate(accountId)).thenReturn(Optional.of(accountWithBalance("10.00")));

            assertThatThrownBy(() -> useCase.execute(accountId, new BigDecimal("10.01")))
                    .isInstanceOf(InsufficientBalanceException.class);

            verify(accountRepository, never()).save(any());
        }

        @Test
        void throwsWhenAccountDoesNotExist() {
            when(accountRepository.findByIdForUpdate(accountId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.execute(accountId, BigDecimal.TEN))
                    .isInstanceOf(AccountNotFoundException.class);
        }
    }
}
