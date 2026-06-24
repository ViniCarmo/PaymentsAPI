package com.vinicius.payments.payments_api.account.infrastructure.mapper;

import com.vinicius.payments.payments_api.account.domain.entity.Account;
import com.vinicius.payments.payments_api.account.infrastructure.persistence.AccountJpaEntity;

public class AccountMapper {

    public static AccountJpaEntity toJpaEntity(Account account) {
        return new AccountJpaEntity(
                account.getId(),
                account.getUserId(),
                account.getBalance(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
    }

    public static Account toDomainEntity(AccountJpaEntity accountJpaEntity) {
        return new Account(
                accountJpaEntity.getId(),
                accountJpaEntity.getUserId(),
                accountJpaEntity.getBalance(),
                accountJpaEntity.getCreatedAt(),
                accountJpaEntity.getUpdatedAt()
        );
    }
}
