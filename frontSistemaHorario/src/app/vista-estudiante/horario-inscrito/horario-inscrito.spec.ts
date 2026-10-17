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
});
