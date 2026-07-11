package com.ar.laboratory.identityservice.auth.domain.exception;

/** Se detectó el reuso de un refresh token ya rotado: posible robo; se invalida la familia. */
public class TokenReuseDetectedException extends RuntimeException {
    public TokenReuseDetectedException() {
        super("Reuso de refresh token detectado; se revocaron todas las sesiones");
    }
}
