package com.ar.laboratory.identityservice.auth.domain.model;

import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Rol RBAC (p. ej. {@code ROLE_USER}) con su conjunto de permisos. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    private Long id;
    private String name;
    @Builder.Default private Set<Permission> permissions = new HashSet<>();
}
