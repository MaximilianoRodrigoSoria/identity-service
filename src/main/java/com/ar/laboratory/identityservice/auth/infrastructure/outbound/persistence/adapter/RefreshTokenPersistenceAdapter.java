package com.ar.laboratory.identityservice.auth.infrastructure.outbound.persistence.adapter;

import com.ar.laboratory.identityservice.auth.application.outbound.port.RefreshTokenRepositoryPort;
import com.ar.laboratory.identityservice.auth.domain.model.RefreshToken;
import com.ar.laboratory.identityservice.auth.infrastructure.outbound.persistence.mapper.RefreshTokenEntityMapper;
import com.ar.laboratory.identityservice.auth.infrastructure.outbound.persistence.repository.RefreshTokenJpaRepository;
import com.ar.laboratory.identityservice.shared.infrastructure.exception.InfrastructureException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Adaptador de persistencia de refresh tokens. */
@Component
@RequiredArgsConstructor
public class RefreshTokenPersistenceAdapter implements RefreshTokenRepositoryPort {

    private final RefreshTokenJpaRepository repository;
    private final RefreshTokenEntityMapper mapper;

    @Override
    public RefreshToken save(RefreshToken token) {
        try {
            return mapper.toDomain(repository.save(mapper.toEntity(token)));
        } catch (Exception e) {
            throw new InfrastructureException("Error guardando refresh token", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        try {
            return repository.findByTokenHash(tokenHash).map(mapper::toDomain);
        } catch (Exception e) {
            throw new InfrastructureException("Error buscando refresh token", e);
        }
    }

    @Override
    @Transactional
    public void revokeFamily(UUID familyId, Instant now) {
        repository.revokeFamily(familyId, now);
    }

    @Override
    @Transactional
    public void revokeAllForUser(UUID userId, Instant now) {
        repository.revokeAllForUser(userId, now);
    }
}
