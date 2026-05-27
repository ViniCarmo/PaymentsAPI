package com.vinicius.payments.payments_api.user.interfaces.controller;

import com.vinicius.payments.payments_api.user.infrastructure.persistence.UserJpaRepository;
import com.vinicius.payments.payments_api.user.interfaces.dto.requests.UserRequestDto;
import com.vinicius.payments.payments_api.user.interfaces.dto.response.UserResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserJpaRepository userJpaRepository;

    public UserController(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto userRequestDto) {

    }
}
