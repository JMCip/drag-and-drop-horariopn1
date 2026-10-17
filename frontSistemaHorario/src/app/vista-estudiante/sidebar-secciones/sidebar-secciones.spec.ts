import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SidebarSecciones } from './sidebar-secciones';

describe('SidebarSecciones', () => {
  let component: SidebarSecciones;
  let fixture: ComponentFixture<SidebarSecciones>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SidebarSecciones],
    }).compileComponents();

    fixture = TestBed.createComponent(SidebarSecciones);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
