import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Topbar, PestanaNav } from '../shared/topbar/topbar';
import { DashboardAdmin } from './dashboard-admin/dashboard-admin';
import { RegistroUsuarios } from './registro-usuarios/registro-usuarios';
import { ReubicacionesAdmin } from './reubicaciones-admin/reubicaciones-admin';

@Component({
  selector: 'app-vista-admin',
  standalone: true,
  imports: [CommonModule, Topbar, DashboardAdmin, RegistroUsuarios, ReubicacionesAdmin],
  templateUrl: './vista-admin.html',
  styleUrl: './vista-admin.css'
})
export class VistaAdmin {
  pestanas: PestanaNav[] = [
    { id: 'panel', etiqueta: 'Panel', icono: 'fa-chart-pie' },
    { id: 'registrar', etiqueta: 'Registrar usuario', icono: 'fa-user-plus' },
    { id: 'reubicaciones', etiqueta: 'Reubicaciones', icono: 'fa-arrows-left-right' }
  ];
  pestanaActiva = 'panel';
}
