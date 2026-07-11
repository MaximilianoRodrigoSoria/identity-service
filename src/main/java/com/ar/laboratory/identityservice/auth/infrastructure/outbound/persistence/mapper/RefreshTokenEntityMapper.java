package com.ar.laboratory.identityservice.auth.infrastructure.outbound.persistence.mapper;

import com.ar.laboratory.identityservice.auth.domain.model.RefreshToken;
import com.ar.laboratory.identityservice.auth.infrastructure.outbound.persistence.entity.RefreshTokenEntity;
import org.springframework.stereotype.Component;

/** Conversión RefreshTokenEntity ↔ RefreshToken (dominio). */
@Component
public class RefreshTokenEntityMapper {

    public RefreshToken toDomain(RefreshTokenEntity e) {
        if (e == null) {
            return null;
        }
        return RefreshToken.builder()
                .id(e.getId())
                .userId(e.getUserId())
                .tokenHash(e.getTokenHash())
                .familyId(e.getFamilyId())
                .expiresAt(e.getExpiresAt())
                .revokedAt(e.getRevokedAt())
                .replacedById(e.getReplacedById())
                .createdAt(e.getCreatedAt())
                .build();
    }

    public RefreshTokenEntity toEntity(RefreshToken t) {
        return RefreshTokenEntity.builder()
                .id(t.getId())
                .userId(t.getUserId())
                .tokenHash(t.getTokenHash())
                .familyId(t.getFamilyId())
                .expiresAt(t.getExpiresAt())
                .revokedAt(t.getRevokedAt())
                .replacedById(t.getReplacedById())
                .createdAt(t.getCreatedAt())
                .build();
    }
}
