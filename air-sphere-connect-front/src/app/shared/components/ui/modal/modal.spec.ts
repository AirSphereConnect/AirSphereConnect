import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalComponent } from './modal';

describe('ModalComponent', () => {
  let component: ModalComponent;
  let fixture: ComponentFixture<ModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit close event when onClose is called', () => {
    let closeCalled = false;
    component.close.subscribe(() => closeCalled = true);

    component.onClose();

    expect(closeCalled).toBeTruthy();
  });

  it('should emit submit event when onSubmit is called', () => {
    let submitCalled = false;
    component.submit.subscribe(() => submitCalled = true);

    component.onSubmit();

    expect(submitCalled).toBeTruthy();
  });

  it('should close modal on backdrop click', () => {
    let closeCalled = false;
    component.close.subscribe(() => closeCalled = true);

    const mockEvent = {
      target: document.createElement('div'),
      currentTarget: document.createElement('div')
    } as any;
    mockEvent.target = mockEvent.currentTarget;

    component.onBackdropClick(mockEvent);

    expect(closeCalled).toBeTruthy();
  });

  it('should not close modal on inner click', () => {
    let closeCalled = false;
    component.close.subscribe(() => closeCalled = true);

    const mockEvent = {
      target: document.createElement('div'),
      currentTarget: document.createElement('section')
    } as any;

    component.onBackdropClick(mockEvent);

    expect(closeCalled).toBeFalsy();
  });
});
