import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Carrera, ETIQUETAS_CARRERA } from '../../models/carrera.enum';
import { EstudianteService } from '../../services/estudiante.service';
import { ProfesorService } from '../../services/profesor.service';

@Component({
  selector: 'app-registro-usuarios',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './registro-usuarios.html',
  styleUrl: './registro-usuarios.css'
})
export class RegistroUsuarios {
  private estudianteService = inject(EstudianteService);
  private profesorService = inject(ProfesorService);
  private cdr = inject(ChangeDetectorRef);

  tipoNuevoUsuario: 'ESTUDIANTE' | 'PROFESOR' = 'ESTUDIANTE';
  nombreNuevo = '';
  correoNuevo = '';
  passNueva = '';
  carreraNueva = Carrera.ING_SISTEMAS;
  mensajeRegistroExito = '';
  mensajeRegistroError = '';

  carrerasElBosque = Object.values(Carrera).map(valor => ({ valor, etiqueta: ETIQUETAS_CARRERA[valor] }));

  get correoEsValido(): boolean {
    return /^[^\s@]+@(unbosque\.edu\.co|gmail\.com)$/i.test(this.correoNuevo.trim());
  }
  get passLongitudOk(): boolean { return this.passNueva.length >= 8; }
  get passTieneLetraYNumero(): boolean { return /[a-zA-Z]/.test(this.passNueva) && /\d/.test(this.passNueva); }
  get passTieneSimbolo(): boolean { return /[^a-zA-Z0-9]/.test(this.passNueva); }

  registrarUsuario(): void {
    this.mensajeRegistroError = '';
    this.mensajeRegistroExito = '';
    if (!this.nombreNuevo.trim() || !this.correoEsValido || !this.passLongitudOk || !this.passTieneLetraYNumero || !this.passTieneSimbolo) {
      this.mensajeRegistroError = 'Verifica los requisitos de correo institucional y contraseña segura.';
      return;
    }

    const datos = { nombre: this.nombreNuevo.trim(), correo: this.correoNuevo.trim(), contrasenia: this.passNueva };
    const peticion = this.tipoNuevoUsuario === 'ESTUDIANTE'
      ? this.estudianteService.create({ ...datos, carrera: this.carreraNueva, listaIdMateriasInscritas: [] })
      : this.profesorService.create({ ...datos, listaIdMaterias: [] });

    peticion.subscribe({
      next: (mensaje) => {
        this.mensajeRegistroExito = mensaje;
        this.nombreNuevo = '';
        this.correoNuevo = '';
        this.passNueva = '';
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.mensajeRegistroError = typeof err.error === 'string' && err.error
          ? err.error
          : 'No se pudo registrar el usuario.';
        this.cdr.markForCheck();
      }
    });
  }
}
