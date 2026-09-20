# Arquitectura de TaskFlow

Bienvenido: este documento explica la estructura del proyecto, el flujo de la petición POST /projects/{projectId}/tasks, dónde están las reglas de negocio, cómo funciona la seguridad con JWT y cómo están organizados los tests.

1) Capas y paquetes

- Controladores (API / frontera HTTP)
  - `src/main/java/com/taskflow/controller/TaskController.java` — expone endpoints como `POST /projects/{projectId}/tasks`.
  - `src/main/java/com/taskflow/controller/ProjectController.java`

- Servicios (orquestación, casos de uso)
  - `src/main/java/com/taskflow/service/TaskService.java`
  - `src/main/java/com/taskflow/service/ProjectService.java`
  Los services coordinan repositorios, mappers y validaciones transaccionales.

- Dominio / Modelo
  - `src/main/java/com/taskflow/model/Task.java` (entidad y reglas de dominio: factories y métodos como `setStatus`)
  - `src/main/java/com/taskflow/model/TaskStatus.java`, `Priority.java`, etc.

- Repositorios (persistencia)
  - `src/main/java/com/taskflow/repository/TaskRepository.java` (extiende `JpaRepository`)

- DTOs y mappers
  - `src/main/java/com/taskflow/dto/TaskRequest.java`
  - `src/main/java/com/taskflow/dto/TaskResponse.java`
  - `src/main/java/com/taskflow/mapper/TaskMapper.java`

- Seguridad y configuración
  - `src/main/java/com/taskflow/config/SecurityConfig.java`
  - `src/main/java/com/taskflow/security/JwtAuthenticationFilter.java`
  - `src/main/java/com/taskflow/security/JwtService.java`

- Manejo de excepciones global
  - `src/main/java/com/taskflow/advice/GlobalExceptionHandler.java`

2) Recorrido completo de POST /projects/{projectId}/tasks

1. El cliente hace POST /projects/{projectId}/tasks con un `TaskRequest` JSON.
2. Llega a `src/main/java/com/taskflow/controller/TaskController.java` -> método `createTask`.
   - El controller primero valida que el proyecto exista usando `ProjectService.buscarPorId(projectId)`; si no existe lanza `ProjectNotFoundException` (mapped a 404 por `GlobalExceptionHandler`).
   - El controller delega la creación a `TaskService.crear(request, projectId)`.
3. En `src/main/java/com/taskflow/service/TaskService.java`:
   - `TaskMapper.aEntidadNueva(request, projectId)` convierte el DTO en la entidad `Task` y utiliza la factory/constructores de `Task` para establecer valores por defecto (p.ej. status `TODO`) y validar invariantes (p.ej. `dueDate` no en el pasado). Si la validación de negocio falla se lanza `TaskValidationException` (mapped a 400).
   - `repository.save(nueva)` (donde `repository` es `TaskRepository`) persiste la entidad; `TaskRepository` es un `JpaRepository` que traduce la entidad a filas en la BD.
4. `TaskRepository.save` devuelve la entidad con id asignado; `TaskController` construye la `Location` `/tasks/{id}` y responde `201 Created` con el `TaskResponse` (generado por `TaskMapper.aResponse`).

Excepciones y mapeos HTTP importantes:
- `ProjectNotFoundException` / `TaskNotFoundException` -> 404 (en `GlobalExceptionHandler`).
- `TaskValidationException` -> 400 (entrada inválida según reglas de dominio en la entidad).
- `TaskStateException` (traducción desde reglas de estado) -> 422 (regla de negocio que impide la operación pese a que el request sea sintácticamente válido).

3) Dónde viven las reglas de negocio

- Reglas de invariantes y transiciones críticas: en la entidad de dominio `src/main/java/com/taskflow/model/Task.java`. Ejemplos: validación de `dueDate` en la factory/constructor, regla "no pasar a DONE si no hay assignee" implementada en `setStatus`.
- Orquestación y políticas de alto nivel: en los servicios (`TaskService`, `ProjectService`). El servicio orquesta llamadas a repositorios y mappers, traduce excepciones de dominio a excepciones de aplicación (p.ej. convertir una `TaskValidationException` checked en una `TaskStateException` cuando corresponde).
- Reglas de acceso a recursos (owner checks) se aplican vía anotaciones de método (`@PreAuthorize`) y componentes de seguridad cuando corresponde.

4) Seguridad con JWT

- La configuración principal está en `src/main/java/com/taskflow/config/SecurityConfig.java`.
  - Cadena de filtros personalizada, CORS configurado, endpoints públicos (`/auth/**`, docs, `/info`) permitidos.
  - `sessionCreationPolicy(SessionCreationPolicy.STATELESS)` para un API sin sesión en servidor.
- `src/main/java/com/taskflow/security/JwtAuthenticationFilter.java` procesa la cabecera `Authorization: Bearer <token>`:
  - Si el header no existe deja pasar la request anónima; si existe extrae el token, valida firma/expiración con `JwtService` y carga `UserDetails` desde la base.
  - Si el token es válido coloca una `Authentication` en el `SecurityContext` y la petición continúa autenticada.
  - Si el token está corrupto o expirado el filtro responde 401 directamente (el filtro corre antes del `DispatcherServlet`, por eso maneja la excepción y corta la cadena).
- `JwtService` contiene la lógica de firma/validación/expiración y extracción del username.
- Autorización granular: `SecurityConfig` controla acceso por URL y `@EnableMethodSecurity` activa `@PreAuthorize` para checks data-driven (p.ej. solo owner puede borrar un proyecto).

5) Organización de tests

- Tests de unidad / servicio: `src/test/java/com/taskflow/...` (nombres tipo `*ServiceTest`, `*UnitTest`).
- Slice tests (controladores con comportamientos acotados): p.ej. `src/test/java/com/taskflow/slice/TaskControllerTest.java` — usan anotaciones de slice para arrancar la capa web sin levantar la BD completa.
- Tests de repositorio / JPA: `@DataJpaTest` para `TaskRepositoryTest` (prueban queries derivadas y mapeo a BD).
- Tests de integración: p.ej. `src/test/java/com/taskflow/integration/AuthControllerTest.java` — arrancan más capas y comprueban flujo completo (login, register, tokens).
- Convención: los tests siguen el mismo paquete base `com.taskflow` para que se apliquen fácilmente los component scans y beans de prueba.

Consejos rápidos para un dev nuevo
- Lee `TaskController`, `TaskService`, `TaskRepository` y `Task` en ese orden para entender el flujo de creación.
- Las reglas de negocio críticas están en `src/main/java/com/taskflow/model/Task.java` — cambiar allí impacta la consistencia del sistema.
- Para añadir endpoints protegidos, usa `@PreAuthorize` y revisa `SecurityConfig` y `JwtAuthenticationFilter`.
- Los errores y sus códigos HTTP se centralizan en `src/main/java/com/taskflow/advice/GlobalExceptionHandler.java`.

Si quieres, genero un diagrama pequeño del flujo o un checklist de pruebas para el endpoint POST y lo agrego aquí.