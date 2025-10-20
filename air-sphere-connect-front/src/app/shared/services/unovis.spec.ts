import { TestBed } from '@angular/core/testing';

import { Unovis } from './unovis';

describe('Unovis', () => {
  let service: Unovis;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Unovis);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
