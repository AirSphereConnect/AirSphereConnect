import { WritableSignal, effect } from '@angular/core';
import { catchError, of } from 'rxjs';
import { City } from '../../../core/models/city.model';

export function inputCitySearch(
  cityService: { searchCities: (query: string) => any },
  querySignal: WritableSignal<string>,
  citySuggestionsSignal: WritableSignal<City[]>
) {
  return effect(() => {
    const query = querySignal();
    if (query.length < 2) {
      citySuggestionsSignal.set([]);
      return;
    }
    cityService
      .searchCities(query)
      .pipe(catchError(() => of([])))
      .subscribe((cities: City[]) => {
        citySuggestionsSignal.set(cities ?? []);
      });
  });
}
