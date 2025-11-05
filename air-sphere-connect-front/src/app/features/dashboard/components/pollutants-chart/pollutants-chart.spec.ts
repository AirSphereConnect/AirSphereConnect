import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PollutantsChart } from './pollutants-chart';

describe('PollutantsChart', () => {
  let component: PollutantsChart;
  let fixture: ComponentFixture<PollutantsChart>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PollutantsChart]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PollutantsChart);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
