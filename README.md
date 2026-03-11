# Task Tracker

Aplicacion CLI para crear y gestionar tareas, con persistencia en PostgreSQL via JDBC.

## Requerimientos

- Java 17+
- Maven
- Docker y Docker Compose

## Como ejecutar

### Con Docker Compose (recomendado)

1. Crea un archivo `.env` en la raiz del proyecto:

```env
POSTGRES_HOST=postgres
POSTGRES_DB=tasktracker
POSTGRES_USER=postgres
POSTGRES_PASSWORD=tu_password
```

2. Compila el proyecto y levanta los contenedores:

```bash
mvn clean package -DskipTests
docker compose build
docker compose run --rm app
```

> El contenedor de Postgres ejecuta automaticamente el script `src/main/resources/init-db.sql` para crear las tablas.

### Sin Docker (solo tests)

```bash
mvn test
```

> Los tests de integracion usan Testcontainers, que levanta un Postgres efimero automaticamente. Solo necesitas Docker corriendo.

### Desde el IDE

Ejecuta `src/main/java/com/tasktracker/app/App.java` directamente. Necesitas las variables de entorno configuradas y Postgres corriendo.

## Funcionalidades

| # | Opcion | Descripcion |
|---|--------|-------------|
| 1 | Crear tarea | `id` y `title` obligatorios, el resto opcional |
| 2 | Listar todas las tareas | Muestra todas las tareas almacenadas |
| 3 | Filtrar por tipo | `PROGRAMMING`, `LIVE`, `UNIVERSITY` |
| 4 | Filtrar por prioridad | `HIGH`, `MEDIUM`, `LOW` |
| 5 | Filtrar por estado | `TODO`, `DOING`, `DONE` |
| 6 | Completar tarea | Cambia el estado a `DONE` |
| 7 | Ordenar por due date | Ordena las tareas por fecha limite |
| 8 | Ordenar por prioridad | Ordena las tareas por nivel de prioridad |
| 9 | Buscar por id | Busca una tarea usando su identificador |
| 10 | Tareas completadas | Muestra solo las tareas con estado `DONE` |
| 11 | Deshacer tarea | Cambia el estado de vuelta a `TODO` |
| 12 | Eliminar tarea | Elimina una tarea por id |
| 13 | Salir | Cierra la aplicacion |

## Arquitectura

```
com.tasktracker.app
├── domain/              Entidades y enums (Task, Event, TaskType, TaskPriority, TaskStatus)
├── repository/          Persistencia: TaskDao (JDBC), TaskRepositoryImpl (in-memory), EventDao
│   └── observer/        Patron Observer para auditoria (AudditLogger, AudditLoggerInDB)
├── service/             Reglas de negocio (TaskService, EventService)
├── cli/                 Interfaz de consola (Menu)
│   └── commands/        Patron Command (SaveTaskCommand, CompleteTaskCommand, etc.)
├── exception/           Excepciones custom (PersistenceException, NotFoundException)
└── utils/               Validaciones y conexion JDBC (VerifyData, ConnectionJdbc)
```

### Capas

- **Domain**: `Task` (inmutable, Builder pattern), `Event` para auditoria, enums para type/priority/status
- **Repository**: `TaskRepository` (interfaz) con dos implementaciones:
  - `TaskDao` — persistencia real con PostgreSQL via JDBC y PreparedStatements
  - `TaskRepositoryImpl` — en memoria, usada en tests unitarios
  - `EventDao` — persiste eventos de auditoria en PostgreSQL
- **Service**: `TaskService` coordina validacion, repositorio y observer. `EventService` gestiona eventos
- **CLI**: `Menu` maneja el input del usuario. Cada accion es un `TaskCommand` ejecutado por `CommandHistory`
- **Observer**: `AudditLoggerInDB` persiste eventos en la tabla `audit_task`. `AudditLogger` los imprime por consola

## Patrones de diseno

### Builder
- `Task` se construye con `Task.Builder(id, title).type(...).priority(...).build()`
- Constructor privado, campos `final`, sin setters — la clase es inmutable
- `Builder.from(task)` crea una copia para operaciones como cambio de estado

### Command
- `TaskCommand` define el contrato `execute()`
- Cada opcion del menu es un comando concreto (`SaveTaskCommand`, `DeleteCommand`, etc.)
- `CommandHistory` ejecuta y guarda el historial de comandos

### Observer
- `Observer` define `update(Task, String action)`
- `AudditLoggerInDB` persiste la auditoria en PostgreSQL via `EventService`
- `AudditLogger` imprime en consola (usado en tests)

## Base de datos

PostgreSQL 16 con dos tablas:

```sql
CREATE TABLE tasks (
    id          INT     PRIMARY KEY,
    title       VARCHAR NOT NULL,
    type        VARCHAR,
    description VARCHAR,
    priority    VARCHAR,
    status      VARCHAR,
    date        DATE,
    due_date    DATE
);

CREATE TABLE audit_task (
    id             SERIAL    PRIMARY KEY,
    id_task        INT       REFERENCES tasks(id),
    action         VARCHAR,
    task_title     VARCHAR,
    execution_date TIMESTAMP
);
```

El schema se ejecuta automaticamente al levantar el contenedor de Postgres via `docker-entrypoint-initdb.d`.

## Tests

73 tests organizados por capa:

| Suite | Tipo | Tests | Descripcion |
|-------|------|-------|-------------|
| `TaskTest` | Unitario | 10 | Construccion, validacion, update de Task |
| `TaskRepositoryImplTest` | Unitario | 20 | CRUD en memoria |
| `TaskServiceTest` | Unitario | 19 | Reglas de negocio, validaciones |
| `CommandTest` | Unitario | 5 | Ejecucion de comandos |
| `MenuTest` | Unitario | 6 | Input/output del CLI |
| `TaskDaoTest` | Integracion | 12 | CRUD contra Postgres real (Testcontainers) |
| `EventDaoTest` | Integracion | 1 | Persistencia de eventos (Testcontainers) |

```bash
mvn test
```

## Stack

- Java 17
- Maven
- PostgreSQL 16
- JDBC con PreparedStatements
- Testcontainers para tests de integracion
- JUnit 5
- Docker / Docker Compose
