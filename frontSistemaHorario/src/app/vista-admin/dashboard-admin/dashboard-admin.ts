import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
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
 * Desde aquí también se cambia el semestre de un estudiante y se eliminan estudiantes y profesores.
 */
@Component({
  selector: 'app-dashboard-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
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
   * Semestres que se pueden elegir: del 1 al 12.
   */
  semestres = Array.from({ length: 12 }, (_, i) => i + 1);
  /**
   * Semestre elegido en el desplegable de cada estudiante, indexado por id de estudiante.
   */
  semestreEditado: Record<number, number | null> = {};
  /**
   * Mensaje de resultado (éxito o error) de la última acción del panel; `null` si no hay.
   */
  mensaje: { exito: boolean; texto: string } | null = null;

  /**
   * Al abrir el panel carga los datos con `cargar`.
   */
  ngOnInit(): void {
    this.cargar();
  }

  /**
   * Carga en paralelo estudiantes, profesores, materias y el total de administradores, y prepara el semestre editable de cada estudiante.
   */
  private cargar(): void {
    forkJoin({
      estudiantes: this.estudianteService.getAll(),
      profesores: this.profesorService.getAll(),
      materias: this.materiaService.getAll(),
      totalAdmins: this.adminService.count()
    }).subscribe(datos => {
      this.estudiantes = datos.estudiantes;
      this.semestreEditado = Object.fromEntries(datos.estudiantes.map(e => [e.id ?? 0, e.semestre ?? null]));
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
   * @param estudiante estudiante a mostrar
   * @returns el nombre legible de su carrera, o "Sin carrera" 
   */
  etiquetaCarrera(estudiante: EstudianteDTO): string {
    return estudiante.carrera ? ETIQUETAS_CARRERA[estudiante.carrera] : 'Sin carrera';
  }

  /**
   * Guarda el semestre elegido para un estudiante. Si el back lo rechaza (por ejemplo, un estudiante sin carrera) muestra su mensaje.
   *
   * @param estudiante estudiante a modificar
   */
  guardarSemestre(estudiante: EstudianteDTO): void {
    const semestre = this.semestreEditado[estudiante.id ?? 0];
    if (estudiante.id === undefined || !semestre) return;

    this.estudianteService.updateById(estudiante.id, { ...estudiante, semestre, contrasenia: undefined }).subscribe({
      next: () => {
        estudiante.semestre = semestre;
        this.mensaje = { exito: true, texto: `Semestre de ${estudiante.nombre} actualizado a ${semestre}.` };
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.mensaje = {
          exito: false,
          texto: typeof err.error === 'string' && err.error ? err.error : 'No se pudo actualizar. Revisa que el estudiante tenga carrera.'
        };
        this.cdr.markForCheck();
      }
    });
  }

  /**
   * Elimina un estudiante después de pedir confirmación. También se borran sus inscripciones.
   *
   * @param estudiante estudiante a eliminar
   */
  eliminarEstudiante(estudiante: EstudianteDTO): void {
    if (estudiante.id === undefined || !confirm(`¿Eliminar al estudiante ${estudiante.nombre}? Se borrarán también sus inscripciones.`)) return;
    this.estudianteService.deleteById(estudiante.id).subscribe({
      next: (texto) => this.terminarEliminacion(texto),
      error: (err) => this.mostrarError(err, 'No se pudo eliminar el estudiante.')
    });
  }

  /**
   * Elimina un profesor después de pedir confirmación. Sus grupos quedan sin profesor asignado.
   *
   * @param profesor profesor a eliminar
   */
  eliminarProfesor(profesor: ProfesorDTO): void {
    if (profesor.id === undefined || !confirm(`¿Eliminar al profesor ${profesor.nombre}? Sus grupos quedarán sin profesor asignado.`)) return;
    this.profesorService.deleteById(profesor.id).subscribe({
      next: (texto) => this.terminarEliminacion(texto),
      error: (err) => this.mostrarError(err, 'No se pudo eliminar el profesor.')
    });
  }

  /**
   * Muestra el mensaje de éxito y recarga los datos.
   *
   * @param texto mensaje que respondió el back
   */
  private terminarEliminacion(texto: string): void {
    this.mensaje = { exito: true, texto };
    this.cargar();
  }

  /**
   * Muestra el mensaje de error que envió el back, o uno por defecto si no hay.
   *
   * @param err error HTTP recibido
   * @param porDefecto mensaje a usar si el back no envió texto
   */
  private mostrarError(err: { error?: unknown }, porDefecto: string): void {
    this.mensaje = { exito: false, texto: typeof err.error === 'string' && err.error ? err.error : porDefecto };
    this.cdr.markForCheck();
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
      detalle: `${this.materiaService.formatearPlanes(m.planes)} · ${m.listaIdEstudiantes?.length ?? 0} inscritos`
    }));
  }
}
