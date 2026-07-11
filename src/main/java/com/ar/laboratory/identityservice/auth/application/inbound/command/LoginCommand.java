package com.ar.laboratory.identityservice.auth.application.inbound.command;

import com.ar.laboratory.identityservice.auth.application.model.AuthTokens;

/** Puerto de entrada: autenticar y emitir tokens. */
public interface LoginCommand {
    AuthTokens execute(String email, String rawPassword);
}
