package com.ar.laboratory.identityservice.auth.domain.exception;

/** No se encontró el usuario. */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
