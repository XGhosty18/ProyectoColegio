import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Modal } from './modal';

describe('Modal', () => {
  let component: Modal;
  let fixture: ComponentFixture<Modal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Modal]
    }).compileComponents();
    fixture = TestBed.createComponent(Modal);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not render when closed', () => {
    fixture.detectChanges();
    const el = fixture.nativeElement as HTMLElement;
    expect(el.querySelector('.overlay')).toBeFalsy();
  });

  it('should render when open', () => {
    fixture.componentRef.setInput('open', true);
    fixture.componentRef.setInput('title', 'Test Modal');
    fixture.detectChanges();
    const el = fixture.nativeElement as HTMLElement;
    expect(el.querySelector('.overlay')).toBeTruthy();
    expect(el.querySelector('h2')?.textContent).toContain('Test Modal');
  });

  it('should emit close on overlay click', () => {
    const spy = jasmine.createSpy();
    component.close.subscribe(spy);
    fixture.componentRef.setInput('open', true);
    fixture.detectChanges();
    const overlay = fixture.nativeElement.querySelector('.overlay');
    overlay?.dispatchEvent(new MouseEvent('click', { bubbles: true }));
    expect(spy).toHaveBeenCalled();
  });
});
