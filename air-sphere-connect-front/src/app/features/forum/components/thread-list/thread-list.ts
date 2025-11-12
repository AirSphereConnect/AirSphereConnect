import {Component, inject, OnInit} from '@angular/core';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {Thread} from '../../../../core/models/thread.model';
import {CommonModule} from '@angular/common';
import {Section} from '../../../../core/models/section.model';
import {SectionService} from '../../../../core/services/section.service';
import {PostService} from '../../../../core/services/post.service';
import {interval} from 'rxjs';

@Component({
  selector: 'app-thread-list',
  standalone: true,
  imports: [
    CommonModule, RouterLink,
  ],
  templateUrl: './thread-list.html',
  styleUrls: ['./thread-list.scss']
})
export class ThreadListComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private sectionService = inject(SectionService);
  private postService = inject(PostService);

  section?: Section;
  threads: Thread[] = [];

  ngOnInit(): void {
      const sectionId = Number(this.route.snapshot.paramMap.get('sectionId'));
      const foundSection = this.sectionService.getSectionById(sectionId);

      if(!foundSection) {
        this.router.navigate(['/forum']);
        return;
      }

      this.section = this.sectionService.getSectionById(sectionId);

      if(this.section) {
        this.threads = this.sectionService.getThreadsBySectionId(sectionId);
      }

  }

  getPostCount(theadId: number): number {
    return this.postService.getPostCountByThreadId(theadId);
  }

  getTotalLikesByThread(threadId: number): number {
    return this.postService.getPostLikesByThreadId(threadId);
  }


  sortThreads(criteria: string) {
    switch (criteria) {
      case 'date':
        this.threads.sort(
          (a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
        );
        break;
      case 'popularite':
        this.threads.sort(
          (a, b) => this.getTotalLikesByThread(b.id) - this.getTotalLikesByThread(a.id));
        break;
    }

  }

}
