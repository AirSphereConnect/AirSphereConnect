import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompteForum } from './compte-forum';

describe('CompteForum', () => {
  let component: CompteForum;
  let fixture: ComponentFixture<CompteForum>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CompteForum]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CompteForum);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
