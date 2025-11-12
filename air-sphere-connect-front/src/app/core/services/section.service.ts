import {inject, Injectable} from '@angular/core';
import {Post} from '../models/post.model';
import {ThreadService} from './thread.service';
import {Section} from '../models/section.model';
import {Thread} from '../models/thread.model';

@Injectable({
  providedIn: 'root'
})
export class SectionService {

  private threasService = inject(ThreadService);

  private sections: Section[] = [
    {id: 1, title: 'section météo', description: 'vive la planete', user: 'Cyril'},
    {id: 2, title: 'section recensement', description: 'vive les gens', user: 'Sandrine'},
    {id: 3, title: 'section qualité air', description: 'vive la pollution', user: 'Nuno'},
  ]

  getAllSections() {
    return this.sections;
  }

  getSectionById(id: number) {
    return this.sections.find((section) => section.id === id);
  }

  getThreadsBySectionId(sectionId: number): Thread[] {
    return this.threasService.getAllThreads().filter((thread) => thread.sectionId === sectionId);
  }

  getThreadCountBySectionId(sectionId: number): number {
    return this.getThreadsBySectionId(sectionId).length;

  }
}
