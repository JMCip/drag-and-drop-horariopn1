import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

/**
 * Componente raíz de la aplicación.
 * Solo contiene el `<router-outlet>`: según la URL, el router carga aquí la pantalla correspondiente
 * (login, vista del estudiante, del profesor o del administrador).
 */
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {}
