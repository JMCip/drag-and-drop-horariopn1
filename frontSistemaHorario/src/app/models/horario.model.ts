import { Dia } from './dia.enum';

export interface HorarioDTO {
  id?: number;
  horaIncio: number;
  horaFin: number;
  dia: Dia;
}
