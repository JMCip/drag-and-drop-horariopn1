import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const estudianteGuard: CanActivateFn = () => {
  const router = inject(Router);
  const rol = localStorage.getItem('rol_horario');
  if (rol === 'ESTUDIANTE' || rol === 'ROLE_ESTUDIANTE') return true;
  router.navigate(['/login']);
  return false;
};

export const profesorGuard: CanActivateFn = () => {
  const router = inject(Router);
  const rol = localStorage.getItem('rol_horario');
  if (rol === 'PROFESOR' || rol === 'ROLE_PROFESOR') return true;
  router.navigate(['/login']);
  return false;
};

export const adminGuard: CanActivateFn = () => {
  const router = inject(Router);
  const rol = localStorage.getItem('rol_horario');
  if (rol === 'ADMIN' || rol === 'ROLE_ADMIN') return true;
  router.navigate(['/login']);
  return false;
};
