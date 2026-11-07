import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Topbar, PestanaNav } from '../shared/topbar/topbar';
import { CronogramaProfesor } from './cronograma-profesor/cronograma-profesor';
import { MateriasGrupos } from './materias-grupos/materias-grupos';

/**
 * Vista del profesor (`/profesor`). Tiene dos pestañas: "Mi horario" y "Mis materias y grupos".
 */
@Component({
  selector: 'app-vista-profesor',
  standalone: true,
  imports: [CommonModule, Topbar, CronogramaProfesor, MateriasGrupos],
  templateUrl: './vista-profesor.html',
  styleUrl: './vista-profesor.css'
})
export class VistaProfesor {
  /**
   * Pestañas de la barra superior.
   */
  pestanas: PestanaNav[] = [
    { id: 'horario', etiqueta: 'Mi horario', icono: 'fa-calendar-days' },
    { id: 'grupos', etiqueta: 'Mis materias y grupos', icono: 'fa-users-rectangle' }
  ];
  /**
   * Pestaña visible actualmente.
   */
  pestanaActiva = 'horario';
}
