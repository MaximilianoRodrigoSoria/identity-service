package com.ar.laboratory.identityservice.auth.application.inbound.command;

import com.ar.laboratory.identityservice.auth.domain.model.User;
import java.util.UUID;

/** Puerto de entrada: obtener el usuario autenticado actual. */
public interface GetCurrentUserCommand {
    User execute(UUID userId);
}
