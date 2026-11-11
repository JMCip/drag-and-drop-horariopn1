import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Topbar, PestanaNav } from '../shared/topbar/topbar';
import { SidebarSecciones } from './sidebar-secciones/sidebar-secciones';
import { CronogramaHorario } from './cronograma-horario/cronograma-horario';
import { HorarioInscrito } from './horario-inscrito/horario-inscrito';
import { HorarioService } from '../services/horario.service';
import { SeccionModel } from '../models/seccion.model';

/**
 * Vista del estudiante (`/estudiante`). Organiza dos pestañas: "Armar horario" (barra lateral con las
 * secciones disponibles y la grilla donde se sueltan) y "Mi horario inscrito".
 */
@Component({
  selector: 'app-vista-estudiante',
  standalone: true,
  imports: [CommonModule, Topbar, SidebarSecciones, CronogramaHorario, HorarioInscrito],
  templateUrl: './vista-estudiante.html',
  styleUrl: './vista-estudiante.css'
})
export class VistaEstudiante implements OnInit {
  private horarioService = inject(HorarioService);
  private cdr = inject(ChangeDetectorRef);

  /**
   * Pestañas de la barra superior.
   */
  pestanas: PestanaNav[] = [
    { id: 'armar', etiqueta: 'Armar horario', icono: 'fa-calendar-plus' },
    { id: 'inscrito', etiqueta: 'Mi horario inscrito', icono: 'fa-calendar-check' }
  ];
  /**
   * Pestaña visible actualmente.
   */
  pestanaActiva = 'armar';

  /**
   * Todas las secciones disponibles para elegir.
   */
  todasLasSecciones: SeccionModel[] = [];
  /**
   * Secciones que están puestas en la grilla (las ya inscritas más las que el estudiante agregó sin guardar).
   */
  seccionesUbicadas: SeccionModel[] = [];
  /**
   * Secciones en las que el estudiante ya está inscrito en el back.
   */
  horarioInscrito: SeccionModel[] = [];

  /**
   * Al abrir la vista carga las secciones disponibles y el horario ya inscrito.
   */
  ngOnInit(): void {
    this.horarioService.getSeccionesDisponibles().subscribe(datos => {
      this.todasLasSecciones = datos;
      this.cdr.markForCheck();
    });
    this.cargarInscrito();
  }

  /**
   * Se ejecuta cuando el estudiante guarda su horario: recarga lo inscrito y cambia a la pestaña "Mi horario inscrito".
   */
  actualizarInscrito(): void {
    this.cargarInscrito();
    this.pestanaActiva = 'inscrito';
  }

  /**
   * Consulta el horario inscrito y lo pone también en la grilla, para que ambas pestañas muestren lo mismo.
   */
  private cargarInscrito(): void {
    this.horarioService.getHorarioInscrito().subscribe(inscrito => {
      this.horarioInscrito = inscrito;
      this.seccionesUbicadas = [...inscrito];
      this.cdr.markForCheck();
    });
  }
}
