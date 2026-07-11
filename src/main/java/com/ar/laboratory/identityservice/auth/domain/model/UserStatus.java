package com.ar.laboratory.identityservice.auth.domain.model;

/** Estado de una cuenta de usuario. */
public enum UserStatus {
    /** Registrado pero pendiente de verificación (email). */
    PENDING,
    /** Activo y habilitado para autenticarse. */
    ACTIVE,
    /** Bloqueado temporalmente (p. ej. por intentos fallidos). */
    LOCKED,
    /** Deshabilitado permanentemente. */
    DISABLED;

    public boolean canAuthenticate() {
        return this == ACTIVE;
    }
}
