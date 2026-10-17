import { ComponentFixture, TestBed } from '@angular/core/testing';
import { VistaProfesor } from './vista-profesor';

describe('VistaProfesor', () => {
  let component: VistaProfesor;
  let fixture: ComponentFixture<VistaProfesor>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VistaProfesor],
    }).compileComponents();

    fixture = TestBed.createComponent(VistaProfesor);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
