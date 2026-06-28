package com.vinicius.payments.payments_api.user.interfaces.controller;

import com.vinicius.payments.payments_api.user.application.useCases.*;
import com.vinicius.payments.payments_api.user.interfaces.dto.requests.UpdatePasswordRequestDto;
import com.vinicius.payments.payments_api.user.interfaces.dto.requests.UserRequestDto;
import com.vinicius.payments.payments_api.user.interfaces.dto.response.UserResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final FindUserByEmailUseCase findUserByEmailUseCase;
    private final FindUserByIdUseCase findUserByIdUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final UpdatePasswordUseCase updatePasswordUseCase;

    public UserController(CreateUserUseCase createUserUseCase, DeleteUserUseCase deleteUserUseCase, FindUserByEmailUseCase findUserByEmailUseCase, FindUserByIdUseCase findUserByIdUseCase, UpdateProfileUseCase updateProfileUseCase, UpdatePasswordUseCase updatePasswordUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
        this.findUserByEmailUseCase = findUserByEmailUseCase;
        this.findUserByIdUseCase = findUserByIdUseCase;
        this.updateProfileUseCase = updateProfileUseCase;
        this.updatePasswordUseCase = updatePasswordUseCase;
    }


    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        var user = createUserUseCase.execute(userRequestDto.name(), userRequestDto.email(), userRequestDto.login(), userRequestDto.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponseDto.from(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> findUserById(@PathVariable Integer id) {
        var user = findUserByIdUseCase.execute(id);
        return ResponseEntity.ok(UserResponseDto.from(user));
    }

    @GetMapping("/email")
    public ResponseEntity<UserResponseDto> findUserByEmail(@RequestParam String email) {
        var user = findUserByEmailUseCase.execute(email);
        return ResponseEntity.ok(UserResponseDto.from(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Integer id) {
        deleteUserUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@Valid @PathVariable Integer id, @RequestBody UserRequestDto userRequestDto) {
        var user = updateProfileUseCase.execute(id, userRequestDto.name(), userRequestDto.email(), userRequestDto.login());
        return ResponseEntity.ok(UserResponseDto.from(user));
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Void> updateUserPassword(@Valid @PathVariable Integer id, @RequestBody UpdatePasswordRequestDto request) {
        updatePasswordUseCase.execute(id, request.newPassword());
        return ResponseEntity.ok().build();
    }
}
