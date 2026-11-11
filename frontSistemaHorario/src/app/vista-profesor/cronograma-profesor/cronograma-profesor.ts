import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { MateriaService } from '../../services/materia.service';

/**
 * Pestaña "Mi horario" del profesor: lista los grupos que dicta con su horario.
 */
@Component({
  selector: 'app-cronograma-profesor',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './cronograma-profesor.html',
  styleUrl: './cronograma-profesor.css'
})
export class CronogramaProfesor implements OnInit {
  private authService = inject(AuthService);
  private materiaService = inject(MateriaService);
  private cdr = inject(ChangeDetectorRef);

  /**
   * Clases del profesor: nombre del grupo y su horario como texto.
   */
  clasesAsignadas: { materia: string; franja: string }[] = [];

  /**
   * Carga las materias y se queda con las que tienen asignado al profesor que inició sesión.
   */
  ngOnInit(): void {
    const idProfesor = this.authService.obtenerUsuarioActual().id;
    this.materiaService.getAll().subscribe(materias => {
      this.clasesAsignadas = materias
        .filter(m => m.idProfesorAsignado === idProfesor)
        .map(m => ({ materia: `${m.nombreMateria} (Grupo ${m.grupo})`, franja: this.materiaService.formatearHorario(m) }));
      this.cdr.markForCheck();
    });
  }
}
