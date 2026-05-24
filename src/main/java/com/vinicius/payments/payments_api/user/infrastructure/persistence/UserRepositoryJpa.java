package com.vinicius.payments.payments_api.user.infrastructure.persistence;

import com.vinicius.payments.payments_api.user.domain.entity.User;
import com.vinicius.payments.payments_api.user.domain.repository.UserRepository;
import com.vinicius.payments.payments_api.user.infrastructure.mapper.UserMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserRepositoryJpa implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    public UserRepositoryJpa(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = UserMapper.toJpaEntity(user);

        UserJpaEntity saved = userJpaRepository.save(entity);

        return UserMapper.toDomainEntity(saved);
    }


    @Override
    public Optional<User> findById(Integer id) {
        return userJpaRepository.findById(id)
                .map(UserMapper::toDomainEntity);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .map(UserMapper::toDomainEntity);
    }

    @Override
    public void deleteById(Integer id) {
        userJpaRepository.deleteById(id);
    }
}
