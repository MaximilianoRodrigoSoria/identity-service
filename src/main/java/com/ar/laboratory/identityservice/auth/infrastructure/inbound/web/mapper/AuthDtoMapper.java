package com.ar.laboratory.identityservice.auth.infrastructure.inbound.web.mapper;

import com.ar.laboratory.identityservice.auth.application.model.AuthTokens;
import com.ar.laboratory.identityservice.auth.application.service.AuthorityResolver;
import com.ar.laboratory.identityservice.auth.domain.model.Role;
import com.ar.laboratory.identityservice.auth.domain.model.User;
import com.ar.laboratory.identityservice.auth.infrastructure.inbound.web.dto.AuthResponse;
import com.ar.laboratory.identityservice.auth.infrastructure.inbound.web.dto.UserResponse;
import java.util.List;
import org.springframework.stereotype.Component;

/** Conversión entre DTOs web y los tipos de aplicación/dominio de auth. */
@Component
public class AuthDtoMapper {

    public AuthResponse toAuthResponse(AuthTokens tokens) {
        return AuthResponse.builder()
                .accessToken(tokens.accessToken())
                .refreshToken(tokens.refreshToken())
                .tokenType(tokens.tokenType())
                .expiresIn(tokens.expiresInSeconds())
                .build();
    }

    public UserResponse toUserResponse(User user) {
        List<String> roles =
                user.getRoles() == null
                        ? List.of()
                        : user.getRoles().stream().map(Role::getName).sorted().toList();
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .status(user.getStatus() == null ? null : user.getStatus().name())
                .emailVerified(user.isEmailVerified())
                .roles(roles)
                .authorities(AuthorityResolver.authoritiesOf(user))
                .build();
    }
}
