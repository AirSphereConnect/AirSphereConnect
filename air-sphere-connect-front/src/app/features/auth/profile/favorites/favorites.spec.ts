import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { of, throwError, BehaviorSubject } from 'rxjs';

import { Favorites } from './favorites';
import { FavoritesService } from '../../../../shared/services/favorites-service';
import { UserService } from '../../../../shared/services/user-service';

describe('Favorites', () => {
  let component: Favorites;
  let fixture: ComponentFixture<Favorites>;
  let favoritesService: jasmine.SpyObj<FavoritesService>;
  let userService: jasmine.SpyObj<UserService>;
  let userProfileSubject: BehaviorSubject<any>;

  const mockUser = {
    id: 1,
    username: 'testuser',
    email: 'test@example.com',
    role: 'USER',
    address: null,
    favorites: [
      { id: 1, cityId: 1, cityName: 'Paris', selectWeather: true, selectAirQuality: false, selectPopulation: true },
      { id: 2, cityId: 2, cityName: 'Lyon', selectWeather: false, selectAirQuality: true, selectPopulation: false }
    ],
    alerts: []
  };

  beforeEach(async () => {
    userProfileSubject = new BehaviorSubject<any>({ user: mockUser });

    const favoritesServiceSpy = jasmine.createSpyObj('FavoritesService', ['deleteFavorites']);
    const userServiceSpy = jasmine.createSpyObj('UserService', ['fetchUserProfile'], {
      userProfile$: userProfileSubject.asObservable()
    });

    await TestBed.configureTestingModule({
      imports: [Favorites],
      providers: [
        provideHttpClient(),
        provideRouter([]),
        { provide: FavoritesService, useValue: favoritesServiceSpy },
        { provide: UserService, useValue: userServiceSpy }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Favorites);
    component = fixture.componentInstance;
    favoritesService = TestBed.inject(FavoritesService) as jasmine.SpyObj<FavoritesService>;
    userService = TestBed.inject(UserService) as jasmine.SpyObj<UserService>;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Initialization', () => {
    it('should initialize user as null', () => {
      const testFixture = TestBed.createComponent(Favorites);
      const testComponent = testFixture.componentInstance;
      expect(testComponent.user).toBeNull();
    });

    it('should initialize editingFavoriteId as null', () => {
      expect(component.editingFavoriteId).toBeNull();
    });

    it('should initialize initialFavoriteData as null', () => {
      expect(component.initialFavoriteData).toBeNull();
    });

    it('should initialize favoritesToDeleteId as null', () => {
      expect(component.favoritesToDeleteId).toBeNull();
    });

    it('should initialize isModalOpen to false', () => {
      expect(component.isModalOpen()).toBeFalse();
    });

    it('should initialize isWarningOpen to false', () => {
      expect(component.isWarningOpen()).toBeFalse();
    });

    it('should initialize warningMessage to null', () => {
      expect(component.warningMessage()).toBeNull();
    });
  });

  describe('ngOnInit', () => {
    it('should subscribe to userProfile$ and set user', () => {
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
      userProfileSubject.next({});
      expect(component.user as any).toEqual(mockUser);
    });
  });

  describe('addFavorites', () => {
    it('should reset editingFavoriteId to null', () => {
      component.editingFavoriteId = 1;
      component.addFavorites();
      expect(component.editingFavoriteId).toBeNull();
    });

    it('should reset initialFavoriteData to null', () => {
      component.initialFavoriteData = { id: 1 };
      component.addFavorites();
      expect(component.initialFavoriteData).toBeNull();
    });

    it('should open modal', () => {
      component.addFavorites();
      expect(component.isModalOpen()).toBeTrue();
    });
  });

  describe('editFavorites', () => {
    it('should set editingFavoriteId when favorite exists', () => {
      component.editFavorites(1);
      expect(component.editingFavoriteId).toBe(1);
    });

    it('should set initialFavoriteData with favorite data', () => {
      component.editFavorites(1);
      expect(component.initialFavoriteData).toEqual(mockUser.favorites[0]);
    });

    it('should open modal when favorite exists', () => {
      component.editFavorites(1);
      expect(component.isModalOpen()).toBeTrue();
    });

    it('should not open modal when favorite does not exist', () => {
      component.editFavorites(999);
      expect(component.isModalOpen()).toBeFalse();
    });

    it('should not set editingFavoriteId when favorite does not exist', () => {
      component.editFavorites(999);
      expect(component.editingFavoriteId).toBeNull();
    });

    it('should handle null user', () => {
      component.user = null;
      component.editFavorites(1);
      expect(component.isModalOpen()).toBeFalse();
    });
  });

  describe('deleteFavorites', () => {
    it('should set favoritesToDeleteId', () => {
      component.deleteFavorites(1);
      expect(component.favoritesToDeleteId).toBe(1);
    });

    it('should set warning message', () => {
      component.deleteFavorites(1);
      expect(component.warningMessage()).toBe('Êtes-vous sûr de vouloir supprimer ce favoris ?');
    });

    it('should open warning dialog', () => {
      component.deleteFavorites(1);
      expect(component.isWarningOpen()).toBeTrue();
    });
  });

  describe('confirmDelete', () => {
    beforeEach(() => {
      component.favoritesToDeleteId = 1;
    });

    it('should not call deleteFavorites if favoritesToDeleteId is null', () => {
      component.favoritesToDeleteId = null;
      component.confirmDelete();
      expect(favoritesService.deleteFavorites).not.toHaveBeenCalled();
    });

    it('should call deleteFavorites with correct id', () => {
      favoritesService.deleteFavorites.and.returnValue(of(undefined));
      component.confirmDelete();
      expect(favoritesService.deleteFavorites).toHaveBeenCalledWith(1);
    });

    describe('on success', () => {
      beforeEach(() => {
        favoritesService.deleteFavorites.and.returnValue(of(undefined));
      });

      it('should fetch user profile', (done) => {
        component.confirmDelete();
        setTimeout(() => {
          expect(userService.fetchUserProfile).toHaveBeenCalled();
          done();
        });
      });

      it('should reset favoritesToDeleteId', (done) => {
        component.confirmDelete();
        setTimeout(() => {
          expect(component.favoritesToDeleteId).toBeNull();
          done();
        });
      });

      it('should close warning dialog', (done) => {
        component.isWarningOpen.set(true);
        component.confirmDelete();
        setTimeout(() => {
          expect(component.isWarningOpen()).toBeFalse();
          done();
        });
      });

      it('should clear warning message', (done) => {
        component.warningMessage.set('Test message');
        component.confirmDelete();
        setTimeout(() => {
          expect(component.warningMessage()).toBeNull();
          done();
        });
      });
    });

    describe('on error', () => {
      beforeEach(() => {
        favoritesService.deleteFavorites.and.returnValue(throwError(() => new Error('Delete failed')));
      });

      it('should close warning dialog', (done) => {
        component.isWarningOpen.set(true);
        component.confirmDelete();
        setTimeout(() => {
          expect(component.isWarningOpen()).toBeFalse();
          done();
        });
      });

      it('should reset favoritesToDeleteId', (done) => {
        component.confirmDelete();
        setTimeout(() => {
          expect(component.favoritesToDeleteId).toBeNull();
          done();
        });
      });

      it('should clear warning message', (done) => {
        component.warningMessage.set('Test message');
        component.confirmDelete();
        setTimeout(() => {
          expect(component.warningMessage()).toBeNull();
          done();
        });
      });

      it('should not fetch user profile on error', (done) => {
        component.confirmDelete();
        setTimeout(() => {
          expect(userService.fetchUserProfile).not.toHaveBeenCalled();
          done();
        });
      });
    });
  });

  describe('onModalClose', () => {
    it('should close modal', () => {
      component.isModalOpen.set(true);
      component.onModalClose();
      expect(component.isModalOpen()).toBeFalse();
    });

    it('should fetch user profile', () => {
      component.onModalClose();
      expect(userService.fetchUserProfile).toHaveBeenCalled();
    });
  });
});
