import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ThreadDetailComponent } from './thread-detail';

describe('ThreadDetailComponent', () => {
  let component: ThreadDetailComponent;
  let fixture: ComponentFixture<ThreadDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ThreadDetailComponent, HttpClientTestingModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            params: of({ id: '123' }),
            snapshot: { params: { id: '123' } }
          }
        }
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
