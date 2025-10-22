import { Component } from '@angular/core';
import {NgOptimizedImage} from "@angular/common";
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-logo',
  imports: [
    NgOptimizedImage,
    RouterLink
  ],
  templateUrl: './logo.html',
  styleUrl: './logo.scss'
})
export class Logo {

}
