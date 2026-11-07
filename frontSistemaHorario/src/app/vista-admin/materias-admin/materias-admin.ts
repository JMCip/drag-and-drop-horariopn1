import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { Carrera, ETIQUETAS_CARRERA } from '../../models/carrera.enum';
import { AsignaturaDTO, DIAS_MATERIA, MateriaDTO } from '../../models/materia.model';
import { FranjaHoraria } from '../../models/seccion.model';
import { ProfesorDTO } from '../../models/usuario.model';
import { AsignaturaService } from '../../services/asignatura.service';
import { MateriaService } from '../../services/materia.service';
import { ProfesorService } from '../../services/profesor.service';

@Component({
  selector: 'app-materias-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './materias-admin.html',
  styleUrls: ['../registro-usuarios/registro-usuarios.css', '../dashboard-admin/dashboard-admin.css', './materias-admin.css']
})
export class MateriasAdmin implements OnInit {
  private materiaService = inject(MateriaService);
  private asignaturaService = inject(AsignaturaService);
  private profesorService = inject(ProfesorService);
  private cdr = inject(ChangeDetectorRef);

  carreras = Object.values(Carrera).map(valor => ({ valor, etiqueta: ETIQUETAS_CARRERA[valor] }));
  horasInicio = [7, 8, 9, 10, 11, 12, 14, 15, 16, 17, 18, 19, 20, 21];
  horasFin = [8, 9, 10, 11, 12, 13, 15, 16, 17, 18, 19, 20, 21, 22];

  carrera = Carrera.ING_SISTEMAS;
  idAsignatura: number | null = null;
  grupo = 1;
  horario = this.horarioVacio();
  // Grupo que se esta editando; null cuando el formulario crea uno nuevo
  editando: MateriaDTO | null = null;

  asignaturas: AsignaturaDTO[] = [];
  cargandoCatalogo = true;
  // Se recalcula solo al cambiar la carrera: un getter con objetos nuevos rompe el select
  opcionesAsignatura: { id: number; etiqueta: string }[] = [];
  materias: MateriaDTO[] = [];
  profesores: ProfesorDTO[] = [];
  profesorElegido: Record<number, number | null> = {};

  mensajeExito = '';
  mensajeError = '';

  ngOnInit(): void {
    this.asignaturaService.getAll().subscribe({
      next: (asignaturas) => {
        this.asignaturas = asignaturas;
        this.opcionesAsignatura = this.calcularOpciones();
        this.cargandoCatalogo = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.cargandoCatalogo = false;
        this.mostrarError(err);
      }
    });
    this.cargar();
  }

  // Materias del catálogo de la carrera elegida, ordenadas por semestre
  private calcularOpciones(): { id: number; etiqueta: string }[] {
    return this.asignaturas
      .map(a => ({ a, plan: a.planes.find(p => p.carrera === this.carrera) }))
      .filter(x => x.plan !== undefined)
      .sort((x, y) => x.plan!.semestre - y.plan!.semestre || x.a.nombre.localeCompare(y.a.nombre))
      .map(x => ({ id: x.a.id, etiqueta: `${x.plan!.semestre}° · ${x.a.nombre}` }));
  }

  cambiarCarrera(): void {
    this.idAsignatura = null;
    this.opcionesAsignatura = this.calcularOpciones();
  }

  nombreDocente(materia: MateriaDTO): string | undefined {
    return this.profesores.find(p => p.id === materia.idProfesorAsignado)?.nombre;
  }

  horarioDe(materia: MateriaDTO): string {
    return this.materiaService.formatearHorario(materia);
  }

  carrerasDe(materia: MateriaDTO): string {
    return this.materiaService.formatearPlanes(materia.planes);
  }

  editarGrupo(materia: MateriaDTO): void {
    this.limpiarMensajes();
    this.editando = materia;
    this.carrera = materia.planes.find(p => p.carrera === this.carrera)?.carrera ?? materia.planes[0]?.carrera ?? this.carrera;
    this.opcionesAsignatura = this.calcularOpciones();
    this.idAsignatura = materia.idAsignatura;
    this.grupo = materia.grupo;
    this.horario = this.horarioVacio();
    for (const franja of this.materiaService.aFranjas(materia)) {
      const dia = this.horario.find(h => h.dia === franja.dia);
      if (dia) {
        dia.inicio = franja.horaInicio;
        dia.fin = franja.horaFin;
      }
    }
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  cancelarEdicion(): void {
    this.editando = null;
    this.idAsignatura = null;
    this.grupo = 1;
    this.horario = this.horarioVacio();
    this.limpiarMensajes();
  }

  quitarProfesor(materia: MateriaDTO): void {
    this.limpiarMensajes();
    if (materia.id === undefined || !confirm(`¿Quitar el profesor de ${materia.nombreMateria} (Grupo ${materia.grupo})?`)) return;
    this.materiaService.quitarProfesor(materia.id).subscribe({
      next: (mensaje) => {
        this.mensajeExito = mensaje;
        this.cargar();
      },
      error: (err) => this.mostrarError(err)
    });
  }

  crearMateria(): void {
    this.limpiarMensajes();
    const diaIncompleto = this.horario.some(h => (h.inicio === null) !== (h.fin === null));
    const franjas: FranjaHoraria[] = this.horario
      .filter(h => h.inicio !== null && h.fin !== null)
      .map(h => ({ dia: h.dia, horaInicio: h.inicio!, horaFin: h.fin! }));

    if (this.idAsignatura === null || diaIncompleto || franjas.length === 0) {
      this.mensajeError = 'Elige la materia y completa el inicio y el fin de cada día con clase.';
      return;
    }

    const datos = { ...this.materiaService.deFranjas(franjas), idAsignatura: this.idAsignatura, grupo: this.grupo };
    const peticion = this.editando?.id !== undefined
      ? this.materiaService.updateById(this.editando.id, datos)
      : this.materiaService.create(datos);

    peticion.subscribe({
      next: (mensaje) => {
        this.mensajeExito = mensaje;
        this.editando = null;
        this.horario = this.horarioVacio();
        this.cargar();
      },
      error: (err) => this.mostrarError(err)
    });
  }

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

  eliminarGrupo(materia: MateriaDTO): void {
    this.limpiarMensajes();
    if (materia.id === undefined || !confirm(`¿Eliminar ${materia.nombreMateria} (Grupo ${materia.grupo})? Se borrarán también sus inscripciones.`)) return;
    this.materiaService.deleteById(materia.id).subscribe({
      next: (mensaje) => {
        this.mensajeExito = mensaje;
        this.cargar();
      },
      error: (err) => this.mostrarError(err)
    });
  }

  private cargar(): void {
    forkJoin([this.materiaService.getAll(), this.profesorService.getAll()]).subscribe(([materias, profesores]) => {
      this.materias = materias;
      this.profesores = profesores;
      this.cdr.markForCheck();
    });
  }

  private horarioVacio() {
    return DIAS_MATERIA.map(dia => ({ dia, inicio: null as number | null, fin: null as number | null }));
  }

  private limpiarMensajes(): void {
    this.mensajeExito = '';
    this.mensajeError = '';
  }

  private mostrarError(err: { error?: unknown }): void {
    this.mensajeError = typeof err.error === 'string' && err.error ? err.error : 'No se pudo completar la operación.';
    this.cdr.markForCheck();
  }
}
