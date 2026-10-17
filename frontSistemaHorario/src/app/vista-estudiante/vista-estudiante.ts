import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Topbar, PestanaNav } from '../shared/topbar/topbar';
import { SidebarSecciones } from './sidebar-secciones/sidebar-secciones';
import { CronogramaHorario } from './cronograma-horario/cronograma-horario';
import { HorarioInscrito } from './horario-inscrito/horario-inscrito';
import { HorarioService } from '../services/horario.service';
import { SeccionModel } from '../models/seccion.model';

@Component({
  selector: 'app-vista-estudiante',
  standalone: true,
  imports: [CommonModule, Topbar, SidebarSecciones, CronogramaHorario, HorarioInscrito],
  templateUrl: './vista-estudiante.html',
  styleUrl: './vista-estudiante.css'
})
export class VistaEstudiante implements OnInit {
  private horarioService = inject(HorarioService);

  pestanas: PestanaNav[] = [
    { id: 'armar', etiqueta: 'Armar horario', icono: 'fa-calendar-plus' },
    { id: 'inscrito', etiqueta: 'Mi horario inscrito', icono: 'fa-calendar-check' }
  ];
  pestanaActiva = 'armar';

  todasLasSecciones: SeccionModel[] = [];
  seccionesUbicadas: SeccionModel[] = [];
  horarioInscrito: SeccionModel[] = [];

  ngOnInit(): void {
    this.horarioService.getSeccionesDisponibles().subscribe(datos => {
      this.todasLasSecciones = datos;
    });
    this.horarioService.getHorarioInscrito().subscribe(inscrito => {
      this.horarioInscrito = inscrito;
    });
  }

  actualizarInscrito(nuevas: SeccionModel[]): void {
    this.horarioInscrito = [...nuevas];
    this.pestanaActiva = 'inscrito';
  }
}
