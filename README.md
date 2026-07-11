<!-- banner-badges -->
<p align="center">
  <a href="https://www.linkedin.com/in/soriamaximilianorodrigo/" target="_blank" rel="noopener noreferrer">
    <img width="100%" src="docs/img/banner.gif" alt="Identity Service — Maximiliano Rodrigo Soria">
  </a>
</p>

<p align="center">
  <a href="LICENSE"><img src="https://img.shields.io/github/license/MaximilianoRodrigoSoria/identity-service?style=flat-square&labelColor=1A1C1F&color=06C69C" alt="License"></a>
  <img src="https://img.shields.io/github/last-commit/MaximilianoRodrigoSoria/identity-service?style=flat-square&labelColor=1A1C1F&color=06C69C" alt="Last commit">
  <img src="https://img.shields.io/github/repo-size/MaximilianoRodrigoSoria/identity-service?style=flat-square&labelColor=1A1C1F&color=06C69C" alt="Repo size">
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-06C69C?style=flat-square&labelColor=1A1C1F&logo=openjdk&logoColor=white" alt="Java">
  <img src="https://img.shields.io/badge/Spring_Security-•-06C69C?style=flat-square&labelColor=1A1C1F&logo=springsecurity&logoColor=white" alt="Spring_Security">
  <img src="https://img.shields.io/badge/JWT-•-06C69C?style=flat-square&labelColor=1A1C1F&logo=jsonwebtokens&logoColor=white" alt="JWT">
  <img src="https://img.shields.io/badge/PostgreSQL-•-06C69C?style=flat-square&labelColor=1A1C1F&logo=postgresql&logoColor=white" alt="PostgreSQL">
  <img src="https://img.shields.io/badge/Redis-•-06C69C?style=flat-square&labelColor=1A1C1F&logo=redis&logoColor=white" alt="Redis">
</p>

# Identity Service

Servicio central de autenticacion y autorizacion: registro, login, tokens JWT con refresh rotativo, RBAC, verificacion de email, MFA, OAuth2 y revocacion de sesiones.

> Proyecto de portafolio backend. Sigue el estandar de **arquitectura hexagonal (Ports & Adapters)**, Java 21 y Spring Boot, con quality gates (Spotless, Checkstyle, PMD, SpotBugs, ArchUnit), testing con Testcontainers y observabilidad (Micrometer + Prometheus).

## Caracteristicas

- Registro y login con hashing de contrasena (BCrypt)
- Access token JWT corto + refresh token rotativo con deteccion de reuso
- RBAC: usuarios, roles y permisos (`recurso:accion`)
- Verificacion de email y recuperacion de contrasena
- MFA por TOTP con codigos de recuperacion
- Login social OAuth2 (Authorization Code + PKCE)
- Gestion y revocacion de sesiones (logout / logout-all)
- Endpoint JWKS / introspeccion para servicios consumidores

## Stack

Java 21 · Spring Security · JWT · PostgreSQL · Redis · Gradle · Flyway · Docker · JUnit 5 · Testcontainers

## Arquitectura

Organizado por **feature** en capas `domain -> application -> infrastructure`, con la regla de dependencia verificada por ArchUnit. La logica de negocio (dominio y casos de uso) no depende de framework ni de infraestructura; los adaptadores (web, persistencia, mensajeria) implementan puertos definidos por la aplicacion.

## API

Contexto: `/identity-service/api/v1/auth`. Seguridad activada (JWT); endpoints publicos: register, login, refresh.

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| `POST` | `/register` | Alta de usuario (BCrypt), rol `ROLE_USER` por defecto |
| `POST` | `/login` | Devuelve access token (JWT corto) + refresh token rotativo |
| `POST` | `/refresh` | Rota el refresh token; detecta reuso (revoca la familia) |
| `GET`  | `/me` | Usuario autenticado (requiere Bearer) |
| `POST` | `/logout` | Revoca todas las sesiones del usuario |
| `POST` | `/password/change` | Cambia la contrasena y revoca sesiones |

## Estado

✅ Nucleo funcional implementado: registro con hash BCrypt, login con access JWT + refresh token rotativo, deteccion de reuso, RBAC (roles/permisos con seed), /me, logout y cambio de contrasena. Persistencia JPA/PostgreSQL + migracion Flyway, Spring Security, tests (unit + Testcontainers). La feature `example` del scaffold se conserva como referencia. Extensiones documentadas para el futuro: verificacion por email, MFA (TOTP), OAuth2 y reset por email.

---

<p align="center">
  <strong>Maximiliano Rodrigo Soria</strong><br>
  <a href="https://www.linkedin.com/in/soriamaximilianorodrigo/">LinkedIn</a> · <a href="mailto:maximilianorodrigosoria@gmail.com">maximilianorodrigosoria@gmail.com</a>
</p>
