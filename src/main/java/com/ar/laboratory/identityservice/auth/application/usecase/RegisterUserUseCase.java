package com.ar.laboratory.identityservice.auth.application.usecase;

import com.ar.laboratory.identityservice.auth.application.inbound.command.RegisterUserCommand;
import com.ar.laboratory.identityservice.auth.application.outbound.port.PasswordHasherPort;
import com.ar.laboratory.identityservice.auth.application.outbound.port.RoleRepositoryPort;
import com.ar.laboratory.identityservice.auth.application.outbound.port.UserRepositoryPort;
import com.ar.laboratory.identityservice.auth.domain.exception.EmailAlreadyRegisteredException;
import com.ar.laboratory.identityservice.auth.domain.model.Role;
import com.ar.laboratory.identityservice.auth.domain.model.User;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Registra un usuario nuevo con el rol por defecto {@code ROLE_USER}. POJO puro sin framework. */
@Slf4j
@RequiredArgsConstructor
public class RegisterUserUseCase implements RegisterUserCommand {

    private static final String DEFAULT_ROLE = "ROLE_USER";

    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;
    private final PasswordHasherPort passwordHasher;

    @Override
    public User execute(String email, String rawPassword) {
        String normalized = email == null ? null : email.trim().toLowerCase();
        if (normalized != null && userRepository.existsByEmail(normalized)) {
            throw new EmailAlreadyRegisteredException(normalized);
        }
        Role role = roleRepository.findByName(DEFAULT_ROLE).orElse(null);
        User user =
                User.createActive(
                        normalized, passwordHasher.hash(rawPassword), role, Instant.now());
        User saved = userRepository.save(user);
        log.info("Usuario registrado id={} email={}", saved.getId(), saved.getEmail());
        return saved;
    }
}
