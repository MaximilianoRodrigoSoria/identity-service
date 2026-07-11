package com.ar.laboratory.identityservice.auth.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ar.laboratory.identityservice.auth.application.model.AuthTokens;
import com.ar.laboratory.identityservice.auth.application.outbound.port.PasswordHasherPort;
import com.ar.laboratory.identityservice.auth.application.outbound.port.RefreshTokenRepositoryPort;
import com.ar.laboratory.identityservice.auth.application.outbound.port.TokenProviderPort;
import com.ar.laboratory.identityservice.auth.application.outbound.port.UserRepositoryPort;
import com.ar.laboratory.identityservice.auth.domain.exception.AccountNotActiveException;
import com.ar.laboratory.identityservice.auth.domain.exception.InvalidCredentialsException;
import com.ar.laboratory.identityservice.auth.domain.model.User;
import com.ar.laboratory.identityservice.auth.domain.model.UserStatus;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginUseCase")
class LoginUseCaseTest {

    @Mock private UserRepositoryPort users;
    @Mock private PasswordHasherPort hasher;
    @Mock private TokenProviderPort tokenProvider;
    @Mock private RefreshTokenRepositoryPort refreshTokens;

    private LoginUseCase useCase;

    private User activeUser(String hash, UserStatus status) {
        return User.builder()
                .id(UUID.randomUUID())
                .email("a@b.com")
                .passwordHash(hash)
                .status(status)
                .createdAt(Instant.now())
                .build();
    }

    @BeforeEach
    void setUp() {
        useCase =
                new LoginUseCase(
                        users, hasher, tokenProvider, refreshTokens, Duration.ofDays(30));
    }

    @Test
    @DisplayName("login OK emite access + refresh y persiste el refresh")
    void loginOk() {
        User user = activeUser("hash", UserStatus.ACTIVE);
        when(users.findByEmail("a@b.com")).thenReturn(Optional.of(user));
        when(hasher.matches("pw", "hash")).thenReturn(true);
        when(tokenProvider.issueAccessToken(anyString(), anyCollection())).thenReturn("access");
        when(tokenProvider.accessTokenTtlSeconds()).thenReturn(900L);
        when(refreshTokens.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AuthTokens tokens = useCase.execute("A@B.com", "pw");

        assertThat(tokens.accessToken()).isEqualTo("access");
        assertThat(tokens.refreshToken()).isNotBlank();
        assertThat(tokens.tokenType()).isEqualTo("Bearer");
        assertThat(tokens.expiresInSeconds()).isEqualTo(900L);
        verify(refreshTokens).save(any());
    }

    @Test
    @DisplayName("contraseña incorrecta → InvalidCredentials")
    void badPassword() {
        when(users.findByEmail("a@b.com")).thenReturn(Optional.of(activeUser("hash", UserStatus.ACTIVE)));
        when(hasher.matches("pw", "hash")).thenReturn(false);
        assertThatThrownBy(() -> useCase.execute("a@b.com", "pw"))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("email inexistente → InvalidCredentials")
    void unknownEmail() {
        when(users.findByEmail("a@b.com")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> useCase.execute("a@b.com", "pw"))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("cuenta no activa → AccountNotActive")
    void inactiveAccount() {
        when(users.findByEmail("a@b.com")).thenReturn(Optional.of(activeUser("hash", UserStatus.LOCKED)));
        when(hasher.matches("pw", "hash")).thenReturn(true);
        assertThatThrownBy(() -> useCase.execute("a@b.com", "pw"))
                .isInstanceOf(AccountNotActiveException.class);
    }
}
