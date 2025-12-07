import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';

import { Forum } from './forum';

describe('Forum', () => {
  let component: Forum;
  let fixture: ComponentFixture<Forum>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Forum],
      providers: [provideHttpClient(), provideRouter([])]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Forum);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
