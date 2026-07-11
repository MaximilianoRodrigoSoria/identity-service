package com.ar.laboratory.identityservice.auth.infrastructure.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propiedades del emisor de tokens del servicio de identidad ({@code app.security.jwt.*}). Reutiliza
 * el mismo {@code secret} que valida el filtro de seguridad del scaffold.
 */
@Data
@ConfigurationProperties(prefix = "app.security.jwt")
public class AuthJwtProperties {

    /** Secret HMAC de firma (≥ 256 bits). */
    private String secret = "changeme-replace-with-a-256-bit-secret-key-in-production";

    /** Vida del access token, en milisegundos (default 15 min). */
    private long accessExpirationMs = 900000;

    /** Vida del refresh token, en días (default 30). */
    private long refreshExpirationDays = 30;
}
