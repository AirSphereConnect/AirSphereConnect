import { TestBed, fakeAsync, flush } from '@angular/core/testing';
import { DestroyRef, inject, signal } from '@angular/core';
import { of, throwError } from 'rxjs';
import { citySearch } from './city-search.util';
import { City } from '../../core/models/city.model';

describe('citySearch', () => {
  let mockCityService: jasmine.SpyObj<{ searchCities: (query: string) => any }>;
  let querySignal: any;
  let citySuggestionsSignal: any;

  const mockCities: City[] = [
    { id: 1, name: 'Montpellier', postalCode:'34000', inseeCode: '34172', areaCode: '243400017', latitude: 43.625, longitude: 3.876, population: 516657, departmentName: 'Herault'},
    { id: 2, name: 'Nîmes', postalCode:'30000', inseeCode: '30189', areaCode: '243000643', latitude: 43.836, longitude: 4.360, population: 150444, departmentName: 'Gard'},

  ];

  beforeEach(() => {
    mockCityService = jasmine.createSpyObj('CityService', ['searchCities']);
    querySignal = signal('');
    citySuggestionsSignal = signal<City[]>([]);
  });

  afterEach(() => {
    TestBed.resetTestingModule();
  });

  it('should clear suggestions when query length is less than 2', fakeAsync(() => {
    const initialCities: City[] = [{ id: 1, name: 'Paris' } as City];
    citySuggestionsSignal.set(initialCities);

    TestBed.runInInjectionContext(() => {
      citySearch(mockCityService, querySignal, citySuggestionsSignal, inject(DestroyRef));
      querySignal.set('a');
      flush();
    });

    expect(citySuggestionsSignal()).toEqual([]);
    expect(mockCityService.searchCities).not.toHaveBeenCalled();
  }));

  it('should clear suggestions when query is empty', fakeAsync(() => {
    const initialCities: City[] = [{ id: 1, name: 'Paris' } as City];
    citySuggestionsSignal.set(initialCities);

    TestBed.runInInjectionContext(() => {
      citySearch(mockCityService, querySignal, citySuggestionsSignal, inject(DestroyRef));
      querySignal.set('');
      flush();
    });

    expect(citySuggestionsSignal()).toEqual([]);
    expect(mockCityService.searchCities).not.toHaveBeenCalled();
  }));

  it('should call searchCities when query length is 2 or more', fakeAsync(() => {
    const mockCities: City[] = [
      { id: 1, name: 'Montpellier', postalCode:'34000', inseeCode: '34172', areaCode: '243400017', latitude: 43.625, longitude: 3.876, population: 516657, departmentName: 'Herault'},
      { id: 2, name: 'Nîmes', postalCode:'30000', inseeCode: '30189', areaCode: '243000643', latitude: 43.836, longitude: 4.360, population: 150444, departmentName: 'Gard'},

    ];
    mockCityService.searchCities.and.returnValue(of(mockCities));

    TestBed.runInInjectionContext(() => {
      citySearch(mockCityService, querySignal, citySuggestionsSignal, inject(DestroyRef));
      querySignal.set('Pa');
      flush();
    });

    expect(mockCityService.searchCities).toHaveBeenCalledWith('Pa');
    expect(citySuggestionsSignal()).toEqual(mockCities);
  }));

  it('should update suggestions with search results', fakeAsync(() => {
    mockCityService.searchCities.and.returnValue(of(mockCities));

    TestBed.runInInjectionContext(() => {
      citySearch(mockCityService, querySignal, citySuggestionsSignal, inject(DestroyRef));
      querySignal.set('Paris');
      flush();
    });

    expect(citySuggestionsSignal()).toEqual(mockCities);
  }));

  it('should handle search with longer queries', fakeAsync(() => {
    mockCityService.searchCities.and.returnValue(of(mockCities));

    TestBed.runInInjectionContext(() => {
      citySearch(mockCityService, querySignal, citySuggestionsSignal, inject(DestroyRef));
      querySignal.set('Paris city');
      flush();
    });

    expect(mockCityService.searchCities).toHaveBeenCalledWith('Paris city');
    expect(citySuggestionsSignal()).toEqual(mockCities);
  }));

  it('should handle empty results from searchCities', fakeAsync(() => {
    mockCityService.searchCities.and.returnValue(of([]));

    TestBed.runInInjectionContext(() => {
      citySearch(mockCityService, querySignal, citySuggestionsSignal, inject(DestroyRef));
      querySignal.set('NonExistentCity');
      flush();
    });

    expect(citySuggestionsSignal()).toEqual([]);
  }));

  it('should handle null results from searchCities', fakeAsync(() => {
    mockCityService.searchCities.and.returnValue(of(null));

    TestBed.runInInjectionContext(() => {
      citySearch(mockCityService, querySignal, citySuggestionsSignal, inject(DestroyRef));
      querySignal.set('Test');
      flush();
    });

    expect(citySuggestionsSignal()).toEqual([]);
  }));

  it('should handle errors from searchCities with catchError', fakeAsync(() => {
    mockCityService.searchCities.and.returnValue(
      throwError(() => new Error('Network error'))
    );

    TestBed.runInInjectionContext(() => {
      citySearch(mockCityService, querySignal, citySuggestionsSignal, inject(DestroyRef));
      querySignal.set('Paris');
      flush();
    });

    expect(citySuggestionsSignal()).toEqual([]);
  }));

  it('should handle HTTP errors gracefully', fakeAsync(() => {
    mockCityService.searchCities.and.returnValue(
      throwError(() => ({ status: 500, message: 'Server error' }))
    );

    TestBed.runInInjectionContext(() => {
      citySearch(mockCityService, querySignal, citySuggestionsSignal, inject(DestroyRef));
      querySignal.set('Test');
      flush();
    });

    expect(citySuggestionsSignal()).toEqual([]);
  }));

  it('should react to multiple query changes', fakeAsync(() => {
    const cities1: City[] = [{ id: 1, name: 'Paris' } as City];
    const cities2: City[] = [{ id: 2, name: 'Lyon' } as City];

    mockCityService.searchCities.and.returnValues(of(cities1), of(cities2));

    TestBed.runInInjectionContext(() => {
      citySearch(mockCityService, querySignal, citySuggestionsSignal, inject(DestroyRef));

      querySignal.set('Pa');
      flush();
      expect(citySuggestionsSignal()).toEqual(cities1);

      querySignal.set('Ly');
      flush();
      expect(citySuggestionsSignal()).toEqual(cities2);
    });

    expect(mockCityService.searchCities).toHaveBeenCalledTimes(2);
  }));

  it('should transition from valid to short query', fakeAsync(() => {
    const mockCities: City[] = [{ id: 1, name: 'Paris' } as City];
    mockCityService.searchCities.and.returnValue(of(mockCities));

    TestBed.runInInjectionContext(() => {
      citySearch(mockCityService, querySignal, citySuggestionsSignal, inject(DestroyRef));

      querySignal.set('Paris');
      flush();
      expect(citySuggestionsSignal()).toEqual(mockCities);

      querySignal.set('P');
      flush();
      expect(citySuggestionsSignal()).toEqual([]);
    });
  }));

  it('should handle undefined in search results', fakeAsync(() => {
    mockCityService.searchCities.and.returnValue(of(undefined));

    TestBed.runInInjectionContext(() => {
      citySearch(mockCityService, querySignal, citySuggestionsSignal, inject(DestroyRef));
      querySignal.set('Test');
      flush();
    });

    expect(citySuggestionsSignal()).toEqual([]);
  }));
});
