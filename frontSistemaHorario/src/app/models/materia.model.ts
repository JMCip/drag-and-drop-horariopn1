import { Carrera } from './carrera.enum';

/**
 * Días de la semana en el orden en que el back guarda el horario: la posición 0 es lunes y la 4 es viernes.
 */
export const DIAS_MATERIA = ['Lun', 'Mar', 'Mié', 'Jue', 'Vie'] as const;
/**
 * Valor que usa el back en `inicio` y `fin` para indicar que ese día no hay clase.
 */
export const SIN_CLASE = -1;

/**
 * Un grupo de una materia, tal como lo devuelve el back (`MateriaDTO`).
 * `inicio` y `fin` tienen 5 posiciones (lunes a viernes) con la hora de entrada y de salida; `-1` = sin clase.
 * El profesor y los estudiantes llegan solo como ids.
 */
export interface MateriaDTO {
  id?: number;
  inicio: number[];
  fin: number[];
  nombreMateria: string;
  carrera: Carrera;
  semestre: number;
  grupo: number;
  idProfesorAsignado?: number | null;
  listaIdEstudiantes: number[];
}
