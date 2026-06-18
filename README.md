# Proyecto Inventario Backend

Este repositorio contiene una API REST desarrollada con Spring Boot para la gestión de usuarios y autenticación JWT.

## Requisitos

- Java 21 JDK instalado
- Maven instalado (opcional, el proyecto incluye el wrapper `mvnw` / `mvnw.cmd`)
- PostgreSQL instalado y accesible

## Configuración inicial

1. Clona el repositorio:

```bash
git clone <URL_DEL_REPOSITORIO>
cd Proyecto_Inventario_Backend
```

2. Crea la base de datos PostgreSQL que usa la aplicación:

```sql
CREATE DATABASE "bd-usuarios";
```

3. Verifica o actualiza la configuración de la conexión a la base de datos en `src/main/resources/application.yaml`.

La configuración actual es:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/bd-usuarios
    username: postgres
    password: 12345
    driver-class-name: org.postgresql.Driver
```

> Si tu servidor PostgreSQL usa otros valores de usuario, contraseña o puerto, ajusta esta sección.

## Ejecutar el proyecto

### En Windows

```powershell
.
\mvnw.cmd spring-boot:run
```

### En Linux/macOS

```bash
./mvnw spring-boot:run
```

La aplicación arranca en `http://localhost:8080`.

## Compilar y empaquetar

### En Windows

```powershell
.
\mvnw.cmd clean package
```

### En Linux/macOS

```bash
./mvnw clean package
```

El archivo JAR se generará en `target/`.

## Ejecutar el JAR generado

```bash
java -jar target/master-0.0.1-SNAPSHOT.jar
```

## Puntos importantes

- El backend expone el endpoint de autenticación en `/auth/login`.
- El endpoint `POST /api/users` requiere el rol `Administrador`.
- El proyecto utiliza JWT para autenticación y Spring Security.

## Desarrollo en IDE

Abre el proyecto en tu IDE preferido y ejecuta la clase principal `ec.edu.espe.master.MasterApplication`.

## Notas

- Si la base de datos no existe o las credenciales no son correctas, la aplicación no arrancará.
- Puedes usar `application.example.yaml` como referencia para crear una copia de configuración.
