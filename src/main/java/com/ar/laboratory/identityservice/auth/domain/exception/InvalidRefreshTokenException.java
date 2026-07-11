package com.ar.laboratory.identityservice.auth.domain.exception;

/** Refresh token inexistente, vencido o inválido. */
public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
