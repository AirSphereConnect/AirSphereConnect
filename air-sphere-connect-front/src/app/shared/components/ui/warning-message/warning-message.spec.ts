import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WarningMessage } from './warning-message';

describe('WarningMessage', () => {
  let component: WarningMessage;
  let fixture: ComponentFixture<WarningMessage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WarningMessage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WarningMessage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
