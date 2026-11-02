import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Role } from '../models/role.enum';

export interface AuthResponse {
  id?: number;
  token: string;
  rol: Role;
  nombre: string;
  correo: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {

  private http = inject(HttpClient);
  private readonly urlBase = 'http://localhost:8080/api/auth';

  iniciarSesion(correo: string, contrasenia: string): Observable<AuthResponse> {
    const params = new HttpParams().set('correo', correo.trim()).set('contrasenia', contrasenia);

    return this.http.post<AuthResponse>(`${this.urlBase}/login`, params).pipe(
      tap(res => {
        localStorage.setItem('token_horario', res.token);
        localStorage.setItem('rol_horario', res.rol);
        localStorage.setItem('nombre_horario', res.nombre);
        localStorage.setItem('correo_horario', res.correo);
        if (res.id !== undefined) {
          localStorage.setItem('id_usuario_horario', res.id.toString());
        }
      })
    );
  }

  obtenerToken(): string | null {
    return localStorage.getItem('token_horario');
  }

  obtenerUsuarioActual() {
    return {
      id: Number(localStorage.getItem('id_usuario_horario') || 1),
      nombre: localStorage.getItem('nombre_horario') || 'Usuario',
      rol: (localStorage.getItem('rol_horario') as Role) || Role.ESTUDIANTE,
      correo: localStorage.getItem('correo_horario') || ''
    };
  }

  cerrarSesion(): void {
    localStorage.removeItem('token_horario');
    localStorage.removeItem('rol_horario');
    localStorage.removeItem('nombre_horario');
    localStorage.removeItem('correo_horario');
    localStorage.removeItem('id_usuario_horario');
  }
}
