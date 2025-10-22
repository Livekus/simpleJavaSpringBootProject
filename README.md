# Simple Java Spring Boot Project

A minimal, production-ready-ish REST API built with **Spring Boot 3.2.5** and **Java 17**. It ships with:

- `GET /greeting` — hello endpoint
- **Book API** (`/api/books`) — CRUD with JPA + H2, validation, proper HTTP semantics
- Actuator health endpoint, dev-friendly config, and tests

---

## Tech stack
- Java 17
- Spring Boot 3.2.5
  - Web (Spring MVC)
  - Data JPA (Hibernate 6)
  - Validation (Jakarta Validation)
  - Actuator
- H2 (in-memory) for local/dev
- Maven

---

## Project structure
```
app/
├─ src/
│  ├─ main/java/com/example/demo/
│  │  ├─ DemoApplication.java
│  │  ├─ GreetingController.java
│  │  ├─ adapter/web/controller/
│  │  │  └─ BookController.java
│  │  ├─ dtos/
│  │  │  └─ BookDtos.java
│  │  ├─ application/portout/repository/
│  │  │  └─ BookRepository.java
│  │  ├─ service/
│  │  │  └─ BookService.java
│  │  └─ domain/model/
│  │     └─ Book.java
│  └─ test/java/com/example/demo/
│     ├─ adapter/web/controller/
│     │  └─ BookControllerTest.java
│     └─ GreetingControllerTest.java
├─ pom.xml
└─ README.md
```

### Architecture (Hexagonal-lite)
- **Domain model**: `book/domain/model/Book` — pure business entity.
- **Ports (domain)**: `book/domain/port/out/BookRepositoryPort` — defines what persistence must provide.
- **Adapters**:
  - **Persistence**: `adapter/persistence/BookRepositoryAdapter` implements the port using `SpringDataBookRepository` (extends `JpaRepository<Book, Long>`).
  - **Web**: `adapter/web/BookController` — HTTP API → delegates to application service.
- **Application layer**: `application/service/BookService` — orchestrates use cases; depends only on ports.
- **DTOs**: `application/dto/BookDtos` — request/response types with validation.
- **Greeting feature**: kept isolated in `greeting/` for clarity.

> Folder names mirror packages. Avoid dot-named directories (e.g., `com.example.demo` as a single folder). Use nested folders like `com/example/demo` so IntelliJ and Java resolve packages correctly.

---

## Prerequisites
- **Java 17+** installed and `JAVA_HOME` set
- **Maven** or use the Maven Wrapper (recommended)

### (Windows) One-time setup via PowerShell
```powershell
# Verify
java -version
mvn -v
```
If Maven isn’t installed, generate the wrapper once (requires Maven available somewhere), or use IntelliJ’s bundled Maven.

---

## Quick start

### 1) Install deps & run
```bash
# from project root (app/)
mvn clean package
mvn spring-boot:run
```
> Alternatively, after packaging: `java -jar target/simple-java-spring-boot-project-0.0.1-SNAPSHOT.jar`

### 2) Smoke test
```bash
# curl
curl http://localhost:8080/greeting
curl "http://localhost:8080/greeting?name=BOM"
```
**Expected**
```json
{"message":"Hello, World!"}
{"message":"Hello, BOM!"}
```

---

## Configuration
`src/main/resources/application.properties`
```properties
spring.datasource.url=jdbc:h2:mem:demo;DB_CLOSE_DELAY=-1
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true
management.endpoints.web.exposure.include=health,info
```
- H2 console: `http://localhost:8080/h2-console` (JDBC URL: shown in logs or `jdbc:h2:mem:demo`)

---

## API

### Greeting
- **GET** `/greeting` → `{ "message": "Hello, World!" }`
- **GET** `/greeting?name=YourName` → `{ "message": "Hello, YourName!" }`

### Book
**Entity** (`Book.java`)
```java
@Entity
@Table(name = "book")
public class Book {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable=false) private String title;
  @Column(nullable=false) private String author;
  @Column(name="published_year") private Integer year; // avoid YEAR keyword in H2
  // constructors/getters/setters
}
```

**Endpoints** (controller paths prefix `/api/books`)

| Method | Path              | Body (JSON)                                      | Response                          |
|-------:|-------------------|--------------------------------------------------|-----------------------------------|
| POST   | `/api/books`      | `{ "title":"DDD","author":"Evans","year":2003 }` | `201 Created` + created resource  |
| GET    | `/api/books/{id}` | –                                                | `200 OK` + book                   |
| PUT    | `/api/books/{id}` | `{ "title":"DDD 2e","author":"Evans","year":2015 }` | `200 OK` + updated book           |
| DELETE | `/api/books/{id}` | –                                                | `204 No Content`                  |

**Example (PowerShell):**
```powershell
# create
irm -Method Post -ContentType 'application/json' `
  -Body '{"title":"DDD","author":"Evans","year":2003}' `
  http://localhost:8080/api/books

# get
irm http://localhost:8080/api/books/1

# update
irm -Method Put -ContentType 'application/json' `
  -Body '{"title":"DDD 2e","author":"Evans","year":2015}' `
  http://localhost:8080/api/books/1

# delete
Invoke-RestMethod -Method Delete http://localhost:8080/api/books/1
```

**Validation**
- DTOs use Jakarta Validation annotations (e.g., `@NotBlank`, `@Min`, `@Max`)
- Invalid input → `400 Bad Request` with RFC7807 `ProblemDetail` (if `@RestControllerAdvice` is present)

---

## Actuator
- `/actuator/health` → app health
- Expose more endpoints via `management.endpoints.web.exposure.include`

---

## Testing
```bash
mvn -q clean test
```
`GreetingControllerTest` uses `@WebMvcTest` + `MockMvc`.

---

## Packaging & Docker
```bash
# fat jar
mvn clean package
java -jar target/simple-java-spring-boot-project-0.0.1-SNAPSHOT.jar

# Build an OCI image (requires Docker)
mvn spring-boot:build-image -DskipTests
# Run it
docker run --rm -p 8080:8080 simple-java-spring-boot-project:0.0.1-SNAPSHOT
```

---

## Troubleshooting
- **Red annotations (`@Entity`, `@Id`, etc.)** → ensure `spring-boot-starter-data-jpa` exists, Maven ⟳ reload, and correct imports `jakarta.persistence.*`.
- **Package errors** → disable *Compact Middle Packages* and ensure folders are nested: `src/main/java/com/example/demo/...`.
- **H2 DDL error about `YEAR`** → we map `year` field to `published_year` via `@Column(name="published_year")`.
- **Duplicate dependency warning** → keep only one `<dependency>` for `spring-boot-starter-data-jpa` in `pom.xml`.
- **`mvn`/`java` not found in IntelliJ terminal** → set `JAVA_HOME`, add Maven to PATH, or use Maven Wrapper; restart IDE to pick up env vars.

---

## Next steps (optional)
- Add `BookRepository`, `BookService`, and controller tests
- Add OpenAPI: `org.springdoc:springdoc-openapi-starter-webmvc-ui` → `/swagger-ui.html`
- Switch to Postgres (prod): set `spring.datasource.url`, add driver `org.postgresql:postgresql`, and use Flyway for schema
- Observability: add metrics, logs JSON, request tracing (Micrometer + OpenTelemetry)

---

## License
MIT (or your preferred license).


