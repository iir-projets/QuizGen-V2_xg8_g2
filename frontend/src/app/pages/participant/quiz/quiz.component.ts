import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router, RouterModule} from '@angular/router';
import {CommonModule} from '@angular/common';
import {Subscription} from 'rxjs';
import {AuthService} from '../../../auth/auth.service';
import {QuizService} from '../../../services/quiz.service';
import {Quiz} from '../../../shared/model/Quiz.model';

interface Question {
  id: number;
  text: string;
  type: 'qcm' | 'vf';
  options?: string[]; // Pour QCM
  reponseCorrecte?: boolean; // Pour V/F
  correctAnswer?: string; // Pour QCM
  explanation?: string;
  score?: number;
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
  imports: [CommonModule, RouterModule],
  styleUrls: ['./quiz.component.css'],
  standalone: true
})
export class QuizComponent implements OnInit, OnDestroy {
  quizId: number | null = null;
  quiz: Quiz | null = null;
  questions: Question[] = [];
  currentQuestionIndex = 0;
  selectedAnswers: { [questionId: number]: string } = {};
  quizResult: QuizResult | null = null;
  timeLeft: number = 600;
  timer: any;
  errorMessage: string | null = null;
  loading = true;
  private subscriptions: Subscription[] = [];

  // User information
  username: string | null = null;
  userAvatar: string | null = null;
  userInitials: string = 'P';

