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

  carreras = Object.values(Carrera).map(valor => ({ valor, etiqueta: ETIQUETAS_CARRERA[valor] }));
  semestres = Array.from({ length: 10 }, (_, i) => i + 1);
  horasInicio = [7, 8, 9, 10, 11, 12, 14, 15, 16, 17, 18, 19, 20, 21];
  horasFin = [8, 9, 10, 11, 12, 13, 15, 16, 17, 18, 19, 20, 21, 22];

  nombreMateria = '';
  carrera = Carrera.ING_SISTEMAS;
  semestre = 1;
  grupo = 1;
  horario = this.horarioVacio();

  materias: MateriaDTO[] = [];
  profesores: ProfesorDTO[] = [];
  profesorElegido: Record<number, number | null> = {};

  mensajeExito = '';
  mensajeError = '';

  ngOnInit(): void {
    this.cargar();
  }

  nombreDocente(materia: MateriaDTO): string | undefined {
    return this.profesores.find(p => p.id === materia.idProfesorAsignado)?.nombre;
  }

  horarioDe(materia: MateriaDTO): string {
    return this.materiaService.formatearHorario(materia);
  }

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
