package com.ar.laboratory.identityservice.auth.application.outbound.port;

import com.ar.laboratory.identityservice.auth.domain.model.Role;
import java.util.Optional;

/** Puerto de salida para consultar roles RBAC. */
public interface RoleRepositoryPort {
    Optional<Role> findByName(String name);
}
