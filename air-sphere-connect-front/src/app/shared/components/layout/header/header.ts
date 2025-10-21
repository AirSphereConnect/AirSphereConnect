import { Component, Input } from '@angular/core';
import { Navbar } from '../../ui/navbar/navbar';
import { NgOptimizedImage } from '@angular/common';
import {Logo} from '../../ui/logo/logo';

@Component({
  selector: 'app-header',
  standalone: true, // ⚠️ important si tu veux importer des composants directement
  templateUrl: './header.html',
  styleUrls: ['./header.scss'],
  imports: [
    Navbar,
    Logo
  ]
})
export class Header {
  @Input() userRole: string | null = null;
}
