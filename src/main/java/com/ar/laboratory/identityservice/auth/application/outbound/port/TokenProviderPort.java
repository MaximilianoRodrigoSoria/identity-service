package com.ar.laboratory.identityservice.auth.application.outbound.port;

import java.util.Collection;

/** Puerto de salida para emitir el access token (JWT). */
public interface TokenProviderPort {

    /** Emite un access token firmado para el subject dado, con sus authorities. */
    String issueAccessToken(String subject, Collection<String> authorities);

    /** Vida del access token, en segundos. */
    long accessTokenTtlSeconds();
}
