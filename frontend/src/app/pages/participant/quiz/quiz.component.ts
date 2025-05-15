import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { AuthService } from '../../../auth/auth.service';

interface Question {
  id: number;
  text: string;
  options: string[];
  correctAnswer: string;
  explanation?: string;
}

interface Quiz {
  id: number;
  title: string;
  description: string;
  questions: string; // JSON string
  timeLimit: string;
  autoCorrection: boolean;
  showResults: boolean;
}

interface QuizResult {
  quizId: number;
  quizTitle: string;
  score: number;
  passed: boolean;
  submissionDate: string;
  correctAnswers?: { [key: number]: string };
  questionExplanations?: { [key: number]: string };
}

@Component({
  selector: 'app-quiz',
  templateUrl: './quiz.component.html',
  imports: [CommonModule],
  styleUrls: ['./quiz.component.css'],
  standalone: true
})
export class QuizComponent implements OnInit, OnDestroy {
  quizId: number;
  quiz: Quiz | null = null;
  questions: Question[] = [];
  currentQuestionIndex = 0;
  selectedAnswers: { [questionId: number]: string } = {};
  quizResult: QuizResult | null = null;
  timeLeft: number = 10;
  timer: any;
  errorMessage: string | null = null;
  loading = true;
  private subscriptions: Subscription[] = [];

  constructor(
    private route: ActivatedRoute,
    private authService: AuthService,
    private router: Router
  ) {
    this.quizId = parseInt(this.route.snapshot.paramMap.get('id') || '1');
  }

  ngOnInit(): void {
    this.loadQuiz();
  }

  loadQuiz(): void {
    this.loading = true;
    this.errorMessage = null;

    // Données statiques pour le quiz
    this.quiz = {
      id: 1,
      title: 'Quiz de Culture Générale',
      description: 'Testez vos connaissances générales',
      questions: JSON.stringify([
        {
          id: 1,
          text: 'Quelle est la capitale de la France?',
          options: ['Londres', 'Berlin', 'Paris', 'Madrid'],
          correctAnswer: 'Paris',
          explanation: 'Paris est la capitale de la France depuis le 5ème siècle.'
        },
        {
          id: 2,
          text: 'Quel est le plus grand océan du monde?',
          options: ['Océan Atlantique', 'Océan Indien', 'Océan Arctique', 'Océan Pacifique'],
          correctAnswer: 'Océan Pacifique',
          explanation: "L'océan Pacifique couvre environ 1/3 de la surface terrestre."
        },
        {
          id: 3,
          text: 'Qui a peint la Joconde?',
          options: ['Vincent van Gogh', 'Pablo Picasso', 'Léonard de Vinci', 'Michel-Ange'],
          correctAnswer: 'Léonard de Vinci',
          explanation: 'Léonard de Vinci a peint ce portrait au début du 16ème siècle.'
        }
      ]),
      timeLimit: '00:05:00',
      autoCorrection: true,
      showResults: true
    };

    try {
      this.questions = JSON.parse(this.quiz.questions);
      this.timeLeft = this.parseTimeLimit(this.quiz.timeLimit);
      this.startTimer();
      this.loading = false;
    } catch (e) {
      console.error('Error parsing questions', e);
      this.handleError('Invalid quiz format');
      this.questions = [];
      this.loading = false;
    }
  }

  private handleError(error: any): void {
    console.error('Quiz Error:', error);
    this.errorMessage = typeof error === 'string' ? error :
      error.error?.message || error.message || 'An error occurred';

    if (error.status === 401) {
      this.authService.logout();
      this.router.navigate(['/login'], { queryParams: { returnUrl: this.router.url } });
    }
  }

  get currentQuestion(): Question | null {
    return this.questions[this.currentQuestionIndex] || null;
  }

  get currentQuestionId(): number | null {
    return this.currentQuestion?.id || null;
  }

  parseTimeLimit(timeLimit: string): number {
    const parts = timeLimit.split(':').map(part => parseInt(part) || 0);
    return parts[0] * 3600 + parts[1] * 60 + parts[2];
  }

  startTimer(): void {
    this.timer = setInterval(() => {
      if (this.timeLeft > 0) {
        this.timeLeft--;
      } else {
        this.timeExpired();
      }
    }, 1000);
  }

  timeExpired(): void {
    clearInterval(this.timer);
    if (!this.quizResult) {
      this.submitQuiz();
    }
  }

  selectAnswer(questionId: number | undefined, answer: string): void {
    if (questionId) {
      this.selectedAnswers[questionId] = answer;
    }
  }

  nextQuestion(): void {
    if (this.currentQuestionIndex < this.questions.length - 1) {
      this.currentQuestionIndex++;
    }
  }

  previousQuestion(): void {
    if (this.currentQuestionIndex > 0) {
      this.currentQuestionIndex--;
    }
  }

  submitQuiz(): void {
    if (this.timer) {
      clearInterval(this.timer);
    }

    // Calcul du score
    let correctCount = 0;
    const correctAnswers: { [key: number]: string } = {};
    const questionExplanations: { [key: number]: string } = {};

    this.questions.forEach(question => {
      correctAnswers[question.id] = question.correctAnswer;
      if (question.explanation) {
        questionExplanations[question.id] = question.explanation;
      }
      if (this.selectedAnswers[question.id] === question.correctAnswer) {
        correctCount++;
      }
    });

    const score = Math.round((correctCount / this.questions.length) * 100);
    const passed = score >= 70; // Seuil de réussite à 70%

    // Création du résultat statique
    this.quizResult = {
      quizId: this.quizId,
      quizTitle: this.quiz?.title || 'Quiz',
      score: score,
      passed: passed,
      submissionDate: new Date().toISOString(),
      correctAnswers: correctAnswers,
      questionExplanations: questionExplanations
    };
  }

  formatTime(seconds: number): string {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  }

  retryQuiz(): void {
    this.quizResult = null;
    this.currentQuestionIndex = 0;
    this.selectedAnswers = {};
    this.timeLeft = this.parseTimeLimit(this.quiz?.timeLimit || '00:05:00');
    this.startTimer();
  }

  ngOnDestroy(): void {
    if (this.timer) {
      clearInterval(this.timer);
    }
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }
}
