import { ComponentFixture, TestBed } from '@angular/core/testing';
import { VistaEstudiante } from './vista-estudiante';

describe('VistaEstudiante', () => {
  let component: VistaEstudiante;
  let fixture: ComponentFixture<VistaEstudiante>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VistaEstudiante],
    }).compileComponents();

    fixture = TestBed.createComponent(VistaEstudiante);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
