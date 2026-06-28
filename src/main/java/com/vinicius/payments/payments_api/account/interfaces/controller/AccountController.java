package com.vinicius.payments.payments_api.account.interfaces.controller;

import com.vinicius.payments.payments_api.account.application.useCases.*;
import com.vinicius.payments.payments_api.account.interfaces.dto.AccountRequestDto;
import com.vinicius.payments.payments_api.account.interfaces.dto.AccountResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final CreateAccountUseCase createAccountUseCase;
    private final DeleteAccountUseCase deleteAccountUseCase;
    private final DepositUseCase depositUseCase;
    private final WithdrawUseCase withdrawUseCase;
    private final FindAccountByIdUseCase findAccountByIdUseCase;
    private final FindByUserIdUseCase findByUserIdUseCase;

    public AccountController(CreateAccountUseCase createAccountUseCase, DeleteAccountUseCase deleteAccountUseCase, DepositUseCase depositUseCase, WithdrawUseCase withdrawUseCase, FindAccountByIdUseCase findAccountByIdUseCase, FindByUserIdUseCase findByUserIdUseCase) {
        this.createAccountUseCase = createAccountUseCase;
        this.deleteAccountUseCase = deleteAccountUseCase;
        this.depositUseCase = depositUseCase;
        this.withdrawUseCase = withdrawUseCase;
        this.findAccountByIdUseCase = findAccountByIdUseCase;
        this.findByUserIdUseCase = findByUserIdUseCase;
    }

    @PostMapping
    public ResponseEntity<AccountResponseDto> createAccount(@RequestBody AccountRequestDto accountRequestDto) {
        var account = createAccountUseCase.execute(accountRequestDto.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(AccountResponseDto.from(account));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Integer id) {
        deleteAccountUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<AccountResponseDto> findAccountById(@PathVariable Integer id) {
        var account = findAccountByIdUseCase.execute(id);
        return ResponseEntity.ok(AccountResponseDto.from(account));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<AccountResponseDto> findAccountByUserId(@PathVariable Integer userId) {
    var account =  findByUserIdUseCase.execute(userId);
    return ResponseEntity.ok(AccountResponseDto.from(account));
    }


}
