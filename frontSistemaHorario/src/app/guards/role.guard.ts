import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

/**
 * Permite entrar a la vista del estudiante solo si el rol guardado en la sesión es ESTUDIANTE.
 * Si no, redirige a `/login`. Acepta también el formato `ROLE_ESTUDIANTE`.
 * Nota: es una protección de la interfaz; la seguridad real la aplica el back con el token.
 */
export const estudianteGuard: CanActivateFn = () => {
  const router = inject(Router);
  const rol = localStorage.getItem('rol_horario');
  if (rol === 'ESTUDIANTE' || rol === 'ROLE_ESTUDIANTE') return true;
  router.navigate(['/login']);
  return false;
};

/**
 * Permite entrar a la vista del profesor solo si el rol guardado en la sesión es PROFESOR.
 * Si no, redirige a `/login`.
 */
export const profesorGuard: CanActivateFn = () => {
  const router = inject(Router);
  const rol = localStorage.getItem('rol_horario');
  if (rol === 'PROFESOR' || rol === 'ROLE_PROFESOR') return true;
  router.navigate(['/login']);
  return false;
};

/**
 * Permite entrar a la vista del administrador solo si el rol guardado en la sesión es ADMIN.
 * Si no, redirige a `/login`.
 */
export const adminGuard: CanActivateFn = () => {
  const router = inject(Router);
  const rol = localStorage.getItem('rol_horario');
  if (rol === 'ADMIN' || rol === 'ROLE_ADMIN') return true;
  router.navigate(['/login']);
  return false;
};
