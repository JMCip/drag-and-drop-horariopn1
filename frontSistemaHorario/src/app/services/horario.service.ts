import { Injectable } from '@angular/core';
import { Observable, Subject, of } from 'rxjs';
import { delay } from 'rxjs/operators';
import { SeccionModel } from '../models/seccion.model';
import { RespuestaValidacionModel } from '../models/validacion.model';

@Injectable({ providedIn: 'root' })
export class HorarioService {
  public seccionArrastrada$ = new Subject<SeccionModel | null>();
  public solicitudAgregar$ = new Subject<SeccionModel>();

  private mockSecciones: SeccionModel[] = [
    {
      id: 101, codigoMateria: 'BD-101', nombreMateria: 'Bases de Datos', grupo: 1,
      docente: 'Prof. A. Pérez', semestre: 4, carrera: 'Ingeniería de Sistemas',
      franjas: [{ dia: 'Lun', horaInicio: 7, horaFin: 9 }, { dia: 'Mié', horaInicio: 9, horaFin: 11 }]
    },
    {
      id: 102, codigoMateria: 'BD-101', nombreMateria: 'Bases de Datos', grupo: 2,
      docente: 'Prof. M. Gómez', semestre: 4, carrera: 'Ingeniería de Sistemas',
      franjas: [{ dia: 'Mar', horaInicio: 7, horaFin: 9 }, { dia: 'Jue', horaInicio: 7, horaFin: 9 }]
    },
    {
      id: 201, codigoMateria: 'CAL-202', nombreMateria: 'Cálculo II', grupo: 1,
      docente: 'Prof. R. Díaz', semestre: 2, carrera: 'Ingeniería de Sistemas',
      franjas: [{ dia: 'Mar', horaInicio: 9, horaFin: 11 }, { dia: 'Jue', horaInicio: 9, horaFin: 11 }]
    },
    {
      id: 202, codigoMateria: 'CAL-202', nombreMateria: 'Cálculo II', grupo: 2,
      docente: 'Prof. R. Díaz', semestre: 2, carrera: 'Ingeniería de Sistemas',
      franjas: [{ dia: 'Lun', horaInicio: 8, horaFin: 10 }, { dia: 'Vie', horaInicio: 7, horaFin: 9 }]
    },
    {
      id: 301, codigoMateria: 'IS-301', nombreMateria: 'Ingeniería de Software I', grupo: 1,
      docente: 'Prof. A. Pérez', semestre: 5, carrera: 'Ingeniería de Sistemas',
      franjas: [{ dia: 'Lun', horaInicio: 7, horaFin: 9 }, { dia: 'Vie', horaInicio: 10, horaFin: 12 }]
    },
    {
      id: 401, codigoMateria: 'RED-401', nombreMateria: 'Redes de Computadores', grupo: 1,
      docente: 'Prof. C. Ruiz', semestre: 6, carrera: 'Ingeniería de Sistemas',
      franjas: [{ dia: 'Mié', horaInicio: 13, horaFin: 15 }, { dia: 'Vie', horaInicio: 13, horaFin: 15 }]
    }
  ];

  getSeccionesDisponibles(): Observable<SeccionModel[]> {
    return of(this.mockSecciones);
  }

  validarChoque(seccionCandidata: SeccionModel, seccionesActuales: SeccionModel[]): Observable<RespuestaValidacionModel> {
    for (const actual of seccionesActuales) {
      for (const fCand of seccionCandidata.franjas) {
        for (const fAct of actual.franjas) {
          if (fCand.dia === fAct.dia) {
            const haySolapamiento = fCand.horaInicio < fAct.horaFin && fCand.horaFin > fAct.horaInicio;
            if (haySolapamiento) {
              const esTotal = fCand.horaInicio === fAct.horaInicio && fCand.horaFin === fAct.horaFin;
              return of({
                tipoTraslape: esTotal ? 'TOTAL' : 'PARCIAL',
                seccionConflictoId: actual.id,
                materiaConflicto: `${actual.nombreMateria} (Grupo ${actual.grupo})`,
                detalleFranja: `${fAct.dia} ${this.formatearHora(fAct.horaInicio)}–${this.formatearHora(fAct.horaFin)}`
              } as RespuestaValidacionModel).pipe(delay(100));
            }
          }
        }
      }
    }
    return of({ tipoTraslape: 'NINGUNO' } as RespuestaValidacionModel).pipe(delay(100));
  }

  guardarHorarioInscrito(secciones: SeccionModel[]): Observable<{ mensaje: string }> {
    localStorage.setItem('horario_inscrito_mock', JSON.stringify(secciones));
    return of({ mensaje: 'Horario guardado exitosamente' }).pipe(delay(200));
  }

  getHorarioInscrito(): Observable<SeccionModel[]> {
    const guardado = localStorage.getItem('horario_inscrito_mock');
    return of(guardado ? JSON.parse(guardado) : []);
  }

  private formatearHora(h: number): string {
    return h < 10 ? `0${h}:00` : `${h}:00`;
  }
}
