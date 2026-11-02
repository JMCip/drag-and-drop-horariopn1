import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { forkJoin } from 'rxjs';
import { ETIQUETAS_CARRERA } from '../../models/carrera.enum';
import { AuthService } from '../../services/auth.service';
import { EstudianteService } from '../../services/estudiante.service';
import { MateriaService } from '../../services/materia.service';

interface GrupoDictado {
  numero: number;
  horario: string;
  estudiantes: { nombre: string; correo: string; carrera: string }[];
}

interface MateriaDictada {
  codigo: string;
  nombre: string;
  abierto: boolean;
  grupos: GrupoDictado[];
}

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

  materiasDictadas: MateriaDictada[] = [];

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
