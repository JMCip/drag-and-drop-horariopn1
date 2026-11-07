import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { DIAS_MATERIA, GrupoMateria, MateriaDTO, PlanEstudio, SIN_CLASE } from '../models/materia.model';
import { ETIQUETAS_CARRERA } from '../models/carrera.enum';
import { SeccionModel, FranjaHoraria } from '../models/seccion.model';

@Injectable({ providedIn: 'root' })
export class MateriaService {

  private http = inject(HttpClient);
  private readonly urlBase = 'http://localhost:8080/materia';

  create(data: GrupoMateria): Observable<string> {
    return this.http.post(`${this.urlBase}/crear`, null, { params: this.aParams(data), responseType: 'text' });
  }

  getAll(): Observable<MateriaDTO[]> {
    return this.http.get<MateriaDTO[]>(`${this.urlBase}/mostrartodo`).pipe(map(l => l ?? []));
  }

  deleteById(id: number): Observable<string> {
    const params = new HttpParams().set('id', id);
    return this.http.delete(`${this.urlBase}/eliminar`, { params, responseType: 'text' });
  }

  updateById(id: number, data: GrupoMateria): Observable<string> {
    const params = this.aParams(data).set('id', id);
    return this.http.put(`${this.urlBase}/actualizar`, null, { params, responseType: 'text' });
  }

  asignarProfesor(idProfesor: number, idMateria: number): Observable<string> {
    const params = new HttpParams().set('idProfesor', idProfesor).set('idMateria', idMateria);
    return this.http.put(`${this.urlBase}/asignarprofesor`, null, { params, responseType: 'text' });
  }

  quitarProfesor(idMateria: number): Observable<string> {
    const params = new HttpParams().set('idMateria', idMateria);
    return this.http.put(`${this.urlBase}/quitarprofesor`, null, { params, responseType: 'text' });
  }

  inscribirEstudiante(idEstudiante: number, idMateria: number): Observable<string> {
    const params = new HttpParams().set('idEstudiante', idEstudiante).set('idMateria', idMateria);
    return this.http.put(`${this.urlBase}/inscribirestudiante`, null, { params, responseType: 'text' });
  }

  desinscribirEstudiante(idEstudiante: number, idMateria: number): Observable<string> {
    const params = new HttpParams().set('idEstudiante', idEstudiante).set('idMateria', idMateria);
    return this.http.put(`${this.urlBase}/desinscribirestudiante`, null, { params, responseType: 'text' });
  }

  moverEstudiante(idEstudiante: number, idMateriaOrigen: number, idMateriaDestino: number): Observable<string> {
    const params = new HttpParams()
      .set('idEstudiante', idEstudiante)
      .set('idMateriaOrigen', idMateriaOrigen)
      .set('idMateriaDestino', idMateriaDestino);
    return this.http.put(`${this.urlBase}/moverestudiante`, null, { params, responseType: 'text' });
  }

  count(): Observable<number> {
    return this.getAll().pipe(map(l => l.length));
  }

  exist(id: number): Observable<boolean> {
    return this.getAll().pipe(map(l => l.some(m => m.id === id)));
  }

  aFranjas(dto: MateriaDTO): FranjaHoraria[] {
    const franjas: FranjaHoraria[] = [];
    DIAS_MATERIA.forEach((dia, i) => {
      if (dto.inicio?.[i] !== undefined && dto.inicio[i] !== SIN_CLASE) {
        franjas.push({ dia, horaInicio: dto.inicio[i], horaFin: dto.fin[i] });
      }
    });
    return franjas;
  }

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

  formatearHorario(dto: MateriaDTO): string {
    return this.aFranjas(dto)
      .map(f => `${f.dia} ${this.formatearHora(f.horaInicio)}–${this.formatearHora(f.horaFin)}`)
      .join(' · ');
  }

  formatearPlanes(planes: PlanEstudio[]): string {
    return planes.map(p => `${ETIQUETAS_CARRERA[p.carrera] ?? p.carrera} (${p.semestre}°)`).join(', ');
  }

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

  private formatearHora(h: number): string {
    return h < 10 ? `0${h}:00` : `${h}:00`;
  }

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
