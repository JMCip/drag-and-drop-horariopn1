import { Injectable, inject } from '@angular/core';
import { Observable, Subject, forkJoin, from, of } from 'rxjs';
import { concatMap, delay, map, switchMap, toArray } from 'rxjs/operators';
import { MateriaDTO } from '../models/materia.model';
import { SeccionModel } from '../models/seccion.model';
import { RespuestaValidacionModel } from '../models/validacion.model';
import { AuthService } from './auth.service';
import { MateriaService } from './materia.service';
import { ProfesorService } from './profesor.service';

/**
 * Lógica del horario del estudiante: carga las secciones disponibles y las inscritas, valida cruces
 * y guarda los cambios. También comunica entre componentes con dos flujos (Subject):
 * la sección que se está arrastrando y las solicitudes de agregar con el botón.
 */
@Injectable({ providedIn: 'root' })
export class HorarioService {
  /**
   * Emite la sección que se está arrastrando desde la barra lateral (`null` cuando se suelta o se cancela).
   */
  public seccionArrastrada$ = new Subject<SeccionModel | null>();
  /**
   * Emite la sección que el estudiante agregó con el botón, sin arrastrar.
   */
  public solicitudAgregar$ = new Subject<SeccionModel>();

  private materiaService = inject(MateriaService);
  private profesorService = inject(ProfesorService);
  private authService = inject(AuthService);

  /**
   * Obtiene todas las secciones (materias con su profesor) que el estudiante puede elegir.
   *
   * @returns observable con la lista de secciones
   */
  getSeccionesDisponibles(): Observable<SeccionModel[]> {
    return this.obtenerSecciones(() => true);
  }

  /**
   * Comprueba si una sección candidata se cruza con las ya ubicadas en el horario.
   * Hay traslape cuando en el mismo día una empieza antes de que la otra termine; es TOTAL si el bloque
   * es idéntico y PARCIAL en otro caso. Se resuelve en el front, sin llamar al back; el back vuelve a
   * validar al inscribir.
   *
   * @param seccionCandidata sección que se quiere agregar
   * @param seccionesActuales secciones ya ubicadas en el horario
   * @returns observable con el tipo de traslape y la materia con la que choca
   */
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

  /**
   * Guarda el horario del estudiante comparando lo que ya estaba inscrito con lo que quedó en pantalla.
   * Primero se quitan las secciones retiradas (para liberar horarios) y después se inscriben las nuevas,
   * una por una y en orden.
   *
   * @param secciones secciones que quedaron en el horario
   * @returns observable que emite un mensaje de éxito; falla si el back rechaza alguna inscripción
   */
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

  /**
   * Obtiene las secciones en las que el estudiante con sesión iniciada ya está inscrito.
   *
   * @returns observable con la lista de secciones inscritas
   */
  getHorarioInscrito(): Observable<SeccionModel[]> {
    const idEstudiante = this.authService.obtenerUsuarioActual().id;
    return this.obtenerSecciones(m => m.listaIdEstudiantes.includes(idEstudiante));
  }

  /**
   * Carga materias y profesores en paralelo, filtra las materias y les pone el nombre del profesor
   * (o "Sin asignar" si no tienen).
   *
   * @param filtro condición que debe cumplir cada materia
   * @returns observable con las secciones resultantes
   */
  private obtenerSecciones(filtro: (materia: MateriaDTO) => boolean): Observable<SeccionModel[]> {
    return forkJoin([this.materiaService.getAll(), this.profesorService.getAll()]).pipe(
      map(([materias, profesores]) => materias.filter(filtro).map(m => {
        const docente = profesores.find(p => p.id === m.idProfesorAsignado)?.nombre ?? 'Sin asignar';
        return this.materiaService.adaptarMateriaASeccion(m, docente);
      }))
    );
  }

  /**
   * Da formato de hora completa, por ejemplo 8 se muestra como `08:00`.
   */
  private formatearHora(h: number): string {
    return h < 10 ? `0${h}:00` : `${h}:00`;
  }
}
