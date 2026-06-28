package com.vinicius.payments.payments_api.account.interfaces.dto;

import com.vinicius.payments.payments_api.account.domain.entity.Account;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponseDto(Integer id,
                                 Integer userId,
                                 BigDecimal balance,
                                 LocalDateTime createdAt,
                                 LocalDateTime updatedAt) {


public static AccountResponseDto from(Account account) {
    return new AccountResponseDto(
            account.getId(),
            account.getUserId(),
            account.getBalance(),
            account.getCreatedAt(),
            account.getUpdatedAt()
    );
}
}