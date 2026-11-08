import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { AsignaturaDTO } from '../models/materia.model';

/**
 * Servicio HTTP del catálogo de asignaturas: consume `GET /asignatura/mostrartodo` del back.
 * Solo consulta; el catálogo no se edita desde la aplicación. Solo el rol ADMIN puede usarlo.
 */
@Injectable({ providedIn: 'root' })
export class AsignaturaService {

  private http = inject(HttpClient);
  /**
   * URL base del endpoint `/asignatura` del back.
   */
  private readonly urlBase = 'http://localhost:8080/asignatura';

  /**
   * Obtiene todo el catálogo de asignaturas con sus planes (carrera y semestre).
   *
   * @returns observable con la lista (vacía si el back no devuelve nada)
   */
  getAll(): Observable<AsignaturaDTO[]> {
    return this.http.get<AsignaturaDTO[]>(`${this.urlBase}/mostrartodo`).pipe(map(l => l ?? []));
  }
}
