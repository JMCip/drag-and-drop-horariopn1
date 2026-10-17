import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { ProfesorDTO } from '../models/usuario.model';

@Injectable({ providedIn: 'root' })
export class ProfesorService {

  // private http = inject(HttpClient);
  // private readonly urlBase = 'http://localhost:8080/profesor';

  private profesoresMemoria: ProfesorDTO[] = [
    { id: 10, nombre: 'Prof. Diego', correo: 'diego@unbosque.edu.co', listaIdMaterias: [101, 301] },
    { id: 11, nombre: 'Prof. Andres Rey', correo: 'rey@unbosque.edu.co', listaIdMaterias: [102] },
    { id: 12, nombre: 'Prof. Cristian', correo: 'cris@unbosque.edu.co', listaIdMaterias: [201, 202] }
  ];

  create(data: ProfesorDTO): Observable<number> {
    const nuevoId = this.profesoresMemoria.length + 10;
    this.profesoresMemoria.push({ ...data, id: nuevoId });
    return of(1);
  }

  getAll(): Observable<ProfesorDTO[]> {
    return of([...this.profesoresMemoria]);
  }

  deleteById(id: number): Observable<number> {
    this.profesoresMemoria = this.profesoresMemoria.filter(p => p.id !== id);
    return of(1);
  }

  updateById(id: number, data: ProfesorDTO): Observable<number> {
    const index = this.profesoresMemoria.findIndex(p => p.id === id);
    if (index !== -1) {
      this.profesoresMemoria[index] = { ...data, id };
      return of(1);
    }
    return of(0);
  }

  count(): Observable<number> {
    return of(this.profesoresMemoria.length);
  }

  exist(id: number): Observable<boolean> {
    return of(this.profesoresMemoria.some(p => p.id === id));
  }
}
