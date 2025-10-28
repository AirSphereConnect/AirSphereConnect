import {Component, inject, OnInit} from '@angular/core';
import {Post} from '../../../../core/models/post.model';
import {Thread} from '../../../../core/models/thread.model';
import {SectionService} from '../../../../core/services/section.service';
import {ThreadService} from '../../../../core/services/thread.service';
import {Section} from '../../../../core/models/section.model';
import {RouterLink} from '@angular/router';
import {map, Observable} from 'rxjs';
import {AsyncPipe} from '@angular/common';

@Component({
  selector: 'app-section',
  imports: [RouterLink, AsyncPipe],
  templateUrl: './section.html',
  styleUrl: './section.scss'
})
export class SectionComponent implements OnInit {
  private sectionService = inject(SectionService)
  private threadService = inject(ThreadService)
  sections$!: Observable<Section[]>;

  ngOnInit() {
    this.sections$ = this.sectionService.getSections();
  }

  getThreadCount(sectionId: number): Observable<number> {
    return this.threadService.getAllThreads().pipe(
      map(threads => threads.filter(thread => thread.sectionId === sectionId).length)
    )
  }


}
