import { Component, Input, Output, EventEmitter, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

/**
 * Una pestaña de la barra superior: su identificador, el texto y un icono opcional (clase de Font Awesome).
 */
export interface PestanaNav {
  id: string;
  etiqueta: string;
  icono?: string;
}

/**
 * Barra superior compartida por las tres vistas. Muestra las pestañas que le pase la vista, el usuario
 * con sesión iniciada y el botón de cerrar sesión. No cambia de pantalla por sí sola: avisa a la vista padre
 * con el evento `cambiarPestana`.
 */
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

  /**
   * Pestañas a mostrar.
   */
  @Input() pestanas: PestanaNav[] = [];
  /**
   * Id de la pestaña seleccionada actualmente.
   */
  @Input() pestanaActiva: string = '';
  /**
   * Se emite con el id de la pestaña cuando el usuario elige una.
   */
  @Output() cambiarPestana = new EventEmitter<string>();
  /**
   * Datos del usuario con sesión iniciada (para mostrar su nombre y rol).
   */

  usuario = this.authService.obtenerUsuarioActual();

  /**
   * Avisa a la vista padre que se eligió una pestaña.
   *
   * @param id identificador de la pestaña elegida
   */
  seleccionar(id: string): void {
    this.cambiarPestana.emit(id);
  }

  /**
   * Cierra la sesión y vuelve al login.
   */
  cerrarSesion(): void {
    this.authService.cerrarSesion();
    this.router.navigate(['/login']);
  }
}
