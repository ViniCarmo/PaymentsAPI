package com.vinicius.payments.payments_api.user.infrastructure.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BCryptPasswordEncoderAdapterTest {

    private final BCryptPasswordEncoderAdapter encoder = new BCryptPasswordEncoderAdapter();

    @Test
    void encodesWithBCryptAndNeverReturnsRawPassword() {
        String encoded = encoder.encode("secret123");

        assertThat(encoded).isNotEqualTo("secret123").startsWith("$2");
        assertThat(encoder.matches("secret123", encoded)).isTrue();
        assertThat(encoder.matches("wrong-password", encoded)).isFalse();
    }

    @Test
    void sameRawPasswordProducesDifferentHashes() {
        assertThat(encoder.encode("secret123")).isNotEqualTo(encoder.encode("secret123"));
    }
}
