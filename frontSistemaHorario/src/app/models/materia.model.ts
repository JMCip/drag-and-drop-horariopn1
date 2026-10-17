import { Carrera } from './carrera.enum';

export interface MateriaDTO {
  id?: number;
  idHorarios: number[];
  nombreMateria: string;
  carrera: Carrera;
  semestre: number;
  idProfesorAsignado?: number;
  listaIdEstudiantes: number[];
}
