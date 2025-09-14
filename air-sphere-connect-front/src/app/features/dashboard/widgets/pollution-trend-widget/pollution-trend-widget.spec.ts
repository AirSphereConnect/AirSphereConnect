import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PollutionTrendWidget } from './pollution-trend-widget';

describe('PollutionTrendWidget', () => {
  let component: PollutionTrendWidget;
  let fixture: ComponentFixture<PollutionTrendWidget>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PollutionTrendWidget]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PollutionTrendWidget);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
