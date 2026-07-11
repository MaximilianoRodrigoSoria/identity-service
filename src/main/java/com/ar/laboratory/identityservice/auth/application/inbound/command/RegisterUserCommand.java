package com.ar.laboratory.identityservice.auth.application.inbound.command;

import com.ar.laboratory.identityservice.auth.domain.model.User;

/** Puerto de entrada: registrar un usuario nuevo. */
public interface RegisterUserCommand {
    User execute(String email, String rawPassword);
}
