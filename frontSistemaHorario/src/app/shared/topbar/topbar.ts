import { Component, Input, Output, EventEmitter, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

export interface PestanaNav {
  id: string;
  etiqueta: string;
  icono?: string;
}

@Component({
  selector: 'app-topbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './topbar.html',
  styleUrl: './topbar.css'
})
export class Topbar {
  private authService = inject(AuthService);
  private router = inject(Router);

  @Input() pestanas: PestanaNav[] = [];
  @Input() pestanaActiva: string = '';
  @Output() cambiarPestana = new EventEmitter<string>();

  usuario = this.authService.obtenerUsuarioActual();

  seleccionar(id: string): void {
    this.cambiarPestana.emit(id);
  }

  cerrarSesion(): void {
    this.authService.cerrarSesion();
    this.router.navigate(['/login']);
  }
}
