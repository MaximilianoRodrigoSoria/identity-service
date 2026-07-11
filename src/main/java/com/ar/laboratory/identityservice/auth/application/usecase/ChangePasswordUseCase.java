package com.ar.laboratory.identityservice.auth.application.usecase;

import com.ar.laboratory.identityservice.auth.application.inbound.command.ChangePasswordCommand;
import com.ar.laboratory.identityservice.auth.application.outbound.port.PasswordHasherPort;
import com.ar.laboratory.identityservice.auth.application.outbound.port.RefreshTokenRepositoryPort;
import com.ar.laboratory.identityservice.auth.application.outbound.port.UserRepositoryPort;
import com.ar.laboratory.identityservice.auth.domain.exception.InvalidCredentialsException;
import com.ar.laboratory.identityservice.auth.domain.exception.UserNotFoundException;
import com.ar.laboratory.identityservice.auth.domain.model.User;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Cambia la contraseña previa verificación de la actual; revoca las sesiones existentes. */
@Slf4j
@RequiredArgsConstructor
public class ChangePasswordUseCase implements ChangePasswordCommand {

    private final UserRepositoryPort userRepository;
    private final PasswordHasherPort passwordHasher;
    private final RefreshTokenRepositoryPort refreshTokenRepository;

    @Override
    public void execute(UUID userId, String currentPassword, String newPassword) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(
                                () -> new UserNotFoundException("Usuario no encontrado: " + userId));
        if (!passwordHasher.matches(currentPassword, user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        Instant now = Instant.now();
        user.changePassword(passwordHasher.hash(newPassword), now);
        userRepository.save(user);
        refreshTokenRepository.revokeAllForUser(userId, now);
        log.info("Contraseña cambiada para usuario {}", userId);
    }
}
