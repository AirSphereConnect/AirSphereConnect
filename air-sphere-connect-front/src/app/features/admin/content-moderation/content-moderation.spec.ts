import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContentModeration } from './content-moderation';

describe('ContentModeration', () => {
  let component: ContentModeration;
  let fixture: ComponentFixture<ContentModeration>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ContentModeration]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ContentModeration);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
