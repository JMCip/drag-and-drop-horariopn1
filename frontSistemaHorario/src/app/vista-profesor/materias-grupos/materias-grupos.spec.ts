import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MateriasGrupos } from './materias-grupos';

describe('MateriasGrupos', () => {
  let component: MateriasGrupos;
  let fixture: ComponentFixture<MateriasGrupos>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MateriasGrupos],
    }).compileComponents();

    fixture = TestBed.createComponent(MateriasGrupos);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
