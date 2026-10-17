import { Routes } from '@angular/router';
import { Login } from './login/login';
import { VistaEstudiante } from './vista-estudiante/vista-estudiante';
import { VistaProfesor } from './vista-profesor/vista-profesor';
import { VistaAdmin } from './vista-admin/vista-admin';
import { estudianteGuard, profesorGuard, adminGuard } from './guards/role.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: Login },
  { path: 'estudiante', component: VistaEstudiante, canActivate: [estudianteGuard] },
  { path: 'profesor', component: VistaProfesor, canActivate: [profesorGuard] },
  { path: 'admin', component: VistaAdmin, canActivate: [adminGuard] },
  { path: '**', redirectTo: 'login' }
];
