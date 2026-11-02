import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { MateriaDTO } from '../../models/materia.model';
import { EstudianteDTO } from '../../models/usuario.model';
import { EstudianteService } from '../../services/estudiante.service';
import { MateriaService } from '../../services/materia.service';

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

  estudiantes: EstudianteDTO[] = [];
  materias: MateriaDTO[] = [];

  idEstudiante: number | null = null;
  idOrigen: number | null = null;
  idDestino: number | null = null;
  resultadoReubicacion: { exito: boolean; mensaje: string } | null = null;

  ngOnInit(): void {
    this.cargar();
  }

  get materiasDelEstudiante(): MateriaDTO[] {
    return this.materias.filter(m => m.listaIdEstudiantes.includes(this.idEstudiante ?? 0));
  }

  get gruposDestino(): MateriaDTO[] {
    const origen = this.materias.find(m => m.id === this.idOrigen);
    return this.materias.filter(m =>
      m.nombreMateria === origen?.nombreMateria && m.id !== origen.id
    );
  }

  etiquetaGrupo(materia: MateriaDTO): string {
    return `${materia.nombreMateria} — Grupo ${materia.grupo} (${this.materiaService.formatearHorario(materia)})`;
  }

  cambiarEstudiante(): void {
    this.idOrigen = null;
    this.idDestino = null;
    this.resultadoReubicacion = null;
  }

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

  private cargar(): void {
    forkJoin([this.estudianteService.getAll(), this.materiaService.getAll()]).subscribe(([estudiantes, materias]) => {
      this.estudiantes = estudiantes;
      this.materias = materias;
      this.cdr.markForCheck();
    });
  }
}
