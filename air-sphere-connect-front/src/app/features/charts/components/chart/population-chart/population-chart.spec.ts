import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PopulationChart } from './population-chart';

describe('PopulationChart', () => {
  let component: PopulationChart;
  let fixture: ComponentFixture<PopulationChart>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PopulationChart]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PopulationChart);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
