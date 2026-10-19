package com.vinicius.payments.payments_api.user.interfaces.dto.response;

import org.junit.jupiter.api.Test;

import java.lang.reflect.RecordComponent;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class UserResponseDtoTest {

    @Test
    void doesNotExposePassword() {
        assertThat(Arrays.stream(UserResponseDto.class.getRecordComponents()).map(RecordComponent::getName))
                .noneMatch(name -> name.toLowerCase().contains("password"));
    }
}
