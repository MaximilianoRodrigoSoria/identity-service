package com.ar.laboratory.identityservice.auth.application.inbound.command;

import com.ar.laboratory.identityservice.auth.application.model.AuthTokens;

/** Puerto de entrada: rotar el refresh token y emitir un nuevo access token. */
public interface RefreshTokenCommand {
    AuthTokens execute(String rawRefreshToken);
}
