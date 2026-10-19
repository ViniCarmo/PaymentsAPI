package com.vinicius.payments.payments_api.user.application.useCases;

import com.vinicius.payments.payments_api.user.domain.entity.User;
import com.vinicius.payments.payments_api.user.domain.exception.EmailAlreadyInUseException;
import com.vinicius.payments.payments_api.user.domain.exception.UserNotFoundException;
import com.vinicius.payments.payments_api.user.domain.port.PasswordEncoder;
import com.vinicius.payments.payments_api.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUseCasesTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private final UUID userId = UUID.randomUUID();

    private User existingUser() {
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        return new User(userId, "Alice", "alice@mail.com", "alice", "$2a$hashed", createdAt, createdAt);
    }

    private void saveReturnsArgument() {
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Nested
    class CreateUser {

        private CreateUserUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new CreateUserUseCase(userRepository, passwordEncoder);
        }

        @Test
        void savesUserWithEncodedPassword() {
            when(userRepository.findByEmail("alice@mail.com")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("secret123")).thenReturn("$2a$hashed");
            saveReturnsArgument();

            User created = useCase.execute("Alice", "alice@mail.com", "alice", "secret123");

            assertThat(created.getPassword()).isEqualTo("$2a$hashed");
            assertThat(created.getName()).isEqualTo("Alice");
            assertThat(created.getEmail()).isEqualTo("alice@mail.com");
            assertThat(created.getLogin()).isEqualTo("alice");
            verify(userRepository).save(created);
        }

        @Test
        void rejectsEmailAlreadyInUse() {
            when(userRepository.findByEmail("alice@mail.com")).thenReturn(Optional.of(existingUser()));

            assertThatThrownBy(() -> useCase.execute("Alice", "alice@mail.com", "alice", "secret123"))
                    .isInstanceOf(EmailAlreadyInUseException.class);

            verify(userRepository, never()).save(any());
            verifyNoInteractions(passwordEncoder);
        }
    }

    @Nested
    class DeleteUser {

        private DeleteUserUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new DeleteUserUseCase(userRepository);
        }

        @Test
        void deletesExistingUser() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser()));

            useCase.execute(userId);

            verify(userRepository).deleteById(userId);
        }

        @Test
        void throwsWhenUserDoesNotExist() {
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.execute(userId)).isInstanceOf(UserNotFoundException.class);

            verify(userRepository, never()).deleteById(any());
        }
    }

    @Nested
    class FindUserById {

        private FindUserByIdUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new FindUserByIdUseCase(userRepository);
        }

        @Test
        void returnsUser() {
            User user = existingUser();
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            assertThat(useCase.execute(userId)).isSameAs(user);
        }

        @Test
        void throwsWhenUserDoesNotExist() {
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.execute(userId))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining(userId.toString());
        }
    }

    @Nested
    class FindUserByEmail {

        private FindUserByEmailUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new FindUserByEmailUseCase(userRepository);
        }

        @Test
        void returnsUser() {
            User user = existingUser();
            when(userRepository.findByEmail("alice@mail.com")).thenReturn(Optional.of(user));

            assertThat(useCase.execute("alice@mail.com")).isSameAs(user);
        }

        @Test
        void throwsWhenUserDoesNotExist() {
            when(userRepository.findByEmail("ghost@mail.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.execute("ghost@mail.com"))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("ghost@mail.com");
        }
    }

    @Nested
    class UpdatePassword {

        private UpdatePasswordUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new UpdatePasswordUseCase(userRepository, passwordEncoder);
        }

        @Test
        void storesEncodedNewPassword() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser()));
            when(passwordEncoder.encode("newSecret")).thenReturn("$2a$newHash");

            useCase.execute(userId, "newSecret");

            ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(saved.capture());
            assertThat(saved.getValue().getPassword()).isEqualTo("$2a$newHash");
        }

        @Test
        void throwsWhenUserDoesNotExist() {
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.execute(userId, "newSecret"))
                    .isInstanceOf(UserNotFoundException.class);

            verify(userRepository, never()).save(any());
            verifyNoInteractions(passwordEncoder);
        }
    }

    @Nested
    class UpdateProfile {

        private UpdateProfileUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new UpdateProfileUseCase(userRepository);
        }

        @Test
        void updatesProfileWithNewEmail() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser()));
            when(userRepository.existsByEmail("new@mail.com")).thenReturn(false);
            saveReturnsArgument();

            User updated = useCase.execute(userId, "Alice B", "new@mail.com", "aliceb");

            assertThat(updated.getName()).isEqualTo("Alice B");
            assertThat(updated.getEmail()).isEqualTo("new@mail.com");
            assertThat(updated.getLogin()).isEqualTo("aliceb");
            assertThat(updated.getPassword()).isEqualTo("$2a$hashed");
        }

        @Test
        void allowsKeepingOwnEmail() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser()));
            when(userRepository.existsByEmail("alice@mail.com")).thenReturn(true);
            saveReturnsArgument();

            User updated = useCase.execute(userId, "Alice B", "alice@mail.com", "alice");

            assertThat(updated.getName()).isEqualTo("Alice B");
        }

        @Test
        void rejectsEmailOwnedByAnotherUser() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser()));
            when(userRepository.existsByEmail("bob@mail.com")).thenReturn(true);

            assertThatThrownBy(() -> useCase.execute(userId, "Alice", "bob@mail.com", "alice"))
                    .isInstanceOf(EmailAlreadyInUseException.class);

            verify(userRepository, never()).save(any());
        }

        @Test
        void throwsWhenUserDoesNotExist() {
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.execute(userId, "Alice", "alice@mail.com", "alice"))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }
}
