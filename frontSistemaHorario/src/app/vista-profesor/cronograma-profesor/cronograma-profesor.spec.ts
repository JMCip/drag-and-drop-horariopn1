import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CronogramaProfesor } from './cronograma-profesor';

describe('CronogramaProfesor', () => {
  let component: CronogramaProfesor;
  let fixture: ComponentFixture<CronogramaProfesor>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CronogramaProfesor],
    }).compileComponents();

    fixture = TestBed.createComponent(CronogramaProfesor);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
