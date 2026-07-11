package com.ar.laboratory.identityservice.auth.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ar.laboratory.identityservice.auth.application.outbound.port.PasswordHasherPort;
import com.ar.laboratory.identityservice.auth.application.outbound.port.RoleRepositoryPort;
import com.ar.laboratory.identityservice.auth.application.outbound.port.UserRepositoryPort;
import com.ar.laboratory.identityservice.auth.domain.exception.EmailAlreadyRegisteredException;
import com.ar.laboratory.identityservice.auth.domain.model.Role;
import com.ar.laboratory.identityservice.auth.domain.model.User;
import com.ar.laboratory.identityservice.auth.domain.model.UserStatus;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterUserUseCase")
class RegisterUserUseCaseTest {

    @Mock private UserRepositoryPort users;
    @Mock private RoleRepositoryPort roles;
    @Mock private PasswordHasherPort hasher;

    private RegisterUserUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegisterUserUseCase(users, roles, hasher);
    }

    @Test
    @DisplayName("registra normalizando el email y asignando ROLE_USER")
    void registersUser() {
        when(users.existsByEmail("a@b.com")).thenReturn(false);
        when(roles.findByName("ROLE_USER"))
                .thenReturn(Optional.of(Role.builder().id(1L).name("ROLE_USER").build()));
        when(hasher.hash("secret12")).thenReturn("hashed");
        when(users.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User user = useCase.execute("A@B.com", "secret12");

        assertThat(user.getEmail()).isEqualTo("a@b.com");
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.getPasswordHash()).isEqualTo("hashed");
        assertThat(user.getRoles()).extracting(Role::getName).containsExactly("ROLE_USER");
    }

    @Test
    @DisplayName("email duplicado → EmailAlreadyRegisteredException")
    void duplicateEmail() {
        when(users.existsByEmail("a@b.com")).thenReturn(true);
        assertThatThrownBy(() -> useCase.execute("a@b.com", "secret12"))
                .isInstanceOf(EmailAlreadyRegisteredException.class);
        verify(users, never()).save(any());
    }
}
