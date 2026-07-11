package com.ar.laboratory.identityservice.auth.application.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Utilidades para refresh tokens: generación de un valor opaco y su hash determinístico (SHA-256),
 * necesario para poder buscarlo por hash sin guardar el valor en claro.
 */
public final class TokenHashing {

    private TokenHashing() {}

    /** Genera un refresh token opaco de alta entropía. */
    public static String generateOpaqueToken() {
        return (UUID.randomUUID().toString() + UUID.randomUUID().toString()).replace("-", "");
    }

    /** Hash SHA-256 en hexadecimal (determinístico) para indexar el token. */
    public static String sha256(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                sb.append(Character.forDigit((b >> 4) & 0xF, 16));
                sb.append(Character.forDigit(b & 0xF, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }
}
