package com.ar.laboratory.identityservice.auth.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ar.laboratory.identityservice.auth.application.model.AuthTokens;
import com.ar.laboratory.identityservice.auth.application.outbound.port.RefreshTokenRepositoryPort;
import com.ar.laboratory.identityservice.auth.application.outbound.port.TokenProviderPort;
import com.ar.laboratory.identityservice.auth.application.outbound.port.UserRepositoryPort;
import com.ar.laboratory.identityservice.auth.application.service.TokenHashing;
import com.ar.laboratory.identityservice.auth.domain.exception.InvalidRefreshTokenException;
import com.ar.laboratory.identityservice.auth.domain.exception.TokenReuseDetectedException;
import com.ar.laboratory.identityservice.auth.domain.model.RefreshToken;
import com.ar.laboratory.identityservice.auth.domain.model.User;
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
@DisplayName("RefreshTokenUseCase")
class RefreshTokenUseCaseTest {

    private static final String RAW = "raw-refresh-token";

    @Mock private RefreshTokenRepositoryPort refreshTokens;
    @Mock private UserRepositoryPort users;
    @Mock private TokenProviderPort tokenProvider;

    private RefreshTokenUseCase useCase;
    private final UUID userId = UUID.randomUUID();
    private final UUID familyId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new RefreshTokenUseCase(refreshTokens, users, tokenProvider, Duration.ofDays(30));
    }

    private RefreshToken active() {
        return RefreshToken.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .tokenHash(TokenHashing.sha256(RAW))
                .familyId(familyId)
                .expiresAt(Instant.now().plusSeconds(3600))
                .createdAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("rota el token y emite un nuevo par")
    void rotates() {
        when(refreshTokens.findByTokenHash(TokenHashing.sha256(RAW)))
                .thenReturn(Optional.of(active()));
        when(users.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).build()));
        when(tokenProvider.issueAccessToken(anyString(), anyCollection())).thenReturn("new-access");
        when(tokenProvider.accessTokenTtlSeconds()).thenReturn(900L);
        when(refreshTokens.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AuthTokens tokens = useCase.execute(RAW);

        assertThat(tokens.accessToken()).isEqualTo("new-access");
        assertThat(tokens.refreshToken()).isNotEqualTo(RAW);
        // Guarda el rotado nuevo y el anterior (revocado).
        verify(refreshTokens, times(2)).save(any());
    }

    @Test
    @DisplayName("token inexistente → InvalidRefreshToken")
    void notFound() {
        when(refreshTokens.findByTokenHash(anyString())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> useCase.execute(RAW))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }

    @Test
    @DisplayName("token ya revocado → detecta reuso y revoca la familia")
    void reuseDetected() {
        RefreshToken revoked = active();
        revoked.revoke(Instant.now());
        when(refreshTokens.findByTokenHash(TokenHashing.sha256(RAW)))
                .thenReturn(Optional.of(revoked));

        assertThatThrownBy(() -> useCase.execute(RAW))
                .isInstanceOf(TokenReuseDetectedException.class);
        verify(refreshTokens).revokeFamily(eq(familyId), any());
    }

    @Test
    @DisplayName("token vencido → InvalidRefreshToken")
    void expired() {
        RefreshToken expired =
                RefreshToken.builder()
                        .tokenHash(TokenHashing.sha256(RAW))
                        .familyId(familyId)
                        .expiresAt(Instant.now().minusSeconds(1))
                        .build();
        when(refreshTokens.findByTokenHash(TokenHashing.sha256(RAW)))
                .thenReturn(Optional.of(expired));
        assertThatThrownBy(() -> useCase.execute(RAW))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }
}
