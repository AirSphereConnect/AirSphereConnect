import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ButtonCloseModal } from './button-close-modal';

describe('ButtonCloseModal', () => {
  let component: ButtonCloseModal;
  let fixture: ComponentFixture<ButtonCloseModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ButtonCloseModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ButtonCloseModal);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
