import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { HistoryService, QuizHistory, GlobalStats } from '../../../services/history.service';

@Component({
  selector: 'app-history',
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.css'],
  standalone: true,
  imports: [CommonModule]
})
export class HistoryComponent implements OnInit, OnDestroy {
  history: QuizHistory[] = [];
  globalStats: GlobalStats = {
    totalQuizzes: 0,
    averageScore: 0,
    topCategory: ''
  };

  activeFilter: string = 'Tous';
  filters = ['Mois', 'Année', 'Tous'];

  loading = false;
  error: string | null = null;
  private subscriptions: Subscription[] = [];

  constructor(
    private historyService: HistoryService,
    public router: Router
  ) {}

  ngOnInit(): void {
    this.loadHistory();
    this.loadStats();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  loadHistory(): void {
    this.loading = true;
    this.error = null;

    this.subscriptions.push(
      this.historyService.getQuizHistory().subscribe({
        next: (data) => {
          this.history = data.map(item => ({
            ...item,
            date: this.formatDate(new Date(item.date))
          }));
          this.loading = false;
        },
        error: (err) => {
          console.error('Error loading quiz history:', err);
          this.error = typeof err === 'string' ? err : 'Failed to load quiz history';
          this.loading = false;
        }
      })
    );
  }

  loadStats(): void {
    this.subscriptions.push(
      this.historyService.getGlobalStats().subscribe({
        next: (stats) => {
          this.globalStats = stats;
        },
        error: (err) => {
          console.error('Error loading statistics:', err);
        }
      })
    );
  }

  setFilter(filter: string): void {
    this.activeFilter = filter;
    // In a real implementation, we would filter the history based on the selected filter
    // For now, we'll just reload all history
    this.loadHistory();
  }

  replayQuiz(quizId: number): void {
    this.router.navigate(['/participant/quiz', quizId]);
  }

  shareQuiz(quizId: number): void {
    // Create a shareable link
    const shareUrl = `${window.location.origin}/participant/quiz/${quizId}`;

    // Try to use the Web Share API if available
    if (navigator.share) {
      navigator.share({
        title: 'Check out this quiz!',
        text: 'I thought you might enjoy this quiz.',
        url: shareUrl,
      })
      .catch((error) => console.log('Error sharing:', error));
    } else {
      // Fallback: copy to clipboard
      this.copyToClipboard(shareUrl);
      alert('Link copied to clipboard!');
    }
  }

  exportData(): void {
    // For now, we'll just export the data as JSON
    const dataStr = JSON.stringify({
      history: this.history,
      statistics: this.globalStats
    }, null, 2);

    const dataUri = 'data:application/json;charset=utf-8,'+ encodeURIComponent(dataStr);
    const exportFileDefaultName = 'quiz-history.json';

    const linkElement = document.createElement('a');
    linkElement.setAttribute('href', dataUri);
    linkElement.setAttribute('download', exportFileDefaultName);
    linkElement.click();
  }

  private formatDate(date: Date): string {
    return date.toLocaleDateString('fr-FR', {
      day: '2-digit',
      month: 'short',
      year: 'numeric'
    });
  }

  private copyToClipboard(text: string): void {
    const textArea = document.createElement('textarea');
    textArea.value = text;
    document.body.appendChild(textArea);
    textArea.select();
    document.execCommand('copy');
    document.body.removeChild(textArea);
  }
}
