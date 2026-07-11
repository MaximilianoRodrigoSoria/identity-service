package com.ar.laboratory.identityservice.auth.infrastructure.inbound.web.dto;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Vista pública de un usuario. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String email;
    private String status;
    private boolean emailVerified;
    private List<String> roles;
    private List<String> authorities;
}
