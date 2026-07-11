package com.ar.laboratory.identityservice.auth.infrastructure.outbound.persistence.repository;

import com.ar.laboratory.identityservice.auth.infrastructure.outbound.persistence.entity.UserEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repositorio JPA de usuarios. */
public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
