import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { MateriaDTO } from '../models/materia.model';
import { HorarioDTO } from '../models/horario.model';
import { Carrera, ETIQUETAS_CARRERA } from '../models/carrera.enum';
import { Dia } from '../models/dia.enum';
import { SeccionModel, FranjaHoraria } from '../models/seccion.model';

@Injectable({ providedIn: 'root' })
export class MateriaService {

  // private http = inject(HttpClient);
  // private readonly urlBase = 'http://localhost:8080/materia';

  private materiasMemoria: MateriaDTO[] = [
    { id: 101, nombreMateria: 'Bases de Datos', carrera: Carrera.ING_SISTEMAS, semestre: 4, idProfesorAsignado: 10, idHorarios: [1, 2], listaIdEstudiantes: [1, 2] },
    { id: 102, nombreMateria: 'Bases de Datos', carrera: Carrera.ING_SISTEMAS, semestre: 4, idProfesorAsignado: 11, idHorarios: [3, 4], listaIdEstudiantes: [] },
    { id: 201, nombreMateria: 'Cálculo II', carrera: Carrera.ING_SISTEMAS, semestre: 2, idProfesorAsignado: 12, idHorarios: [5, 6], listaIdEstudiantes: [1] },
    { id: 202, nombreMateria: 'Cálculo II', carrera: Carrera.ING_SISTEMAS, semestre: 2, idProfesorAsignado: 12, idHorarios: [7, 8], listaIdEstudiantes: [] },
    { id: 301, nombreMateria: 'Ingeniería de Software I', carrera: Carrera.ING_SISTEMAS, semestre: 5, idProfesorAsignado: 10, idHorarios: [9, 10], listaIdEstudiantes: [3] }
  ];

  private horariosMemoria: HorarioDTO[] = [
    { id: 1, dia: Dia.LUNES, horaIncio: 7, horaFin: 9 },
    { id: 2, dia: Dia.MIERCOLES, horaIncio: 9, horaFin: 11 },
    { id: 3, dia: Dia.MARTES, horaIncio: 7, horaFin: 9 },
    { id: 4, dia: Dia.JUEVES, horaIncio: 7, horaFin: 9 },
    { id: 5, dia: Dia.MARTES, horaIncio: 9, horaFin: 11 },
    { id: 6, dia: Dia.JUEVES, horaIncio: 9, horaFin: 11 },
    { id: 7, dia: Dia.LUNES, horaIncio: 8, horaFin: 10 },
    { id: 8, dia: Dia.VIERNES, horaIncio: 7, horaFin: 9 },
    { id: 9, dia: Dia.LUNES, horaIncio: 7, horaFin: 9 },
    { id: 10, dia: Dia.VIERNES, horaIncio: 10, horaFin: 12 }
  ];

  create(data: MateriaDTO): Observable<number> {
    const nuevoId = this.materiasMemoria.length + 400;
    this.materiasMemoria.push({ ...data, id: nuevoId });
    return of(1);
  }

  getAll(): Observable<MateriaDTO[]> {
    return of([...this.materiasMemoria]);
  }

  deleteById(id: number): Observable<number> {
    this.materiasMemoria = this.materiasMemoria.filter(m => m.id !== id);
    return of(1);
  }

  updateById(id: number, data: MateriaDTO): Observable<number> {
    const index = this.materiasMemoria.findIndex(m => m.id === id);
    if (index !== -1) {
      this.materiasMemoria[index] = { ...data, id };
      return of(1);
    }
    return of(0);
  }

  count(): Observable<number> {
    return of(this.materiasMemoria.length);
  }

  exist(id: number): Observable<boolean> {
    return of(this.materiasMemoria.some(m => m.id === id));
  }

  mapearDiaAGrilla(dia: Dia): 'Lun' | 'Mar' | 'Mié' | 'Jue' | 'Vie' | 'Sáb' {
    const mapa: Record<Dia, 'Lun' | 'Mar' | 'Mié' | 'Jue' | 'Vie' | 'Sáb'> = {
      [Dia.LUNES]: 'Lun',
      [Dia.MARTES]: 'Mar',
      [Dia.MIERCOLES]: 'Mié',
      [Dia.JUEVES]: 'Jue',
      [Dia.VIERNES]: 'Vie',
      [Dia.SABADO]: 'Sáb'
    };
    return mapa[dia] ?? 'Lun';
  }

  adaptarMateriaASeccion(
    dto: MateriaDTO,
    horarios: HorarioDTO[] = this.horariosMemoria,
    nombreDocente = 'Prof. Asignado',
    grupoNumero = 1
  ): SeccionModel {
    const franjas: FranjaHoraria[] = (dto.idHorarios || [])
      .map(idH => horarios.find(h => h.id === idH))
      .filter((h): h is HorarioDTO => !!h)
      .map(h => ({
        dia: this.mapearDiaAGrilla(h.dia),
        horaInicio: h.horaIncio,
        horaFin: h.horaFin
      }));

    return {
      id: dto.id ?? 0,
      codigoMateria: `MAT-${dto.id}`,
      nombreMateria: dto.nombreMateria,
      grupo: grupoNumero,
      docente: nombreDocente,
      semestre: dto.semestre,
      carrera: ETIQUETAS_CARRERA[dto.carrera] ?? dto.carrera,
      franjas,
      estudiantesInscritos: dto.listaIdEstudiantes?.length ?? 0
    };
  }
}
