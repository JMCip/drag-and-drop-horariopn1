import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { Carrera, ETIQUETAS_CARRERA } from '../../models/carrera.enum';
import { DIAS_MATERIA, MateriaDTO } from '../../models/materia.model';
import { FranjaHoraria } from '../../models/seccion.model';
import { ProfesorDTO } from '../../models/usuario.model';
import { MateriaService } from '../../services/materia.service';
import { ProfesorService } from '../../services/profesor.service';

/**
 * Pestaña "Materias" del administrador: crear materias (grupos) con su horario, listarlas y asignarles profesor.
 */
@Component({
  selector: 'app-materias-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './materias-admin.html',
  styleUrls: ['../registro-usuarios/registro-usuarios.css', '../dashboard-admin/dashboard-admin.css', './materias-admin.css']
})
export class MateriasAdmin implements OnInit {
  private materiaService = inject(MateriaService);
  private profesorService = inject(ProfesorService);
  private cdr = inject(ChangeDetectorRef);

  /**
   * Carreras para el desplegable (valor y nombre legible).
   */
  carreras = Object.values(Carrera).map(valor => ({ valor, etiqueta: ETIQUETAS_CARRERA[valor] }));
  /**
   * Semestres disponibles: del 1 al 10.
   */
  semestres = Array.from({ length: 10 }, (_, i) => i + 1);
  /**
   * Horas de inicio permitidas. Se omite la 13 por la hora de almuerzo.
   */
  horasInicio = [7, 8, 9, 10, 11, 12, 14, 15, 16, 17, 18, 19, 20, 21];
  /**
   * Horas de fin permitidas. Se omite la 14 porque una clase no puede cruzar el almuerzo (13 a 14).
   */
  horasFin = [8, 9, 10, 11, 12, 13, 15, 16, 17, 18, 19, 20, 21, 22];

  /**
   * Nombre de la materia que se está creando.
   */
  nombreMateria = '';
  /**
   * Carrera elegida para la materia nueva.
   */
  carrera = Carrera.ING_SISTEMAS;
  /**
   * Semestre elegido para la materia nueva.
   */
  semestre = 1;
  /**
   * Número de grupo de la materia nueva.
   */
  grupo = 1;
  /**
   * Horario de la materia nueva: una fila por día con hora de inicio y de fin (o vacío si ese día no hay clase).
   */
  horario = this.horarioVacio();

  /**
   * Materias existentes.
   */
  materias: MateriaDTO[] = [];
  /**
   * Profesores disponibles para asignar.
   */
  profesores: ProfesorDTO[] = [];
  /**
   * Profesor elegido en el desplegable de cada materia, indexado por id de materia.
   */
  profesorElegido: Record<number, number | null> = {};

  /**
   * Mensaje de éxito devuelto por el back.
   */
  mensajeExito = '';
  /**
   * Mensaje de error a mostrar.
   */
  mensajeError = '';

  /**
   * Al abrir la pestaña carga materias y profesores.
   */
  ngOnInit(): void {
    this.cargar();
  }

  /**
   * @param materia materia a consultar
   * @returns el nombre del profesor asignado, o `undefined` si no tiene
   */
  nombreDocente(materia: MateriaDTO): string | undefined {
    return this.profesores.find(p => p.id === materia.idProfesorAsignado)?.nombre;
  }

  /**
   * @param materia materia a consultar
   * @returns el horario como texto legible
   */
  horarioDe(materia: MateriaDTO): string {
    return this.materiaService.formatearHorario(materia);
  }

  /**
   * Valida el formulario (nombre, y inicio y fin completos en cada día con clase, al menos un día) y crea
   * la materia. Si sale bien, limpia el formulario y recarga la lista; si no, muestra el mensaje del back.
   */
  crearMateria(): void {
    this.limpiarMensajes();
    const diaIncompleto = this.horario.some(h => (h.inicio === null) !== (h.fin === null));
    const franjas: FranjaHoraria[] = this.horario
      .filter(h => h.inicio !== null && h.fin !== null)
      .map(h => ({ dia: h.dia, horaInicio: h.inicio!, horaFin: h.fin! }));

    if (!this.nombreMateria.trim() || diaIncompleto || franjas.length === 0) {
      this.mensajeError = 'Escribe el nombre y completa el inicio y el fin de cada día con clase.';
      return;
    }

    this.materiaService.create({
      ...this.materiaService.deFranjas(franjas),
      nombreMateria: this.nombreMateria.trim(),
      carrera: this.carrera,
      semestre: this.semestre,
      grupo: this.grupo,
      listaIdEstudiantes: []
    }).subscribe({
      next: (mensaje) => {
        this.mensajeExito = mensaje;
        this.nombreMateria = '';
        this.horario = this.horarioVacio();
        this.cargar();
      },
      error: (err) => this.mostrarError(err)
    });
  }

  /**
   * Asigna a la materia el profesor elegido en su desplegable. Muestra el mensaje del back.
   *
   * @param materia materia a la que se asigna el profesor
   */
  asignarProfesor(materia: MateriaDTO): void {
    this.limpiarMensajes();
    const idProfesor = this.profesorElegido[materia.id ?? 0];
    if (!idProfesor || materia.id === undefined) {
      this.mensajeError = 'Elige un profesor.';
      return;
    }

    this.materiaService.asignarProfesor(idProfesor, materia.id).subscribe({
      next: (mensaje) => {
        this.mensajeExito = mensaje;
        this.cargar();
      },
      error: (err) => this.mostrarError(err)
    });
  }

  /**
   * Recarga materias y profesores desde el back.
   */
  private cargar(): void {
    forkJoin([this.materiaService.getAll(), this.profesorService.getAll()]).subscribe(([materias, profesores]) => {
      this.materias = materias;
      this.profesores = profesores;
      this.cdr.markForCheck();
    });
  }

  /**
   * @returns un horario nuevo con los 5 días sin hora de inicio ni de fin
   */
  private horarioVacio() {
    return DIAS_MATERIA.map(dia => ({ dia, inicio: null as number | null, fin: null as number | null }));
  }

  /**
   * Borra los mensajes de éxito y de error.
   */
  private limpiarMensajes(): void {
    this.mensajeExito = '';
    this.mensajeError = '';
  }

  /**
   * Muestra el mensaje de error que envió el back, o uno genérico si no hay.
   *
   * @param err error HTTP recibido
   */
  private mostrarError(err: { error?: unknown }): void {
    this.mensajeError = typeof err.error === 'string' && err.error ? err.error : 'No se pudo completar la operación.';
    this.cdr.markForCheck();
  }
}
