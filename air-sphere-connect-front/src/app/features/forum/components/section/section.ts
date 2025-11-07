import {Component, computed, inject} from '@angular/core';
import {Thread} from '../../../../core/models/thread.model';
import {SectionService} from '../../../../core/services/section.service';
import {ThreadService} from '../../../../core/services/thread.service';
import {Section} from '../../../../core/models/section.model';
import {RouterLink} from '@angular/router';
import {toSignal} from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-section',
  imports: [RouterLink],
  templateUrl: './section.html',
  styleUrl: './section.scss'
})
export class SectionComponent {
  private sectionService = inject(SectionService)
  private threadService = inject(ThreadService)

  readonly sections = toSignal(
    this.sectionService.getSections(),
    {initialValue: [] as Section[]}
  )

  readonly threads = toSignal(
    this.threadService.getAllThreads(),
    {initialValue: [] as Thread[]}
  )

  readonly sectionsWithCount = computed(() => {
    return this.sections().map(section => ({
      ...section,
      threadCount: this.threads().filter(thread => thread.rubricId === section.id).length
    }))
  })


}
