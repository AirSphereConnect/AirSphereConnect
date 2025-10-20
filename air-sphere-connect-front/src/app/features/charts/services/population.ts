import {Injectable, InputSignal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Population} from '../models/population.model';

@Injectable({
  providedIn: 'root'
})
export class PopulationService {
  private urlPopulation = "http://localhost:8080/api/history";

  constructor(private http: HttpClient) { }

  getPopulationByCityGraph(cityName: InputSignal<string>): Observable<Population[]> {
    return this.http.get<Population[]>(`${this.urlPopulation}/${cityName()}`);
  }
}
