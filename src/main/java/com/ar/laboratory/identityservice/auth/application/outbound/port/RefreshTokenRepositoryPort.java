package com.ar.laboratory.identityservice.auth.application.outbound.port;

import com.ar.laboratory.identityservice.auth.domain.model.RefreshToken;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/** Puerto de salida para la persistencia de refresh tokens. */
public interface RefreshTokenRepositoryPort {
    RefreshToken save(RefreshToken token);

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    /** Revoca todos los tokens activos de una familia (rotación con detección de reuso). */
    void revokeFamily(UUID familyId, Instant now);

    /** Revoca todos los tokens activos de un usuario (logout / cambio de contraseña). */
    void revokeAllForUser(UUID userId, Instant now);
}
