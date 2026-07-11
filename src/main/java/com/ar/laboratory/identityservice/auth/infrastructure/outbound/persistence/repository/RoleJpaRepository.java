package com.ar.laboratory.identityservice.auth.infrastructure.outbound.persistence.repository;

import com.ar.laboratory.identityservice.auth.infrastructure.outbound.persistence.entity.RoleEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repositorio JPA de roles. */
public interface RoleJpaRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByName(String name);
}
