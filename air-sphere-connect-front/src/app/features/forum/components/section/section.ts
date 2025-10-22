import {Component, inject} from '@angular/core';
import {Post} from '../../../../core/models/post.model';
import {Thread} from '../../../../core/models/thread.model';
import {SectionService} from '../../../../core/services/section.service';
import {ThreadService} from '../../../../core/services/thread.service';
import {Section} from '../../../../core/models/section.model';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-section',
  imports: [RouterLink],
  templateUrl: './section.html',
  styleUrl: './section.scss'
})
export class SectionComponent {

  private sectionService = inject(SectionService)

  sections: Section[] = this.sectionService.getAllSections();

  getThreadCount(sectionId: number): number {
    return this.sectionService.getThreadCountBySectionId(sectionId);
  }


}
