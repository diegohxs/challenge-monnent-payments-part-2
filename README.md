# Monnet Payments - Technical Challenge

# 3. Nueva aplicación Spring Boot

Se desarrolló una nueva aplicación Spring Boot para consumir información de usuarios desde JSONPlaceholder, persistirla en una base de datos H2 y exponerla mediante un API REST protegido mediante API Token.

La aplicación también incorpora Spring Boot Actuator para monitoreo, documentación mediante Swagger/OpenAPI y pruebas unitarias utilizando JUnit 5 y Mockito.

---


# Ejecución de la aplicación

## Compilar

```bash
mvn clean install
```

## Ejecutar

```bash
mvn spring-boot:run
```

La aplicación estará disponible en:

```text
http://localhost:8080
```

---

# Endpoints principales

| Método | Endpoint | Descripción | Seguridad |
|--------|----------|-------------|-----------|
| GET | `/users` | Obtiene los usuarios almacenados en H2 | API Token |
| GET | `/actuator/health` | Estado de salud de la aplicación | API Token |
| GET | `/swagger-ui.html` | Documentación Swagger | Público |
| GET | `/v3/api-docs` | Especificación OpenAPI | Público |

---

# Ejemplo de consumo de `/users`

Request:

```http
GET /users HTTP/1.1
Host: localhost:8080
X-API-TOKEN: mi-token
```

Respuesta:

```text
200 OK
```

La respuesta contiene la lista de usuarios almacenados en H2.

---

# Ejemplo de consumo de Actuator

Request:

```http
GET /actuator/health HTTP/1.1
Host: localhost:8080
X-API-TOKEN: mi-token
```

Respuesta:

```text
200 OK
```

Sin el header:

```text
401 Unauthorized
```

---

# Flujo general de la aplicación

```text
                         ┌──────────────────────┐
                         │    JSONPlaceholder   │
                         │      /users          │
                         └──────────┬───────────┘
                                    │
                                    │ OpenFeign
                                    ▼
                         ┌──────────────────────┐
                         │     UserService      │
                         │     loadUsers()      │
                         └──────────┬───────────┘
                                    │
                                    │ saveAll()
                                    ▼
                         ┌──────────────────────┐
                         │         H2           │
                         │       users          │
                         └──────────┬───────────┘
                                    │
                                    │ findAll()
                                    ▼
                         ┌──────────────────────┐
                         │  ApplicationService  │
                         └──────────┬───────────┘
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │    UserController    │
                         │      GET /users      │
                         └──────────────────────┘
```

La seguridad se aplica transversalmente mediante:

```text
Client
  ↓
X-API-TOKEN
  ↓
ApiTokenFilter
  ↓
Spring Security
  ↓
Controller
```

La documentación OpenAPI se integra sobre los endpoints REST y documenta el header:

```text
X-API-TOKEN
```

permitiendo realizar las pruebas directamente desde Swagger UI.

---

## Tecnologías utilizadas

- Java 11
- Spring Boot 2.7.3
- Spring Web
- Spring Data JPA
- H2 Database
- Spring Cloud OpenFeign
- Spring Security
- Spring Boot Actuator
- Springdoc OpenAPI
- JUnit 5
- Mockito
- Lombok
- Maven

---

## 3.a Consumo de usuarios y persistencia en H2

La aplicación consume la información publicada por JSONPlaceholder:

```text
https://jsonplaceholder.typicode.com/users
```

Para realizar el consumo del servicio externo se utiliza **Spring Cloud OpenFeign**.

### Cliente JSONPlaceholder

El cliente se encuentra en:

```text
infraestructure/services/JsonPlaceholderClient.java
```

Implementación:

```java
@FeignClient(
        name = "jsonPlaceholderClient",
        url = "https://jsonplaceholder.typicode.com"
)
public interface JsonPlaceholderClient {

    @GetMapping("/users")
    List<User> getUsers();
}
```

El uso de Feign permite definir el consumo del servicio externo mediante una interfaz, evitando implementar manualmente la comunicación HTTP.

---

### Modelo de datos

El modelo `User` representa la estructura completa retornada por JSONPlaceholder.

```text
User
├── id
├── name
├── username
├── email
├── address
│   ├── street
│   ├── suite
│   ├── city
│   ├── zipcode
│   └── geo
│       ├── lat
│       └── lng
├── phone
├── website
└── company
    ├── name
    ├── catchPhrase
    └── bs
```

La estructura se representa utilizando:

```text
User
Address
Geo
Company
```

