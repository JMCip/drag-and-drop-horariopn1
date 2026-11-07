import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Topbar, PestanaNav } from '../shared/topbar/topbar';
import { DashboardAdmin } from './dashboard-admin/dashboard-admin';
import { MateriasAdmin } from './materias-admin/materias-admin';
import { RegistroUsuarios } from './registro-usuarios/registro-usuarios';
import { ReubicacionesAdmin } from './reubicaciones-admin/reubicaciones-admin';

/**
 * Vista del administrador (`/admin`). Tiene cuatro pestañas: Panel, Materias, Registrar usuario y Reubicaciones.
 */
@Component({
  selector: 'app-vista-admin',
  standalone: true,
  imports: [CommonModule, Topbar, DashboardAdmin, MateriasAdmin, RegistroUsuarios, ReubicacionesAdmin],
  templateUrl: './vista-admin.html',
  styleUrl: './vista-admin.css'
})
export class VistaAdmin {
  /**
   * Pestañas de la barra superior.
   */
  pestanas: PestanaNav[] = [
    { id: 'panel', etiqueta: 'Panel', icono: 'fa-chart-pie' },
    { id: 'materias', etiqueta: 'Materias', icono: 'fa-book' },
    { id: 'registrar', etiqueta: 'Registrar usuario', icono: 'fa-user-plus' },
    { id: 'reubicaciones', etiqueta: 'Reubicaciones', icono: 'fa-arrows-left-right' }
  ];
  /**
   * Pestaña visible actualmente.
   */
  pestanaActiva = 'panel';
}
