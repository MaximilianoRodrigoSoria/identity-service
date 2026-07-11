package com.ar.laboratory.identityservice.auth.application.inbound.command;

import java.util.UUID;

/** Puerto de entrada: cambiar la contraseña del usuario autenticado. */
public interface ChangePasswordCommand {
    void execute(UUID userId, String currentPassword, String newPassword);
}
