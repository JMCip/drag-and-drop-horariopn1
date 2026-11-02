import { Injectable, inject } from '@angular/core';
import { Observable, Subject, forkJoin, from, of } from 'rxjs';
import { concatMap, delay, map, switchMap, toArray } from 'rxjs/operators';
import { MateriaDTO } from '../models/materia.model';
import { SeccionModel } from '../models/seccion.model';
import { RespuestaValidacionModel } from '../models/validacion.model';
import { AuthService } from './auth.service';
import { MateriaService } from './materia.service';
import { ProfesorService } from './profesor.service';

@Injectable({ providedIn: 'root' })
export class HorarioService {
  public seccionArrastrada$ = new Subject<SeccionModel | null>();
  public solicitudAgregar$ = new Subject<SeccionModel>();

  private materiaService = inject(MateriaService);
  private profesorService = inject(ProfesorService);
  private authService = inject(AuthService);

  getSeccionesDisponibles(): Observable<SeccionModel[]> {
    return this.obtenerSecciones(() => true);
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
    const idEstudiante = this.authService.obtenerUsuarioActual().id;

    return this.getHorarioInscrito().pipe(
      switchMap(inscritas => {
        const quitadas = inscritas.filter(i => !secciones.some(s => s.id === i.id));
        const nuevas = secciones.filter(s => !inscritas.some(i => i.id === s.id));
        // Primero se liberan los horarios y despues se inscriben las nuevas
        return from([
          ...quitadas.map(s => this.materiaService.desinscribirEstudiante(idEstudiante, s.id)),
          ...nuevas.map(s => this.materiaService.inscribirEstudiante(idEstudiante, s.id))
        ]);
      }),
      concatMap(peticion => peticion),
      toArray(),
      map(() => ({ mensaje: 'Horario guardado exitosamente' }))
    );
  }

  getHorarioInscrito(): Observable<SeccionModel[]> {
    const idEstudiante = this.authService.obtenerUsuarioActual().id;
    return this.obtenerSecciones(m => m.listaIdEstudiantes.includes(idEstudiante));
  }

  private obtenerSecciones(filtro: (materia: MateriaDTO) => boolean): Observable<SeccionModel[]> {
    return forkJoin([this.materiaService.getAll(), this.profesorService.getAll()]).pipe(
      map(([materias, profesores]) => materias.filter(filtro).map(m => {
        const docente = profesores.find(p => p.id === m.idProfesorAsignado)?.nombre ?? 'Sin asignar';
        return this.materiaService.adaptarMateriaASeccion(m, docente);
      }))
    );
  }

  private formatearHora(h: number): string {
    return h < 10 ? `0${h}:00` : `${h}:00`;
  }
}
