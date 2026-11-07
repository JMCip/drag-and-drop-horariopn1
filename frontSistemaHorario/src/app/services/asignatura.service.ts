import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { AsignaturaDTO } from '../models/materia.model';

@Injectable({ providedIn: 'root' })
export class AsignaturaService {

  private http = inject(HttpClient);
  private readonly urlBase = 'http://localhost:8080/asignatura';

  getAll(): Observable<AsignaturaDTO[]> {
    return this.http.get<AsignaturaDTO[]>(`${this.urlBase}/mostrartodo`).pipe(map(l => l ?? []));
  }
}
