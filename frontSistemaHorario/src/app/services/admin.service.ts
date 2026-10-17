import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { AdminDTO } from '../models/usuario.model';

@Injectable({ providedIn: 'root' })
export class AdminService {

  // private http = inject(HttpClient);
  // private readonly urlBase = 'http://localhost:8080/admin';

  private adminsMemoria: AdminDTO[] = [
    { id: 1, correo: 'admin.secretaria@unbosque.edu.co' }
  ];

  create(data: AdminDTO): Observable<number> {
    const nuevoId = this.adminsMemoria.length + 1;
    this.adminsMemoria.push({ ...data, id: nuevoId });
    return of(1);
  }

  getAll(): Observable<AdminDTO[]> {
    return of([...this.adminsMemoria]);
  }

  deleteById(id: number): Observable<number> {
    this.adminsMemoria = this.adminsMemoria.filter(a => a.id !== id);
    return of(1);
  }

  updateById(id: number, data: AdminDTO): Observable<number> {
    const index = this.adminsMemoria.findIndex(a => a.id === id);
    if (index !== -1) {
      this.adminsMemoria[index] = { ...data, id };
      return of(1);
    }
    return of(0);
  }

  count(): Observable<number> {
    return of(this.adminsMemoria.length);
  }

  exist(id: number): Observable<boolean> {
    return of(this.adminsMemoria.some(a => a.id === id));
  }
}
