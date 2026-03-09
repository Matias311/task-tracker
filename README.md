# Task tracker
Aplicacion de consola para crear y gestionar en Memoria. 

## Requerimientos
- Java 17+ 
- Maven 

## Cómo ejecutar
### 1) Correr la app
```bash
mvn test
mvn package
java -cp target/<tu-jar>.jar com.tasktracker.app.App
```

> Si no tienes JAR configurado, también puedes correr el `main` desde tu IDE:
`src/main/java/com/tasktracker/app/App.java`

## Funcionalidades
- Crear tarea (id, title obligatorios; el resto opcional)
- Listar todas las tareas
- Filtrar por:
  - Tipo (`PROGRAMMING`, `LIVE`, `UNIVERSITY`)
  - Prioridad (`HIGH`, `MEDIUM`, `LOW`)
  - Estado (`TODO`, `DOING`, `DONE`)
- Completar una tarea (cambia estado a `DONE`)
- Marcar tarea como no hecha (cambia estado a `TODO`)
- Eliminar tarea
- Ordenar tareas por:
  - Due date
  - Priority

## Arquitectura (capas)
El proyecto está organizado por capas:

- `domain`: entidades y enums
  - `Task`, `TaskType`, `TaskPriority`, `TaskStatus`
- `repository`: persistencia en memoria (in-memory)
  - `TaskRepository`, `TaskRepositoryImpl`
- `service`: reglas de negocio / validaciones
  - `TaskService`
- `cli`: interfaz por consola y comandos
  - `Menu` y `cli/commands/*`
- `utils`: validaciones comunes
  - `VerifyData`

## Patrones de diseño utilizados
### Builder 
- `Task` se construye con `Task.Builder`.
- Se usa un constructor privado y validaciones centralizadas.
- La clase `Task` es inmutable (campos `final`, sin setters).

### Command 
- `TaskCommand` define la interfaz `execute()`.
- Comandos concretos (ej: `SaveTaskCommand`, `CompleteTaskCommand`, `DeleteCommand`).
- `CommandHistory` ejecuta un comando y lo guarda en historial.

### Observer 
- `Observer` define la interfaz `update()`.
- `AudditLogger` implementa Observer y guarda en memoria (de manera ordenada) los eventos 
- Se usa para la auditoria. Usa la clase `Event` para crear un nuevo evento. 

## Tests
Los tests están en `src/test/java` e incluyen:
- `TaskTest` (domain)
- `TaskRepositoryImplTest` (repository)
- `TaskServiceTest` (service)
- `MenuTest` + `CommandTest` (cli)
Para ejecutar:
```bash
mvn test
```

## Notas
- El repositorio actual guarda las tareas en memoria (no persistencia a disco).
- Las fechas usan `LocalDate`.
