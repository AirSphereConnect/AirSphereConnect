import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FavoritesEdit } from './favorites-edit';

describe('FavoritesEdit', () => {
  let component: FavoritesEdit;
  let fixture: ComponentFixture<FavoritesEdit>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FavoritesEdit]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FavoritesEdit);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
