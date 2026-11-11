# Guía del equipo: correr el proyecto con la base compartida

Todos usamos la misma base de datos online en Aiven. Lo que uno crea, los demás lo ven.

**No necesitas instalar un servidor MySQL ni crear la base.** Las tablas, el catálogo de materias y el admin ya existen en Aiven. No vuelvas a correr los scripts `.sql`.

Si algún día se crea una base nueva (por ejemplo local), se corre `sistemahorario/base_de_datos.sql` y después `sistemahorario/catalogo_materias.sql`.

## Qué necesitas instalar

- JDK 17
- Eclipse o Spring Tools, para correr el back
- Node.js, versión LTS
- Git o GitHub Desktop
- MySQL Workbench, solo si quieres ver los datos

## Primera vez

1. Baja la última versión del repositorio.

2. Pídele a Santiago por privado la contraseña de Aiven y la clave del admin. Nunca las subas al repositorio ni las pegues en grupos.

3. Abre `sistemahorario/src/main/resources/application.properties` y cambia solo estas tres líneas:

   ```properties
   spring.datasource.url=jdbc:mysql://mysql-3e8e35b3-dragndrop28.i.aivencloud.com:27924/defaultdb?serverTimezone=America/Bogota&sslMode=REQUIRED
   spring.datasource.username=avnadmin
   spring.datasource.password=LA_CONTRASEÑA_DE_AIVEN
   ```

   No borres `jwt.secret` ni `jwt.expiration`, y deja `spring.jpa.hibernate.ddl-auto=validate`.

4. Para que git no te ofrezca subir ese archivo, corre esto una vez en la carpeta del repositorio:

   ```bash
   git update-index --skip-worktree sistemahorario/src/main/resources/application.properties
   ```

   Si algún día alguien cambia ese archivo en el repositorio y git no te deja actualizar, deshazlo con `--no-skip-worktree`.

5. Instala las dependencias del front, solo la primera vez:

   ```bash
   cd frontSistemaHorario
   npm install
   ```

## Cada vez que trabajes

1. **Back:** en Eclipse, clic derecho en el proyecto `sistemahorario` > Run As > Spring Boot App. Espera el mensaje `Started SistemahorarioApplication`.
2. **Front:** en la carpeta `frontSistemaHorario` corre `npx ng serve`.
3. Abre <http://localhost:4200>.

El back usa el puerto 8080 y el front el 4200. Cada quien los corre en su propio computador; lo único compartido es la base.

## Usuarios y materias

- **Estudiantes y profesores:** entra como admin > pestaña "Registrar usuario". Al estudiante se le elige carrera y semestre.
- **Semestre del estudiante:** el admin lo cambia en Panel > Estudiantes.
- **Catálogo:** las materias de cada carrera y su semestre salen del Excel del proyecto. Están cargadas en la base (tablas `asignatura` y `plan_estudio`) y no se editan desde la app.
- **Grupos:** admin > pestaña "Materias": se elige la carrera, la materia del catálogo, el número de grupo y el horario, y después se asigna el profesor. Un grupo de una materia común (por ejemplo Matemáticas 1) sirve a todas las carreras que la tienen.
- **Qué ve un estudiante:** solo los grupos de materias de su carrera, del semestre 1 hasta el suyo. El back lo valida también al inscribir.

Swagger está en <http://localhost:8080/swagger-ui.html>. Ejecuta `POST /api/auth/login`, copia el `token`, dale al botón **Authorize**, pégalo y prueba los endpoints.

Solo el admin puede crear, editar o borrar usuarios y materias, asignar profesor y mover estudiantes de grupo. Un estudiante solo puede inscribir o desinscribir su propio horario.

## Conectar Workbench para ver los datos

Crea una conexión nueva con el botón `+` junto a "MySQL Connections":

| Campo | Valor |
| --- | --- |
| Hostname | `mysql-3e8e35b3-dragndrop28.i.aivencloud.com` |
| Port | `27924` |
| Username | `avnadmin` |
| Default Schema | `defaultdb` |
| Pestaña SSL > Use SSL | `Require` |

El aviso de versión 8.4 es normal: dale a "Continue Anyway".

## Cuidado al ser una base compartida

- Si borras algo, se borra para todos.
- Si el back no conecta después de varios días sin usarla, revisa que el servicio esté en Running. El plan gratis de Aiven lo apaga por inactividad y avisa antes por correo a la cuenta dueña. Santiago puede volver a encenderlo desde la consola de Aiven.
- Hay 1 GB de espacio.

## Si algo falla

| Error | Causa probable |
| --- | --- |
| `Access denied for user` | Contraseña equivocada en `application.properties` |
| `Unable to determine Dialect` | El back no pudo conectarse: revisa contraseña, URL y que Aiven esté en Running |
| `Communications link failure` | Aiven apagado, o tu red bloquea el puerto 27924 |
| `Unable to establish loopback connection` | Problema de Java en ese computador, no del proyecto |
| Error de "Unknown column" o de validación | Tu código no está actualizado: baja la última versión |
