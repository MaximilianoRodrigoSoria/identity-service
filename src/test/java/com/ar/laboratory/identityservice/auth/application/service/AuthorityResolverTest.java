package com.ar.laboratory.identityservice.auth.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ar.laboratory.identityservice.auth.domain.model.Permission;
import com.ar.laboratory.identityservice.auth.domain.model.Role;
import com.ar.laboratory.identityservice.auth.domain.model.User;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("AuthorityResolver")
class AuthorityResolverTest {

    @Test
    @DisplayName("combina nombres de rol y de permiso, sin duplicados")
    void resolvesRolesAndPermissions() {
        Permission read = Permission.builder().id(1L).name("user:read").build();
        Role role =
                Role.builder().id(1L).name("ROLE_USER").permissions(Set.of(read)).build();
        User user = User.builder().roles(Set.of(role)).build();

        assertThat(AuthorityResolver.authoritiesOf(user))
                .containsExactlyInAnyOrder("ROLE_USER", "user:read");
    }

    @Test
    @DisplayName("usuario sin roles → lista vacía")
    void noRoles() {
        assertThat(AuthorityResolver.authoritiesOf(User.builder().build())).isEmpty();
    }
}