Las clases `Address`, `Geo` y `Company` se implementaron como objetos embebidos utilizando JPA.

Para evitar conflictos entre columnas con el mismo nombre, se definieron nombres específicos para las propiedades embebidas.

Ejemplo:

```text
address_street
address_suite
address_city
address_zipcode
address_lat
address_lng

company_name
company_catch_phrase
company_bs
```

---

### Configuración de H2

La aplicación utiliza H2 como base de datos en memoria.

Configuración:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:challenge
    driver-class-name: org.h2.Driver
    username: sa
    password:

  jpa:
    hibernate:
      ddl-auto: create
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

Con:

```yaml
ddl-auto: create
```

la estructura de la base de datos se genera al iniciar la aplicación.

---

### Repositorio

La persistencia se realiza utilizando Spring Data JPA.

Archivo:

```text
domain/repositories/UserRepository.java
```

Implementación:

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
```

Al extender `JpaRepository`, se dispone de las operaciones estándar de persistencia, incluyendo:

```text
save()
saveAll()
findAll()
findById()
delete()
```

---

### Carga inicial de usuarios

Al iniciar la aplicación se utiliza un `CommandLineRunner` para ejecutar la carga inicial de usuarios.

En:

```text
ChallengeMonnetApplication.java
```

se utiliza:

```java
@Bean
public CommandLineRunner loadUsers(UserService userService) {
    return args -> userService.loadUsers();
}
```

El proceso de carga es:

```text
Aplicación inicia
      ↓
CommandLineRunner
      ↓
UserService
      ↓
JsonPlaceholderClient
      ↓
JSONPlaceholder
      ↓
Lista de usuarios
      ↓
UserRepository.saveAll()
      ↓
H2
```

---

### UserService

Archivo:

```text
infraestructure/services/UserService.java
```

Implementación:

```java
@Component
public class UserService {

    private final JsonPlaceholderClient jsonPlaceholderClient;
    private final UserRepository userRepository;

    public UserService(
            JsonPlaceholderClient jsonPlaceholderClient,
            UserRepository userRepository) {
        this.jsonPlaceholderClient = jsonPlaceholderClient;
        this.userRepository = userRepository;
    }

    public void loadUsers() {
        List<User> users = jsonPlaceholderClient.getUsers();
        userRepository.saveAll(users);
    }
}
```

El método `loadUsers()` realiza dos operaciones:

1. Obtiene los usuarios desde JSONPlaceholder.
2. Persiste los usuarios en H2 utilizando `saveAll()`.

De esta manera, al iniciar la aplicación los usuarios quedan disponibles en la base de datos.

---

## 3.b Endpoint REST `/users`

Se creó un endpoint REST para consultar los usuarios almacenados en H2.

### Endpoint

```text
GET /users
```

URL:

```text
http://localhost:8080/users
```

El endpoint obtiene los usuarios almacenados en la base de datos.

El flujo es:

```text
Cliente
   ↓
UserController
   ↓
ApplicationService
   ↓
UserRepository
   ↓
H2
```

Por lo tanto, el endpoint `/users` consulta la base de datos y no realiza directamente una nueva llamada a JSONPlaceholder.

---

### UserController

Archivo:

```text
infraestructure/controllers/UserController.java
```

Implementación:

```java
@RestController
@RequestMapping
@SecurityRequirement(name = "X-API-TOKEN")
public class UserController {

    private final ApplicationService applicationService;

    public UserController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return applicationService.getUser();
    }
}
```

El controlador recibe la petición HTTP y delega la operación al `ApplicationService`.

---

### ApplicationService

Archivo:

```text
application/services/ApplicationService.java
```

Implementación:

```java
@Component
public class ApplicationService {

    private final UserRepository userRepository;

    public ApplicationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUser() {
        return userRepository.findAll();
    }
}
```

El servicio obtiene todos los registros mediante:

```java
userRepository.findAll();
```

y retorna la información al controlador.

---

## 3.c Seguridad mediante API Token

La aplicación está protegida utilizando un **API Token**.

El token se configura mediante `application.yml`:

```yaml
security:
  api-token: mi-token
