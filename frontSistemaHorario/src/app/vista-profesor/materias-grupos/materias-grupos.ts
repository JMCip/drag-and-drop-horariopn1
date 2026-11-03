import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { forkJoin } from 'rxjs';
import { ETIQUETAS_CARRERA } from '../../models/carrera.enum';
import { AuthService } from '../../services/auth.service';
import { EstudianteService } from '../../services/estudiante.service';
import { MateriaService } from '../../services/materia.service';

/**
 * Un grupo que dicta el profesor, con su horario y los estudiantes inscritos.
 */
interface GrupoDictado {
  numero: number;
  horario: string;
  estudiantes: { nombre: string; correo: string; carrera: string }[];
}

/**
 * Una materia que dicta el profesor, con sus grupos. `abierto` indica si el listado está desplegado.
 */
interface MateriaDictada {
  codigo: string;
  nombre: string;
  abierto: boolean;
  grupos: GrupoDictado[];
}

/**
 * Pestaña "Mis materias y grupos" del profesor: muestra cada materia que dicta, su horario y la lista de
 * estudiantes inscritos con su correo y carrera.
 */
@Component({
  selector: 'app-materias-grupos',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './materias-grupos.html',
  styleUrl: './materias-grupos.css'
})
export class MateriasGrupos implements OnInit {
  private authService = inject(AuthService);
  private materiaService = inject(MateriaService);
  private estudianteService = inject(EstudianteService);
  private cdr = inject(ChangeDetectorRef);

  /**
   * Materias que dicta el profesor con sesión iniciada.
   */
  materiasDictadas: MateriaDictada[] = [];

  /**
   * Carga materias y estudiantes en paralelo, filtra las materias del profesor y relaciona a cada una con sus
   * estudiantes mediante los ids de inscritos. La primera materia queda desplegada.
   */
  ngOnInit(): void {
    const idProfesor = this.authService.obtenerUsuarioActual().id;
    forkJoin([this.materiaService.getAll(), this.estudianteService.getAll()]).subscribe(([materias, estudiantes]) => {
      this.materiasDictadas = materias
        .filter(m => m.idProfesorAsignado === idProfesor)
        .map((m, i) => ({
          codigo: `MAT-${m.id}`,
          nombre: m.nombreMateria,
          abierto: i === 0,
          grupos: [{
            numero: m.grupo,
            horario: this.materiaService.formatearHorario(m),
            estudiantes: estudiantes
              .filter(e => m.listaIdEstudiantes.includes(e.id ?? 0))
              .map(e => ({ nombre: e.nombre, correo: e.correo, carrera: e.carrera ? ETIQUETAS_CARRERA[e.carrera] : 'Sin carrera' }))
          }]
        }));
      this.cdr.markForCheck();
    });
  }
}
