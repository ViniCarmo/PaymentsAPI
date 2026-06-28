package com.vinicius.payments.payments_api.account.interfaces.controller;

import com.vinicius.payments.payments_api.account.application.useCases.*;
import com.vinicius.payments.payments_api.account.interfaces.dto.AccountRequestDto;
import com.vinicius.payments.payments_api.account.interfaces.dto.AccountResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/accounts")
public class AcountController {

    private final CreateAccountUseCase createAccountUseCase;
    private final DeleteAccountUseCase deleteAccountUseCase;
    private final DepositUseCase depositUseCase;
    private final WithdrawUseCase withdrawUseCase;
    private final FindAccountByIdUseCase findAccountByIdUseCase;
    private final FindByUserIdUseCase findByUserIdUseCase;

    public AcountController(CreateAccountUseCase createAccountUseCase, DeleteAccountUseCase deleteAccountUseCase, DepositUseCase depositUseCase, WithdrawUseCase withdrawUseCase, FindAccountByIdUseCase findAccountByIdUseCase, FindByUserIdUseCase findByUserIdUseCase) {
        this.createAccountUseCase = createAccountUseCase;
        this.deleteAccountUseCase = deleteAccountUseCase;
        this.depositUseCase = depositUseCase;
        this.withdrawUseCase = withdrawUseCase;
        this.findAccountByIdUseCase = findAccountByIdUseCase;
        this.findByUserIdUseCase = findByUserIdUseCase;
    }

        @PostMapping
        public ResponseEntity<AccountResponseDto> createAccount(@RequestBody AccountRequestDto accountRequestDto){
            var account = createAccountUseCase.execute(accountRequestDto.userId());
            return ResponseEntity.status(HttpStatus.CREATED).body(AccountResponseDto.from(account));
        }
    }
