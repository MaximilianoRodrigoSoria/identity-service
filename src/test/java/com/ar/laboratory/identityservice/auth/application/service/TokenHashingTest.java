package com.ar.laboratory.identityservice.auth.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("TokenHashing")
class TokenHashingTest {

    @Test
    @DisplayName("sha256 es determinístico y de 64 hex")
    void sha256Deterministic() {
        String a = TokenHashing.sha256("hola");
        String b = TokenHashing.sha256("hola");
        assertThat(a).isEqualTo(b).hasSize(64).matches("[0-9a-f]{64}");
        assertThat(TokenHashing.sha256("otro")).isNotEqualTo(a);
    }

    @Test
    @DisplayName("generateOpaqueToken produce valores distintos y sin guiones")
    void opaqueTokens() {
        String t1 = TokenHashing.generateOpaqueToken();
        String t2 = TokenHashing.generateOpaqueToken();
        assertThat(t1).isNotEqualTo(t2).doesNotContain("-");
        assertThat(t1.length()).isGreaterThanOrEqualTo(32);
    }
}
