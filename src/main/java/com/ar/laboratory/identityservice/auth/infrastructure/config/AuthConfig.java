package com.ar.laboratory.identityservice.auth.infrastructure.config;

import com.ar.laboratory.identityservice.auth.application.inbound.command.ChangePasswordCommand;
import com.ar.laboratory.identityservice.auth.application.inbound.command.GetCurrentUserCommand;
import com.ar.laboratory.identityservice.auth.application.inbound.command.LoginCommand;
import com.ar.laboratory.identityservice.auth.application.inbound.command.LogoutCommand;
import com.ar.laboratory.identityservice.auth.application.inbound.command.RefreshTokenCommand;
import com.ar.laboratory.identityservice.auth.application.inbound.command.RegisterUserCommand;
import com.ar.laboratory.identityservice.auth.application.outbound.port.PasswordHasherPort;
import com.ar.laboratory.identityservice.auth.application.outbound.port.RefreshTokenRepositoryPort;
import com.ar.laboratory.identityservice.auth.application.outbound.port.RoleRepositoryPort;
import com.ar.laboratory.identityservice.auth.application.outbound.port.TokenProviderPort;
import com.ar.laboratory.identityservice.auth.application.outbound.port.UserRepositoryPort;
import com.ar.laboratory.identityservice.auth.application.usecase.ChangePasswordUseCase;
import com.ar.laboratory.identityservice.auth.application.usecase.GetCurrentUserUseCase;
import com.ar.laboratory.identityservice.auth.application.usecase.LoginUseCase;
import com.ar.laboratory.identityservice.auth.application.usecase.LogoutUseCase;
import com.ar.laboratory.identityservice.auth.application.usecase.RefreshTokenUseCase;
import com.ar.laboratory.identityservice.auth.application.usecase.RegisterUserUseCase;
import com.ar.laboratory.identityservice.auth.infrastructure.security.AuthJwtProperties;
import java.time.Duration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Wiring de los casos de uso del feature auth (POJOs) con sus puertos. */
@Configuration
@EnableConfigurationProperties(AuthJwtProperties.class)
public class AuthConfig {

    private static Duration refreshTtl(AuthJwtProperties props) {
        return Duration.ofDays(props.getRefreshExpirationDays());
    }

    @Bean
    public RegisterUserCommand registerUserCommand(
            UserRepositoryPort users, RoleRepositoryPort roles, PasswordHasherPort hasher) {
        return new RegisterUserUseCase(users, roles, hasher);
    }

    @Bean
    public LoginCommand loginCommand(
            UserRepositoryPort users,
            PasswordHasherPort hasher,
            TokenProviderPort tokenProvider,
            RefreshTokenRepositoryPort refreshTokens,
            AuthJwtProperties props) {
        return new LoginUseCase(users, hasher, tokenProvider, refreshTokens, refreshTtl(props));
    }

    @Bean
    public RefreshTokenCommand refreshTokenCommand(
            RefreshTokenRepositoryPort refreshTokens,
            UserRepositoryPort users,
            TokenProviderPort tokenProvider,
            AuthJwtProperties props) {
        return new RefreshTokenUseCase(refreshTokens, users, tokenProvider, refreshTtl(props));
    }

    @Bean
    public LogoutCommand logoutCommand(RefreshTokenRepositoryPort refreshTokens) {
        return new LogoutUseCase(refreshTokens);
    }

    @Bean
    public GetCurrentUserCommand getCurrentUserCommand(UserRepositoryPort users) {
        return new GetCurrentUserUseCase(users);
    }

    @Bean
    public ChangePasswordCommand changePasswordCommand(
            UserRepositoryPort users,
            PasswordHasherPort hasher,
            RefreshTokenRepositoryPort refreshTokens) {
        return new ChangePasswordUseCase(users, hasher, refreshTokens);
    }
}
