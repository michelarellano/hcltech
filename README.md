# HCLTech — Portal de solicitudes de hardware

## Descripción

Portal web interno desarrollado para HCLTech que digitaliza el proceso de solicitud de hardware en el IT Secure Room (ITSR) en el edificio Minerva. Permite a los empleados solicitar equipos como headsets, teclados, cables y mouses de forma estructurada, eliminando la dependencia del correo electrónico.

## Problema identificado

El proceso actual para solicitar hardware depende del correo electrónico. El empleado redacta una solicitud manualmente, la envía a un SSRO, quien debe revisar, aprobar y reenviar al departamento de IT. Este proceso tiene bastantes problemas de demoras, ya que solo los SSROs son los encargados de aprobar y dependemos totalmente de ellos.

## Solución

Sistema web con tres roles diferenciados (Empleado, SSRO, IT) que centraliza el flujo de solicitudes, registra cada acción y mantiene el estado actualizado en tiempo real.

## Arquitectura
<img width="920" height="729" alt="image" src="https://github.com/user-attachments/assets/6511e247-b534-40b3-9036-e9fdd3e99f99" />

---

## Tabla de contenidos

- [Descripción](#descripción)
- [Problema identificado](#problema-identificado)
- [Solución](#solución)
- [Arquitectura](#arquitectura)
- [Requerimientos](#requerimientos)
- [Instalación](#instalación)
- [Configuración](#configuración)
- [Uso](#uso)
- [Contribución](#contribución)
- [Roadmap](#roadmap)

---

## Requerimientos

### Servidores
- Servidor de aplicación: Spring Boot 4.0.5 con Apache Tomcat 9.0 (puerto 8080).
- Servidor web: Nginx (para ambientes de producción)
- Base de datos: HeidiSQL/MySQL 9.6

### Software requerido
- Java 17 (JDK)
- Maven 3.8
- MySQL 9.6
- Git
- 

### Paquetes adicionales (spring initializr)
- spring-boot-starter-web
- spring-boot-starter-security
- spring-boot-starter-data-jpa
- mysql-connector-j
- spring-boot-devtools
- lombok
- spring-boot-starter-test

---

## Instalación

### Ambiente de desarrollo

### 1. Clonar el repositorio

```bash
git clone https://github.com/michelarellano/hcltech.git
cd hcltech
```

### 2. Crear la base de datos

Abre tu cliente MySQL (HeidiSQL, MySQL Workbench o terminal) y ejecuta:

```bash
mysql -u root -p < db/schema.sql
```

O abre el archivo `db/schema.sql` directamente en HeidiSQL y ejecútalo.

### 3. Configurar las credenciales

Abre el archivo `src/main/resources/application.properties` y actualiza:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hcltech
spring.datasource.username=root
spring.datasource.password=TU_CONTRASEÑA
```

### 4. Ejecutar la aplicación

```bash
mvn spring-boot:run
```

La aplicación estará disponible en `http://localhost:8080`

### 5. Ejecutar pruebas manualmente

```bash
mvn test
```

O desde Eclipse: clic derecho sobre el proyecto → **Run As → JUnit Test**

### Implementación en la nube (Heroku)

1. Instalar Heroku CLI y hacer login
heroku login

2. Crear la aplicación
heroku create hcltech-app

3. Agregar base de datos MySQL
heroku addons:create jawsdb:kitefin

4. Configurar variables de entorno
heroku config:set SPRING_DATASOURCE_URL=<URL_DE_JAWSDB>
heroku config:set SPRING_DATASOURCE_USERNAME=<USUARIO>
heroku config:set SPRING_DATASOURCE_PASSWORD=<CONTRASEÑA>

5. Hacer deploy
git push heroku master

---

## Configuración

### Archivo de configuración principal

`src/main/resources/application.properties`

```properties
# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/hcltech
spring.datasource.username=root
spring.datasource.password=TU_CONTRASEÑA
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

### Configuración de roles

Los roles se insertan automáticamente al crear la base de datos con `db/schema.sql`:

| Rol | Descripción |
|---|---|
| `EMPLEADO` | Puede crear y consultar sus solicitudes |
| `SSRO` | Puede aprobar o rechazar solicitudes |
| `IT` | Puede registrar la entrega del hardware |

---

## Uso

### Como empleado

#### Registrarse en el sistema

Para crear tu cuenta necesitas pedirle al administrador que te registre con tu correo corporativo y rol asignado.

#### Crear una solicitud de hardware

Envía una solicitud con tu correo y contraseña:

- **Tipo de hardware:** el equipo que necesitas (Headset, Teclado, Mouse, etc.)
- **Justificación:** explica brevemente por qué lo necesitas
- **Urgencia:** baja, media o alta

Ejemplo:
POST http://localhost:8080/api/solicitudes
Usuario: tu@hcltech.com
Contraseña: tu contraseña
{
"tipoHardware": "Headset",
"justificacion": "Necesito headset para videollamadas con clientes",
"urgencia": "alta"
}

Recibirás una confirmación cuando tu solicitud sea creada, aprobada o rechazada.

#### Consultar tus solicitudes
GET http://localhost:8080/api/solicitudes
Usuario: tu@hcltech.com
Contraseña: tu contraseña

Verás el listado de todas tus solicitudes con su estado actual:
- **Pendiente** — en espera de revisión del SSRO
- **Aprobada** — autorizada, el equipo IT la atenderá
- **Rechazada** — no autorizada, revisa el comentario del SSRO
- **Entregada** — hardware entregado

---

### Como SSRO

#### Ver solicitudes pendientes
GET http://localhost:8080/api/ssro/solicitudes
Usuario: ssro@hcltech.com
Contraseña: tu contraseña

#### Aprobar o rechazar una solicitud
PATCH http://localhost:8080/api/ssro/solicitudes/{id}/decision
Usuario: ssro@hcltech.com
Contraseña: tu contraseña
{
"decision": "aprobada",
"comentario": "Aprobado, proceder con entrega"
}

Los valores válidos para `decision` son `aprobada` o `rechazada`.

---

### Como equipo IT

#### Ver solicitudes aprobadas
GET http://localhost:8080/api/it/solicitudes
Usuario: it@hcltech.com
Contraseña: tu contraseña

#### Registrar entrega de hardware
PATCH http://localhost:8080/api/it/solicitudes/{id}/entrega
Usuario: it@hcltech.com
Contraseña: tu contraseña

Al registrar la entrega, el sistema guarda automáticamente la fecha y hora.

---

## Uso — Administrador

### Registrar un nuevo usuario
POST http://localhost:8080/api/auth/registro
{
"nombre": "Nombre Completo",
"correo": "usuario@hcltech.com",
"contrasena": "contraseña",
"rol": "EMPLEADO"
}

Los roles disponibles son: `EMPLEADO`, `SSRO`, `IT`

### Verificar usuario autenticado
GET http://localhost:8080/api/auth/me
Usuario: usuario@hcltech.com
Contraseña: contraseña

### Consultar la base de datos directamente

Abre HeidiSQL, conéctate a `localhost:3306` y selecciona la base de datos `hcltech`. Las tablas disponibles son:

| Tabla | Descripción |
|---|---|
| `usuarios` | Todos los usuarios registrados |
| `roles` | Roles del sistema |
| `solicitudes` | Todas las solicitudes con su estado |


---

## Contribución

¿Quieres mejorar el proyecto? Sigue estos pasos:

### 1. Clona el repositorio

```bash
git clone https://github.com/michelarellano/hcltech.git
cd hcltech
```

### 2. Crea un branch para tu mejora

Usa un nombre descriptivo que refleje lo que vas a hacer:

```bash
git checkout develop
git pull origin develop
git checkout -b feature/nombre-de-tu-mejora
```

### 3. Haz tus cambios y guárdalos

```bash
git add .
git commit -m "feat: descripción breve de tu cambio"
```

### 4. Sube tu branch

```bash
git push origin feature/nombre-de-tu-mejora
```

### 5. Abre un Pull Request

1. Ve al repositorio en GitHub
2. Haz clic en **"Compare & pull request"**
3. Asegúrate que vaya hacia `develop`, no a `master`
4. Describe claramente qué cambiaste y por qué
5. Haz clic en **"Create pull request"**

### 6. Espera la revisión

Un miembro del equipo revisará tu PR. Una vez aprobado, se hará el merge a `develop`. Los cambios llegarán a `master` en el siguiente release.

### Convenciones para commits

| Prefijo | Uso |
|---|---|
| `feat:` | Nueva funcionalidad |
| `fix:` | Corrección de bug |
| `docs:` | Cambios en documentación |
| `test:` | Agregar o modificar pruebas |
| `ci:` | Cambios en pipeline de CI/CD |
---

## Roadmap

Las siguientes funcionalidades están fuera del alcance de la v1.0 y serán consideradas para versiones futuras:

| Funcionalidad | Descripción | Prioridad |
|---|---|---|
| SSO corporativo | Integración con el sistema de autenticación de HCLTech | Alta |
| Notificaciones por correo | Envío automático de correo al empleado cuando su solicitud es aprobada o rechazada | Alta |
| Control de inventario | Módulo completo para gestionar el stock disponible en el ITSR | Media |
| Número de serie | Registro del número de serie del hardware entregado vinculado al empleado | Media |
| Dashboard de métricas | Visualización de solicitudes por departamento, tipo de hardware y tiempos de respuesta | Media |
| App móvil | Versión móvil del portal para consulta de solicitudes | Baja |
| Firma digital | Confirmación digital de recepción del hardware por parte del empleado | Baja |
| Soporte multi-sede | Soporte para múltiples ITSR en distintas ubicaciones de HCLTech | Baja |


Si llegaste hasta acá,
Gracias por leer y espero que este proyecto sea de su agrado :)
