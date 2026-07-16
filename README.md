# ms-app-medica-citas-bff

Capa **BFF** (Backend for Frontend) del módulo **Citas**. Puerta de entrada del módulo para el cliente (vía API Gateway): valida el contrato (`@Valid`) y delega en `ms-app-medica-citas-bs`. No accede a la base de datos.

| | |
|---|---|
| **Puerto** | `8090` |
| **Patrón** | Controller → Service → Client (Feign) |
| **Ruta base** | `/api/v1/citas` |
| **Llama a** | `citas-bs` (8091) · `pacientes-bff` |
| **Pruebas** | `CitasServiceTest` (JUnit 5 + Mockito) |
| **Swagger** | `http://localhost:8090/swagger-ui.html` — agregado también al Gateway como pestaña **Citas** |

Endpoints principales: `POST /agendar`, `GET /listar`, `GET /{id}`, `GET /listar/con-pacientes`, `PUT /actualizar`, `DELETE /eliminar/{id}`.

## Ejecución

```bash
# Con todo el ecosistema (recomendado), desde app-medica-et-fullstack-1/
docker compose up --build

# Individual
./mvnw spring-boot:run     # mvnw.cmd en Windows
./mvnw test
```
