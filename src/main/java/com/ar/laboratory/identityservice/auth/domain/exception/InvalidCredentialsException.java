package com.ar.laboratory.identityservice.auth.domain.exception;

/** Credenciales inválidas (mensaje genérico para no filtrar si el email existe). */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Credenciales inválidas");
    }
}
