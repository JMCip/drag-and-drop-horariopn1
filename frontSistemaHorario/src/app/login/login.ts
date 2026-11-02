import { ChangeDetectorRef, Component, inject } from '@angular/core';
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
  private cdr = inject(ChangeDetectorRef);

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
      error: (err) => {
        this.cargando = false;
        this.mensajeError = err.status === 0
          ? 'No se pudo conectar con el servidor'
          : 'Correo o contraseña incorrectos';
        this.cdr.markForCheck();
      }
    });
  }
}
