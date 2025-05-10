import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Quiz, QuizResponse, QuizResult} from '../../../shared/model/quiz.model';
import {QuizParticipationService} from '../../../services/QuizParticipation.service';
import {CommonModule} from '@angular/common';
import {Question} from '../../../shared/model/question.model';
import {Subscription} from 'rxjs';
import {AuthService} from '../../../auth/auth.service';

interface QuizResult {
  quizId: number;
  quizTitle: string;
  score: number;
  passed: boolean;
  submissionDate: string;
  scoreDetails?: {
    totalCorrect: number;
    totalQuestions: number;
    questionResults: Array<{
      questionId: number;
      questionText: string;
      questionType: string;
      userAnswer: string;
      isCorrect: boolean;
      scoreObtained: number;
      maxScore: number;
    }>;
  };
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
    private quizService: QuizParticipationService,
    private authService: AuthService,
    private router: Router
  ) {
    this.quizId = parseInt(this.route.snapshot.paramMap.get('id') || '2');
  }

  ngOnInit(): void {
    this.loadQuiz();
  }

  loadQuiz(): void {
    this.loading = true;
    this.errorMessage = null;

    const quizSub = this.quizService.getQuizById(this.quizId).subscribe({
      next: (quiz) => {
        this.quiz = quiz;
        try {
          this.questions = JSON.parse(quiz.questions).map((q: any, index: number) => ({
            id: q.id || index + 1,
            text: q.text || 'Question',
            options: q.options || [],
            correctAnswer: q.correctAnswer || '',  // Ensure default value if undefined
            explanation: q.explanation || ''       // Ensure default value if undefined
          }));
          this.timeLeft = this.parseTimeLimit(quiz.timeLimit || '00:05:00');
          this.startTimer();
        } catch (e) {
          console.error('Error parsing questions', e);
          this.handleError('Invalid quiz format');
          this.questions = [];
        }
        this.loading = false;
      },
      error: (err) => {
        this.handleError(err);
        this.loading = false;
      }
    });

    this.subscriptions.push(quizSub);
  }

  private handleError(error: any): void {
    console.error('Quiz Error:', error);
    this.errorMessage = typeof error === 'string' ? error :
      error.error?.message || error.message || 'An error occurred';

    if (error.status === 401) {
      this.authService.logout();
      this.router.navigate(['/login'], {queryParams: {returnUrl: this.router.url}});
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
      // this.submitQuiz();
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

  /* submitQuiz(): void {
     if (this.timer) {
       clearInterval(this.timer);
     }

     if (!this.quiz) {
       this.handleError('No quiz data available');
       return;
     }

     const userId = this.authService.getCurrentUserId();
     if (!userId) {
       this.handleError('User not authenticated');
       this.authService.logout();
       this.router.navigate(['/login'], {queryParams: {returnUrl: this.router.url}});
       return;
     }

     const response: QuizResponse = {
       quizId: this.quizId,
       participantId: userId,
       answers: this.selectedAnswers
     };

     this.loading = true;
     this.errorMessage = null;

     const submitSub = this.quizService.submitQuiz(this.quizId, response).subscribe({
       next: (result) => {
         this.quizResult = result;
         this.loading = false;
       },
       error: (err) => {
         this.loading = false;
         this.handleError(err);
         if (typeof err === 'string' && err.includes('Session expired')) {
           this.authService.logout();
           this.router.navigate(['/login'], {
             queryParams: {returnUrl: this.router.url, reason: 'session_expired'}
           });
         }
       }
     });

     this.subscriptions.push(submitSub);
   }*/

  formatTime(seconds: number): string {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  }

  retryQuiz(): void {
    this.quizResult = null;
    this.currentQuestionIndex = 0;
    this.selectedAnswers = {};
    this.loadQuiz();
  }

  ngOnDestroy(): void {
    if (this.timer) {
      clearInterval(this.timer);
    }
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }


  submitQuiz(): void {
    if (this.timer) {
      clearInterval(this.timer);
    }

    const userId = this.authService.getCurrentUserId();
    const response: QuizResponse = {
      quizId: this.quizId,
      participantId: userId,
      answers: this.selectedAnswers,
    };

    this.loading = true;
    this.quizService.submitQuiz(this.quizId, response).subscribe({
      next: (result) => {
        this.quizResult = result;
        this.loading = false;
      },
      error: (err) => {
        this.handleError(err);
      }
    });
  }
}
