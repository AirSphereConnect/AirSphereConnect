import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';

import { ThreadDetailComponent } from './thread-detail';

describe('ThreadDetailComponent', () => {
  let component: ThreadDetailComponent;
  let fixture: ComponentFixture<ThreadDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ThreadDetailComponent],
      providers: [
        provideHttpClient(),
        provideRouter([{ path: 'thread/:id', component: ThreadDetailComponent }])
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ThreadDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
