import {Component, Input} from '@angular/core';
import { Navbar } from '../../ui/navbar/navbar';
import {Logo} from '../../ui/logo/logo';


@Component({
  selector: 'app-header',
  standalone: true,
  templateUrl: './header.html',
  styleUrls: ['./header.scss'],
  imports: [
    Navbar,
    Logo,
  ]
})
export class Header {
  @Input() userRole: string | null = null;
  @Input() scrolled = false;
  menuOpen = false;


}
