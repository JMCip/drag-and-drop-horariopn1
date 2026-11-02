-- Crear materias directamente en la base (Workbench, conexion Aiven, base defaultdb)
--
-- inicio y fin son int[] de Java guardados en binario. Cada columna es @p seguido
-- de 5 bloques de 8 caracteres: Lun, Mar, Mie, Jue, Vie. Si no hay clase: FFFFFFFF.
--
-- Hora -> bloque:
--  7 00000007   10 0000000A   13 0000000D   16 00000010   19 00000013
--  8 00000008   11 0000000B   14 0000000E   17 00000011   20 00000014
--  9 00000009   12 0000000C   15 0000000F   18 00000012   21 00000015
--                                                         22 00000016
--
-- carrera: ING_SISTEMAS, ING_INDUSTRIAL, BIOING, ING_ROBOTICA, MEDICINA,
--          PSICOLOGIA, MAT_CIENCIA_DATOS, ESTADISTICA, FISICA
-- Nombre: solo letras, numeros y espacios. Semestre: de 1 a 10.
-- Grupo: si no se indica es 1. Para otro grupo agrega la columna grupo al INSERT;
-- nombre y grupo juntos no se pueden repetir.
-- Horario: de 7 a 13 o de 14 a 22 (almuerzo de 13 a 14), maximo 4 horas por dia.

SET @p = 'ACED0005757200025B494DBA602676EAB2A5020000787000000005';

-- Bases de Datos: Lun 7-9 y Mie 9-11
INSERT INTO materia (nombre_materia, carrera, semestre, inicio, fin) VALUES (
  'Bases de Datos', 'ING_SISTEMAS', 4,
  UNHEX(CONCAT(@p, '00000007', 'FFFFFFFF', '00000009', 'FFFFFFFF', 'FFFFFFFF')),
  UNHEX(CONCAT(@p, '00000009', 'FFFFFFFF', '0000000B', 'FFFFFFFF', 'FFFFFFFF'))
);

-- Calculo II: Mar 9-11 y Jue 9-11
INSERT INTO materia (nombre_materia, carrera, semestre, inicio, fin) VALUES (
  'Calculo II', 'ING_SISTEMAS', 2,
  UNHEX(CONCAT(@p, 'FFFFFFFF', '00000009', 'FFFFFFFF', '00000009', 'FFFFFFFF')),
  UNHEX(CONCAT(@p, 'FFFFFFFF', '0000000B', 'FFFFFFFF', '0000000B', 'FFFFFFFF'))
);

-- Redes de Computadores: Mie 14-16 y Vie 14-16
INSERT INTO materia (nombre_materia, carrera, semestre, inicio, fin) VALUES (
  'Redes de Computadores', 'ING_SISTEMAS', 6,
  UNHEX(CONCAT(@p, 'FFFFFFFF', 'FFFFFFFF', '0000000E', 'FFFFFFFF', '0000000E')),
  UNHEX(CONCAT(@p, 'FFFFFFFF', 'FFFFFFFF', '00000010', 'FFFFFFFF', '00000010'))
);
