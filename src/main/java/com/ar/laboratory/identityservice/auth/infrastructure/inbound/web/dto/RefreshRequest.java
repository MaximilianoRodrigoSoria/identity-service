package com.ar.laboratory.identityservice.auth.infrastructure.inbound.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Solicitud de rotación de refresh token. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshRequest {

    @NotBlank(message = "El refreshToken es obligatorio")
    private String refreshToken;
}
