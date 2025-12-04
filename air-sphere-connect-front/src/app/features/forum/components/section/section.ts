import {Component, computed, inject, signal} from '@angular/core';
import {Thread} from '../../../../core/models/thread.model';
import {SectionService} from '../../../../core/services/section.service';
import {ThreadService} from '../../../../core/services/thread.service';
import {Section} from '../../../../core/models/section.model';
import {RouterLink} from '@angular/router';
import {toSignal} from '@angular/core/rxjs-interop';
import {UserService} from '../../../../shared/services/user-service';
import {Button} from '../../../../shared/components/ui/button/button';
import {IconComponent} from '../../../../shared/components/ui/icon/icon';

@Component({
  selector: 'app-section',
  imports: [RouterLink, Button, IconComponent],
  templateUrl: './section.html',
  styleUrl: './section.scss'
})
export class SectionComponent {
  private readonly sectionService = inject(SectionService);
  private readonly threadService = inject(ThreadService);
  protected readonly userService = inject(UserService);

  readonly sections = signal<Section[]>([]);

  readonly threads = toSignal(
    this.threadService.getAllThreads(),
    {initialValue: [] as Thread[]}
  );

  constructor() {
    this.sectionService.getSections().subscribe(
      sections => this.sections.set(sections)
    );
  }

  readonly sectionsWithCount = computed(() => {
    return this.sections().map(section => ({
      ...section,
      threadCount: this.threads().filter(thread => thread.rubricId === section.id).length
    }));
  });

  // État modale et création
  readonly showCreateSectionModale = signal(false);
  readonly newSectionForumId = signal<number>(1);
  readonly newSectionTitle = signal('');
  readonly newSectionDescription = signal('');
  readonly isCreating = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly isDeletingPerSection = signal<Record<number, boolean>>({});

  // Vérifie si l'utilisateur actuel est un administrateur
  readonly isAdmin = computed(() => this.userService.currentUserProfile?.user.role === 'ADMIN');

  openCreateSectionModal() {
    this.showCreateSectionModale.set(true);
    this.errorMessage.set(null);
  }

  closeCreateSectionModal() {
    this.showCreateSectionModale.set(false);
    this.newSectionForumId.set(1);
    this.newSectionTitle.set('');
    this.newSectionDescription.set('');
  }

  createSection() {
    const title = this.newSectionTitle().trim();
    const description = this.newSectionDescription().trim();
    const userId = this.userService.currentUserProfile?.user.id;
    const forumId = this.newSectionForumId();

    if (!userId) {
      this.errorMessage.set('Vous devez être connecté pour créer une section.');
      return;
    }

    if (!title) {
      this.errorMessage.set('Le titre de la section ne peut pas être vide.');
      return;
    }

    if (!description) {
      this.errorMessage.set('La description de la section ne peut pas être vide.');
      return;
    }

    this.isCreating.set(true);

    this.sectionService.createSection(title, description, forumId, userId).subscribe({
      next: () => {
        this.closeCreateSectionModal();
        this.isCreating.set(false);
        globalThis.location.reload();
      },
      error: (error) => {
        this.isCreating.set(false);
        const statusCode = error.status;
        const messages: Record<number, string> = {
          403: 'Vous n\'avez pas les droits pour créer une section',
          401: 'Vous devez être connecté pour créer une section',
          409: 'Une section avec ce titre existe déjà'
        };
        this.errorMessage.set(messages[statusCode] || 'Erreur lors de la création de la section');
      }
    });
  }

  deleteSection(section: Section) {
    if (!confirm(`Êtes-vous sûr de vouloir supprimer "${section.title}" ?`)) return;

    const userId = this.userService.currentUserProfile?.user?.id;
    if (!userId) {
      alert('Vous devez être connecté pour supprimer une section');
      return;
    }

    this.isDeletingPerSection.update(state => ({ ...state, [section.id]: true }));

    this.sectionService.deleteSection(section.id, userId).subscribe({
      next: () => {
        this.sections.update(sections => sections.filter(s => s.id !== section.id));
        this.isDeletingPerSection.update(state => ({ ...state, [section.id]: false }));
      },
      error: (error) => {
        this.isDeletingPerSection.update(state => ({ ...state, [section.id]: false }));
        const statusCode = error.status;
        const messages: Record<number, string> = {
          403: 'Vous n\'avez pas les droits pour supprimer cette section',
          401: 'Vous devez être connecté'
        };
        alert(messages[statusCode] || 'Erreur lors de la suppression');
      }
    });
  }

}
