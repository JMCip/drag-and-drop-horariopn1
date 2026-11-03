import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { forkJoin } from 'rxjs';
import { MateriaDTO } from '../../models/materia.model';
import { EstudianteDTO, ProfesorDTO } from '../../models/usuario.model';
import { ETIQUETAS_CARRERA } from '../../models/carrera.enum';
import { AdminService } from '../../services/admin.service';
import { EstudianteService } from '../../services/estudiante.service';
import { MateriaService } from '../../services/materia.service';
import { ProfesorService } from '../../services/profesor.service';

/**
 * Pestaña "Panel" del administrador: totales de usuarios y listados de estudiantes, profesores y materias.
 */
@Component({
  selector: 'app-dashboard-admin',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard-admin.html',
  styleUrl: './dashboard-admin.css'
})
export class DashboardAdmin implements OnInit {
  private adminService = inject(AdminService);
  private estudianteService = inject(EstudianteService);
  private profesorService = inject(ProfesorService);
  private materiaService = inject(MateriaService);
  private cdr = inject(ChangeDetectorRef);

  /**
   * Listado que se muestra: estudiantes, profesores o materias.
   */
  subPestanaListado: 'estudiantes' | 'profesores' | 'materias' = 'estudiantes';

  /**
   * Estudiantes registrados.
   */
  estudiantes: EstudianteDTO[] = [];
  /**
   * Profesores registrados.
   */
  profesores: ProfesorDTO[] = [];
  /**
   * Materias (grupos) registradas.
   */
  materias: MateriaDTO[] = [];
  /**
   * Cantidad de administradores.
   */
  totalAdmins = 0;

  /**
   * Carga en paralelo estudiantes, profesores, materias y el total de administradores.
   */
  ngOnInit(): void {
    forkJoin({
      estudiantes: this.estudianteService.getAll(),
      profesores: this.profesorService.getAll(),
      materias: this.materiaService.getAll(),
      totalAdmins: this.adminService.count()
    }).subscribe(datos => {
      this.estudiantes = datos.estudiantes;
      this.profesores = datos.profesores;
      this.materias = datos.materias;
      this.totalAdmins = datos.totalAdmins;
      this.cdr.markForCheck();
    });
  }

  /**
   * Total de usuarios del sistema: estudiantes, profesores y administradores.
   */
  get totalUsuarios(): number {
    return this.estudiantes.length + this.profesores.length + this.totalAdmins;
  }

  /**
   * Filas del listado activo con tres textos por fila (nombre, segunda línea y detalle), según la sub pestaña elegida.
   */
  get filas(): { nombre: string; segunda: string; detalle: string }[] {
    if (this.subPestanaListado === 'estudiantes') {
      return this.estudiantes.map(e => ({
        nombre: e.nombre,
        segunda: e.correo,
        detalle: `${e.carrera ? ETIQUETAS_CARRERA[e.carrera] : 'Sin carrera'} · ${e.listaIdMateriasInscritas?.length ?? 0} materias inscritas`
      }));
    }
    if (this.subPestanaListado === 'profesores') {
      return this.profesores.map(p => ({
        nombre: p.nombre,
        segunda: p.correo,
        detalle: `Docente · ${p.listaIdMaterias?.length ?? 0} materias`
      }));
    }
    return this.materias.map(m => ({
      nombre: `${m.nombreMateria} (Grupo ${m.grupo})`,
      segunda: this.profesores.find(p => p.id === m.idProfesorAsignado)?.nombre ?? 'Sin asignar',
      detalle: `${ETIQUETAS_CARRERA[m.carrera] ?? m.carrera} · Semestre ${m.semestre} · ${m.listaIdEstudiantes?.length ?? 0} inscritos`
    }));
  }
}
