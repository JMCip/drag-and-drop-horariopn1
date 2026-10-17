import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-cronograma-profesor',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './cronograma-profesor.html',
  styleUrl: './cronograma-profesor.css'
})
export class CronogramaProfesor {
  clasesAsignadas = [
    { materia: 'Bases de Datos (Grupo 1)', franja: 'Lun 07:00–09:00 · Mié 09:00–11:00' },
    { materia: 'Ingeniería de Software I (Grupo 1)', franja: 'Lun 09:00–11:00 · Vie 10:00–12:00' }
  ];
}
