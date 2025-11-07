import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CityUtils } from './city-utils';

describe('CityUtils', () => {
  let component: CityUtils;
  let fixture: ComponentFixture<CityUtils>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CityUtils]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CityUtils);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
