import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { MateriaDTO } from '../../models/materia.model';
import { EstudianteDTO } from '../../models/usuario.model';
import { EstudianteService } from '../../services/estudiante.service';
import { MateriaService } from '../../services/materia.service';

/**
 * Pestaña "Reubicaciones" del administrador: mueve a un estudiante de un grupo a otro de la misma materia.
 */
@Component({
  selector: 'app-reubicaciones-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reubicaciones-admin.html',
  styleUrl: './reubicaciones-admin.css'
})
export class ReubicacionesAdmin implements OnInit {
  private estudianteService = inject(EstudianteService);
  private materiaService = inject(MateriaService);
  private cdr = inject(ChangeDetectorRef);

  /**
   * Estudiantes registrados.
   */
  estudiantes: EstudianteDTO[] = [];
  /**
   * Materias (grupos) registradas.
   */
  materias: MateriaDTO[] = [];

  /**
   * Estudiante elegido.
   */
  idEstudiante: number | null = null;
  /**
   * Grupo del que sale el estudiante.
   */
  idOrigen: number | null = null;
  /**
   * Grupo al que pasa el estudiante.
   */
  idDestino: number | null = null;
  /**
   * Resultado de la última reubicación, para mostrarlo al administrador.
   */
  resultadoReubicacion: { exito: boolean; mensaje: string } | null = null;

  /**
   * Al abrir la pestaña carga estudiantes y materias.
   */
  ngOnInit(): void {
    this.cargar();
  }

  /**
   * Materias en las que está inscrito el estudiante elegido (posibles grupos de origen).
   */
  get materiasDelEstudiante(): MateriaDTO[] {
    return this.materias.filter(m => m.listaIdEstudiantes.includes(this.idEstudiante ?? 0));
  }

  /**
   * Otros grupos de la misma asignatura que el grupo de origen (posibles destinos).
   */
  get gruposDestino(): MateriaDTO[] {
    const origen = this.materias.find(m => m.id === this.idOrigen);
    return this.materias.filter(m =>
      m.idAsignatura === origen?.idAsignatura && m.id !== origen.id
    );
  }

  /**
   * Texto para el desplegable, por ejemplo `Cálculo — Grupo 2 (Lun 08:00–10:00)`.
   *
   * @param materia grupo a describir
   * @returns la etiqueta como texto
   */
  etiquetaGrupo(materia: MateriaDTO): string {
    return `${materia.nombreMateria} — Grupo ${materia.grupo} (${this.materiaService.formatearHorario(materia)})`;
  }

  /**
   * Al cambiar de estudiante se limpian el origen, el destino y el resultado anterior.
   */
  cambiarEstudiante(): void {
    this.idOrigen = null;
    this.idDestino = null;
    this.resultadoReubicacion = null;
  }

  /**
   * Pide al back mover al estudiante entre los dos grupos. Muestra el mensaje de éxito o de error del back.
   */
  moverEstudiante(): void {
    if (this.idEstudiante === null || this.idOrigen === null || this.idDestino === null) {
      this.resultadoReubicacion = { exito: false, mensaje: 'Elige el estudiante, el grupo de origen y el de destino.' };
      return;
    }

    this.materiaService.moverEstudiante(this.idEstudiante, this.idOrigen, this.idDestino).subscribe({
      next: (mensaje) => {
        this.resultadoReubicacion = { exito: true, mensaje };
        this.idOrigen = null;
        this.idDestino = null;
        this.cargar();
      },
      error: (err) => {
        this.resultadoReubicacion = {
          exito: false,
          mensaje: typeof err.error === 'string' && err.error ? err.error : 'No se pudo mover al estudiante.'
        };
        this.cdr.markForCheck();
      }
    });
  }

  /**
   * Recarga estudiantes y materias desde el back.
   */
  private cargar(): void {
    forkJoin([this.estudianteService.getAll(), this.materiaService.getAll()]).subscribe(([estudiantes, materias]) => {
      this.estudiantes = estudiantes;
      this.materias = materias;
      this.cdr.markForCheck();
    });
  }
}
