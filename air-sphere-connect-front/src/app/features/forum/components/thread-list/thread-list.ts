import {Component, computed, effect, inject, OnInit, signal} from '@angular/core';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {Thread} from '../../../../core/models/thread.model';
import {CommonModule} from '@angular/common';
import {Section} from '../../../../core/models/section.model';
import {SectionService} from '../../../../core/services/section.service';
import {PostService} from '../../../../core/services/post.service';
import {firstValueFrom, map, Observable, of} from 'rxjs';
import {ThreadService} from '../../../../core/services/thread.service';
import {UserService} from '../../../../shared/services/user-service';
import {FormsModule} from '@angular/forms';
import {toSignal} from "@angular/core/rxjs-interop";

@Component({
  selector: 'app-thread-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    FormsModule
  ],
  templateUrl: './thread-list.html',
  styleUrls: ['./thread-list.scss']
})
export class ThreadListComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private sectionService = inject(SectionService);
  private postService = inject(PostService);
  private threadService = inject(ThreadService);
  userService = inject(UserService);

  // Signals pour le tri
  sortCriteria = signal<string>('date');
  isAscending = signal<boolean>(true);

  // Signals pour les données
  section = signal<Section | null>(null);
  threads = signal<Thread[]>([]);

  // Signals pour le formulaire
  newThreadTitle = signal<string>('');
  newThreadContent = signal<string>('');
  showModal = signal<boolean>(false);

  // Signal computed pour les threads triés
  sortedThreads = computed(() => {
    const currentThreads = this.threads();
    const criteria = this.sortCriteria();
    const ascending = this.isAscending();

    if (!currentThreads || currentThreads.length === 0) return [];

    const sorted = [...currentThreads];

    switch (criteria) {
      case 'date':
        return sorted.sort((a, b) => {
          const dateA = new Date(a.createdAt).getTime();
          const dateB = new Date(b.createdAt).getTime();
          return ascending ? dateA - dateB : dateB - dateA;
        });

      case 'titre':
        return sorted.sort((a, b) => {
          const titleA = a.title.toLowerCase();
          const titleB = b.title.toLowerCase();
          return ascending
            ? titleA.localeCompare(titleB)
            : titleB.localeCompare(titleA);
        });

      default:
        return sorted;
    }
  });

  // Initialisation avec effect
 // constructor() {
  //  effect(() => {
    //  this.loadThreadsForSection();
   // });
 // }

  ngOnInit(): void {
    const sectionId = Number(this.route.snapshot.paramMap.get('sectionId'));
    this.loadSection(sectionId);
  }

  private async loadSection(sectionId: number): Promise<void> {
    try {
      const sectionData = await firstValueFrom(this.sectionService.getSectionById(sectionId));
      this.section.set(sectionData);
    } catch (error) {
      console.error('Erreur lors du chargement de la section:', error);
      this.router.navigate(['/forum']);
    }
  }

  sortThreads(criteria: string): void {
    if (this.sortCriteria() === criteria) {
      this.isAscending.update(v => !v);
    } else {
      this.sortCriteria.set(criteria);
      this.isAscending.set(true);
    }
  }

  getSortIndicator(criteria: string): string {
    if (this.sortCriteria() !== criteria) {
      return '↕';
    }
    return this.isAscending() ? '↑' : '↓';
  }

  async addThread(event?: Event): Promise<void> {
    if (event) {
      event.preventDefault();
      event.stopPropagation();
    }

    const title = this.newThreadTitle().trim();
    const content = this.newThreadContent().trim();
    const currentSection = this.section();

    if (!currentSection || !title || !content) {
      return;
    }

    try {
      const created = await firstValueFrom(
        this.threadService.addThread(title, content, currentSection.id)
      );

      // Mettre à jour la liste des threads
      //this.threads.update(current => [...current, created]);

      // Fermer la modale et réinitialiser le formulaire
      this.closeModal();
    } catch (error) {
      console.error('Erreur lors de l\'ajout du thread:', error);
    }
  }

  openModal(): void {
    this.showModal.set(true);
  }

  closeModal(): void {
    this.showModal.set(false);
    this.newThreadTitle.set('');
    this.newThreadContent.set('');
  }

  protected getTotalLikesByThread(threadId: number): Observable<number> {
    return this.postService.getPostByThreadId(threadId).pipe(
      map(posts => posts.reduce((total, post) => total + (post.likes || 0), 0))
    );
  }

  protected getPostCount(threadId: number): Observable<number> {
    return this.postService.getPostByThreadId(threadId).pipe(
      map(posts => posts.length)
    );
  }
}
