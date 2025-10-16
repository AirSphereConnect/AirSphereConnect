import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {Header} from './shared/components/layout/header/header';
import {Login} from './features/auth/login/login';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Header, Login],
  templateUrl: './app.html',
  styleUrls: ['./app.scss']
})
export class App {
  protected readonly title = signal('air-sphere-connect-front');
}
