import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { BehaviorSubject } from 'rxjs';

import { Profile } from './profile';
import { UserService } from '../../../../shared/services/user-service';

describe('Profile', () => {
  let component: Profile;
  let fixture: ComponentFixture<Profile>;
  let userService: jasmine.SpyObj<UserService>;
  let userProfileSubject: BehaviorSubject<any>;

  const mockUser = {
    id: 1,
    username: 'testuser',
    email: 'test@example.com',
    role: 'USER',
    address: {
      street: '123 Rue de la Paix',
      city: { id: 1, name: 'Paris' }
    },
    favorites: [],
    alerts: []
  };

  beforeEach(async () => {
    userProfileSubject = new BehaviorSubject<any>({ user: mockUser });

    const userServiceSpy = jasmine.createSpyObj('UserService', ['fetchUserProfile'], {
      userProfile$: userProfileSubject.asObservable()
    });

    await TestBed.configureTestingModule({
      imports: [Profile],
      providers: [
        provideHttpClient(),
        provideRouter([]),
        { provide: UserService, useValue: userServiceSpy }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Profile);
    component = fixture.componentInstance;
    userService = TestBed.inject(UserService) as jasmine.SpyObj<UserService>;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Initialization', () => {
    it('should initialize user as null', () => {
      const testFixture = TestBed.createComponent(Profile);
      const testComponent = testFixture.componentInstance;
      expect(testComponent.user).toBeNull();
    });

    it('should initialize tabs as empty array', () => {
      const testFixture = TestBed.createComponent(Profile);
      const testComponent = testFixture.componentInstance;
      expect(testComponent.tabs).toEqual([]);
    });
  });

  describe('ngOnInit', () => {
    it('should subscribe to userProfile$', () => {
      expect(component.user as any).toEqual(mockUser);
    });

    it('should update user when userProfile$ emits', () => {
      const newUser = { id: 2, username: 'newuser', email: 'new@example.com', role: 'USER', address: null, favorites: [], alerts: [] };
      userProfileSubject.next({ user: newUser });

      expect(component.user as any).toEqual(newUser);
    });

    it('should handle null profile', () => {
      userProfileSubject.next(null);
      expect(component.user as any).toEqual(mockUser);
    });

    it('should handle profile without user', () => {
      userProfileSubject.next({ user: null, role: 'GUEST' } as any);
      expect(component.user as any).toEqual(mockUser);
    });
  });

  describe('ngAfterViewInit', () => {
    it('should initialize tabs with 4 items', () => {
      expect(component.tabs.length).toBe(4);
    });

    it('should have correct tab labels', () => {
      expect(component.tabs[0].label).toBe('Mon profil');
      expect(component.tabs[1].label).toBe('Mes rubriques');
      expect(component.tabs[2].label).toBe('Mes favoris');
      expect(component.tabs[3].label).toBe('Mes alertes');
    });

    it('should have templates for each tab', () => {
      expect(component.tabs[0].template).toBeDefined();
      expect(component.tabs[1].template).toBeDefined();
      expect(component.tabs[2].template).toBeDefined();
      expect(component.tabs[3].template).toBeDefined();
    });

    it('should assign profilUser template to first tab', () => {
      expect(component.tabs[0].template).toBe(component.profilUser);
    });

    it('should assign thread template to second tab', () => {
      expect(component.tabs[1].template).toBe(component.thread);
    });

    it('should assign favorites template to third tab', () => {
      expect(component.tabs[2].template).toBe(component.favorites);
    });

    it('should assign alerts template to fourth tab', () => {
      expect(component.tabs[3].template).toBe(component.alerts);
    });
  });

  describe('ViewChild references', () => {
    it('should have profilUser ViewChild', () => {
      expect(component.profilUser).toBeDefined();
    });

    it('should have thread ViewChild', () => {
      expect(component.thread).toBeDefined();
    });

    it('should have favorites ViewChild', () => {
      expect(component.favorites).toBeDefined();
    });

    it('should have alerts ViewChild', () => {
      expect(component.alerts).toBeDefined();
    });
  });
});
