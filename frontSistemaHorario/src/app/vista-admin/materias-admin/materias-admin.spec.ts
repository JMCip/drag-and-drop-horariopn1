import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { MateriasAdmin } from './materias-admin';

describe('MateriasAdmin', () => {
  let component: MateriasAdmin;
  let fixture: ComponentFixture<MateriasAdmin>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MateriasAdmin],
      providers: [provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(MateriasAdmin);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
