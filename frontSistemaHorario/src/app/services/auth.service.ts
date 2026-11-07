import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Role } from '../models/role.enum';

/**
 * Respuesta del login del back: token JWT y datos del usuario.
 */
export interface AuthResponse {
  id?: number;
  token: string;
  rol: Role;
  nombre: string;
  correo: string;
}

/**
 * Maneja la sesión del usuario. Guarda el token y los datos del usuario en `localStorage`
 * con las claves `token_horario`, `rol_horario`, `nombre_horario`, `correo_horario` e `id_usuario_horario`.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {

  private http = inject(HttpClient);
  /**
   * URL del back para autenticación. Si el back cambia de dirección, hay que actualizarla aquí.
   */
  private readonly urlBase = 'http://localhost:8080/api/auth';

  /**
   * Inicia sesión llamando a `POST /api/auth/login`. Si el login es correcto, guarda el token y los datos del usuario.
   *
   * @param correo correo del usuario (se recortan los espacios)
   * @param contrasenia contraseña en texto plano
   * @returns observable con la respuesta del back; falla con 401 si las credenciales son incorrectas
   */
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

  /**
   * @returns el token JWT guardado, o `null` si no hay sesión
   */
  obtenerToken(): string | null {
    return localStorage.getItem('token_horario');
  }

  /**
   * Lee de `localStorage` los datos del usuario con sesión iniciada.
   * Si falta algún dato usa valores por defecto (id 1, nombre "Usuario", rol estudiante).
   *
   * @returns id, nombre, rol y correo del usuario actual
   */
  obtenerUsuarioActual() {
    return {
      id: Number(localStorage.getItem('id_usuario_horario') || 1),
      nombre: localStorage.getItem('nombre_horario') || 'Usuario',
      rol: (localStorage.getItem('rol_horario') as Role) || Role.ESTUDIANTE,
      correo: localStorage.getItem('correo_horario') || ''
    };
  }

  /**
   * Cierra la sesión borrando del navegador el token y los datos del usuario.
   */
  cerrarSesion(): void {
    localStorage.removeItem('token_horario');
    localStorage.removeItem('rol_horario');
    localStorage.removeItem('nombre_horario');
    localStorage.removeItem('correo_horario');
    localStorage.removeItem('id_usuario_horario');
  }
}
