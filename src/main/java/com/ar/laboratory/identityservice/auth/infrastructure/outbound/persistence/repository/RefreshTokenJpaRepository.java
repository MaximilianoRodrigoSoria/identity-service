package com.ar.laboratory.identityservice.auth.infrastructure.outbound.persistence.repository;

import com.ar.laboratory.identityservice.auth.infrastructure.outbound.persistence.entity.RefreshTokenEntity;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Repositorio JPA de refresh tokens. */
public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, UUID> {

    Optional<RefreshTokenEntity> findByTokenHash(String tokenHash);

    @Modifying
    @Query(
            "UPDATE RefreshTokenEntity r SET r.revokedAt = :now "
                    + "WHERE r.familyId = :familyId AND r.revokedAt IS NULL")
    int revokeFamily(@Param("familyId") UUID familyId, @Param("now") Instant now);

    @Modifying
    @Query(
            "UPDATE RefreshTokenEntity r SET r.revokedAt = :now "
                    + "WHERE r.userId = :userId AND r.revokedAt IS NULL")
    int revokeAllForUser(@Param("userId") UUID userId, @Param("now") Instant now);
}
