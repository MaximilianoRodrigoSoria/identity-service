package com.ar.laboratory.identityservice.auth.infrastructure.inbound.web.controller;

import com.ar.laboratory.identityservice.auth.application.inbound.command.ChangePasswordCommand;
import com.ar.laboratory.identityservice.auth.application.inbound.command.GetCurrentUserCommand;
import com.ar.laboratory.identityservice.auth.application.inbound.command.LoginCommand;
import com.ar.laboratory.identityservice.auth.application.inbound.command.LogoutCommand;
import com.ar.laboratory.identityservice.auth.application.inbound.command.RefreshTokenCommand;
import com.ar.laboratory.identityservice.auth.application.inbound.command.RegisterUserCommand;
import com.ar.laboratory.identityservice.auth.infrastructure.inbound.web.dto.AuthResponse;
import com.ar.laboratory.identityservice.auth.infrastructure.inbound.web.dto.ChangePasswordRequest;
import com.ar.laboratory.identityservice.auth.infrastructure.inbound.web.dto.LoginRequest;
import com.ar.laboratory.identityservice.auth.infrastructure.inbound.web.dto.RefreshRequest;
import com.ar.laboratory.identityservice.auth.infrastructure.inbound.web.dto.RegisterRequest;
import com.ar.laboratory.identityservice.auth.infrastructure.inbound.web.dto.UserResponse;
import com.ar.laboratory.identityservice.auth.infrastructure.inbound.web.mapper.AuthDtoMapper;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** API de autenticación y autorización. */
@Tag(name = "Auth", description = "Registro, login, refresh y gestión de sesión")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@RateLimiter(name = "auth-api")
public class AuthController {

    private final RegisterUserCommand registerUserCommand;
    private final LoginCommand loginCommand;
    private final RefreshTokenCommand refreshTokenCommand;
    private final LogoutCommand logoutCommand;
    private final GetCurrentUserCommand getCurrentUserCommand;
    private final ChangePasswordCommand changePasswordCommand;
    private final AuthDtoMapper mapper;

    @Operation(summary = "Registrar un usuario")
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        var user = registerUserCommand.execute(request.getEmail(), request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toUserResponse(user));
    }

    @Operation(summary = "Autenticarse y obtener tokens")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        var tokens = loginCommand.execute(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(mapper.toAuthResponse(tokens));
    }

    @Operation(summary = "Rotar el refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        var tokens = refreshTokenCommand.execute(request.getRefreshToken());
        return ResponseEntity.ok(mapper.toAuthResponse(tokens));
    }

    @Operation(summary = "Cerrar todas las sesiones del usuario autenticado")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Authentication authentication) {
        logoutCommand.execute(currentUserId(authentication));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtener el usuario autenticado")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        var user = getCurrentUserCommand.execute(currentUserId(authentication));
        return ResponseEntity.ok(mapper.toUserResponse(user));
    }

    @Operation(summary = "Cambiar la contraseña del usuario autenticado")
    @PostMapping("/password/change")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request, Authentication authentication) {
        changePasswordCommand.execute(
                currentUserId(authentication),
                request.getCurrentPassword(),
                request.getNewPassword());
        return ResponseEntity.noContent().build();
    }

    private UUID currentUserId(Authentication authentication) {
        return UUID.fromString(authentication.getName());
    }
}
