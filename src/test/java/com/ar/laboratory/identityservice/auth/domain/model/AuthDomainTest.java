package com.ar.laboratory.identityservice.auth.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Dominio de identidad")
class AuthDomainTest {

    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");

    @Test
    @DisplayName("createActive crea usuario ACTIVE con el rol dado")
    void createActive() {
        Role role = Role.builder().id(1L).name("ROLE_USER").build();
        User user = User.createActive("a@b.com", "hash", role, NOW);
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.getRoles()).extracting(Role::getName).containsExactly("ROLE_USER");
        assertThat(user.getId()).isNotNull();
        assertThat(UserStatus.ACTIVE.canAuthenticate()).isTrue();
        assertThat(UserStatus.LOCKED.canAuthenticate()).isFalse();
    }

    @Test
    @DisplayName("changePassword y markEmailVerified actualizan el usuario")
    void mutations() {
        User user = User.createActive("a@b.com", "old", null, NOW);
        user.changePassword("new", NOW.plusSeconds(1));
        assertThat(user.getPasswordHash()).isEqualTo("new");

        User pending =
                User.builder().status(UserStatus.PENDING).emailVerified(false).build();
        pending.markEmailVerified(NOW);
        assertThat(pending.isEmailVerified()).isTrue();
        assertThat(pending.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("RefreshToken: isActive, revoke y revokeReplacedBy")
    void refreshTokenLifecycle() {
        RefreshToken token =
                RefreshToken.builder()
                        .id(UUID.randomUUID())
                        .userId(UUID.randomUUID())
                        .tokenHash("h")
                        .familyId(UUID.randomUUID())
                        .expiresAt(NOW.plusSeconds(3600))
                        .createdAt(NOW)
                        .build();
        assertThat(token.isActive(NOW)).isTrue();
        assertThat(token.isRevoked()).isFalse();

        UUID replacement = UUID.randomUUID();
        token.revokeReplacedBy(replacement, NOW.plusSeconds(1));
        assertThat(token.isRevoked()).isTrue();
        assertThat(token.getReplacedById()).isEqualTo(replacement);
        assertThat(token.isActive(NOW.plusSeconds(2))).isFalse();
    }

    @Test
    @DisplayName("RefreshToken vencido no está activo")
    void expiredNotActive() {
        RefreshToken token =
                RefreshToken.builder().expiresAt(NOW.minusSeconds(1)).build();
        assertThat(token.isActive(NOW)).isFalse();
    }
}
