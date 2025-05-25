import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { QuizService } from '../../../services/quiz.service';
import { AuthService } from '../../../auth/auth.service';

@Component({
  selector: 'app-list-quizzes',
  templateUrl: './list-quizzes.component.html',
  styleUrls: ['./list-quizzes.component.css'],
  standalone: true,
  imports: [CommonModule, RouterModule]
})
export class ListQuizzesComponent implements OnInit {
  activeFilter: string = 'Tous';
  filters = ['Public', 'Privé', 'Tous'];
  searchQuery: string = '';
  quizzes: any[] = [];
  filteredQuizzes: any[] = [];
  isLoading = true;

  // User information
  username: string | null = null;
  userAvatar: string | null = null;
  userInitials: string = 'P';

  // Sidebar state
  sidebarActive: boolean = false;

  constructor(
    private quizService: QuizService,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadQuizzes();
    this.loadUserInfo();
  }

  loadUserInfo(): void {
    // Get user from auth service
    const currentUser = this.authService.getCurrentUser();
    if (currentUser) {
      this.username = currentUser.name || currentUser.email || 'Participant';
      // Generate initials from name or email
      this.userInitials = this.getInitials(this.username);
      // You can set avatar URL here if available in the future
      // this.userAvatar = currentUser.avatarUrl;
    } else {
      this.username = 'Participant';
      this.userInitials = 'P';
    }
  }

  getInitials(name: string | null): string {
    if (!name) return 'P';

    const parts = name.split(' ');
    if (parts.length === 1) {
      return parts[0].charAt(0).toUpperCase();
    }

    return (parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
  }

  toggleSidebar(): void {
    this.sidebarActive = !this.sidebarActive;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
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
