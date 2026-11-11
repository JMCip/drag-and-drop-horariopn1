import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HorarioInscrito } from './horario-inscrito';

describe('HorarioInscrito', () => {
  let component: HorarioInscrito;
  let fixture: ComponentFixture<HorarioInscrito>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HorarioInscrito],
    }).compileComponents();

    fixture = TestBed.createComponent(HorarioInscrito);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('genera el PDF con la tabla de materias y el horario semanal', async () => {
    component.horarioInscrito = [
      { id: 1, codigoMateria: 'MAT-1', nombreMateria: 'Bases de datos 1', grupo: 1, docente: 'Diego Pérez', semestre: 4, carrera: 'Ingeniería de Sistemas',
        franjas: [{ dia: 'Lun', horaInicio: 7, horaFin: 9 }, { dia: 'Mié', horaInicio: 9, horaFin: 11 }] },
      { id: 2, codigoMateria: 'MAT-2', nombreMateria: 'Matemáticas 1', grupo: 2, docente: 'Sin asignar', semestre: 1, carrera: 'Ingeniería de Sistemas',
        franjas: [{ dia: 'Jue', horaInicio: 14, horaFin: 16 }] }
    ];
    const doc = await component.crearPDF();

    expect(doc.getNumberOfPages()).toBe(2);
    const contenido = doc.output();
    expect(contenido).toContain('Bases de datos 1');
    expect(contenido).toContain('Horario semanal');
    expect(contenido).toContain('Almuerzo');
  });
});
