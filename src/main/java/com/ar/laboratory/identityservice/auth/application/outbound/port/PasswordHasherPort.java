package com.ar.laboratory.identityservice.auth.application.outbound.port;

/** Puerto de salida para el hashing y verificación de contraseñas. */
public interface PasswordHasherPort {
    String hash(String rawPassword);

    boolean matches(String rawPassword, String passwordHash);
}
