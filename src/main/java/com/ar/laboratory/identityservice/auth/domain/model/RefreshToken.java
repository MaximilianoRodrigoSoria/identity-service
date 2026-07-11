package com.ar.laboratory.identityservice.auth.domain.model;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Refresh token persistido (opaco). Se guarda solo el hash del valor entregado al cliente; su
 * rotación y la detección de reuso se apoyan en {@code familyId} y {@code revokedAt}.
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    private UUID id;
    private UUID userId;
    private String tokenHash;
    private UUID familyId;
    private Instant expiresAt;
    private Instant revokedAt;
    private UUID replacedById;
    private Instant createdAt;

    public boolean isActive(Instant now) {
        return revokedAt == null && expiresAt != null && expiresAt.isAfter(now);
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public void revoke(Instant now) {
        if (this.revokedAt == null) {
            this.revokedAt = now;
        }
    }

    public void revokeReplacedBy(UUID replacement, Instant now) {
        revoke(now);
        this.replacedById = replacement;
    }
}
