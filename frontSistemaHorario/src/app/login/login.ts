import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { Role } from '../models/role.enum';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  private authService = inject(AuthService);
  private router = inject(Router);

  correo = '';
  contrasena = '';
  mostrarPass = false;
  cargando = false;
  mensajeError = '';

  iniciarSesion(): void {
    this.mensajeError = '';
    if (!this.correo.trim() || !this.contrasena.trim()) {
      this.mensajeError = 'Correo o contraseña incorrectos';
      return;
    }

    this.cargando = true;
    this.authService.iniciarSesion(this.correo, this.contrasena).subscribe({
      next: (res) => {
        this.cargando = false;
        if (res.rol === Role.ADMIN) {
          this.router.navigate(['/admin']);
        } else if (res.rol === Role.PROFESOR) {
          this.router.navigate(['/profesor']);
        } else {
          this.router.navigate(['/estudiante']);
        }
      },
      error: () => {
        this.cargando = false;
        this.mensajeError = 'Correo o contraseña incorrectos';
      }
    });
  }

  // Helper para probar rápido los 3 roles en la demo
  cargarCredencialesDemo(rol: 'estudiante' | 'profesor' | 'admin'): void {
    this.contrasena = '12345678';
    if (rol === 'estudiante') this.correo = 'jcipagauta@unbosque.edu.co';
    if (rol === 'profesor') this.correo = 'prof.perez@unbosque.edu.co';
    if (rol === 'admin') this.correo = 'admin.secretaria@unbosque.edu.co';
    this.iniciarSesion();
  }
}
