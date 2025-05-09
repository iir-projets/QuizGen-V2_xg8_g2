import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterModule } from '@angular/router';
import { QuizService } from '../../../services/quiz.service';
import { AuthService } from '../../../auth/auth.service';
import { HeaderComponent } from '../header/header.component';
import { FooterComponent } from '../footer/footer.component';

@Component({
  selector: 'app-mes-quiz',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent, FooterComponent],
  templateUrl: './mes-quiz.component.html',
  styleUrls: ['./mes-quiz.component.css'],
  providers: [DatePipe]
})
export class MesQuizComponent implements OnInit {
  quizzes: any[] = [];
  loading = true;
  error: string | null = null;
  sharePanel = { open: false };
  detailsPanel = { open: false };
  selectedQuiz: any = null;
  shareableLink = '';
  isLinkCopied = false;
  qrCodeUrl = '';

  constructor(
    private quizService: QuizService,
    private authService: AuthService,
    private datePipe: DatePipe
  ) {}

  ngOnInit(): void {
    this.loadQuizzes();
  }

  loadQuizzes(): void {
    this.quizService.getUserQuizzes().subscribe({
      next: (data) => {
        this.quizzes = data.map(quiz => ({
          ...quiz,
          createdAt: this.parseDate(quiz.createdAt),
          publishDate: quiz.publishDate ? this.parseDate(quiz.publishDate) : null
        }));
        this.loading = false;
      },
      error: (err) => {
        this.handleError(err);
      }
    });
  }

  private parseDate(dateInput: any): Date {
    if (dateInput instanceof Date) {
      return dateInput;
    }
    if (Array.isArray(dateInput)) {
      return new Date(
        dateInput[0],
        dateInput[1] - 1,
        dateInput[2],
        dateInput[3],
        dateInput[4]
      );
    }
    if (typeof dateInput === 'string') {
      return new Date(dateInput);
    }
    return new Date();
  }

  formatDate(date: Date): string {
    return this.datePipe.transform(date, 'dd/MM/yyyy') || '';
  }

  formatDateTime(date: Date): string {
    return this.datePipe.transform(date, 'dd/MM/yyyy HH:mm') || '';
  }

  private handleError(err: any): void {
    console.error('Error:', err);
    this.error = 'Impossible de charger vos quiz. Veuillez réessayer plus tard.';
    this.loading = false;
  }

  deleteQuiz(quizId: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce quiz ?')) {
      this.quizService.deleteQuiz(quizId).subscribe({
        next: () => {
          this.quizzes = this.quizzes.filter(quiz => quiz.id !== quizId);
          alert('Quiz supprimé avec succès !');
        },
        error: (err) => {
          console.error('Erreur lors de la suppression du quiz:', err);
          alert('Impossible de supprimer ce quiz. Veuillez réessayer plus tard.');
        }
      });
    }
  }

  openShareSidebar(quiz: any): void {
    this.selectedQuiz = quiz;
    this.shareableLink = `${window.location.origin}/quiz/${quiz.id}`;
    this.generateQRCode();
    this.sharePanel.open = true;
    this.isLinkCopied = false;
  }

  closeShareSidebar(): void {
    this.sharePanel.open = false;
  }

  generateQRCode(): void {
    this.qrCodeUrl = `https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=${encodeURIComponent(this.shareableLink)}`;
  }

  openDetailsPopup(quiz: any): void {
    this.selectedQuiz = quiz;
    this.detailsPanel.open = true;
  }

  closeDetailsPopup(): void {
    this.detailsPanel.open = false;
  }

  copyShareLink(inputElement: HTMLInputElement): void {
    inputElement.select();
    document.execCommand('copy');
    this.isLinkCopied = true;
    setTimeout(() => this.isLinkCopied = false, 2000);
  }

  shareVia(platform: string): void {
    let shareUrl = '';
    const quizUrl = encodeURIComponent(this.shareableLink);
    const quizTitle = encodeURIComponent(this.selectedQuiz?.title || 'Mon Quiz');

    switch(platform) {
      case 'facebook':
        shareUrl = `https://www.facebook.com/sharer/sharer.php?u=${quizUrl}`;
        break;
      case 'twitter':
        shareUrl = `https://twitter.com/intent/tweet?url=${quizUrl}&text=${quizTitle}`;
        break;
      case 'linkedin':
        shareUrl = `https://www.linkedin.com/sharing/share-offsite/?url=${quizUrl}`;
        break;
      case 'whatsapp':
        shareUrl = `https://wa.me/?text=${quizTitle}%20${quizUrl}`;
        break;
      case 'email':
        shareUrl = `mailto:?subject=${quizTitle}&body=${quizUrl}`;
        break;
    }

    if (shareUrl) {
      window.open(shareUrl, '_blank');
    }
  }
}
