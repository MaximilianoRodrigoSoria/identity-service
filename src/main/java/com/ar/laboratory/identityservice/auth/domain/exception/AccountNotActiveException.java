package com.ar.laboratory.identityservice.auth.domain.exception;

/** La cuenta no está activa (pendiente, bloqueada o deshabilitada). */
public class AccountNotActiveException extends RuntimeException {
    public AccountNotActiveException(String message) {
        super(message);
    }
}
