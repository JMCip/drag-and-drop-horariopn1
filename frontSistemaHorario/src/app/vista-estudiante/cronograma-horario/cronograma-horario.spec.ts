import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CronogramaHorario } from './cronograma-horario';

describe('CronogramaHorario', () => {
  let component: CronogramaHorario;
  let fixture: ComponentFixture<CronogramaHorario>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CronogramaHorario],
    }).compileComponents();

    fixture = TestBed.createComponent(CronogramaHorario);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
