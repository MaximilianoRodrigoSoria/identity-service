package com.ar.laboratory.identityservice.auth.application.service;

import com.ar.laboratory.identityservice.auth.domain.model.Permission;
import com.ar.laboratory.identityservice.auth.domain.model.User;
import java.util.List;
import java.util.stream.Stream;

/** Deriva las authorities de un usuario: nombres de rol (ROLE_*) + nombres de permiso. */
public final class AuthorityResolver {

    private AuthorityResolver() {}

    public static List<String> authoritiesOf(User user) {
        if (user.getRoles() == null) {
            return List.of();
        }
        return user.getRoles().stream()
                .flatMap(
                        role ->
                                Stream.concat(
                                        Stream.of(role.getName()),
                                        role.getPermissions() == null
                                                ? Stream.<String>empty()
                                                : role.getPermissions().stream()
                                                        .map(Permission::getName)))
                .distinct()
                .toList();
    }
}
