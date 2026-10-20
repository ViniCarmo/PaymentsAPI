package com.vinicius.payments.payments_api.user.infrastructure.mapper;

import com.vinicius.payments.payments_api.user.domain.entity.User;
import com.vinicius.payments.payments_api.user.infrastructure.persistence.UserJpaEntity;

public class UserMapper {

    public static UserJpaEntity toJpaEntity(User user){
        return new UserJpaEntity(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getPassword(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public static User toDomainEntity(UserJpaEntity entity){
        return new User(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getLogin(),
                entity.getPassword(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
