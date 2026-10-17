import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SeccionModel, FranjaHoraria } from '../../models/seccion.model';

@Component({
  selector: 'app-horario-inscrito',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './horario-inscrito.html',
  styleUrl: './horario-inscrito.css'
})
export class HorarioInscrito {
  @Input() horarioInscrito: SeccionModel[] = [];
  @Output() irAArmar = new EventEmitter<void>();

  descargarJSON(): void {
    const dataStr = 'data:text/json;charset=utf-8,' + encodeURIComponent(JSON.stringify(this.horarioInscrito, null, 2));
    const anchor = document.createElement('a');
    anchor.setAttribute('href', dataStr);
    anchor.setAttribute('download', 'mi_horario_el_bosque.json');
    anchor.click();
  }

  formatearFranja(f: FranjaHoraria): string {
    const hi = f.horaInicio < 10 ? `0${f.horaInicio}` : f.horaInicio;
    const hf = f.horaFin < 10 ? `0${f.horaFin}` : f.horaFin;
    return `${f.dia} ${hi}–${hf}`;
  }
}
