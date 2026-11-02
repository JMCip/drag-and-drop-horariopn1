import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { MateriasGrupos } from './materias-grupos';

describe('MateriasGrupos', () => {
  let component: MateriasGrupos;
  let fixture: ComponentFixture<MateriasGrupos>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MateriasGrupos],
      providers: [provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(MateriasGrupos);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
