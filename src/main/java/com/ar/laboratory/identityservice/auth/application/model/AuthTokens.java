package com.ar.laboratory.identityservice.auth.application.model;

/** Par de tokens emitido tras un login o refresh exitoso. */
public record AuthTokens(
        String accessToken, String refreshToken, String tokenType, long expiresInSeconds) {

    public static AuthTokens bearer(String access, String refresh, long expiresInSeconds) {
        return new AuthTokens(access, refresh, "Bearer", expiresInSeconds);
    }
}
