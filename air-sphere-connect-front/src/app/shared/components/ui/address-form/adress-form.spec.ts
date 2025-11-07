import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdressForm } from './address-form';

describe('AdressForm', () => {
  let component: AdressForm;
  let fixture: ComponentFixture<AdressForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdressForm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdressForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
