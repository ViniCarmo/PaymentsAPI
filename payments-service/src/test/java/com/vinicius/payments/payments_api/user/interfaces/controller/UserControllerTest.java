package com.vinicius.payments.payments_api.user.interfaces.controller;

import com.vinicius.payments.payments_api.user.application.useCases.*;
import com.vinicius.payments.payments_api.user.domain.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateUserUseCase createUserUseCase;
    @MockitoBean
    private DeleteUserUseCase deleteUserUseCase;
    @MockitoBean
    private FindUserByEmailUseCase findUserByEmailUseCase;
    @MockitoBean
    private FindUserByIdUseCase findUserByIdUseCase;
    @MockitoBean
    private UpdateProfileUseCase updateProfileUseCase;
    @MockitoBean
    private UpdatePasswordUseCase updatePasswordUseCase;

    private final UUID userId = UUID.randomUUID();

    @Test
    void createNeverReturnsPassword() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        when(createUserUseCase.execute("Alice", "alice@mail.com", "alice", "secret123"))
                .thenReturn(new User(userId, "Alice", "alice@mail.com", "alice", "$2a$hash", now, now));

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Alice\",\"email\":\"alice@mail.com\",\"login\":\"alice\",\"password\":\"secret123\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void updateProfileDoesNotRequirePassword() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        when(updateProfileUseCase.execute(userId, "Alice", "alice@mail.com", "alice"))
                .thenReturn(new User(userId, "Alice", "alice@mail.com", "alice", "$2a$hash", now, now));

        mockMvc.perform(put("/api/v1/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Alice\",\"email\":\"alice@mail.com\",\"login\":\"alice\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void updateProfileValidatesBody() throws Exception {
        mockMvc.perform(put("/api/v1/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"email\":\"not-an-email\",\"login\":\"alice\"}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(updateProfileUseCase);
    }

    @Test
    void updatePasswordValidatesBody() throws Exception {
        mockMvc.perform(put("/api/v1/users/{id}/password", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newPassword\":\"123\"}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(updatePasswordUseCase);
    }
}
