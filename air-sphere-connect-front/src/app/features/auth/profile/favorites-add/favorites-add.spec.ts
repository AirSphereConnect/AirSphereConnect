import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FavoritesAdd } from './favorites-add';

describe('FavoritesAdd', () => {
  let component: FavoritesAdd;
  let fixture: ComponentFixture<FavoritesAdd>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FavoritesAdd]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FavoritesAdd);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
