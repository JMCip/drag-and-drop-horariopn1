import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

/**
 * Interceptor HTTP que se ejecuta en todas las peticiones.
 * 1. Si hay token guardado, agrega la cabecera `Authorization: Bearer <token>`.
 * 2. Si el back responde 401 (token vencido o usuario eliminado), cierra la sesión y manda al login.
 *    Se excluyen las rutas de `/api/auth/`, porque allí un 401 solo significa credenciales incorrectas.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const token = authService.obtenerToken();

  const peticion = token
    ? req.clone({ headers: req.headers.set('Authorization', `Bearer ${token}`) })
    : req;

  return next(peticion).pipe(
    catchError((error: HttpErrorResponse) => {
      // Token vencido o usuario eliminado. En el login un 401 solo significa credenciales incorrectas.
      if (error.status === 401 && !req.url.includes('/api/auth/')) {
        authService.cerrarSesion();
        router.navigate(['/login']);
      }
      return throwError(() => error);
    })
  );
};
