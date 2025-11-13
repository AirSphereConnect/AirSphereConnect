import {Component, computed, inject, signal} from '@angular/core';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {toSignal} from '@angular/core/rxjs-interop';
import {tap} from 'rxjs';

// Services et modèles
import {PostService} from '../../../../core/services/post.service';
import {ThreadService} from '../../../../core/services/thread.service';
import {SectionService} from '../../../../core/services/section.service';
import {FormsModule} from '@angular/forms';
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
  readonly threads = toSignal(
    this.threadService.getThreadsBySectionId(this.sectionId()).pipe(
      tap(threads => console.log('Threads chargés pour la section', this.sectionId(), ':', threads))
    ),
    {initialValue: []}
  );
  readonly posts = toSignal(this.postService.getPosts(), {initialValue: []});
  readonly postsLikes = toSignal(this.postService.getLikesByThreadId(this.threadId()), {initialValue: 0});


  // 🔹 Computed pour threads enrichis avec postCount et likeCount
  readonly threadsWithCounts = computed(() => {

    const allThreads = this.threads();
    const allPosts = this.posts();

    console.log('Threads:', allThreads);
    console.log('Posts:', allPosts);


    console.log('🔍 Threads:', allThreads); // ✅ Ajoute ça

    return allThreads.map(thread => {
      console.log('📝 Thread individuel:', thread); // ✅ Et ça
      const threadPosts = allPosts.filter(p => {
        console.log(`Comparing post.threadId (${p.threadId}) with thread.id (${thread.id})`);
        return p.threadId === thread.id;
      });

      console.log(`Thread ${thread.id} has ${threadPosts.length} posts`, threadPosts);

      const likeCount = threadPosts.reduce((sum, post) => sum + (post.likeCount || 0), 0);

      return {
        ...thread,
        postCount: threadPosts.length,
        likeCount
      };
    });
  });

  // 🔹 Computed pour UI
  readonly threadCount = computed(() => this.threads().length);


  // 🔹 Signaux pour UI (modal, tri, formulaire)
  readonly showModal = signal(false);
  readonly newThreadTitle = signal('');
  readonly newThreadContent = signal('');
  readonly sortCriteria = signal<string>('date');
  readonly isAscending = signal<boolean>(true);
  readonly isSubmitting = signal<boolean>(false);

  // 🔹 Tri des threads
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

  // 🔹 Méthodes UI
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

    if(!userId) {
      alert('Vous devez être connecté pour créer un thread.');
      return;
    }

    if (!title) {
      alert('le titre est obligatoire');
      return;
    }

    // appel du service pour créer le thread et le post initial
    this.isSubmitting.set(true);

    this.threadService.addThread(title, sectionId, userId).subscribe({
      next: (newPost) => {
        console.log('Nouveau thread créé avec le post initial:', newPost);

        this.closeModal();
        this.isSubmitting.set(false);

        // rafraîchir la liste des threads
        this.threadService.getThreadsBySectionId(sectionId).subscribe(() => {
          window.location.reload();
        });
      },
      error: (error) => {
        console.error('Erreur lors de la création du thread:', error);
        alert('Erreur lors de la création du thread. Veuillez réessayer.');
        this.isSubmitting.set(false);
      }
    });
  }

  deleteThread(thread: any): boolean {
    const currentUser = this.userService.currentUserProfile;
    if(!currentUser) return false;


    if(currentUser.user.role === 'ADMIN') return true;

    return currentUser.user.id === thread.userId;
  }
}
