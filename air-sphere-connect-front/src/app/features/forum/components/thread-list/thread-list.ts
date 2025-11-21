import {Component, computed, inject, signal} from '@angular/core';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {toSignal} from '@angular/core/rxjs-interop';
import {FormsModule} from '@angular/forms';

// Services et modèles
import {PostService} from '../../../../core/services/post.service';
import {ThreadService} from '../../../../core/services/thread.service';
import {SectionService} from '../../../../core/services/section.service';
import {UserService} from '../../../../shared/services/user-service';

@Component({
  selector: 'app-thread-list',
  standalone: true,
  imports: [RouterLink, FormsModule],
  templateUrl: './thread-list.html',
  styleUrls: ['./thread-list.scss']
})
export class ThreadListComponent {
  // 🔹 Injections
  private readonly route = inject(ActivatedRoute);
  private readonly postService = inject(PostService);
  private readonly threadService = inject(ThreadService);
  private readonly sectionService = inject(SectionService);
  protected readonly userService = inject(UserService);

  // 🔹 Signaux pour IDs
  readonly sectionId = signal<number>(Number(this.route.snapshot.paramMap.get('sectionId')));
  readonly threadId = signal<number>(Number(this.route.snapshot.paramMap.get('threadId')));

  // 🔹 Données principales
  readonly section = toSignal(this.sectionService.getSectionById(this.sectionId()), {initialValue: undefined});
  readonly threads = signal<any[]>([]);
  errorMessage = signal<string | null>(null);

  constructor() {
    this.loadThreads();
  }

  private loadThreads() {
    this.threadService.getThreadsBySectionId(this.sectionId()).subscribe(threads => this.threads.set(threads));
  }

  private getUserId(): number | null {
    const userId = this.userService.currentUserProfile?.user.id;
    if (!userId) {
      this.errorMessage.set('Vous devez être connecté');
    }
    return userId ?? null;
  }

  readonly posts = toSignal(this.postService.getPosts(), {initialValue: []});
  readonly postsLikes = toSignal(this.postService.getLikesByThreadId(this.threadId()), {initialValue: 0});
  readonly isDeleting = signal<boolean>(false);
  readonly isDeletingPerThread = signal<Record<number, boolean>>({});

  // 🔹 Vérifie si l'utilisateur est admin
  readonly isAdmin = computed(() => {
    const user = this.userService.currentUserProfile;
    return user?.user.role === 'ADMIN';
  });

  // 🔹 Vérifie si l'utilisateur peut supprimer un thread
  canDeleteThread(thread: any): boolean {
    const user = this.userService.currentUserProfile;
    return this.isAdmin() || user?.user.id === thread.userId;
  }

  // 🔹 Threads enrichis
  readonly threadsWithCounts = computed(() => {
    const allThreads = this.threads();
    const allPosts = this.posts();
    return allThreads.map(thread => {
      const threadPosts = allPosts.filter(p => p.threadId === thread.id);
      const likeCount = threadPosts.reduce((sum, post) => sum + (post.likeCount || 0), 0);
      return {...thread, postCount: threadPosts.length, likeCount};
    });
  });

  // 🔹 Tri
  readonly sortCriteria = signal<string>('date');
  readonly isAscending = signal<boolean>(true);
  readonly sortedThreads = computed(() => {
    const threads = [...this.threadsWithCounts()];
    const criteria = this.sortCriteria();
    const ascending = this.isAscending();
    return threads.sort((a, b) => {
      switch (criteria) {
        case 'date':
          return ascending
            ? new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()
            : new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
        case 'titre':
          return ascending
            ? a.title.localeCompare(b.title)
            : b.title.localeCompare(a.title);
        case 'reponses':
          return ascending
            ? a.postCount - b.postCount
            : b.postCount - a.postCount;
        case 'popularite':
          return ascending
            ? a.likeCount - b.likeCount
            : b.likeCount - a.likeCount;
        default:
          return 0;
      }
    });
  });

  sortThreads(criteria: 'date' | 'titre' | 'reponses' | 'popularite') {
    if (this.sortCriteria() === criteria) {
      this.isAscending.update(v => !v);
    } else {
      this.sortCriteria.set(criteria);
      this.isAscending.set(true);
    }
  }

  getSortIndicator(criteria: string): string {
    if (this.sortCriteria() !== criteria) return '↕';
    return this.isAscending() ? '↑' : '↓';
  }

  // 🔹 Création de thread
  readonly showModal = signal(false);
  readonly newThreadTitle = signal('');
  readonly newThreadContent = signal('');
  readonly isSubmitting = signal(false);

  openModal() {
    this.showModal.set(true);
  }

  closeModal() {
    this.showModal.set(false);
    this.newThreadTitle.set('');
    this.newThreadContent.set('');
  }

  createThread() {
    const title = this.newThreadTitle().trim();
    const sectionId = this.sectionId();
    const userId = this.userService.currentUserProfile?.user?.id;

    if (!userId) {
      alert('Vous devez être connecté pour créer un thread.');
      return;
    }

    if (!title) {
      alert('Le titre est obligatoire.');
      return;
    }

    this.isSubmitting.set(true);

    this.threadService.addThread(title, sectionId, userId).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.closeModal();
        this.loadThreads(); // ✅ Rafraîchissement réactif
      },
      error: (error) => {
        console.error('Erreur lors de la création du thread:', error);
        alert('Erreur lors de la création du thread.');
        this.isSubmitting.set(false);
      }
    });
  }

  // 🔹 Suppression d’un thread
  deleteThread(thread: any) {
    if (!thread || typeof thread.id !== 'number' || thread.id <= 0) {
      console.error('ID de thread invalide:', thread);
      alert('Impossible de supprimer : ID invalide.');
      return;
    }

    const userId = this.userService.currentUserProfile?.user?.id;
    if (!userId) {
      console.error('ID utilisateur invalide:', userId);
      alert('Vous devez être connecté pour supprimer.');
      return;
    }

    if (!confirm(`Voulez-vous vraiment supprimer "${thread.title}" ?`)) return;

    this.isDeletingPerThread.update(state => ({ ...state, [thread.id]: true }));

    this.threadService.deleteThread(thread.id, userId).subscribe({
      next: () => {
        this.isDeletingPerThread.update(state => ({ ...state, [thread.id]: false }));
        this.threads.update(ts => ts.filter(t => t.id !== thread.id));
      },
      error: (error) => {
        console.error('Erreur lors de la suppression du thread:', error);
        let message = 'Erreur lors de la suppression.';
        if (error.status === 401) {
          message = 'Non autorisé – vérifiez votre connexion.';
        } else if (error.status === 403) {
          message = 'Interdit – vous n\'avez pas les droits.';
        } else if (error.status === 0) {
          message = 'Erreur CORS ou réseau – vérifiez la config serveur.';
        }
        alert(message);
        this.isDeletingPerThread.update(state => ({ ...state, [thread.id]: false }));
      }
    });
  }
}
