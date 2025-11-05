import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CityPopulationChart } from './city-population-chart';

describe('CityPopulationChart', () => {
  let component: CityPopulationChart;
  let fixture: ComponentFixture<CityPopulationChart>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CityPopulationChart]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CityPopulationChart);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
