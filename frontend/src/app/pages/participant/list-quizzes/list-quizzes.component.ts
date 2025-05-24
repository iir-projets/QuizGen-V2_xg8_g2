import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { QuizService } from '../../../services/quiz.service';

@Component({
  selector: 'app-list-quizzes',
  templateUrl: './list-quizzes.component.html',
  styleUrls: ['./list-quizzes.component.css'],
  standalone: true,
  imports: [CommonModule]
})
export class ListQuizzesComponent implements OnInit {
  activeFilter: string = 'Tous';
  filters = ['Public', 'Privé', 'Tous'];
  searchQuery: string = '';
  quizzes: any[] = [];
  filteredQuizzes: any[] = [];
  isLoading = true;

  constructor(
    private quizService: QuizService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadQuizzes();
  }

  loadQuizzes(): void {
    this.isLoading = true;
    this.quizService.getPublicQuizzes().subscribe({
      next: (quizzes) => {
        this.quizzes = quizzes;
        this.filteredQuizzes = quizzes;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading quizzes', err);
        this.isLoading = false;
      }
    });
  }

  setFilter(filter: string): void {
    this.activeFilter = filter;
    this.updateFilteredQuizzes();
  }

  updateFilteredQuizzes(): void {
    this.filteredQuizzes = this.quizzes.filter(quiz => {
      const matchesSearch = quiz.title.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
        quiz.description.toLowerCase().includes(this.searchQuery.toLowerCase());
      const matchesFilter = this.activeFilter === 'Tous' ||
        (this.activeFilter === 'Public' && quiz.isPublic) ||
        (this.activeFilter === 'Privé' && !quiz.isPublic);
      return matchesSearch && matchesFilter;
    });
  }

  onSearchChange(event: Event): void {
    this.searchQuery = (event.target as HTMLInputElement).value;
    this.updateFilteredQuizzes();
  }

  getDifficultyColor(difficulty: string): string {
    switch(difficulty) {
      case 'Facile': return '#4CAF50';
      case 'Moyen': return '#FFC107';
      case 'Difficile': return '#F44336';
      default: return '#9333EA';
    }
  }


  navigateToQuiz(quizId: number): void {
    this.router.navigate([`/participant/quiz/${quizId}`]);
  }

  navigateToHistory(): void {
    this.router.navigate(['/participant/history']);
  }
}
