-- Agrega grupos a las materias y carrera a los estudiantes.
-- Correr UNA sola vez, en bases creadas antes de este cambio.
-- No borra datos: las materias existentes quedan como grupo 1.
-- Una base nueva no lo necesita: base_de_datos.sql ya trae estas columnas.

ALTER TABLE materia ADD COLUMN grupo INT NOT NULL DEFAULT 1;
ALTER TABLE materia ADD CONSTRAINT uk_materia_nombre_grupo UNIQUE (nombre_materia, grupo);

ALTER TABLE estudiante ADD COLUMN carrera ENUM('ING_SISTEMAS','ING_INDUSTRIAL','BIOING','ING_ROBOTICA','MEDICINA','PSICOLOGIA','MAT_CIENCIA_DATOS','ESTADISTICA','FISICA');
