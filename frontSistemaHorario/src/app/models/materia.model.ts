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
 * Carrera y semestre en los que una asignatura aparece en el plan de estudios.
 */
export interface PlanEstudio {
  carrera: Carrera;
  semestre: number;
}

/**
 * Asignatura del catálogo, con los planes (carrera y semestre) en los que aparece.
 */
// Materia del catálogo (plan de estudios de cada carrera)
export interface AsignaturaDTO {
  id: number;
  nombre: string;
  planes: PlanEstudio[];
}

/**
 * Un grupo de una asignatura del catálogo, tal como lo devuelve el back (`MateriaDTO`).
 * `inicio` y `fin` tienen 5 posiciones (lunes a viernes) con la hora de entrada y de salida; `-1` = sin clase.
 * El profesor y los estudiantes llegan solo como ids. `planes` indica en qué carreras y semestres aparece la
 * asignatura; `carrera` y `semestre` solo vienen cuando consulta un estudiante.
 */
// Un grupo de una materia del catálogo, con horario y profesor
export interface MateriaDTO {
  id?: number;
  idAsignatura: number;
  nombreMateria: string;
  inicio: number[];
  fin: number[];
  grupo: number;
  idProfesorAsignado?: number | null;
  listaIdEstudiantes: number[];
  planes: PlanEstudio[];
  // Solo cuando consulta un estudiante: su carrera y el semestre de la materia en ella
  carrera?: Carrera | null;
  semestre?: number | null;
}

/**
 * Datos que se envían al back para crear o editar un grupo: asignatura, número de grupo y horario.
 */
export interface GrupoMateria {
  idAsignatura: number;
  grupo: number;
  inicio: number[];
  fin: number[];
}