```

El cliente debe enviar el token mediante el siguiente header HTTP:

```text
X-API-TOKEN: mi-token
```

---

### Comportamiento de seguridad

#### Sin token

```text
GET /users
```

Resultado:

```text
401 Unauthorized
```

#### Token incorrecto

```text
X-API-TOKEN: token-incorrecto
```

Resultado:

```text
401 Unauthorized
```

#### Token correcto

```text
X-API-TOKEN: mi-token
```

Resultado:

```text
200 OK
```

---

### ApiTokenFilter

La validación del token se realiza mediante un filtro basado en `OncePerRequestFilter`.

Archivo:

```text
config/ApiTokenFilter.java
```

Implementación:

```java
public class ApiTokenFilter extends OncePerRequestFilter {

    private static final String API_TOKEN_HEADER = "X-API-TOKEN";

    @Value("${security.api-token}")
    private String apiToken;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String token = request.getHeader(API_TOKEN_HEADER);

        if (apiToken.equals(token)) {
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            "api-token-user",
                            null,
                            AuthorityUtils.NO_AUTHORITIES
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
```

El filtro realiza el siguiente proceso:

```text
Request
  ↓
Obtiene X-API-TOKEN
  ↓
Compara con security.api-token
  ↓
Token válido
  ↓
Autentica la petición
```

Cuando el token no es válido, la petición continúa sin autenticación y Spring Security responde con:

```text
401 Unauthorized
```

---

### SecurityConfig

Archivo:

```text
config/SecurityConfig.java
```

La aplicación utiliza una política **stateless**:

```java
.sessionManagement()
    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
```

Esto significa que no se utilizan sesiones para mantener la autenticación.

La configuración permite acceder públicamente a los recursos de Swagger y exige autenticación para el resto de endpoints:

```java
.authorizeRequests()
    .antMatchers(
        "/swagger-ui.html",
        "/swagger-ui/**",
        "/v3/api-docs/**"
    ).permitAll()
    .anyRequest().authenticated()
```

El filtro de API Token se agrega antes de `UsernamePasswordAuthenticationFilter`:

```java
.addFilterBefore(
    apiTokenFilter(),
    UsernamePasswordAuthenticationFilter.class
);
```

De esta manera:

- `/users` requiere API Token.
- `/actuator/health` requiere API Token.
- Swagger UI es público.
- OpenAPI JSON es público.

---

## 3.d Spring Boot Actuator

Se agregó **Spring Boot Actuator** para exponer información de monitoreo de la aplicación.

La dependencia utilizada es:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

La exposición de endpoints se configura mediante:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health
```

### Endpoint Health

```text
GET /actuator/health
```

URL:

```text
http://localhost:8080/actuator/health
```

El endpoint está protegido por Spring Security debido a la regla:

```java
.anyRequest().authenticated()
```

Por lo tanto, requiere:

```text
X-API-TOKEN: mi-token
```

### Con token válido

```text
200 OK
```

### Sin token

```text
401 Unauthorized
```

---

# Swagger / OpenAPI

La documentación del API se implementó utilizando **Springdoc OpenAPI**.

Se utiliza Springdoc en lugar de Springfox para evitar problemas de compatibilidad con Spring Boot 2.7.3.

---

## Dependencia

En `pom.xml`:

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-ui</artifactId>
    <version>1.6.11</version>
</dependency>
```

---

## Configuración de Swagger

La configuración de las rutas de Swagger se encuentra en `application.yml`:

```yaml
springdoc:
  swagger-ui:
    path: /swagger-ui.html
  api-docs:
    path: /v3/api-docs
```

La documentación se genera automáticamente a partir de los controllers y annotations de OpenAPI.

---

## SwaggerConfig

Se creó `SwaggerConfig` para documentar el API Token como mecanismo de seguridad.

Archivo:

```text
config/SwaggerConfig.java
```

Implementación:

```java
package com.monnetpayments.challenge_monnet.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "X-API-TOKEN",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.HEADER,
        paramName = "X-API-TOKEN",
        description = "API Token required to access protected endpoints"
)
public class SwaggerConfig {
}
```

Esta configuración le indica a OpenAPI que existe un esquema de seguridad:

```text
X-API-TOKEN
```

y que debe enviarse como:

```text
HTTP Header
```

---

## SecurityRequirement

El `UserController` utiliza:

```java
@SecurityRequirement(name = "X-API-TOKEN")
```

Esto permite que Swagger muestre que el endpoint `/users` requiere autenticación.

Ejemplo:

```java
@RestController
@RequestMapping
@SecurityRequirement(name = "X-API-TOKEN")
public class UserController {
    ...
}
```

---

## Uso de Swagger UI

Swagger UI queda disponible en:

```text
http://localhost:8080/swagger-ui.html
```

La especificación OpenAPI queda disponible en:

```text
http://localhost:8080/v3/api-docs
```

Dentro de Swagger UI se puede utilizar el botón:

```text
Authorize
```

para ingresar el token:

```text
mi-token
```

Swagger utilizará automáticamente el header:

```text
X-API-TOKEN: mi-token
```

en los endpoints protegidos.

De esta manera es posible probar `/users` directamente desde Swagger sin tener que agregar manualmente el header en cada petición.

---

# 3.e Pruebas unitarias

Se implementaron pruebas unitarias utilizando:

- JUnit 5
- Mockito

El objetivo es probar la lógica de los servicios de forma aislada, sin realizar llamadas reales a JSONPlaceholder ni utilizar H2 durante estas pruebas.

---

## UserServiceTest

Se prueba el método:

```java
loadUsers()
```

La prueba utiliza mocks para:

```text
JsonPlaceholderClient
UserRepository
```

El comportamiento esperado es:

```text
JsonPlaceholderClient.getUsers()
          ↓
      List<User>
          ↓
UserRepository.saveAll()
```

Implementación:

```java
package com.monnetpayments.challenge_monnet.infraestructure.services;

import com.monnetpayments.challenge_monnet.domain.entities.User;
import com.monnetpayments.challenge_monnet.domain.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private JsonPlaceholderClient jsonPlaceholderClient;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldLoadUsers() {
        List<User> users = List.of(
                new User(),
                new User()
        );

        when(jsonPlaceholderClient.getUsers()).thenReturn(users);

        userService.loadUsers();

        verify(jsonPlaceholderClient).getUsers();
        verify(userRepository).saveAll(users);
    }
}
```

La prueba verifica que:

- `getUsers()` sea ejecutado.
- `saveAll()` sea ejecutado con la misma lista de usuarios obtenida.

---

## ApplicationServiceTest

Se prueba el método:

```java
getUser()
```

Se utiliza un mock de:

```text
UserRepository
```

Implementación:

```java
package com.monnetpayments.challenge_monnet.application.services;

