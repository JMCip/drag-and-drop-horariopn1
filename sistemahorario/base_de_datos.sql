-- Base de datos del Sistema de Horarios
-- Correr una sola vez. Crea la base, las tablas y el admin inicial.
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
  PRIMARY KEY (id),
  CONSTRAINT fk_estudiante_usuario FOREIGN KEY (id) REFERENCES usuario (id)
) ENGINE=InnoDB;

-- inicio y fin son int[] de Java: los escribe el back, no se llenan a mano
CREATE TABLE IF NOT EXISTS materia (
  id BIGINT NOT NULL AUTO_INCREMENT,
  nombre_materia VARCHAR(255),
  carrera ENUM('ING_SISTEMAS','ING_INDUSTRIAL','BIOING','ING_ROBOTICA','MEDICINA','PSICOLOGIA','MAT_CIENCIA_DATOS','ESTADISTICA','FISICA'),
  semestre SMALLINT NOT NULL,
  grupo INT NOT NULL DEFAULT 1,
  inicio VARBINARY(255),
  fin VARBINARY(255),
  profesor_id BIGINT,
  PRIMARY KEY (id),
  CONSTRAINT uk_materia_nombre_grupo UNIQUE (nombre_materia, grupo),
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
