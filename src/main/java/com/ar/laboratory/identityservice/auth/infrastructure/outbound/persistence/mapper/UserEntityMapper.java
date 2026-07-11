package com.ar.laboratory.identityservice.auth.infrastructure.outbound.persistence.mapper;

import com.ar.laboratory.identityservice.auth.domain.model.Role;
import com.ar.laboratory.identityservice.auth.domain.model.User;
import com.ar.laboratory.identityservice.auth.domain.model.UserStatus;
import com.ar.laboratory.identityservice.auth.infrastructure.outbound.persistence.entity.UserEntity;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Conversión UserEntity ↔ User (dominio). */
@Component
@RequiredArgsConstructor
public class UserEntityMapper {

    private final RoleEntityMapper roleMapper;

    public User toDomain(UserEntity e) {
        if (e == null) {
            return null;
        }
        Set<Role> roles =
                e.getRoles() == null
                        ? new HashSet<>()
                        : e.getRoles().stream()
                                .map(roleMapper::toDomain)
                                .collect(Collectors.toCollection(HashSet::new));
        return User.builder()
                .id(e.getId())
                .email(e.getEmail())
                .passwordHash(e.getPasswordHash())
                .status(e.getStatus() == null ? null : UserStatus.valueOf(e.getStatus()))
                .emailVerified(e.isEmailVerified())
                .roles(roles)
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    /** Convierte a entidad SIN las asociaciones de roles (el adapter resuelve refs administradas). */
    public UserEntity toEntityShallow(User u) {
        return UserEntity.builder()
                .id(u.getId())
                .email(u.getEmail())
                .passwordHash(u.getPasswordHash())
                .status(u.getStatus() == null ? null : u.getStatus().name())
                .emailVerified(u.isEmailVerified())
                .createdAt(u.getCreatedAt())
                .updatedAt(u.getUpdatedAt())
                .build();
    }
}
