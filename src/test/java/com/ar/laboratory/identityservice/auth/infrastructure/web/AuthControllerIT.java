package com.ar.laboratory.identityservice.auth.infrastructure.web;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Tests de integración del flujo de auth con contexto completo, PostgreSQL real y Spring Security
 * activado. Cubre registro, login, /me autenticado, refresh y logout end-to-end.
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
            "app.security.enabled=true",
            "spring.jpa.hibernate.ddl-auto=validate"
        })
@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
@DisplayName("AuthController - Integration Tests")
class AuthControllerIT {

    private static final String BASE = "/identity-service/api/v1/auth";

    @Container @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @LocalServerPort private int port;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private WebTestClient client;

    @BeforeEach
    void setUp() {
        client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
    }

    private JsonNode post(String path, Map<String, ?> body, int expectedStatus) throws Exception {
        byte[] bytes =
                client.post()
                        .uri(BASE + path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(body)
                        .exchange()
                        .expectStatus()
                        .isEqualTo(expectedStatus)
                        .expectBody()
                        .returnResult()
                        .getResponseBodyContent();
        return (bytes == null || bytes.length == 0) ? null : objectMapper.readTree(bytes);
    }

    private void register(String email, String password) throws Exception {
        JsonNode node =
                post("/register", Map.of("email", email, "password", password), 201);
        assertThat(node.get("email").asText()).isEqualTo(email);
        assertThat(node.get("roles").toString()).contains("ROLE_USER");
    }

    private JsonNode login(String email, String password) throws Exception {
        JsonNode node = post("/login", Map.of("email", email, "password", password), 200);
        assertThat(node.get("accessToken").asText()).isNotBlank();
        assertThat(node.get("refreshToken").asText()).isNotBlank();
        return node;
    }

    @Test
    @DisplayName("flujo completo: register → login → /me → refresh → logout")
    void fullFlow() throws Exception {
        String email = "flow@x.com";
        register(email, "password1");
        JsonNode tokens = login(email, "password1");
        String access = tokens.get("accessToken").asText();
        String refresh = tokens.get("refreshToken").asText();

        // /me autenticado
        client.get()
                .uri(BASE + "/me")
                .header("Authorization", "Bearer " + access)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.email")
                .isEqualTo(email);

        // refresh rota el token
        JsonNode refreshed = post("/refresh", Map.of("refreshToken", refresh), 200);
        assertThat(refreshed.get("accessToken").asText()).isNotBlank();
        assertThat(refreshed.get("refreshToken").asText()).isNotEqualTo(refresh);

        // logout
        client.post()
                .uri(BASE + "/logout")
                .header("Authorization", "Bearer " + access)
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    @DisplayName("register duplicado → 409")
    void duplicateRegister() throws Exception {
        String email = "dup@x.com";
        register(email, "password1");
        post("/register", Map.of("email", email, "password", "password1"), 409);
    }

    @Test
    @DisplayName("login con contraseña incorrecta → 401")
    void badLogin() throws Exception {
        String email = "bad@x.com";
        register(email, "password1");
        post("/login", Map.of("email", email, "password", "wrongpass"), 401);
    }

    @Test
    @DisplayName("register con email inválido → 400")
    void invalidEmail() throws Exception {
        post("/register", Map.of("email", "no-es-email", "password", "password1"), 400);
    }

    @Test
    @DisplayName("/me sin token → 401")
    void meWithoutToken() {
        client.get().uri(BASE + "/me").exchange().expectStatus().isUnauthorized();
    }
}
