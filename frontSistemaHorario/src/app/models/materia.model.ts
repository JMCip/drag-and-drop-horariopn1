import { Carrera } from './carrera.enum';

export const DIAS_MATERIA = ['Lun', 'Mar', 'Mié', 'Jue', 'Vie'] as const;
export const SIN_CLASE = -1;

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
