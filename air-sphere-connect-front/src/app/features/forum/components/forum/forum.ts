import {Component, inject, OnInit} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {SectionService} from '../../../../core/services/section.service';
@Component({
  selector: 'app-forum',
  standalone: true,
  imports: [
    RouterOutlet,
  ],
  templateUrl: './forum.html',
  styleUrls: ['./forum.scss']
})
export class Forum implements OnInit {
  private readonly sectionService = inject(SectionService);

  sections$ = this.sectionService.getSections();


  ngOnInit() {
    this.sections$.subscribe({
      next: () => {},
      error: (err) => this.logError('Erreur de chargement des sections', err)
    });
  }

  private logError(message: string, error?: unknown): void {
    // Only log errors in development mode
    if (typeof ngDevMode !== 'undefined' && ngDevMode) {
      console.error(message, error);
    }
  }
}
