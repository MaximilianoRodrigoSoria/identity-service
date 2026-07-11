package com.ar.laboratory.identityservice.auth.domain.model;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Usuario del programa de identidad. La contraseña se guarda únicamente como hash; el dominio no
 * conoce el algoritmo (se aplica en infraestructura vía un puerto).
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private UUID id;
    private String email;
    private String passwordHash;
    private UserStatus status;
    private boolean emailVerified;
    @Builder.Default private Set<Role> roles = new HashSet<>();
    private Instant createdAt;
    private Instant updatedAt;

    /** Crea un usuario nuevo, activo, con el rol provisto. */
    public static User createActive(String email, String passwordHash, Role role, Instant now) {
        Set<Role> roles = new HashSet<>();
        if (role != null) {
            roles.add(role);
        }
        return User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .passwordHash(passwordHash)
                .status(UserStatus.ACTIVE)
                .emailVerified(false)
                .roles(roles)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /** Reemplaza el hash de la contraseña. */
    public void changePassword(String newPasswordHash, Instant now) {
        this.passwordHash = newPasswordHash;
        this.updatedAt = now;
    }

    /** Marca el email como verificado y activa la cuenta si estaba pendiente. */
    public void markEmailVerified(Instant now) {
        this.emailVerified = true;
        if (this.status == UserStatus.PENDING) {
            this.status = UserStatus.ACTIVE;
        }
        this.updatedAt = now;
    }
}
