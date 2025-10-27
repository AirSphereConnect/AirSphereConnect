import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AlertsForm } from './alerts-form';

describe('AlertsForm', () => {
  let component: AlertsForm;
  let fixture: ComponentFixture<AlertsForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlertsForm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AlertsForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
