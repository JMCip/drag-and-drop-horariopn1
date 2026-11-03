/**
 * Un bloque de clase en un día concreto (por ejemplo, lunes de 8 a 10).
 */
export interface FranjaHoraria {
  dia: 'Lun' | 'Mar' | 'Mié' | 'Jue' | 'Vie' | 'Sáb';
  horaInicio: number;
  horaFin: number;
}

/**
 * Representación de una materia (grupo) lista para mostrar y arrastrar en la pantalla del estudiante.
 * Se construye a partir de un `MateriaDTO` con `MateriaService.adaptarMateriaASeccion`.
 */
export interface SeccionModel {
  id: number;
  codigoMateria: string;
  nombreMateria: string;
  grupo: number;
  docente: string;
  semestre: number;
  carrera: string;
  franjas: FranjaHoraria[];
  estudiantesInscritos?: number;
}
