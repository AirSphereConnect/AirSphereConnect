import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AirQualityWidget } from './air-quality-widget';

describe('AirQualityWidget', () => {
  let component: AirQualityWidget;
  let fixture: ComponentFixture<AirQualityWidget>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AirQualityWidget]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AirQualityWidget);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
