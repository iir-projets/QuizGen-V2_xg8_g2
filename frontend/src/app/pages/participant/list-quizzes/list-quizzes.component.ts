import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

interface Quiz {
  id: number;
  title: string;
  category: string;
  description: string;
  timeLimit: string;
  questionCount: number;
  difficulty: 'Facile' | 'Moyen' | 'Difficile';
  isPublic: boolean;
  publishDate: string;
}

@Component({
  selector: 'app-list-quizzes',
  templateUrl: './list-quizzes.component.html',
  styleUrls: ['./list-quizzes.component.css'],
  standalone: true,
  imports: [CommonModule, RouterModule]
})
export class ListQuizzesComponent {
  activeFilter: string = 'Tous';
  filters = ['Public', 'Privé', 'Tous'];
  searchQuery: string = '';

  quizzes: Quiz[] = [
    {
      id: 1,
      title: 'Culture Générale Avancée',
      category: 'CultureG',
      description: 'Testez vos connaissances générales avec des questions variées',
      timeLimit: '10 min',
      questionCount: 20,
      difficulty: 'Moyen',
      isPublic: true,
      publishDate: '15 Nov 2023'
    },
    {
      id: 2,
      title: 'Développement Web Moderne',
      category: 'Techno',
      description: 'Quiz complet sur les technologies web actuelles',
      timeLimit: '15 min',
      questionCount: 25,
      difficulty: 'Difficile',
      isPublic: true,
      publishDate: '05 Déc 2023'
    },
    {
      id: 3,
      title: 'Science Fondamentale',
      category: 'Science',
      description: 'Les bases de la physique, chimie et biologie',
      timeLimit: '8 min',
      questionCount: 15,
      difficulty: 'Facile',
      isPublic: false,
      publishDate: '01 Déc 2023'
    },
    {
      id: 4,
      title: 'Histoire Contemporaine',
      category: 'Histoire',
      description: 'Événements marquants du 20ème siècle',
      timeLimit: '12 min',
      questionCount: 18,
      difficulty: 'Moyen',
      isPublic: true,
      publishDate: '10 Nov 2023'
    }
  ];

  filteredQuizzes = this.quizzes;

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
}
