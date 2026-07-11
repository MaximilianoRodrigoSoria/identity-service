package com.ar.laboratory.identityservice.auth.infrastructure.outbound.persistence.adapter;

import com.ar.laboratory.identityservice.auth.application.outbound.port.RoleRepositoryPort;
import com.ar.laboratory.identityservice.auth.domain.model.Role;
import com.ar.laboratory.identityservice.auth.infrastructure.outbound.persistence.mapper.RoleEntityMapper;
import com.ar.laboratory.identityservice.auth.infrastructure.outbound.persistence.repository.RoleJpaRepository;
import com.ar.laboratory.identityservice.shared.infrastructure.exception.InfrastructureException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Adaptador de persistencia de roles. */
@Component
@RequiredArgsConstructor
public class RolePersistenceAdapter implements RoleRepositoryPort {

    private final RoleJpaRepository roleRepository;
    private final RoleEntityMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> findByName(String name) {
        try {
            return roleRepository.findByName(name).map(mapper::toDomain);
        } catch (Exception e) {
            throw new InfrastructureException("Error buscando rol por nombre", e);
        }
    }
}
