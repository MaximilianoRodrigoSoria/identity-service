package com.ar.laboratory.identityservice.auth.application.usecase;

import com.ar.laboratory.identityservice.auth.application.inbound.command.RefreshTokenCommand;
import com.ar.laboratory.identityservice.auth.application.model.AuthTokens;
import com.ar.laboratory.identityservice.auth.application.outbound.port.RefreshTokenRepositoryPort;
import com.ar.laboratory.identityservice.auth.application.outbound.port.TokenProviderPort;
import com.ar.laboratory.identityservice.auth.application.outbound.port.UserRepositoryPort;
import com.ar.laboratory.identityservice.auth.application.service.AuthorityResolver;
import com.ar.laboratory.identityservice.auth.application.service.TokenHashing;
import com.ar.laboratory.identityservice.auth.domain.exception.InvalidRefreshTokenException;
import com.ar.laboratory.identityservice.auth.domain.exception.TokenReuseDetectedException;
import com.ar.laboratory.identityservice.auth.domain.model.RefreshToken;
import com.ar.laboratory.identityservice.auth.domain.model.User;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

/**
 * Rota el refresh token: valida el presentado, detecta reuso (token ya rotado ⇒ revoca la familia),
 * emite un nuevo par access+refresh manteniendo la familia.
 */
@Slf4j
public class RefreshTokenUseCase implements RefreshTokenCommand {

    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final UserRepositoryPort userRepository;
    private final TokenProviderPort tokenProvider;
    private final Duration refreshTtl;

    public RefreshTokenUseCase(
            RefreshTokenRepositoryPort refreshTokenRepository,
            UserRepositoryPort userRepository,
            TokenProviderPort tokenProvider,
            Duration refreshTtl) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
        this.refreshTtl = refreshTtl;
    }

    @Override
    public AuthTokens execute(String rawRefreshToken) {
        Instant now = Instant.now();
        String hash = TokenHashing.sha256(rawRefreshToken);
        RefreshToken current =
                refreshTokenRepository
                        .findByTokenHash(hash)
                        .orElseThrow(
                                () -> new InvalidRefreshTokenException("Refresh token inválido"));

        // Reuso de un token ya rotado ⇒ posible robo: se invalida toda la familia.
        if (current.isRevoked()) {
            refreshTokenRepository.revokeFamily(current.getFamilyId(), now);
            log.warn("Reuso de refresh token detectado (familia={})", current.getFamilyId());
            throw new TokenReuseDetectedException();
        }
        if (!current.isActive(now)) {
            throw new InvalidRefreshTokenException("Refresh token vencido");
        }

        User user =
                userRepository
                        .findById(current.getUserId())
                        .orElseThrow(
                                () -> new InvalidRefreshTokenException("Usuario inexistente"));

        // Rotación: se crea el nuevo token y se revoca el anterior enlazándolo.
        String newRaw = TokenHashing.generateOpaqueToken();
        UUID newId = UUID.randomUUID();
        RefreshToken rotated =
                RefreshToken.builder()
                        .id(newId)
                        .userId(user.getId())
                        .tokenHash(TokenHashing.sha256(newRaw))
                        .familyId(current.getFamilyId())
                        .expiresAt(now.plus(refreshTtl))
                        .createdAt(now)
                        .build();
        current.revokeReplacedBy(newId, now);
        refreshTokenRepository.save(current);
        refreshTokenRepository.save(rotated);

        String access =
                tokenProvider.issueAccessToken(
                        user.getId().toString(), AuthorityResolver.authoritiesOf(user));
        return AuthTokens.bearer(access, newRaw, tokenProvider.accessTokenTtlSeconds());
    }
}
