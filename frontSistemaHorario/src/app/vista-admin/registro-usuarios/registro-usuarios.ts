import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Carrera, ETIQUETAS_CARRERA } from '../../models/carrera.enum';
import { EstudianteService } from '../../services/estudiante.service';
import { ProfesorService } from '../../services/profesor.service';

/**
 * Pestaña "Registrar usuario" del administrador: crea estudiantes y profesores. Valida en pantalla el correo
 * institucional y que la contraseña sea segura antes de enviar (el back vuelve a validar).
 */
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

  /**
   * Tipo de usuario que se va a crear.
   */
  tipoNuevoUsuario: 'ESTUDIANTE' | 'PROFESOR' = 'ESTUDIANTE';
  /**
   * Nombre del usuario nuevo.
   */
  nombreNuevo = '';
  /**
   * Correo del usuario nuevo.
   */
  correoNuevo = '';
  /**
   * Contraseña del usuario nuevo.
   */
  passNueva = '';
  /**
   * Carrera elegida (solo aplica a estudiantes).
   */
  carreraNueva = Carrera.ING_SISTEMAS;
  /**
   * Mensaje de éxito devuelto por el back.
   */
  mensajeRegistroExito = '';
  /**
   * Mensaje de error a mostrar.
   */
  mensajeRegistroError = '';

  /**
   * Carreras para el desplegable (valor y nombre legible).
   */
  carrerasElBosque = Object.values(Carrera).map(valor => ({ valor, etiqueta: ETIQUETAS_CARRERA[valor] }));

  /**
   * El correo debe terminar en `@unbosque.edu.co` o `@gmail.com`.
   */
  get correoEsValido(): boolean {
    return /^[^\s@]+@(unbosque\.edu\.co|gmail\.com)$/i.test(this.correoNuevo.trim());
  }
  /**
   * La contraseña debe tener al menos 8 caracteres.
   */
  get passLongitudOk(): boolean { return this.passNueva.length >= 8; }
  /**
   * La contraseña debe tener al menos una letra y un número.
   */
  get passTieneLetraYNumero(): boolean { return /[a-zA-Z]/.test(this.passNueva) && /\d/.test(this.passNueva); }
  /**
   * La contraseña debe tener al menos un símbolo (carácter que no sea letra ni número).
   */
  get passTieneSimbolo(): boolean { return /[^a-zA-Z0-9]/.test(this.passNueva); }

  /**
   * Comprueba los requisitos y crea un estudiante o un profesor según el tipo elegido.
   * Si sale bien, limpia los campos; si no, muestra el mensaje del back.
   */
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