  // Sidebar state
  sidebarActive: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private quizService: QuizService,
    private authService: AuthService,
    private router: Router
  ) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const quizId = +params['id'];
      this.quizId = quizId;
      this.loadQuiz(quizId);
    });
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

  loadQuiz(quizId: number): void {
    this.loading = true;
    this.errorMessage = null;

    this.subscriptions.push(
      this.quizService.getQuizById(quizId).subscribe({
        next: (quiz) => {
          this.quiz = quiz;
          try {
            this.questions = this.parseQuestions(quiz.questions);
            this.timeLeft = this.parseTimeLimit(quiz.timeLimit);
            this.startTimer();
            this.loading = false;
          } catch (e) {
            console.error('Error parsing questions', e);
            this.handleError('Format de quiz invalide');
            this.timeLeft = 600;
            this.startTimer();
          }
        },
        error: (err) => {
          console.error('Error loading quiz', err);
          this.handleError(err);
          this.router.navigate(['/participant/quizzes']);
        }
      })
    );
  }

  private parseQuestions(questionsJson: string): Question[] {
    try {
      console.log('Parsing questions JSON:', questionsJson);
      const parsed = JSON.parse(questionsJson);
      if (!Array.isArray(parsed)) {
        throw new Error('Questions should be an array');
      }

      return parsed.map((q, index) => {
        console.log('Processing question:', q);

        // Ensure question has an ID
        const id = q.id != null ? Number(q.id) : index + 1;

        // Handle QCM questions
        let options: string[] | undefined;
        let correctAnswer: string | undefined;

        if (q.type === 'qcm') {
          // Handle different formats of options
          if (Array.isArray(q.options)) {
            // Check if options are objects with text property or just strings
            if (q.options.length > 0 && typeof q.options[0] === 'object' && q.options[0].text) {
              // Options are objects with text property
              options = q.options.map((opt: any) => opt.text);

              // Find the correct option
              const correctOption = q.options.find((opt: any) => {
                if (typeof opt.isCorrect === 'boolean') {
                  return opt.isCorrect;
                } else if (typeof opt.isCorrect === 'string') {
                  return opt.isCorrect.toLowerCase() === 'true' || opt.isCorrect === '1';
                } else if (typeof opt.isCorrect === 'number') {
                  return opt.isCorrect === 1;
                }
                return false;
              });

              correctAnswer = correctOption?.text;
              console.log(`QCM question ${id}: Found correct option:`, correctOption);
            } else {
              // Options are just strings
              options = q.options;
              // In this case, correctAnswer might be stored separately
              correctAnswer = q.correctAnswer;
              console.log(`QCM question ${id}: Using string options:`, options);
            }
          } else if (typeof q.options === 'string') {
            // Options might be a comma-separated string
            options = q.options.split(',').map((opt: string) => opt.trim());
            correctAnswer = q.correctAnswer;
            console.log(`QCM question ${id}: Parsed options from string:`, options);
          }
        }

        // Handle VF questions
        let reponseCorrecte: boolean | undefined;
        if (q.type === 'vf') {
          // Handle different formats of reponseCorrecte
          if (typeof q.reponseCorrecte === 'boolean') {
            reponseCorrecte = q.reponseCorrecte;
          } else if (typeof q.reponseCorrecte === 'string') {
            const rcLower = q.reponseCorrecte.toLowerCase().trim();
            reponseCorrecte = rcLower === 'true' || rcLower === 'vrai' || rcLower === '1';
          } else if (typeof q.reponseCorrecte === 'number') {
            reponseCorrecte = q.reponseCorrecte === 1;
          }

          // For VF questions, correctAnswer should be 'Vrai' or 'Faux'
          correctAnswer = reponseCorrecte != null ? (reponseCorrecte ? 'Vrai' : 'Faux') : undefined;
          console.log(`VF question ${id}: Parsed reponseCorrecte:`, reponseCorrecte, 'correctAnswer:', correctAnswer);
        }

        // Ensure score is a number
        const score = q.score != null ? Number(q.score) : 1;

        // Log the processed question for debugging
        console.log('Processed question:', {
          id,
          text: q.text,
          type: q.type,
          options,
          correctAnswer,
          reponseCorrecte,
          score
        });

        return {
          id,
          text: q.text,
          type: q.type,
          options,
          correctAnswer,
          reponseCorrecte,
          explanation: q.explanation,
          score
        };
      });
    } catch (e) {
      console.error('Error parsing questions JSON', e);
      console.error('JSON content:', questionsJson);
      throw new Error('Invalid questions format');
    }
  }

  submitQuiz(): void {
    if (this.timer) {
      clearInterval(this.timer);
    }

    if (!this.quiz || !this.quizId) {
      this.handleError('Quiz information missing');
      return;
    }

    const participantId = this.authService.getUserId();
    if (!participantId) {
      this.handleError('User not authenticated');
      return;
    }

    if (!this.authService.getToken()) {
      this.handleError('Session expired');
      this.authService.logout();
      this.router.navigate(['/login']);
      return;
    }

    // Format answers before submission
    const formattedAnswers = this.formatAnswersForSubmission();

    // Log the answers being submitted
    console.log('Original answers:', this.selectedAnswers);
    console.log('Formatted answers for submission:', formattedAnswers);

    const responseDTO = {
      quizId: this.quizId,
      participantId: participantId,
      answers: formattedAnswers
    };

    this.loading = true;
    this.subscriptions.push(
      this.quizService.submitQuiz(this.quizId, responseDTO).subscribe({
        next: (result) => {
          console.log('Quiz result received:', result);

          // Ensure we have a valid score
          let score = result.score;
          if (score === undefined || score === null) {
            console.warn('Score is undefined or null, using 0 as default');
            score = 0;
          }

          // Determine if the quiz was passed
          const passingGrade = this.quiz?.passingGrade || 60;
          const passed = result.passed !== undefined ? result.passed : (score >= passingGrade);

          console.log(`Score: ${score}, Passing grade: ${passingGrade}, Passed: ${passed}`);

          this.quizResult = {
            quizId: result.quizId,
            quizTitle: result.quizTitle || this.quiz?.title || 'Quiz',
            score: score,
            passed: passed,
            submissionDate: result.submissionDate || new Date().toISOString(),
            correctAnswers: result.correctAnswers
          };

          console.log('Final quiz result object:', this.quizResult);
          this.loading = false;
        },
        error: (err) => {
          console.error('Error submitting quiz', err);
          this.handleError(err);
          this.loading = false;
        }
      })
    );
  }

  selectAnswer(questionId: number | undefined, answer: string): void {
    if (questionId) {
      // Get the question type to format the answer appropriately
      const question = this.questions.find(q => q.id === questionId);
      if (question) {
        if (question.type === 'vf') {
          // For VF questions, normalize the answer to a consistent format
          const normalizedAnswer = answer.toLowerCase().trim();

          if (normalizedAnswer === 'true' || normalizedAnswer === 'vrai' || normalizedAnswer === '1') {
            this.selectedAnswers[questionId] = 'Vrai';
            console.log(`Selected VF answer for question ${questionId}: ${answer} -> Vrai`);
          } else if (normalizedAnswer === 'false' || normalizedAnswer === 'faux' || normalizedAnswer === '0') {
            this.selectedAnswers[questionId] = 'Faux';
            console.log(`Selected VF answer for question ${questionId}: ${answer} -> Faux`);
          } else {
            // Default to false for any unrecognized value
            this.selectedAnswers[questionId] = 'Faux';
            console.log(`Selected VF answer for question ${questionId}: ${answer} -> Faux (default)`);
          }
        } else if (question.type === 'qcm') {
          // For QCM questions, try to match with an option
          if (question.options && question.options.length > 0) {
            const normalizedAnswer = answer.toLowerCase().trim();

            // Try to find an exact match (case-insensitive)
            const matchingOption = question.options.find(
              opt => opt.toLowerCase().trim() === normalizedAnswer
            );

            if (matchingOption) {
              // Use the exact option text if found
              this.selectedAnswers[questionId] = matchingOption;
              console.log(`Selected QCM answer for question ${questionId}: ${answer} -> ${matchingOption} (matched)`);
            } else {
              // Otherwise keep the original answer
              this.selectedAnswers[questionId] = answer;
              console.log(`Selected QCM answer for question ${questionId}: ${answer} (no match)`);
            }
          } else {
            this.selectedAnswers[questionId] = answer;
            console.log(`Selected QCM answer for question ${questionId}: ${answer} (no options available)`);
          }
        } else {
          // For other question types, store the answer as is
          this.selectedAnswers[questionId] = answer;
          console.log(`Selected answer for question ${questionId} of type ${question.type}: ${answer}`);
        }
      } else {
        // If question not found, store the answer as is
        this.selectedAnswers[questionId] = answer;
        console.log(`Selected answer for unknown question ${questionId}: ${answer}`);
      }
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

  parseTimeLimit(timeLimit: string): number {
    if (!timeLimit) return 600;

    if (/^\d+$/.test(timeLimit)) {
      return parseInt(timeLimit) * 60;
    }

    const parts = timeLimit.split(':').map(part => {
      const num = parseInt(part);
      return isNaN(num) ? 0 : num;
    });

    if (parts.length === 3) {
      return parts[0] * 3600 + parts[1] * 60 + parts[2];
    }

    if (parts.length === 2) {
      return parts[0] * 60 + parts[1];
    }

    return 600;
  }

  formatTime(seconds: number): string {
    if (isNaN(seconds)) return '00:00';
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  }

  private handleError(error: any): void {
    this.loading = false;
    this.errorMessage = typeof error === 'string' ? error :
      error.error?.message || error.message || 'Une erreur est survenue';

    if (error.status === 401) {
      this.authService.logout();
      this.router.navigate(['/login'], {
        queryParams: {returnUrl: this.router.url}
      });
    }
  }

  ngOnDestroy(): void {
    if (this.timer) {
      clearInterval(this.timer);
    }
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  navigateToListe() {
    this.router.navigate(['/participant/quizzes']);
  }

  /**
   * Format answers for submission to ensure they match the expected format in the backend
   */
  private formatAnswersForSubmission(): { [questionId: number]: string } {
    const formattedAnswers: { [questionId: number]: string } = {};
    console.log('Formatting answers for submission...');

    // Process each answer based on its question type
    for (const [questionIdStr, answer] of Object.entries(this.selectedAnswers)) {
      const questionId = Number(questionIdStr);
      const question = this.questions.find(q => q.id === questionId);

      if (!question) {
        // If question not found, keep the original answer
        formattedAnswers[questionId] = answer;
        console.log(`Question ${questionId} not found, using original answer: ${answer}`);
        continue;
      }

      console.log(`Formatting answer for question ${questionId} of type ${question.type}: ${answer}`);

      if (question.type === 'vf') {
        // For VF questions, ensure consistent boolean representation
        const normalizedAnswer = answer.toLowerCase().trim();

        if (normalizedAnswer === 'true' || normalizedAnswer === 'vrai' || normalizedAnswer === '1') {
          formattedAnswers[questionId] = 'Vrai';
          console.log(`VF question ${questionId}: Normalized "${answer}" to "Vrai"`);
        } else if (normalizedAnswer === 'false' || normalizedAnswer === 'faux' || normalizedAnswer === '0') {
          formattedAnswers[questionId] = 'Faux';
          console.log(`VF question ${questionId}: Normalized "${answer}" to "Faux"`);
        } else {
          // Default to false for any other value
          formattedAnswers[questionId] = 'Faux';
          console.log(`VF question ${questionId}: Unrecognized value "${answer}", defaulting to "Faux"`);
        }
      } else if (question.type === 'qcm') {
        // For QCM questions, ensure the answer matches one of the options exactly
        if (question.options && question.options.length > 0) {
          const normalizedAnswer = answer.toLowerCase().trim();

          // Try to find an exact match (case-insensitive)
          const matchingOption = question.options.find(
            opt => opt.toLowerCase().trim() === normalizedAnswer
          );

          if (matchingOption) {
            // Use the exact option text if found
            formattedAnswers[questionId] = matchingOption;
            console.log(`QCM question ${questionId}: Using exact option "${matchingOption}"`);
          } else {
            // If no exact match, use the original answer
            formattedAnswers[questionId] = answer;
            console.log(`QCM question ${questionId}: No exact match found, using original answer "${answer}"`);
          }
        } else {
          formattedAnswers[questionId] = answer;
          console.log(`QCM question ${questionId}: No options available, using original answer "${answer}"`);
        }
      } else {
        // For other question types, keep the original answer
        formattedAnswers[questionId] = answer;
        console.log(`Question ${questionId} of type ${question.type}: Using original answer "${answer}"`);
      }
    }

    console.log('Final formatted answers:', formattedAnswers);
    return formattedAnswers;
  }
}
