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

  subPestanaListado: 'estudiantes' | 'profesores' | 'materias' = 'estudiantes';

  estudiantes: EstudianteDTO[] = [];
  profesores: ProfesorDTO[] = [];
  materias: MateriaDTO[] = [];
  totalAdmins = 0;

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

  get totalUsuarios(): number {
    return this.estudiantes.length + this.profesores.length + this.totalAdmins;
  }

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
