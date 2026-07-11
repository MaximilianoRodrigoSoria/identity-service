package com.ar.laboratory.identityservice.auth.application.outbound.port;

import com.ar.laboratory.identityservice.auth.domain.model.User;
import java.util.Optional;
import java.util.UUID;

/** Puerto de salida para la persistencia de usuarios. */
public interface UserRepositoryPort {
    User save(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
