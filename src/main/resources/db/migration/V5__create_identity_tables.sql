-- ─────────────────────────────────────────────────────────────────────────────
-- Modelo de identidad: usuarios, RBAC (roles/permisos) y refresh tokens.
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS app.users (
    id             UUID         PRIMARY KEY,
    email          VARCHAR(255) NOT NULL UNIQUE,
    password_hash  TEXT         NOT NULL,
    status         VARCHAR(20)  NOT NULL,
    email_verified BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMP    NOT NULL,
    updated_at     TIMESTAMP    NOT NULL
);

CREATE TABLE IF NOT EXISTS app.roles (
    id   BIGSERIAL   PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS app.permissions (
    id   BIGSERIAL   PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS app.user_roles (
    user_id UUID   NOT NULL REFERENCES app.users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES app.roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS app.role_permissions (
    role_id       BIGINT NOT NULL REFERENCES app.roles(id) ON DELETE CASCADE,
    permission_id BIGINT NOT NULL REFERENCES app.permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE IF NOT EXISTS app.refresh_tokens (
    id             UUID        PRIMARY KEY,
    user_id        UUID        NOT NULL REFERENCES app.users(id) ON DELETE CASCADE,
    token_hash     VARCHAR(64) NOT NULL UNIQUE,
    family_id      UUID        NOT NULL,
    expires_at     TIMESTAMP   NOT NULL,
    revoked_at     TIMESTAMP,
    replaced_by_id UUID,
    created_at     TIMESTAMP   NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user   ON app.refresh_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_family ON app.refresh_tokens(family_id);

-- Seed de roles y permisos (idempotente).
INSERT INTO app.roles(name) VALUES ('ROLE_USER'), ('ROLE_ADMIN')
    ON CONFLICT (name) DO NOTHING;

INSERT INTO app.permissions(name)
    VALUES ('user:read'), ('user:write'), ('role:read'), ('role:write')
    ON CONFLICT (name) DO NOTHING;

-- ROLE_ADMIN obtiene todos los permisos.
INSERT INTO app.role_permissions(role_id, permission_id)
SELECT r.id, p.id
FROM app.roles r CROSS JOIN app.permissions p
WHERE r.name = 'ROLE_ADMIN'
    ON CONFLICT DO NOTHING;

-- ROLE_USER obtiene solo user:read.
INSERT INTO app.role_permissions(role_id, permission_id)
SELECT r.id, p.id
FROM app.roles r JOIN app.permissions p ON p.name = 'user:read'
WHERE r.name = 'ROLE_USER'
    ON CONFLICT DO NOTHING;

COMMENT ON TABLE app.users          IS 'Usuarios del servicio de identidad';
COMMENT ON TABLE app.refresh_tokens IS 'Refresh tokens (hash SHA-256), con familia para rotación y detección de reuso';
