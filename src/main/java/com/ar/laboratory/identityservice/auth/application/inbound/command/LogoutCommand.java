package com.ar.laboratory.identityservice.auth.application.inbound.command;

import java.util.UUID;

/** Puerto de entrada: cerrar todas las sesiones del usuario (revoca sus refresh tokens). */
public interface LogoutCommand {
    void execute(UUID userId);
}