import com.monnetpayments.challenge_monnet.domain.entities.User;
import com.monnetpayments.challenge_monnet.domain.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ApplicationService applicationService;

    @Test
    void shouldReturnUsers() {
        List<User> users = List.of(
                new User(),
                new User()
        );

        when(userRepository.findAll()).thenReturn(users);

        List<User> result = applicationService.getUser();

        assertEquals(users, result);
        verify(userRepository).findAll();
    }
}
```

La prueba verifica que:

- `findAll()` sea ejecutado.
- El resultado del repositorio sea retornado correctamente.

---

## Ejecución de pruebas

Las pruebas pueden ejecutarse utilizando Maven:

```bash
mvn test
```

También se puede ejecutar el build completo:

```bash
mvn clean install
```

---

# Estructura del proyecto

```text
com.monnetpayments.challenge_monnet
│
├── ChallengeMonnetApplication.java
│
├── application
│   └── services
│       └── ApplicationService.java
│
├── config
│   ├── ApiTokenFilter.java
│   ├── SecurityConfig.java
│   └── SwaggerConfig.java
│
├── domain
│   ├── entities
│   │   ├── User.java
│   │   ├── Address.java
│   │   ├── Geo.java
│   │   └── Company.java
│   │
│   └── repositories
│       └── UserRepository.java
│
└── infraestructure
    ├── controllers
    │   └── UserController.java
    │
    └── services
        ├── JsonPlaceholderClient.java
        └── UserService.java
```

---

# Configuración completa de application.yml

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:challenge
    driver-class-name: org.h2.Driver
    username: sa
    password:

  jpa:
    hibernate:
      ddl-auto: create
    show-sql: true
    properties:
      hibernate:
        format_sql: true

  mvc:
    pathmatch:
      matching-strategy: ant_path_matcher

security:
  api-token: mi-token

management:
  endpoints:
    web:
      exposure:
        include: health

springdoc:
  swagger-ui:
    path: /swagger-ui.html
  api-docs:
    path: /v3/api-docs
```

---

# Dependencias principales del proyecto

Las dependencias utilizadas incluyen:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-ui</artifactId>
    <version>1.6.11</version>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
</dependency>

<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</dependency>
```

---

# Configuración de Spring Cloud

La aplicación utiliza Spring Cloud OpenFeign.

El proyecto utiliza el BOM de Spring Cloud:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>2021.0.5</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

Esto permite mantener las versiones de las dependencias de Spring Cloud alineadas con la versión utilizada de Spring Boot.

---
