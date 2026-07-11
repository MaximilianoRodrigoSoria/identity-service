package com.ar.laboratory.identityservice.auth.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ar.laboratory.identityservice.auth.application.outbound.port.PasswordHasherPort;
import com.ar.laboratory.identityservice.auth.application.outbound.port.RefreshTokenRepositoryPort;
import com.ar.laboratory.identityservice.auth.application.outbound.port.UserRepositoryPort;
import com.ar.laboratory.identityservice.auth.domain.exception.InvalidCredentialsException;
import com.ar.laboratory.identityservice.auth.domain.exception.UserNotFoundException;
import com.ar.laboratory.identityservice.auth.domain.model.User;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Logout / GetCurrentUser / ChangePassword")
class SessionUseCasesTest {

    @Mock private UserRepositoryPort users;
    @Mock private PasswordHasherPort hasher;
    @Mock private RefreshTokenRepositoryPort refreshTokens;

    private final UUID userId = UUID.randomUUID();

    @Test
    @DisplayName("logout revoca todos los refresh tokens del usuario")
    void logout() {
        new LogoutUseCase(refreshTokens).execute(userId);
        verify(refreshTokens).revokeAllForUser(eq(userId), any());
    }

    @Test
    @DisplayName("getCurrentUser devuelve el usuario o lanza si no existe")
    void getCurrentUser() {
        User user = User.builder().id(userId).email("a@b.com").build();
        when(users.findById(userId)).thenReturn(Optional.of(user));
        assertThat(new GetCurrentUserUseCase(users).execute(userId)).isEqualTo(user);

        when(users.findById(userId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> new GetCurrentUserUseCase(users).execute(userId))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("changePassword verifica la actual, actualiza y revoca sesiones")
    void changePasswordOk() {
        User user = User.builder().id(userId).passwordHash("old").build();
        when(users.findById(userId)).thenReturn(Optional.of(user));
        when(hasher.matches("current", "old")).thenReturn(true);
        when(hasher.hash("newpass12")).thenReturn("newhash");
        when(users.save(any())).thenAnswer(inv -> inv.getArgument(0));

        new ChangePasswordUseCase(users, hasher, refreshTokens)
                .execute(userId, "current", "newpass12");

        assertThat(user.getPasswordHash()).isEqualTo("newhash");
        verify(refreshTokens).revokeAllForUser(eq(userId), any());
    }

    @Test
    @DisplayName("changePassword con contraseña actual incorrecta → InvalidCredentials")
    void changePasswordBadCurrent() {
        User user = User.builder().id(userId).passwordHash("old").build();
        when(users.findById(userId)).thenReturn(Optional.of(user));
        when(hasher.matches("wrong", "old")).thenReturn(false);

        assertThatThrownBy(
                        () ->
                                new ChangePasswordUseCase(users, hasher, refreshTokens)
                                        .execute(userId, "wrong", "newpass12"))
                .isInstanceOf(InvalidCredentialsException.class);
        verify(users, never()).save(any());
    }
}
