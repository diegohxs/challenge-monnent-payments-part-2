# Docker

## Prerequisites

- Docker installed.
- Maven installed or use the Maven Wrapper included in the project.

## Build the application

First, generate the application JAR:

```bash
./mvnw clean package
```

On Windows:

```bash
mvnw.cmd clean package
```

The generated JAR will be located in:

```text
target/challenge_monnet-0.0.1-SNAPSHOT.jar
```

## Build the Docker image

From the project root:

```bash
docker build -t challenge-monnet .
```

## Run the container

```bash
docker run -p 8080:8080 challenge-monnet
```

The application will be available at:

```text
http://localhost:8080
```

## Users endpoint

The `/users` endpoint requires the API Token configured in the application.

```http
GET http://localhost:8080/users
X-API-TOKEN: mi-token
```

Example using curl:

```bash
curl -H "X-API-TOKEN: mi-token" http://localhost:8080/users
```

## Stop the container

List running containers:

```bash
docker ps
```

Stop the container:

```bash
docker stop <container_id>
```

## Dockerfile

The project includes a Dockerfile based on Eclipse Temurin Java 11:

```dockerfile
FROM eclipse-temurin:11-jre

WORKDIR /app

COPY target/challenge_monnet-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```