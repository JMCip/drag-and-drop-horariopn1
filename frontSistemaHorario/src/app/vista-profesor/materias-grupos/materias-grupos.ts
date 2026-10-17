import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-materias-grupos',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './materias-grupos.html',
  styleUrl: './materias-grupos.css'
})
export class MateriasGrupos {
  materiasDictadas = [
    {
      codigo: 'BD-101',
      nombre: 'Bases de Datos',
      abierto: true,
      grupos: [
        {
          numero: 1,
          horario: 'Lun 07:00–09:00 · Mié 09:00–11:00',
          estudiantes: [
            { nombre: 'Jose Manuel Cipagauta', correo: 'jcipagauta@unbosque.edu.co', carrera: 'Ingeniería de Sistemas' },
            { nombre: 'Laura Valentina Mora', correo: 'lmora@unbosque.edu.co', carrera: 'Ingeniería de Sistemas' }
          ]
        }
      ]
    }
  ];
}
