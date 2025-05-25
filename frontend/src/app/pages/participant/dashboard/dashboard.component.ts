import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { Subscription } from 'rxjs';
import { QuizService } from '../../../services/quiz.service';
import { HistoryService, QuizHistory } from '../../../services/history.service';
import { AuthService } from '../../../auth/auth.service';

interface StatCard {
  icon: string;
  value: string | number;
  label: string;
}

interface RecentActivity {
  id: number;
  type: 'quiz_completed' | 'quiz_started' | 'achievement';
  title: string;
  date: string;
  score?: number;
  passed?: boolean;
}

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
  standalone: true,
  imports: [CommonModule, RouterModule]
})
export class DashboardComponent implements OnInit, OnDestroy {
  // User information
  username: string | null = null;
  userAvatar: string | null = null;
  userInitials: string = 'P';

  // Dashboard data
  stats: StatCard[] = [];
  recentQuizzes: any[] = [];
  popularQuizzes: any[] = [];
  recentActivity: RecentActivity[] = [];

  // UI states
  loading = {
    stats: true,
    recentQuizzes: true,
    popularQuizzes: true,
    activity: true
  };

  error = {
    stats: null as string | null,
    recentQuizzes: null as string | null,
    popularQuizzes: null as string | null,
    activity: null as string | null
  };

  // Sidebar state
  sidebarActive: boolean = false;

  private subscriptions: Subscription[] = [];

  constructor(
    private quizService: QuizService,
    private historyService: HistoryService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadUserInfo();
    this.loadStats();
    this.loadRecentQuizzes();
    this.loadPopularQuizzes();
    this.loadRecentActivity();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  loadUserInfo(): void {
    const currentUser = this.authService.getCurrentUser();
    if (currentUser) {
      this.username = currentUser.name || currentUser.email || 'Participant';
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

  loadStats(): void {
    this.loading.stats = true;
    this.error.stats = null;

    this.subscriptions.push(
      this.historyService.getGlobalStats().subscribe({
        next: (stats) => {
          this.stats = [
            { icon: '📊', value: stats.totalQuizzes, label: 'Quiz complétés' },
            { icon: '📈', value: `${stats.averageScore}%`, label: 'Score moyen' },
            { icon: '🏆', value: stats.topCategory || 'N/A', label: 'Meilleure catégorie' },
            { icon: '⏱️', value: this.calculateTotalTime(), label: 'Temps total' }
          ];
          this.loading.stats = false;
        },
        error: (err) => {
          console.error('Error loading stats:', err);
          this.error.stats = 'Impossible de charger les statistiques';
          this.loading.stats = false;
        }
      })
    );
  }

  loadRecentQuizzes(): void {
    this.loading.recentQuizzes = true;
    this.error.recentQuizzes = null;

    this.subscriptions.push(
      this.quizService.getPublicQuizzes().subscribe({
        next: (quizzes) => {
          // Sort by date and take the 3 most recent
          this.recentQuizzes = quizzes
            .sort((a, b) => new Date(b.createdAt || 0).getTime() - new Date(a.createdAt || 0).getTime())
            .slice(0, 3);
          this.loading.recentQuizzes = false;
        },
        error: (err) => {
          console.error('Error loading recent quizzes:', err);
          this.error.recentQuizzes = 'Impossible de charger les quiz récents';
          this.loading.recentQuizzes = false;
        }
      })
    );
  }

  loadPopularQuizzes(): void {
    this.loading.popularQuizzes = true;
    this.error.popularQuizzes = null;

    this.subscriptions.push(
      this.quizService.getPublicQuizzes().subscribe({
        next: (quizzes) => {
          // Sort by popularity (for now, just random)
          this.popularQuizzes = [...quizzes]
            .sort(() => 0.5 - Math.random())
            .slice(0, 3);
          this.loading.popularQuizzes = false;
        },
        error: (err) => {
          console.error('Error loading popular quizzes:', err);
          this.error.popularQuizzes = 'Impossible de charger les quiz populaires';
          this.loading.popularQuizzes = false;
        }
      })
    );
  }

  loadRecentActivity(): void {
    this.loading.activity = true;
    this.error.activity = null;

    this.subscriptions.push(
      this.historyService.getQuizHistory().subscribe({
        next: (history) => {
          this.recentActivity = history.map(item => ({
            id: item.quizId,
            type: 'quiz_completed',
            title: item.title,
            date: this.formatDate(new Date(item.date)),
            score: item.score,
            passed: item.passed
          })).slice(0, 5);
          this.loading.activity = false;
        },
        error: (err) => {
          console.error('Error loading activity:', err);
          this.error.activity = 'Impossible de charger l\'activité récente';
          this.loading.activity = false;
        }
      })
    );
  }

  navigateToQuiz(quizId: number): void {
    this.router.navigate([`/participant/quiz/${quizId}`]);
  }

  navigateToAllQuizzes(): void {
    this.router.navigate(['/participant/quizzes']);
  }

  navigateToHistory(): void {
    this.router.navigate(['/participant/history']);
  }

  navigateToProfile(): void {
    this.router.navigate(['/participant/profile']);
  }

  private calculateTotalTime(): string {
    // This would ideally come from the backend
    // For now, just return a placeholder
    return '3h 45m';
  }

  private formatDate(date: Date): string {
    return date.toLocaleDateString('fr-FR', {
      day: '2-digit',
      month: 'short',
      year: 'numeric'
    });
  }

  getDifficultyColor(difficulty: string): string {
    switch(difficulty) {
      case 'Facile': return '#4CAF50';
      case 'Moyen': return '#FFC107';
      case 'Difficile': return '#F44336';
      default: return '#9333EA';
    }
  }

  retryLoading(section: 'stats' | 'recentQuizzes' | 'popularQuizzes' | 'activity'): void {
    switch(section) {
      case 'stats':
        this.loadStats();
        break;
      case 'recentQuizzes':
        this.loadRecentQuizzes();
        break;
      case 'popularQuizzes':
        this.loadPopularQuizzes();
        break;
      case 'activity':
        this.loadRecentActivity();
        break;
    }
  }
}
