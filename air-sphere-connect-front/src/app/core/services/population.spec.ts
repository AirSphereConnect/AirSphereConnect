import { TestBed } from '@angular/core/testing';

import { Population } from './population';

describe('Population', () => {
  let service: Population;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Population);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
