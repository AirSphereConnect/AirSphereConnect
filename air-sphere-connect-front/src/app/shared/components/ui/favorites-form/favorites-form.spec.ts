import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FavoritesForm } from './favorites-form';

describe('FavoritesForm', () => {
  let component: FavoritesForm;
  let fixture: ComponentFixture<FavoritesForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FavoritesForm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FavoritesForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
