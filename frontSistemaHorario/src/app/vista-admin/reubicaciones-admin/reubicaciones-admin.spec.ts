import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReubicacionesAdmin } from './reubicaciones-admin';

describe('ReubicacionesAdmin', () => {
  let component: ReubicacionesAdmin;
  let fixture: ComponentFixture<ReubicacionesAdmin>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReubicacionesAdmin],
    }).compileComponents();

    fixture = TestBed.createComponent(ReubicacionesAdmin);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
