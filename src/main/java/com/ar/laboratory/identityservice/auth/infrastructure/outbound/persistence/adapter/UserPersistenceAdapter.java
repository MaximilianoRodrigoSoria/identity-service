package com.ar.laboratory.identityservice.auth.infrastructure.outbound.persistence.adapter;

import com.ar.laboratory.identityservice.auth.application.outbound.port.UserRepositoryPort;
import com.ar.laboratory.identityservice.auth.domain.model.Role;
import com.ar.laboratory.identityservice.auth.domain.model.User;
import com.ar.laboratory.identityservice.auth.infrastructure.outbound.persistence.entity.RoleEntity;
import com.ar.laboratory.identityservice.auth.infrastructure.outbound.persistence.entity.UserEntity;
import com.ar.laboratory.identityservice.auth.infrastructure.outbound.persistence.mapper.UserEntityMapper;
import com.ar.laboratory.identityservice.auth.infrastructure.outbound.persistence.repository.RoleJpaRepository;
import com.ar.laboratory.identityservice.auth.infrastructure.outbound.persistence.repository.UserJpaRepository;
import com.ar.laboratory.identityservice.shared.infrastructure.exception.InfrastructureException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Adaptador de persistencia de usuarios; resuelve las asociaciones de rol como refs administradas. */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final UserJpaRepository userRepository;
    private final RoleJpaRepository roleRepository;
    private final UserEntityMapper mapper;

    @Override
    @Transactional
    public User save(User user) {
        try {
            UserEntity entity = mapper.toEntityShallow(user);
            Set<RoleEntity> managedRoles = new HashSet<>();
            if (user.getRoles() != null) {
                for (Role role : user.getRoles()) {
                    if (role.getId() != null) {
                        roleRepository.findById(role.getId()).ifPresent(managedRoles::add);
                    } else if (role.getName() != null) {
                        roleRepository.findByName(role.getName()).ifPresent(managedRoles::add);
                    }
                }
            }
            entity.setRoles(managedRoles);
            return mapper.toDomain(userRepository.save(entity));
        } catch (Exception e) {
            log.error("Error guardando usuario {}", user.getEmail(), e);
            throw new InfrastructureException("Error guardando usuario", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(UUID id) {
        try {
            return userRepository.findById(id).map(mapper::toDomain);
        } catch (Exception e) {
            throw new InfrastructureException("Error buscando usuario por id", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        try {
            return userRepository.findByEmail(email).map(mapper::toDomain);
        } catch (Exception e) {
            throw new InfrastructureException("Error buscando usuario por email", e);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        try {
            return userRepository.existsByEmail(email);
        } catch (Exception e) {
            throw new InfrastructureException("Error verificando existencia de email", e);
        }
    }
}
