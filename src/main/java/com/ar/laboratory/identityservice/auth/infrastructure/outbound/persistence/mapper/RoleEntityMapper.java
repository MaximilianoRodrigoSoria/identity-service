package com.ar.laboratory.identityservice.auth.infrastructure.outbound.persistence.mapper;

import com.ar.laboratory.identityservice.auth.domain.model.Permission;
import com.ar.laboratory.identityservice.auth.domain.model.Role;
import com.ar.laboratory.identityservice.auth.infrastructure.outbound.persistence.entity.RoleEntity;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/** Conversión RoleEntity ↔ Role (dominio), incluyendo sus permisos. */
@Component
public class RoleEntityMapper {

    public Role toDomain(RoleEntity e) {
        if (e == null) {
            return null;
        }
        Set<Permission> permissions =
                e.getPermissions() == null
                        ? new HashSet<>()
                        : e.getPermissions().stream()
                                .map(p -> Permission.builder().id(p.getId()).name(p.getName()).build())
                                .collect(Collectors.toCollection(HashSet::new));
        return Role.builder().id(e.getId()).name(e.getName()).permissions(permissions).build();
    }
}
