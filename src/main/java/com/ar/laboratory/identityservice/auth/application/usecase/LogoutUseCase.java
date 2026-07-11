package com.ar.laboratory.identityservice.auth.application.usecase;

import com.ar.laboratory.identityservice.auth.application.inbound.command.LogoutCommand;
import com.ar.laboratory.identityservice.auth.application.outbound.port.RefreshTokenRepositoryPort;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Cierra todas las sesiones del usuario revocando sus refresh tokens activos. */
@Slf4j
@RequiredArgsConstructor
public class LogoutUseCase implements LogoutCommand {

    private final RefreshTokenRepositoryPort refreshTokenRepository;

    @Override
    public void execute(UUID userId) {
        refreshTokenRepository.revokeAllForUser(userId, Instant.now());
        log.info("Logout: sesiones revocadas para usuario {}", userId);
    }
}
