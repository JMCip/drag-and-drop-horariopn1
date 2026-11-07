import { Carrera } from './carrera.enum';

export const DIAS_MATERIA = ['Lun', 'Mar', 'Mié', 'Jue', 'Vie'] as const;
export const SIN_CLASE = -1;

export interface PlanEstudio {
  carrera: Carrera;
  semestre: number;
}

// Materia del catálogo (plan de estudios de cada carrera)
export interface AsignaturaDTO {
  id: number;
  nombre: string;
  planes: PlanEstudio[];
}

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

export interface GrupoMateria {
  idAsignatura: number;
  grupo: number;
  inicio: number[];
  fin: number[];
}
