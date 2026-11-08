  import { ChangeDetectorRef, Component, Input, Output, EventEmitter, OnInit, OnDestroy, inject, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { SeccionModel } from '../../models/seccion.model';
import { HorarioService } from '../../services/horario.service';

/**
 * Acción registrada para poder deshacerla con Ctrl+Z.
 */
interface AccionHistorial {
  tipo: 'AGREGAR' | 'QUITAR';
  seccion: SeccionModel;
}

/**
 * Grilla semanal donde el estudiante arma su horario. Recibe las secciones arrastradas (evento `drop`
 * de HTML5) o agregadas con botón, valida cruces antes de ubicarlas, permite quitarlas y deshacer
 * (Ctrl+Z) y guarda el horario en el back con confirmación.
 * <p>
 * La grilla muestra de 7:00 a 21:00 de lunes a sábado; la hora de almuerzo (13:00) y el sábado desde las
 * 12:00 están bloqueadas.
 */
@Component({
  selector: 'app-cronograma-horario',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './cronograma-horario.html',
  styleUrl: './cronograma-horario.css'
})
export class CronogramaHorario implements OnInit, OnDestroy {
  private horarioService = inject(HorarioService);
  private cdr = inject(ChangeDetectorRef);
  /**
   * Agrupa las suscripciones para cancelarlas todas al destruir el componente.
   */
  private subs = new Subscription();

  /**
   * Secciones que están en la grilla.
   */
  @Input() seccionesUbicadas: SeccionModel[] = [];
  /**
   * Se emite con la lista nueva cada vez que se agrega o quita una sección.
   */
  @Output() seccionesUbicadasChange = new EventEmitter<SeccionModel[]>();
  /**
   * Se emite cuando el back confirma que el horario se guardó.
   */
  @Output() horarioGuardado = new EventEmitter<SeccionModel[]>();
  /**
   * Columnas de la grilla.
   */

  dias: ('Lun' | 'Mar' | 'Mié' | 'Jue' | 'Vie' | 'Sáb')[] = ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'];
  /**
   * Filas de la grilla: horas de inicio de 7 a 21.
   */
  horas: number[] = Array.from({ length: 15 }, (_, i) => i + 7);

  /**
   * Sección que se está arrastrando en este momento, o `null`.
   */
  seccionArrastrando: SeccionModel | null = null;
  /**
   * Id de la sección con la que chocó el último intento, para resaltarla en rojo un instante.
   */
  seccionConflictoId: number | null = null;
  /**
   * Sección que se intentó agregar y chocó; se dibuja un momento para que el estudiante vea dónde cae.
   */
  candidataEnConflicto: SeccionModel | null = null;

  /**
   * Historial de acciones para deshacer (la última acción es la que se revierte primero).
   */
  pilaDeshacer: AccionHistorial[] = [];
  /**
   * Mensaje flotante que se muestra al estudiante (éxito, error o información).
   */
  toast: { tipo: 'exito' | 'error' | 'info'; titulo: string; mensaje: string } | null = null;
  /**
   * Si es `true`, se muestra la ventana para confirmar el guardado.
   */
  mostrarModalConfirmar = false;
  /**
   * Indica que se está guardando el horario en el back.
   */
  guardando = false;

  /**
   * Se suscribe a la sección arrastrada desde la barra lateral y a las solicitudes de agregar con botón.
   */
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

  /**
   * Cancela las suscripciones para evitar fugas de memoria.
   */
  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }

  /**
   * Escucha el teclado en toda la ventana: Ctrl+Z (o Cmd+Z) deshace la última acción si hay alguna.
   *
   * @param event evento de teclado
   */
  @HostListener('window:keydown', ['$event'])
  manejarAtajoTeclado(event: KeyboardEvent): void {
    if ((event.ctrlKey || event.metaKey) && event.key.toLowerCase() === 'z' && this.pilaDeshacer.length > 0) {
      event.preventDefault();
      this.deshacer();
    }
  }

  /**
   * Indica si una celda no se puede usar: la hora de almuerzo (13:00) y el sábado desde las 12:00.
   *
   * @param dia día de la columna
   * @param hora hora de la fila
   * @returns `true` si está bloqueada
   */
  esCeldaBloqueada(dia: string, hora: number): boolean {
    return hora === 13 || (dia === 'Sáb' && hora >= 12);
  }

  /**
   * Indica si la celda cae dentro de la sección que se está arrastrando, para mostrar una vista previa.
   *
   * @param dia día de la columna
   * @param hora hora de la fila
   * @returns `true` si la celda hace parte de la vista previa
   */
  esFranjaPrevia(dia: string, hora: number): boolean {
    if (!this.seccionArrastrando) return false;
    return this.seccionArrastrando.franjas.some(f => f.dia === dia && hora >= f.horaInicio && hora < f.horaFin);
  }

  /**
   * Busca qué sección ocupa una celda. Si hay una candidata en conflicto, esa tiene prioridad para dibujarla.
   *
   * @param dia día de la columna
   * @param hora hora de la fila
   * @returns la sección que ocupa la celda, o `undefined` si está libre
   */
  obtenerSeccionEnCelda(dia: string, hora: number): SeccionModel | undefined {
    if (this.candidataEnConflicto) {
      const enCand = this.candidataEnConflicto.franjas.some(f => f.dia === dia && hora >= f.horaInicio && hora < f.horaFin);
      if (enCand) return this.candidataEnConflicto;
    }
    return this.seccionesUbicadas.find(s => s.franjas.some(f => f.dia === dia && hora >= f.horaInicio && hora < f.horaFin));
  }

  /**
   * Indica si la celda es la primera hora de un bloque de clase (donde se dibuja el nombre de la materia).
   *
   * @param seccion sección a revisar
   * @param dia día de la columna
   * @param hora hora de la fila
   * @returns `true` si ahí empieza un bloque
   */
  esInicioDeBloque(seccion: SeccionModel, dia: string, hora: number): boolean {
    return seccion.franjas.some(f => f.dia === dia && f.horaInicio === hora);
  }

  /**
   * Cuántas filas debe ocupar el bloque que empieza en esa celda.
   *
   * @param seccion sección a revisar
   * @param dia día de la columna
   * @param hora hora de inicio del bloque
   * @returns duración en horas (1 si no se encuentra la franja)
   */
  obtenerDuracionBloque(seccion: SeccionModel, dia: string, hora: number): number {
    const franja = seccion.franjas.find(f => f.dia === dia && f.horaInicio === hora);
    return franja ? (franja.horaFin - franja.horaInicio) : 1;
  }

  /**
   * Evento `dragover`: cancela el comportamiento por defecto del navegador, necesario para que la grilla acepte el `drop`.
   *
   * @param event evento de arrastre
   */
  permitirSoltar(event: DragEvent): void {
    event.preventDefault();
  }

  /**
   * Evento `drop`: cuando se suelta una sección sobre la grilla, intenta ubicarla.
   *
   * @param event evento de arrastre
   */
  soltarEnGrilla(event: DragEvent): void {
    event.preventDefault();
    if (this.seccionArrastrando) {
      this.intentarUbicarSeccion(this.seccionArrastrando);
      this.seccionArrastrando = null;
    }
  }

  /**
   * Valida el cruce de la sección con las ya ubicadas. Si no hay cruce, la agrega y lo registra para deshacer.
   * Si hay cruce, muestra un aviso de traslape total o parcial y resalta el choque durante 1,5 segundos.
   *
   * @param candidata sección que se quiere ubicar
   */
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
            this.cdr.markForCheck();
          }, 1500);
        }
        this.cdr.markForCheck();
      }
    });
  }

  /**
   * Quita una sección de la grilla y avisa al padre con la lista actualizada.
   *
   * @param seccion sección a quitar
   * @param registrarDeshacer si es `false`, no se guarda en el historial (se usa al deshacer una adición)
   */
  quitarSeccion(seccion: SeccionModel, registrarDeshacer = true): void {
    const nuevaLista = this.seccionesUbicadas.filter(s => s.id !== seccion.id);
    this.seccionesUbicadas = nuevaLista;
    this.seccionesUbicadasChange.emit(nuevaLista);
    if (registrarDeshacer) {
      this.pilaDeshacer.push({ tipo: 'QUITAR', seccion });
    }
  }

  /**
   * Revierte la última acción: si fue agregar, la quita; si fue quitar, la vuelve a poner.
   */
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

  /**
   * Guarda el horario en el back. Si sale bien, cierra la ventana y emite `horarioGuardado`;
   * si el back rechaza alguna inscripción, muestra su mensaje de error.
   */
  confirmarGuardado(): void {
    this.guardando = true;
    this.horarioService.guardarHorarioInscrito(this.seccionesUbicadas).subscribe({
      next: () => {
        this.guardando = false;
        this.mostrarModalConfirmar = false;
        this.horarioGuardado.emit(this.seccionesUbicadas);
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.guardando = false;
        this.mostrarModalConfirmar = false;
        this.mostrarToast('error', 'No se pudo inscribir', typeof err.error === 'string' && err.error ? err.error : 'Intenta de nuevo.');
        this.cdr.markForCheck();
      }
    });
  }

  /**
   * Muestra un mensaje flotante. Los de éxito e información desaparecen a los 4 segundos; los de error se quedan hasta que se cierren.
   *
   * @param tipo tipo de mensaje
   * @param titulo título del mensaje
   * @param mensaje texto del mensaje
   */
  mostrarToast(tipo: 'exito' | 'error' | 'info', titulo: string, mensaje: string): void {
    this.toast = { tipo, titulo, mensaje };
    if (tipo !== 'error') {
      setTimeout(() => {
        if (this.toast?.titulo === titulo) this.toast = null;
        this.cdr.markForCheck();
      }, 4000);
    }
  }
}
