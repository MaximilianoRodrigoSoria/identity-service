package com.ar.laboratory.identityservice.auth.application.usecase;

import com.ar.laboratory.identityservice.auth.application.inbound.command.GetCurrentUserCommand;
import com.ar.laboratory.identityservice.auth.application.outbound.port.UserRepositoryPort;
import com.ar.laboratory.identityservice.auth.domain.exception.UserNotFoundException;
import com.ar.laboratory.identityservice.auth.domain.model.User;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

/** Devuelve el usuario autenticado actual. POJO puro sin framework. */
@RequiredArgsConstructor
public class GetCurrentUserUseCase implements GetCurrentUserCommand {

    private final UserRepositoryPort userRepository;

    @Override
    public User execute(UUID userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado: " + userId));
    }
}
