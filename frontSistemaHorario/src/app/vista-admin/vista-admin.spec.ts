import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { VistaAdmin } from './vista-admin';

describe('VistaAdmin', () => {
  let component: VistaAdmin;
  let fixture: ComponentFixture<VistaAdmin>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VistaAdmin],
      providers: [provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(VistaAdmin);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
