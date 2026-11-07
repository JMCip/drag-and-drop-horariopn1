import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { DIAS_MATERIA, GrupoMateria, MateriaDTO, PlanEstudio, SIN_CLASE } from '../models/materia.model';
import { ETIQUETAS_CARRERA } from '../models/carrera.enum';
import { SeccionModel, FranjaHoraria } from '../models/seccion.model';

/**
 * Servicio HTTP de materias: consume los endpoints `/materia` del back y ofrece utilidades
 * para convertir entre el formato del back (arreglos `inicio` y `fin`) y el del front (franjas por día).
 */
@Injectable({ providedIn: 'root' })
export class MateriaService {

  private http = inject(HttpClient);
  /**
   * URL base del endpoint `/materia` del back.
   */
  private readonly urlBase = 'http://localhost:8080/materia';

  create(data: GrupoMateria): Observable<string> {
  /**
   * Crea un grupo de una materia.
   *
   * @param data datos de la materia
   * @returns observable con el mensaje de respuesta del back
   */
  create(data: MateriaDTO): Observable<string> {
    return this.http.post(`${this.urlBase}/crear`, null, { params: this.aParams(data), responseType: 'text' });
  }

  /**
   * Obtiene todas las materias (grupos).
   *
   * @returns observable con la lista (vacía si el back no devuelve nada)
   */
  getAll(): Observable<MateriaDTO[]> {
    return this.http.get<MateriaDTO[]>(`${this.urlBase}/mostrartodo`).pipe(map(l => l ?? []));
  }

  /**
   * Elimina una materia por su id.
   *
   * @param id identificador de la materia
   * @returns observable con el mensaje de respuesta del back
   */
  deleteById(id: number): Observable<string> {
    const params = new HttpParams().set('id', id);
    return this.http.delete(`${this.urlBase}/eliminar`, { params, responseType: 'text' });
  }

  updateById(id: number, data: GrupoMateria): Observable<string> {
    const params = this.aParams(data).set('id', id);
    return this.http.put(`${this.urlBase}/actualizar`, null, { params, responseType: 'text' });
  }

  /**
   * Asigna un profesor a una materia (solo ADMIN).
   *
   * @param idProfesor identificador del profesor
   * @param idMateria identificador de la materia
   * @returns observable con el mensaje de respuesta del back
   */
  asignarProfesor(idProfesor: number, idMateria: number): Observable<string> {
    const params = new HttpParams().set('idProfesor', idProfesor).set('idMateria', idMateria);
    return this.http.put(`${this.urlBase}/asignarprofesor`, null, { params, responseType: 'text' });
  }

  quitarProfesor(idMateria: number): Observable<string> {
    const params = new HttpParams().set('idMateria', idMateria);
    return this.http.put(`${this.urlBase}/quitarprofesor`, null, { params, responseType: 'text' });
  }

  /**
   * Inscribe a un estudiante en una materia. El estudiante solo puede inscribirse a sí mismo.
   *
   * @param idEstudiante identificador del estudiante
   * @param idMateria identificador de la materia
   * @returns observable con el mensaje de respuesta del back
   */
  inscribirEstudiante(idEstudiante: number, idMateria: number): Observable<string> {
    const params = new HttpParams().set('idEstudiante', idEstudiante).set('idMateria', idMateria);
    return this.http.put(`${this.urlBase}/inscribirestudiante`, null, { params, responseType: 'text' });
  }

  /**
   * Quita la inscripción de un estudiante en una materia.
   *
   * @param idEstudiante identificador del estudiante
   * @param idMateria identificador de la materia
   * @returns observable con el mensaje de respuesta del back
   */
  desinscribirEstudiante(idEstudiante: number, idMateria: number): Observable<string> {
    const params = new HttpParams().set('idEstudiante', idEstudiante).set('idMateria', idMateria);
    return this.http.put(`${this.urlBase}/desinscribirestudiante`, null, { params, responseType: 'text' });
  }

  /**
   * Mueve a un estudiante de un grupo a otro de la misma materia (solo ADMIN).
   *
   * @param idEstudiante identificador del estudiante
   * @param idMateriaOrigen grupo del que sale
   * @param idMateriaDestino grupo al que entra
   * @returns observable con el mensaje de respuesta del back
   */
  moverEstudiante(idEstudiante: number, idMateriaOrigen: number, idMateriaDestino: number): Observable<string> {
    const params = new HttpParams()
      .set('idEstudiante', idEstudiante)
      .set('idMateriaOrigen', idMateriaOrigen)
      .set('idMateriaDestino', idMateriaDestino);
    return this.http.put(`${this.urlBase}/moverestudiante`, null, { params, responseType: 'text' });
  }

  /**
   * Cuenta las materias a partir de la lista completa.
   *
   * @returns observable con la cantidad
   */
  count(): Observable<number> {
    return this.getAll().pipe(map(l => l.length));
  }

  /**
   * Indica si existe una materia con ese id.
   *
   * @param id identificador a buscar
   * @returns observable con `true` si existe
   */
  exist(id: number): Observable<boolean> {
    return this.getAll().pipe(map(l => l.some(m => m.id === id)));
  }

  /**
   * Convierte los arreglos `inicio` y `fin` del back en una lista de franjas, omitiendo los días sin clase.
   *
   * @param dto materia tal como la devuelve el back
   * @returns franjas con día, hora de inicio y hora de fin
   */
  aFranjas(dto: MateriaDTO): FranjaHoraria[] {
    const franjas: FranjaHoraria[] = [];
    DIAS_MATERIA.forEach((dia, i) => {
      if (dto.inicio?.[i] !== undefined && dto.inicio[i] !== SIN_CLASE) {
        franjas.push({ dia, horaInicio: dto.inicio[i], horaFin: dto.fin[i] });
      }
    });
    return franjas;
  }

  /**
   * Hace lo contrario de `aFranjas`: arma los arreglos `inicio` y `fin` de 5 posiciones que espera el back.
   * Los días sin franja quedan en `-1`.
   *
   * @param franjas franjas de clase
   * @returns arreglos `inicio` y `fin` de lunes a viernes
   */
  deFranjas(franjas: FranjaHoraria[]): { inicio: number[]; fin: number[] } {
    const inicio: number[] = Array(DIAS_MATERIA.length).fill(SIN_CLASE);
    const fin: number[] = Array(DIAS_MATERIA.length).fill(SIN_CLASE);
    for (const f of franjas) {
      const i = DIAS_MATERIA.findIndex(d => d === f.dia);
      if (i !== -1) {
        inicio[i] = f.horaInicio;
        fin[i] = f.horaFin;
      }
    }
    return { inicio, fin };
  }

  /**
   * Genera un texto legible del horario, por ejemplo `Lun 08:00–10:00 · Mié 14:00–16:00`.
   *
   * @param dto materia a describir
   * @returns el horario como texto
   */
  formatearHorario(dto: MateriaDTO): string {
    return this.aFranjas(dto)
      .map(f => `${f.dia} ${this.formatearHora(f.horaInicio)}–${this.formatearHora(f.horaFin)}`)
      .join(' · ');
  }

  formatearPlanes(planes: PlanEstudio[]): string {
    return planes.map(p => `${ETIQUETAS_CARRERA[p.carrera] ?? p.carrera} (${p.semestre}°)`).join(', ');
  }

  /**
   * Convierte una materia del back en una sección lista para mostrar y arrastrar.
   *
   * @param dto materia tal como la devuelve el back
   * @param nombreDocente nombre del profesor a mostrar
   * @returns la sección equivalente (el código se arma como `MAT-<id>`)
   */
  adaptarMateriaASeccion(
    dto: MateriaDTO,
    nombreDocente = 'Prof. Asignado'
  ): SeccionModel {
    return {
      id: dto.id ?? 0,
      codigoMateria: `MAT-${dto.id}`,
      nombreMateria: dto.nombreMateria,
      grupo: dto.grupo,
      docente: nombreDocente,
      semestre: dto.semestre ?? Math.min(...dto.planes.map(p => p.semestre)),
      carrera: dto.carrera ? ETIQUETAS_CARRERA[dto.carrera] : this.formatearPlanes(dto.planes),
      franjas: this.aFranjas(dto),
      estudiantesInscritos: dto.listaIdEstudiantes?.length ?? 0
    };
  }

  /**
   * Da formato de hora completa, por ejemplo 8 se muestra como `08:00`.
   */
  private formatearHora(h: number): string {
    return h < 10 ? `0${h}:00` : `${h}:00`;
  }

  /**
   * Convierte la materia en parámetros HTTP. Spring recibe los arreglos repitiendo el parámetro
   * (`inicio=7&inicio=-1&...`), por eso se usa `append` para cada hora.
   */
  // Spring recibe los arreglos repitiendo el parámetro: inicio=7&inicio=-1&...
  private aParams(data: GrupoMateria): HttpParams {
    let params = new HttpParams()
      .set('idAsignatura', data.idAsignatura)
      .set('grupo', data.grupo);
    data.inicio.forEach(h => params = params.append('inicio', h));
    data.fin.forEach(h => params = params.append('fin', h));
    return params;
  }
}
