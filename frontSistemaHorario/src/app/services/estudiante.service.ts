import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { EstudianteDTO } from '../models/usuario.model';

@Injectable({ providedIn: 'root' })
export class EstudianteService {
  // Cuando el back esté listo:
  // private http = inject(HttpClient);
  // private readonly urlBase = 'http://localhost:8080/estudiante';

  private estudiantesMemoria: EstudianteDTO[] = [
    { id: 1, nombre: 'Jose Manuel Cipagauta', correo: 'jcipagauta@unbosque.edu.co', listaIdMateriasInscritas: [101, 201] },
    { id: 2, nombre: 'jean Paul Quintero', correo: 'jeanp@unbosque.edu.co', listaIdMateriasInscritas: [101] },
    { id: 3, nombre: 'Soto Cartagena', correo: 'csoto@unbosque.edu.co', listaIdMateriasInscritas: [301] }
  ];

  create(data: EstudianteDTO): Observable<number> {
    const nuevoId = this.estudiantesMemoria.length + 1;
    this.estudiantesMemoria.push({ ...data, id: nuevoId });
    return of(1);
  }

  getAll(): Observable<EstudianteDTO[]> {
    return of([...this.estudiantesMemoria]);
  }

  deleteById(id: number): Observable<number> {
    this.estudiantesMemoria = this.estudiantesMemoria.filter(e => e.id !== id);
    return of(1);
  }

  updateById(id: number, data: EstudianteDTO): Observable<number> {
    const index = this.estudiantesMemoria.findIndex(e => e.id === id);
    if (index !== -1) {
      this.estudiantesMemoria[index] = { ...data, id };
      return of(1);
    }
    return of(0);
  }

  count(): Observable<number> {
    return of(this.estudiantesMemoria.length);
  }

  exist(id: number): Observable<boolean> {
    return of(this.estudiantesMemoria.some(e => e.id === id));
  }
}
