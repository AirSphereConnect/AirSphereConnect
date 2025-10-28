import {Component, inject, OnInit} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {Section} from '../../../../core/models/section.model';
import {Observable} from 'rxjs';
import {SectionService} from '../../../../core/services/section.service';

@Component({
  selector: 'app-forum',
  standalone: true,
  imports: [
    RouterOutlet
  ],
  templateUrl: './forum.html',
  styleUrls: ['./forum.scss']
})
export class ForumComponent implements OnInit {
  private sectionService = inject(SectionService);

  sections$ = this.sectionService.getSections();


  ngOnInit() {
    this.sections$.subscribe({
      next: (sections: Section[]) => console.log('Loaded sections:', sections),
      error: (err) => console.error('Error loading sections:', err)
    });
  }
}
