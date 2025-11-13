import {Component, computed, inject, signal} from '@angular/core';
import {Thread} from '../../../../core/models/thread.model';
import {SectionService} from '../../../../core/services/section.service';
import {ThreadService} from '../../../../core/services/thread.service';
import {Section} from '../../../../core/models/section.model';
import {RouterLink} from '@angular/router';
import {toSignal} from '@angular/core/rxjs-interop';
import {UserService} from '../../../../shared/services/user-service';

@Component({
  selector: 'app-section',
  imports: [RouterLink],
  templateUrl: './section.html',
  styleUrl: './section.scss'
})
export class SectionComponent {
  private sectionService = inject(SectionService)
  private threadService = inject(ThreadService)
  private userService = inject(UserService);

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

  // Vérifie si l'utilisateur actuel est un administrateur
  readonly isAdmin = computed(() => {
    const admin = this.userService.currentUserProfile;
    return admin?.user.role === 'ADMIN';
  });

  // etat modale
  showCreateSectionModale = signal(false);
  newSectionForumId = signal<number>(1);
  newSectionTitle = signal('');
  newSectionDescription = signal('');
  isCreating = signal(false);
  errorMessage = signal<string | null>(null);

  openCreateSectionModal() {
    this.showCreateSectionModale.set(true);
    this.errorMessage.set(null);
  }

  closeCreateSectionModal() {
    this.showCreateSectionModale.set(false);
    this.newSectionForumId.set(1);
    this.newSectionTitle.set('')
    this.newSectionDescription.set('')
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

    if(!title) {
      this.errorMessage.set('Le titre de la section ne peut pas être vide.');
      return;
    }
    if(!description) {
      this.errorMessage.set('La description de la section ne peut pas être vide.');
      return;
    }

    this.isCreating.set(true);

    this.sectionService.createSection(title, description, forumId, userId).subscribe({
      next: (section) => {
        console.log('Section créée avec succès :', section);
        this.closeCreateSectionModal();
        this.isCreating.set(false);

         window.location.reload();
      },
      error: (error) => {
        console.error('Erreur lors de la création de la section :', error);
        if (error.status === 403) {
          this.errorMessage.set('Vous n\'avez pas les droits pour créer une section');
        } else if (error.status === 401) {
          this.errorMessage.set('Vous devez être connecté pour créer une section');
        } else if (error.status === 409) {
          this.errorMessage.set('Une section avec ce titre existe déjà');
        } else {
          this.errorMessage.set('Erreur lors de la création de la section');
        }

        this.isCreating.set(false);
      }
    })
  }

  deleteSection(sectionId: number) {
    if (!confirm('Êtes-vous sûr de vouloir supprimer cette section ?')) {
      return;
    }

    this.sectionService.deleteSection(sectionId).subscribe({
      next: () => {
        console.log('Section supprimée avec succès');
        // Actualiser la liste des sections après la suppression
        window.location.reload();
      },
      error: (error) => {
        console.error('Erreur lors de la suppression de la section :', error);
        if (error.status === 403) {
          alert('Vous n\'avez pas les droits pour supprimer cette section');
        } else if (error.status === 401) {
          alert('Vous devez être connecté pour supprimer une section');
        } else {
          alert('Erreur lors de la suppression de la section');
        }
      }
    });
  }

}
