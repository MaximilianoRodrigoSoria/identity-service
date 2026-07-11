package com.ar.laboratory.identityservice.auth.infrastructure.security;

import com.ar.laboratory.identityservice.auth.application.outbound.port.TokenProviderPort;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

/**
 * Emite el access token (JWT HS256). Usa el mismo secret y el mismo claim {@code roles} que el
 * filtro de seguridad del scaffold, de modo que el token emitido se valida sin cambios.
 */
@Component
public class JwtAccessTokenAdapter implements TokenProviderPort {

    private final SecretKey signingKey;
    private final long accessTtlMs;

    public JwtAccessTokenAdapter(AuthJwtProperties props) {
        this.signingKey = Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
        this.accessTtlMs = props.getAccessExpirationMs();
    }

    @Override
    public String issueAccessToken(String subject, Collection<String> authorities) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTtlMs);
        return Jwts.builder()
                .subject(subject)
                .claim("roles", List.copyOf(authorities))
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey)
                .compact();
    }

    @Override
    public long accessTokenTtlSeconds() {
        return accessTtlMs / 1000;
    }
}
