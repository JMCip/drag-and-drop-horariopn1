import { Component, Input, Output, EventEmitter, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SeccionModel, FranjaHoraria } from '../../models/seccion.model';
import { DIAS_MATERIA } from '../../models/materia.model';
import { AuthService } from '../../services/auth.service';

const HORAS_GRILLA = Array.from({ length: 15 }, (_, i) => i + 7);
const COLORES_PDF: [number, number, number][] = [[214, 234, 248], [213, 245, 227], [252, 243, 207], [250, 219, 216], [232, 218, 239], [209, 242, 235]];

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

  private authService = inject(AuthService);
  generandoPDF = false;

  descargarJSON(): void {
    const dataStr = 'data:text/json;charset=utf-8,' + encodeURIComponent(JSON.stringify(this.horarioInscrito, null, 2));
    const anchor = document.createElement('a');
    anchor.setAttribute('href', dataStr);
    anchor.setAttribute('download', 'mi_horario_el_bosque.json');
    anchor.click();
  }

  async descargarPDF(): Promise<void> {
    this.generandoPDF = true;
    try {
      const doc = await this.crearPDF();
      doc.save('mi_horario_el_bosque.pdf');
    } finally {
      this.generandoPDF = false;
    }
  }

  // Las librerias se cargan solo al pulsar el boton, para no pesar en la carga inicial
  async crearPDF() {
    const [{ jsPDF }, { autoTable }] = await Promise.all([import('jspdf'), import('jspdf-autotable')]);
    const doc = new jsPDF({ orientation: 'landscape', unit: 'mm', format: 'a4' });
    const usuario = this.authService.obtenerUsuarioActual();

    doc.setFontSize(16);
    doc.text('Horario inscrito - Universidad El Bosque', 14, 16);
    doc.setFontSize(10);
    doc.text(`Estudiante: ${usuario.nombre} (${usuario.correo})`, 14, 23);
    doc.text(`Generado: ${new Date().toLocaleString('es-CO')}`, 14, 28);

    autoTable(doc, {
      startY: 33,
      head: [['Código', 'Materia', 'Grupo', 'Docente', 'Horario']],
      body: this.horarioInscrito.map(s => [
        s.codigoMateria, s.nombreMateria, `Grupo ${s.grupo}`, s.docente, s.franjas.map(f => this.formatearFranja(f)).join(', ')
      ]),
      styles: { fontSize: 9 },
      headStyles: { fillColor: [33, 37, 41] }
    });

    const color = new Map(this.horarioInscrito.map((s, i) => [s.id, COLORES_PDF[i % COLORES_PDF.length]]));
    const ocupadas = new Set<string>();
    const filas = HORAS_GRILLA.map(hora => {
      const fila: unknown[] = [`${this.dosDigitos(hora)}:00 - ${this.dosDigitos(hora + 1)}:00`];
      for (const dia of DIAS_MATERIA) {
        if (ocupadas.has(`${dia}-${hora}`)) continue;
        const bloque = this.horarioInscrito
          .flatMap(s => s.franjas.filter(f => f.dia === dia && f.horaInicio === hora).map(f => ({ s, f })))[0];
        if (bloque) {
          const duracion = bloque.f.horaFin - bloque.f.horaInicio;
          for (let h = hora + 1; h < bloque.f.horaFin; h++) ocupadas.add(`${dia}-${h}`);
          fila.push({ content: `${bloque.s.nombreMateria}
Grupo ${bloque.s.grupo}`, rowSpan: duracion, styles: { fillColor: color.get(bloque.s.id), valign: 'middle', halign: 'center' } });
        } else {
          fila.push(hora === 13 ? { content: 'Almuerzo', styles: { textColor: [150, 150, 150], halign: 'center' } } : '');
        }
      }
      return fila;
    });

    doc.addPage();
    doc.setFontSize(14);
    doc.text('Horario semanal', 14, 16);
    autoTable(doc, {
      startY: 21,
      head: [['Hora', ...DIAS_MATERIA]],
      body: filas as never,
      theme: 'grid',
      styles: { fontSize: 8, cellPadding: 1.5, minCellHeight: 8 },
      headStyles: { fillColor: [33, 37, 41], halign: 'center' },
      columnStyles: { 0: { cellWidth: 28 } }
    });
    return doc;
  }

  private dosDigitos(h: number): string {
    return h < 10 ? `0${h}` : `${h}`;
  }

  formatearFranja(f: FranjaHoraria): string {
    const hi = f.horaInicio < 10 ? `0${f.horaInicio}` : f.horaInicio;
    const hf = f.horaFin < 10 ? `0${f.horaFin}` : f.horaFin;
    return `${f.dia} ${hi}–${hf}`;
  }
}
