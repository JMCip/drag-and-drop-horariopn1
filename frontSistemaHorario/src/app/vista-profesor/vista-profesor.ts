import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Topbar, PestanaNav } from '../shared/topbar/topbar';
import { CronogramaProfesor } from './cronograma-profesor/cronograma-profesor';
import { MateriasGrupos } from './materias-grupos/materias-grupos';

@Component({
  selector: 'app-vista-profesor',
  standalone: true,
  imports: [CommonModule, Topbar, CronogramaProfesor, MateriasGrupos],
  templateUrl: './vista-profesor.html',
  styleUrl: './vista-profesor.css'
})
export class VistaProfesor {
  pestanas: PestanaNav[] = [
    { id: 'horario', etiqueta: 'Mi horario', icono: 'fa-calendar-days' },
    { id: 'grupos', etiqueta: 'Mis materias y grupos', icono: 'fa-users-rectangle' }
  ];
  pestanaActiva = 'horario';
}
