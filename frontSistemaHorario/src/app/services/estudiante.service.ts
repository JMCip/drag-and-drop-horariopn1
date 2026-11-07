import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { EstudianteDTO } from '../models/usuario.model';

@Injectable({ providedIn: 'root' })
export class EstudianteService {

  private http = inject(HttpClient);
  private readonly urlBase = 'http://localhost:8080/estudiante';

  create(data: EstudianteDTO): Observable<string> {
    return this.http.post(`${this.urlBase}/crear`, this.aParams(data), { responseType: 'text' });
  }

  getAll(): Observable<EstudianteDTO[]> {
    return this.http.get<EstudianteDTO[]>(`${this.urlBase}/mostrartodo`).pipe(map(l => l ?? []));
  }

  deleteById(id: number): Observable<string> {
    const params = new HttpParams().set('id', id);
    return this.http.delete(`${this.urlBase}/eliminar`, { params, responseType: 'text' });
  }

  updateById(id: number, data: EstudianteDTO): Observable<string> {
    return this.http.put(`${this.urlBase}/actualizar`, this.aParams(data).set('id', id), { responseType: 'text' });
  }

  count(): Observable<number> {
    return this.getAll().pipe(map(l => l.length));
  }

  exist(id: number): Observable<boolean> {
    return this.getAll().pipe(map(l => l.some(e => e.id === id)));
  }

  private aParams(data: EstudianteDTO): HttpParams {
    const params = new HttpParams()
      .set('nombre', data.nombre)
      .set('correo', data.correo)
      .set('carrera', data.carrera ?? '')
      .set('semestre', data.semestre ?? '');
    return data.contrasenia ? params.set('contrasenia', data.contrasenia) : params;
  }
}
