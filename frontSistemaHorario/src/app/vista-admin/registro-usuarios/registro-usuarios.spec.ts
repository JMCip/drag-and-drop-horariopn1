import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RegistroUsuarios } from './registro-usuarios';

describe('RegistroUsuarios', () => {
  let component: RegistroUsuarios;
  let fixture: ComponentFixture<RegistroUsuarios>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegistroUsuarios],
    }).compileComponents();

    fixture = TestBed.createComponent(RegistroUsuarios);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
