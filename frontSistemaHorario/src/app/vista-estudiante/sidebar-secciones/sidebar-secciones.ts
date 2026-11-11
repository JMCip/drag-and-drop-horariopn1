import { Component, Input, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SeccionModel, FranjaHoraria } from '../../models/seccion.model';
import { HorarioService } from '../../services/horario.service';

/**
 * Barra lateral con las secciones que el estudiante puede agregar. Permite buscar por nombre o código,
 * filtrar por semestre y agrupar por materia. Cada sección se puede arrastrar a la grilla (drag and drop de HTML5)
 * o agregar con un botón.
 */
@Component({
  selector: 'app-sidebar-secciones',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './sidebar-secciones.html',
  styleUrl: './sidebar-secciones.css'
})
export class SidebarSecciones {
  private horarioService = inject(HorarioService);

  /**
   * Todas las secciones disponibles.
   */
  @Input() todasLasSecciones: SeccionModel[] = [];
  /**
   * Secciones que ya están en la grilla; se ocultan de la lista.
   */
  @Input() seccionesUbicadas: SeccionModel[] = [];
  /**
   * Texto del buscador (nombre o código de la materia).
   */

  textoBusqueda = '';
  /**
   * Semestre elegido en el filtro; vacío = todos.
   */
  semestreFiltro = '';
  /**
   * Materias cuyo listado de grupos está plegado, indexado por código.
   */
  materiasPlegadas: Record<string, boolean> = {};

  /**
   * Semestres que tienen secciones disponibles, sin repetir y en orden; alimenta el filtro de semestre.
   */
  get semestresDisponibles(): number[] {
    return [...new Set(this.todasLasSecciones.map(s => s.semestre))].sort((a, b) => a - b);
  }

  /**
   * Secciones que se muestran: excluye las ya ubicadas y aplica el texto de búsqueda y el filtro de semestre.
   */
  get seccionesDisponiblesFiltradas(): SeccionModel[] {
    const ubicadasIds = new Set(this.seccionesUbicadas.map(s => s.id));
    return this.todasLasSecciones.filter(s => {
      if (ubicadasIds.has(s.id)) return false;
      const coincideTexto = !this.textoBusqueda.trim() ||
        s.nombreMateria.toLowerCase().includes(this.textoBusqueda.toLowerCase()) ||
        s.codigoMateria.toLowerCase().includes(this.textoBusqueda.toLowerCase());
      const coincideSemestre = !this.semestreFiltro || s.semestre.toString() === this.semestreFiltro;
      return coincideTexto && coincideSemestre;
    });
  }

  /**
   * Agrupa las secciones filtradas por nombre de materia, para mostrar cada materia con sus grupos.
   */
  get materiasAgrupadas(): { nombre: string; codigo: string; secciones: SeccionModel[] }[] {
    const mapa = new Map<string, { nombre: string; codigo: string; secciones: SeccionModel[] }>();
    for (const s of this.seccionesDisponiblesFiltradas) {
      if (!mapa.has(s.nombreMateria)) {
        mapa.set(s.nombreMateria, { nombre: s.nombreMateria, codigo: s.codigoMateria, secciones: [] });
      }
      mapa.get(s.nombreMateria)!.secciones.push(s);
    }
    return Array.from(mapa.values());
  }

  /**
   * Borra el texto de búsqueda y el filtro de semestre.
   */
  limpiarFiltros(): void {
    this.textoBusqueda = '';
    this.semestreFiltro = '';
  }

  /**
   * Pliega o despliega los grupos de una materia.
   *
   * @param codigo código de la materia
   */
  toggleMateria(codigo: string): void {
    this.materiasPlegadas[codigo] = !this.materiasPlegadas[codigo];
  }

  /**
   * Se ejecuta al empezar a arrastrar una sección (evento `dragstart`). Avisa a la grilla cuál se está arrastrando.
   *
   * @param seccion sección arrastrada
   */
  iniciarArrastre(seccion: SeccionModel): void {
    this.horarioService.seccionArrastrada$.next(seccion);
  }

  /**
   * Se ejecuta al terminar el arrastre (evento `dragend`). Avisa a la grilla que ya no hay sección arrastrada.
   */
  finalizarArrastre(): void {
    this.horarioService.seccionArrastrada$.next(null);
  }

  /**
   * Alternativa al arrastre: pide a la grilla agregar la sección con un clic.
   *
   * @param seccion sección a agregar
   */
  agregarPorBoton(seccion: SeccionModel): void {
    this.horarioService.solicitudAgregar$.next(seccion);
  }

  /**
   * Texto de una franja, por ejemplo `Lun 08–10`.
   *
   * @param f franja a mostrar
   * @returns la franja como texto
   */
  formatearFranja(f: FranjaHoraria): string {
    const hi = f.horaInicio < 10 ? `0${f.horaInicio}` : f.horaInicio;
    const hf = f.horaFin < 10 ? `0${f.horaFin}` : f.horaFin;
    return `${f.dia} ${hi}–${hf}`;
  }
}
