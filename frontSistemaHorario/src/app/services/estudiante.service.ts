import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { EstudianteDTO } from '../models/usuario.model';

/**
 * Servicio HTTP de estudiantes: consume los endpoints `/estudiante` del back.
 * Los métodos que modifican datos devuelven el mensaje de texto que responde el back; si hay error, el
 * mensaje viaja en `err.error`. Solo el rol ADMIN puede crear, editar y borrar.
 */
@Injectable({ providedIn: 'root' })
export class EstudianteService {

  private http = inject(HttpClient);
  /**
   * URL base del endpoint `/estudiante` del back.
   */
  private readonly urlBase = 'http://localhost:8080/estudiante';

  /**
   * Crea un registro enviando los datos como parámetros de la petición.
   *
   * @param data datos a crear
   * @returns observable con el mensaje de respuesta del back
   */
  create(data: EstudianteDTO): Observable<string> {
    return this.http.post(`${this.urlBase}/crear`, this.aParams(data), { responseType: 'text' });
  }

  /**
   * Obtiene todos los registros.
   *
   * @returns observable con la lista (vacía si el back no devuelve nada)
   */
  getAll(): Observable<EstudianteDTO[]> {
    return this.http.get<EstudianteDTO[]>(`${this.urlBase}/mostrartodo`).pipe(map(l => l ?? []));
  }

  /**
   * Elimina un registro por su id.
   *
   * @param id identificador del registro
   * @returns observable con el mensaje de respuesta del back
   */
  deleteById(id: number): Observable<string> {
    const params = new HttpParams().set('id', id);
    return this.http.delete(`${this.urlBase}/eliminar`, { params, responseType: 'text' });
  }

  /**
   * Actualiza un registro. Si no se envía contraseña, el back conserva la actual.
   *
   * @param id identificador del registro
   * @param data nuevos datos
   * @returns observable con el mensaje de respuesta del back
   */
  updateById(id: number, data: EstudianteDTO): Observable<string> {
    return this.http.put(`${this.urlBase}/actualizar`, this.aParams(data).set('id', id), { responseType: 'text' });
  }

  /**
   * Cuenta los registros. No hay un endpoint de conteo, así que se calcula a partir de la lista completa.
   *
   * @returns observable con la cantidad
   */
  count(): Observable<number> {
    return this.getAll().pipe(map(l => l.length));
  }

  /**
   * Indica si existe un registro con ese id. Se calcula a partir de la lista completa.
   *
   * @param id identificador a buscar
   * @returns observable con `true` si existe
   */
  exist(id: number): Observable<boolean> {
    return this.getAll().pipe(map(l => l.some(e => e.id === id)));
  }

  /**
   * Convierte el objeto en parámetros HTTP, que es el formato que espera el back.
   * La contraseña solo se incluye si tiene valor.
   */
  private aParams(data: EstudianteDTO): HttpParams {
    const params = new HttpParams()
      .set('nombre', data.nombre)
      .set('correo', data.correo)
      .set('carrera', data.carrera ?? '');
    return data.contrasenia ? params.set('contrasenia', data.contrasenia) : params;
  }
}
