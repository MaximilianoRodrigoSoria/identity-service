package com.ar.laboratory.identityservice.auth.application.usecase;

import com.ar.laboratory.identityservice.auth.application.inbound.command.LoginCommand;
import com.ar.laboratory.identityservice.auth.application.model.AuthTokens;
import com.ar.laboratory.identityservice.auth.application.outbound.port.PasswordHasherPort;
import com.ar.laboratory.identityservice.auth.application.outbound.port.RefreshTokenRepositoryPort;
import com.ar.laboratory.identityservice.auth.application.outbound.port.TokenProviderPort;
import com.ar.laboratory.identityservice.auth.application.outbound.port.UserRepositoryPort;
import com.ar.laboratory.identityservice.auth.application.service.AuthorityResolver;
import com.ar.laboratory.identityservice.auth.application.service.TokenHashing;
import com.ar.laboratory.identityservice.auth.domain.exception.AccountNotActiveException;
import com.ar.laboratory.identityservice.auth.domain.exception.InvalidCredentialsException;
import com.ar.laboratory.identityservice.auth.domain.model.RefreshToken;
import com.ar.laboratory.identityservice.auth.domain.model.User;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

/** Autentica por email/contraseña y emite access token (JWT) + refresh token rotativo. */
@Slf4j
public class LoginUseCase implements LoginCommand {

    private final UserRepositoryPort userRepository;
    private final PasswordHasherPort passwordHasher;
    private final TokenProviderPort tokenProvider;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final Duration refreshTtl;

    public LoginUseCase(
            UserRepositoryPort userRepository,
            PasswordHasherPort passwordHasher,
            TokenProviderPort tokenProvider,
            RefreshTokenRepositoryPort refreshTokenRepository,
            Duration refreshTtl) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.tokenProvider = tokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTtl = refreshTtl;
    }

    @Override
    public AuthTokens execute(String email, String rawPassword) {
        String normalized = email == null ? null : email.trim().toLowerCase();
        User user =
                userRepository
                        .findByEmail(normalized)
                        .orElseThrow(InvalidCredentialsException::new);

        if (!passwordHasher.matches(rawPassword, user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        if (!user.getStatus().canAuthenticate()) {
            throw new AccountNotActiveException("La cuenta no está activa: " + user.getStatus());
        }

        Instant now = Instant.now();
        String access =
                tokenProvider.issueAccessToken(
                        user.getId().toString(), AuthorityResolver.authoritiesOf(user));

        String rawRefresh = TokenHashing.generateOpaqueToken();
        RefreshToken refresh =
                RefreshToken.builder()
                        .id(UUID.randomUUID())
                        .userId(user.getId())
                        .tokenHash(TokenHashing.sha256(rawRefresh))
                        .familyId(UUID.randomUUID())
                        .expiresAt(now.plus(refreshTtl))
                        .createdAt(now)
                        .build();
        refreshTokenRepository.save(refresh);

        log.info("Login OK id={}", user.getId());
        return AuthTokens.bearer(access, rawRefresh, tokenProvider.accessTokenTtlSeconds());
    }
}
