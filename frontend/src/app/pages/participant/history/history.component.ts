import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

interface QuizHistory {
  id: number;
  category: string;
  date: string;
  title: string;
  score: number;
}

interface GlobalStats {
  totalQuizzes: number;
  averageScore: number;
  topCategory: string;
}

@Component({
  selector: 'app-history',
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.css'],
  standalone: true,
  imports: [CommonModule]
})
export class HistoryComponent {
  history: QuizHistory[] = [
    {
      id: 1,
      category: 'CultureG',
      date: '12 Nov 2023',
      title: 'Quiz sur la culture générale',
      score: 78
    },
    {
      id: 2,
      category: 'Techno',
      date: '05 Déc 2023',
      title: 'Quiz sur le développement web',
      score: 68
    },
    {
      id: 3,
      category: 'Science',
      date: '01 Déc 2023',
      title: 'Quiz sur la science',
      score: 85
    },
    {
      id: 4,
      category: 'CultureG',
      date: '12 Nov 2023',
      title: 'Quiz sur la culture générale',
      score: 92
    }
  ];

  globalStats: GlobalStats = {
    totalQuizzes: 24,
    averageScore: 82,
    topCategory: 'Science'
  };

  activeFilter: string = 'Tous';
  filters = ['Mois', 'Année', 'Tous'];

  setFilter(filter: string): void {
    this.activeFilter = filter;
  }

  replayQuiz(quizId: number): void {
    console.log('Rejouer le quiz', quizId);
  }

  shareQuiz(quizId: number): void {
    console.log('Partager le quiz', quizId);
  }

  exportData(): void {
    console.log('Exporter les données');
  }
}
