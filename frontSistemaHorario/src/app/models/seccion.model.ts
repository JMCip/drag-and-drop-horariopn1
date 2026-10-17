export interface FranjaHoraria {
  dia: 'Lun' | 'Mar' | 'Mié' | 'Jue' | 'Vie' | 'Sáb';
  horaInicio: number;
  horaFin: number;
}

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
