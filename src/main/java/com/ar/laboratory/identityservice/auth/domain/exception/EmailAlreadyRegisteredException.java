package com.ar.laboratory.identityservice.auth.domain.exception;

/** El email ya está registrado. */
public class EmailAlreadyRegisteredException extends RuntimeException {
    public EmailAlreadyRegisteredException(String email) {
        super("El email ya está registrado: " + email);
    }
}
