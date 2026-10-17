import { Injectable } from '@angular/core';
import { Observable, of, throwError } from 'rxjs';
import { delay, tap } from 'rxjs/operators';
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
  //inyectar el HttpClient!!!

  iniciarSesion(correo: string, contrasenia: string): Observable<AuthResponse> {
    const c = correo.toLowerCase().trim();
    if (contrasenia === '12345678' || contrasenia === 'admin123') {
      let respuesta: AuthResponse;
      if (c.includes('admin')) {
        respuesta = { id: 1, token: 'mock-jwt-admin', rol: Role.ADMIN, nombre: 'Secretaría Académica', correo };
      } else if (c.includes('prof')) {
        respuesta = { id: 10, token: 'mock-jwt-prof', rol: Role.PROFESOR, nombre: 'Prof. A. Pérez', correo };
      } else {
        respuesta = { id: 1, token: 'mock-jwt-est', rol: Role.ESTUDIANTE, nombre: 'Jose Manuel Cipagauta', correo };
      }

      return of(respuesta).pipe(
        delay(150),
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

    return throwError(() => ({ status: 401, error: 'Correo o contraseña incorrectos' }));
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
