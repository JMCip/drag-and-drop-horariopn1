import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-registro-usuarios',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './registro-usuarios.html',
  styleUrl: './registro-usuarios.css'
})
export class RegistroUsuarios {
  tipoNuevoUsuario: 'ESTUDIANTE' | 'PROFESOR' = 'ESTUDIANTE';
  nombreNuevo = '';
  correoNuevo = '';
  passNueva = '';
  carreraNueva = 'Ingeniería de Sistemas';
  mensajeRegistroExito = '';
  mensajeRegistroError = '';

  carrerasElBosque: string[] = [
    'Ingeniería de Sistemas', 'Ingeniería Industrial', 'Bioingeniería',
    'Ingeniería Robótica', 'Medicina', 'Psicología',
    'Matemática y Ciencia de Datos', 'Estadística', 'Física'
  ];

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
    this.mensajeRegistroExito = `Usuario ${this.nombreNuevo} (${this.tipoNuevoUsuario}) registrado exitosamente.`;
    this.nombreNuevo = '';
    this.correoNuevo = '';
    this.passNueva = '';
  }
}
