import { Component, Input, Output, EventEmitter, OnInit, OnDestroy, inject, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { SeccionModel } from '../../models/seccion.model';
import { HorarioService } from '../../services/horario.service';

interface AccionHistorial {
  tipo: 'AGREGAR' | 'QUITAR';
  seccion: SeccionModel;
}

@Component({
  selector: 'app-cronograma-horario',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './cronograma-horario.html',
  styleUrl: './cronograma-horario.css'
})
export class CronogramaHorario implements OnInit, OnDestroy {
  private horarioService = inject(HorarioService);
  private subs = new Subscription();

  @Input() seccionesUbicadas: SeccionModel[] = [];
  @Output() seccionesUbicadasChange = new EventEmitter<SeccionModel[]>();
  @Output() horarioGuardado = new EventEmitter<SeccionModel[]>();

  dias: ('Lun' | 'Mar' | 'Mié' | 'Jue' | 'Vie' | 'Sáb')[] = ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'];
  horas: number[] = Array.from({ length: 15 }, (_, i) => i + 7);

  seccionArrastrando: SeccionModel | null = null;
  seccionConflictoId: number | null = null;
  candidataEnConflicto: SeccionModel | null = null;

  pilaDeshacer: AccionHistorial[] = [];
  toast: { tipo: 'exito' | 'error' | 'info'; titulo: string; mensaje: string } | null = null;
  mostrarModalConfirmar = false;
  guardando = false;

  ngOnInit(): void {
    this.subs.add(
      this.horarioService.seccionArrastrada$.subscribe(sec => {
        this.seccionArrastrando = sec;
      })
    );
    this.subs.add(
      this.horarioService.solicitudAgregar$.subscribe(sec => {
        this.intentarUbicarSeccion(sec);
      })
    );
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }

  @HostListener('window:keydown', ['$event'])
  manejarAtajoTeclado(event: KeyboardEvent): void {
    if ((event.ctrlKey || event.metaKey) && event.key.toLowerCase() === 'z' && this.pilaDeshacer.length > 0) {
      event.preventDefault();
      this.deshacer();
    }
  }

  esCeldaBloqueada(dia: string, hora: number): boolean {
    return hora === 13 || (dia === 'Sáb' && hora >= 12);
  }

  esFranjaPrevia(dia: string, hora: number): boolean {
    if (!this.seccionArrastrando) return false;
    return this.seccionArrastrando.franjas.some(f => f.dia === dia && hora >= f.horaInicio && hora < f.horaFin);
  }

  obtenerSeccionEnCelda(dia: string, hora: number): SeccionModel | undefined {
    if (this.candidataEnConflicto) {
      const enCand = this.candidataEnConflicto.franjas.some(f => f.dia === dia && hora >= f.horaInicio && hora < f.horaFin);
      if (enCand) return this.candidataEnConflicto;
    }
    return this.seccionesUbicadas.find(s => s.franjas.some(f => f.dia === dia && hora >= f.horaInicio && hora < f.horaFin));
  }

  esInicioDeBloque(seccion: SeccionModel, dia: string, hora: number): boolean {
    return seccion.franjas.some(f => f.dia === dia && f.horaInicio === hora);
  }

  obtenerDuracionBloque(seccion: SeccionModel, dia: string, hora: number): number {
    const franja = seccion.franjas.find(f => f.dia === dia && f.horaInicio === hora);
    return franja ? (franja.horaFin - franja.horaInicio) : 1;
  }

  permitirSoltar(event: DragEvent): void {
    event.preventDefault();
  }

  soltarEnGrilla(event: DragEvent): void {
    event.preventDefault();
    if (this.seccionArrastrando) {
      this.intentarUbicarSeccion(this.seccionArrastrando);
      this.seccionArrastrando = null;
    }
  }

  intentarUbicarSeccion(candidata: SeccionModel): void {
    this.horarioService.validarChoque(candidata, this.seccionesUbicadas).subscribe({
      next: (res) => {
        if (res.tipoTraslape === 'NINGUNO') {
          const nuevaLista = [...this.seccionesUbicadas, candidata];
          this.seccionesUbicadas = nuevaLista;
          this.seccionesUbicadasChange.emit(nuevaLista);
          this.pilaDeshacer.push({ tipo: 'AGREGAR', seccion: candidata });
        } else {
          this.seccionConflictoId = res.seccionConflictoId ?? null;
          this.candidataEnConflicto = candidata;
          const etiquetaTipo = res.tipoTraslape === 'TOTAL' ? 'Traslape total' : 'Traslape parcial';
          this.mostrarToast('error', etiquetaTipo, `con ${res.materiaConflicto} — ${res.detalleFranja}`);

          setTimeout(() => {
            this.seccionConflictoId = null;
            this.candidataEnConflicto = null;
          }, 1500);
        }
      }
    });
  }

  quitarSeccion(seccion: SeccionModel, registrarDeshacer = true): void {
    const nuevaLista = this.seccionesUbicadas.filter(s => s.id !== seccion.id);
    this.seccionesUbicadas = nuevaLista;
    this.seccionesUbicadasChange.emit(nuevaLista);
    if (registrarDeshacer) {
      this.pilaDeshacer.push({ tipo: 'QUITAR', seccion });
    }
  }

  deshacer(): void {
    const ultima = this.pilaDeshacer.pop();
    if (!ultima) return;
    if (ultima.tipo === 'AGREGAR') {
      this.quitarSeccion(ultima.seccion, false);
    } else {
      const nuevaLista = [...this.seccionesUbicadas, ultima.seccion];
      this.seccionesUbicadas = nuevaLista;
      this.seccionesUbicadasChange.emit(nuevaLista);
    }
    this.mostrarToast('info', 'Acción deshecha', `Se revirtió el cambio en ${ultima.seccion.nombreMateria}`);
  }

  confirmarGuardado(): void {
    this.guardando = true;
    this.horarioService.guardarHorarioInscrito(this.seccionesUbicadas).subscribe(() => {
      this.guardando = false;
      this.mostrarModalConfirmar = false;
      this.horarioGuardado.emit(this.seccionesUbicadas);
    });
  }

  mostrarToast(tipo: 'exito' | 'error' | 'info', titulo: string, mensaje: string): void {
    this.toast = { tipo, titulo, mensaje };
    if (tipo !== 'error') {
      setTimeout(() => {
        if (this.toast?.titulo === titulo) this.toast = null;
      }, 4000);
    }
  }
}
