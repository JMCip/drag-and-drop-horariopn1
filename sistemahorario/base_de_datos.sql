-- Base de datos del Sistema de Horarios (base nueva)
-- 1) Correr este archivo una sola vez: crea la base, las tablas y el admin inicial.
-- 2) Despues correr catalogo_materias.sql para cargar las materias de cada carrera.
-- En Aiven: quitar las dos primeras instrucciones y usar la base defaultdb.

CREATE DATABASE IF NOT EXISTS sistemahorario;
USE sistemahorario;

CREATE TABLE IF NOT EXISTS usuario (
  id BIGINT NOT NULL AUTO_INCREMENT,
  correo VARCHAR(255) NOT NULL,
  contrasenia VARCHAR(255) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT uk_usuario_correo UNIQUE (correo)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS admin (
  id BIGINT NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_admin_usuario FOREIGN KEY (id) REFERENCES usuario (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS profesor (
  id BIGINT NOT NULL,
  nombre VARCHAR(255),
  PRIMARY KEY (id),
  CONSTRAINT fk_profesor_usuario FOREIGN KEY (id) REFERENCES usuario (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS estudiante (
  id BIGINT NOT NULL,
  nombre VARCHAR(255),
  carrera ENUM('ING_SISTEMAS','ING_INDUSTRIAL','BIOING','ING_ROBOTICA','MEDICINA','PSICOLOGIA','MAT_CIENCIA_DATOS','ESTADISTICA','FISICA'),
  semestre SMALLINT,
  PRIMARY KEY (id),
  CONSTRAINT fk_estudiante_usuario FOREIGN KEY (id) REFERENCES usuario (id)
) ENGINE=InnoDB;

-- Catalogo: cada materia una sola vez
CREATE TABLE IF NOT EXISTS asignatura (
  id BIGINT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(255) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT uk_asignatura_nombre UNIQUE (nombre)
) ENGINE=InnoDB;

-- En que semestre va cada materia dentro de cada carrera
CREATE TABLE IF NOT EXISTS plan_estudio (
  id BIGINT NOT NULL AUTO_INCREMENT,
  asignatura_id BIGINT NOT NULL,
  carrera ENUM('ING_SISTEMAS','ING_INDUSTRIAL','BIOING','ING_ROBOTICA','MEDICINA','PSICOLOGIA','MAT_CIENCIA_DATOS','ESTADISTICA','FISICA') NOT NULL,
  semestre SMALLINT NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT uk_plan UNIQUE (asignatura_id, carrera, semestre),
  CONSTRAINT fk_plan_asignatura FOREIGN KEY (asignatura_id) REFERENCES asignatura (id)
) ENGINE=InnoDB;

-- Grupos de una materia del catalogo. inicio y fin son int[] de Java que escribe el back.
CREATE TABLE IF NOT EXISTS materia (
  id BIGINT NOT NULL AUTO_INCREMENT,
  asignatura_id BIGINT NOT NULL,
  grupo INT NOT NULL DEFAULT 1,
  inicio VARBINARY(255),
  fin VARBINARY(255),
  profesor_id BIGINT,
  PRIMARY KEY (id),
  CONSTRAINT uk_materia_asignatura_grupo UNIQUE (asignatura_id, grupo),
  CONSTRAINT fk_materia_asignatura FOREIGN KEY (asignatura_id) REFERENCES asignatura (id),
  CONSTRAINT fk_materia_profesor FOREIGN KEY (profesor_id) REFERENCES profesor (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS materia_estudiante (
  materia_id BIGINT NOT NULL,
  estudiante_id BIGINT NOT NULL,
  PRIMARY KEY (materia_id, estudiante_id),
  CONSTRAINT fk_me_materia FOREIGN KEY (materia_id) REFERENCES materia (id),
  CONSTRAINT fk_me_estudiante FOREIGN KEY (estudiante_id) REFERENCES estudiante (id)
) ENGINE=InnoDB;

-- Admin inicial: admin@unbosque.edu.co (la clave se comparte por privado)
INSERT INTO usuario (correo, contrasenia) VALUES ('admin@unbosque.edu.co', '$2a$10$iDrEX7w2s0oY6e0WkwnM6.Ww0YP.c55xy0KaRF7pGWQqxeVSese9e');
INSERT INTO admin (id) SELECT id FROM usuario WHERE correo = 'admin@unbosque.edu.co';
