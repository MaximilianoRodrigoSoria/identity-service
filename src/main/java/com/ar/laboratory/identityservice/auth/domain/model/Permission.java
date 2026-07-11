package com.ar.laboratory.identityservice.auth.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Permiso de grano fino con formato {@code recurso:accion} (p. ej. {@code user:read}). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Permission {
    private Long id;
    private String name;
}
