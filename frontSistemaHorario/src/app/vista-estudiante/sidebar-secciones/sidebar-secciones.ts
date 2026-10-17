import { Component, Input, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SeccionModel, FranjaHoraria } from '../../models/seccion.model';
import { HorarioService } from '../../services/horario.service';

@Component({
  selector: 'app-sidebar-secciones',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './sidebar-secciones.html',
  styleUrl: './sidebar-secciones.css'
})
export class SidebarSecciones {
  private horarioService = inject(HorarioService);

  @Input() todasLasSecciones: SeccionModel[] = [];
  @Input() seccionesUbicadas: SeccionModel[] = [];

  textoBusqueda = '';
  semestreFiltro = '';
  materiasPlegadas: Record<string, boolean> = {};

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

  get materiasAgrupadas(): { nombre: string; codigo: string; secciones: SeccionModel[] }[] {
    const mapa = new Map<string, { nombre: string; codigo: string; secciones: SeccionModel[] }>();
    for (const s of this.seccionesDisponiblesFiltradas) {
      if (!mapa.has(s.codigoMateria)) {
        mapa.set(s.codigoMateria, { nombre: s.nombreMateria, codigo: s.codigoMateria, secciones: [] });
      }
      mapa.get(s.codigoMateria)!.secciones.push(s);
    }
    return Array.from(mapa.values());
  }

  limpiarFiltros(): void {
    this.textoBusqueda = '';
    this.semestreFiltro = '';
  }

  toggleMateria(codigo: string): void {
    this.materiasPlegadas[codigo] = !this.materiasPlegadas[codigo];
  }

  iniciarArrastre(seccion: SeccionModel): void {
    this.horarioService.seccionArrastrada$.next(seccion);
  }

  finalizarArrastre(): void {
    this.horarioService.seccionArrastrada$.next(null);
  }

  agregarPorBoton(seccion: SeccionModel): void {
    this.horarioService.solicitudAgregar$.next(seccion);
  }

  formatearFranja(f: FranjaHoraria): string {
    const hi = f.horaInicio < 10 ? `0${f.horaInicio}` : f.horaInicio;
    const hf = f.horaFin < 10 ? `0${f.horaFin}` : f.horaFin;
    return `${f.dia} ${hi}–${hf}`;
  }
}
