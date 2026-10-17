import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-reubicaciones-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reubicaciones-admin.html',
  styleUrl: './reubicaciones-admin.css'
})
export class ReubicacionesAdmin {
  estudianteSeleccionado = 'jcipagauta@unbosque.edu.co';
  grupoDestinoEstudiante = '2';
  resultadoReubicacion: { exito: boolean; mensaje: string } | null = null;

  moverEstudiante(): void {
    if (this.grupoDestinoEstudiante === '2') {
      this.resultadoReubicacion = { exito: true, mensaje: 'Estudiante movido a Bases de Datos – Grupo 2 sin choques.' };
    } else {
      this.resultadoReubicacion = { exito: false, mensaje: 'Error 409: Cruce con Cálculo II (Martes 07:00–09:00).' };
    }
  }
}
