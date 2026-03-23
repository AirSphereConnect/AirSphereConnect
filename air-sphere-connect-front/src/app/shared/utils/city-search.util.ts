import { DestroyRef, WritableSignal, effect } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { catchError, of } from 'rxjs';
import { City } from '../../core/models/city.model';

/**
 * Crée un effet pour la recherche de villes avec debouncing automatique
 * @param cityService - Service contenant la méthode searchCities
 * @param querySignal - Signal contenant la requête de recherche
 * @param citySuggestionsSignal - Signal pour stocker les résultats
 * @param destroyRef - Référence de destruction du composant appelant
 */
export function citySearch(
  cityService: { searchCities: (query: string) => any },
  querySignal: WritableSignal<string>,
  citySuggestionsSignal: WritableSignal<City[]>,
  destroyRef: DestroyRef
) {
  return effect(() => {
    const query = querySignal();
    if (query.length < 2) {
      citySuggestionsSignal.set([]);
      return;
    }
    cityService
      .searchCities(query)
      .pipe(
        catchError(() => of([])),
        takeUntilDestroyed(destroyRef)
      )
      .subscribe((cities: City[]) => {
        citySuggestionsSignal.set(cities ?? []);
      });
  });
}
