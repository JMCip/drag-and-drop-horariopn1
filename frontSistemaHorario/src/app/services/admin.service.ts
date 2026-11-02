import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { AdminDTO } from '../models/usuario.model';

@Injectable({ providedIn: 'root' })
export class AdminService {

  private http = inject(HttpClient);
  private readonly urlBase = 'http://localhost:8080/admin';

  create(data: AdminDTO): Observable<string> {
    return this.http.post(`${this.urlBase}/crear`, this.aParams(data), { responseType: 'text' });
  }

  getAll(): Observable<AdminDTO[]> {
    return this.http.get<AdminDTO[]>(`${this.urlBase}/mostrartodo`).pipe(map(l => l ?? []));
  }

  deleteById(id: number): Observable<string> {
    const params = new HttpParams().set('id', id);
    return this.http.delete(`${this.urlBase}/eliminar`, { params, responseType: 'text' });
  }

  updateById(id: number, data: AdminDTO): Observable<string> {
    return this.http.put(`${this.urlBase}/actualizar`, this.aParams(data).set('id', id), { responseType: 'text' });
  }

  count(): Observable<number> {
    return this.getAll().pipe(map(l => l.length));
  }

  exist(id: number): Observable<boolean> {
    return this.getAll().pipe(map(l => l.some(a => a.id === id)));
  }

  private aParams(data: AdminDTO): HttpParams {
    const params = new HttpParams().set('correo', data.correo);
    return data.contrasenia ? params.set('contrasenia', data.contrasenia) : params;
  }
}
