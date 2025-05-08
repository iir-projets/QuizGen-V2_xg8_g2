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
      // Format: [2025,5,7,15,8] -> année, mois (1-12), jour, heure, minute
      return new Date(
        dateInput[0],
        dateInput[1] - 1, // Les mois sont 0-indexés dans Date
        dateInput[2],
        dateInput[3],
        dateInput[4]
      );
    }

    if (typeof dateInput === 'string') {
      return new Date(dateInput);
    }

    return new Date(); // Fallback si le format n'est pas reconnu
  }

  formatDate(date: Date): string {
    return this.datePipe.transform(date, 'dd/MM/yyyy') || '';
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
}
