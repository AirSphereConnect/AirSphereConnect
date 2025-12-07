import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';

import { PostComponent } from './post';
import { Post } from '../../../../core/models/post.model';

describe('PostComponent', () => {
  let component: PostComponent;
  let fixture: ComponentFixture<PostComponent>;

  const mockPost: Post = {
    id: 1,
    content: 'Test content',
    createdAt: new Date(),
    username: 'testuser',
    userRole: 'USER',
    threadId: 1,
    threadTitle: 'Test Thread',
    userId: 1,
    likeCount: 0,
    dislikeCount: 0,
    currentUserReaction: null
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PostComponent],
      providers: [provideHttpClient(), provideRouter([])]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PostComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('post', mockPost);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
