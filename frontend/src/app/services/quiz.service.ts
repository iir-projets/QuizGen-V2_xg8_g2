import {Observable, throwError} from 'rxjs';
import {AuthService} from '../auth/auth.service';
import {HttpClient, HttpErrorResponse, HttpHeaders} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {catchError, map, tap} from 'rxjs/operators';
import {Router} from '@angular/router';
import {Quiz} from '../shared/model/Quiz.model';

@Injectable({
  providedIn: 'root'
})
export class QuizService {
  private apiUrl = 'http://localhost:8080/api/quizzes';
  private apiUrlParticipant = 'http://localhost:8080/api/participant/quizzes';

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private router: Router
  ) {
  }


  // Méthode pour créer un quiz
  createQuiz(quizData: any): Observable<any> {
    const token = this.authService.getToken();

    if (!token) {
      console.error('Aucun token disponible');
      this.router.navigate(['/login']);
      return throwError(() => 'Not authenticated');
    }

    // Vérifier le rôle avant l'envoi
    if (!this.authService.hasRole('CREATOR')) {
      console.error('Rôle insuffisant pour créer un quiz');
      return throwError(() => 'Insufficient permissions');
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    // Logging pour déboguer
    console.log('Envoi de requête de création de quiz avec token:', token.substring(0, 20) + '...');
    console.log('Rôle utilisateur:', this.authService.getUserRole());

    return this.http.post(this.apiUrl, quizData, {headers}).pipe(
      tap(response => {
        console.log('Quiz créé avec succès:', response);
      }),
      catchError((error: HttpErrorResponse) => {
        console.error('Erreur lors de la création du quiz:', error);
        console.error('Statut:', error.status, error.statusText);
        console.error('Message d\'erreur:', error.error);

        if (error.status === 401) {
          console.log('Session expirée, redirection vers login...');
          this.authService.logout();
          this.router.navigate(['/login'], {
            queryParams: {reason: 'session_expired'}
          });
          return throwError(() => 'Session expirée');
        }

        if (error.status === 403) {
          return throwError(() => 'Vous n\'avez pas les permissions nécessaires');
        }

        return throwError(() => error.error?.message || 'Erreur lors de la création du quiz');
      })
    );
  }

  // Méthode pour récupérer tous les quiz de l'utilisateur connecté
  getUserQuizzes(): Observable<any[]> {
    const token = this.authService.getToken();

    if (!token) {
      console.error('Aucun token disponible');
      this.router.navigate(['/login']);
      return throwError(() => 'Not authenticated');
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    return this.http.get<any[]>(`${this.apiUrl}/my-quizzes`, {headers}).pipe(
      tap(quizzes => {
        console.log('Quiz récupérés avec succès:', quizzes);
      }),
      catchError((error: HttpErrorResponse) => {
        console.error('Erreur lors de la récupération des quiz:', error);

        if (error.status === 401) {
          console.log('Session expirée, redirection vers login...');
          this.authService.logout();
          this.router.navigate(['/login'], {
            queryParams: {reason: 'session_expired'}
          });
          return throwError(() => 'Session expirée');
        }

        return throwError(() => error.error?.message || 'Erreur lors de la récupération des quiz');
      })
    );
  }

  // Méthode pour supprimer un quiz
  deleteQuiz(quizId: number): Observable<any> {
    const token = this.authService.getToken();

    if (!token) {
      console.error('Aucun token disponible');
      this.router.navigate(['/login']);
      return throwError(() => 'Not authenticated');
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    return this.http.delete(`${this.apiUrl}/${quizId}`, {headers}).pipe(
      tap(() => {
        console.log('Quiz supprimé avec succès');
      }),
      catchError((error: HttpErrorResponse) => {
        console.error('Erreur lors de la suppression du quiz:', error);

        if (error.status === 401) {
          console.log('Session expirée, redirection vers login...');
          this.authService.logout();
          this.router.navigate(['/login'], {
            queryParams: {reason: 'session_expired'}
          });
          return throwError(() => 'Session expirée');
        }

        if (error.status === 403) {
          return throwError(() => 'Vous n\'avez pas les permissions nécessaires pour supprimer ce quiz');
        }

        return throwError(() => error.error?.message || 'Erreur lors de la suppression du quiz');
      })
    );
  }

  // quiz.service.ts
  getQuizzesByCreator(creatorId: number): Observable<any[]> {
    const token = this.authService.getToken();

    if (!token) {
      this.router.navigate(['/login']);
      return throwError(() => 'Not authenticated');
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.get<any[]>(`${this.apiUrl}/creator/${creatorId}`, {headers}).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          this.authService.logout();
          this.router.navigate(['/login'], {
            queryParams: {reason: 'session_expired'}
          });
        }
        return throwError(() => error.error?.message || 'Error fetching quizzes');
      })
    );
  }


  /*  getPublicQuizzes(): Observable<Quiz[]> {
      return this.http.get<Quiz[]>(`${this.apiUrl}/public`);
    }*/
  /*
    getQuizById(id: number): Observable<Quiz> {
      return this.http.get<Quiz>(`${this.apiUrl}/${id}`);

    }*/

  /////////////////// khaoula


  getPublicQuizzes(): Observable<Quiz[]> {
    return this.http.get<Quiz[]>(`${this.apiUrlParticipant}/public`).pipe(
      map(quizzes => quizzes.map(quiz => this.transformQuiz(quiz)))
    );
  }

  private transformQuiz(quiz: any): Quiz {
    const questions = JSON.parse(quiz.questions);
    const questionCount = questions.length;

    // Déduire la difficulté basée sur le temps et le nombre de questions
    const timeInMinutes = this.parseTimeToMinutes(quiz.timeLimit);
    let difficulty: 'Facile' | 'Moyen' | 'Difficile' = 'Moyen';

    if (timeInMinutes < 5 && questionCount < 10) {
      difficulty = 'Facile';
    } else if (timeInMinutes > 15 || questionCount > 20) {
      difficulty = 'Difficile';
    }

    // Créer une catégorie basée sur le domaine
    const categories = ['CultureG', 'Techno', 'Science', 'Histoire'];
    const category = categories[quiz.domaine % categories.length];

    return {
      ...quiz,
      category,
      difficulty,
      questionCount,
      publishDate: this.formatDate(quiz.publishDate)
    };
  }

  private parseTimeToMinutes(timeLimit: string): number {
    if (!timeLimit) return 0;
    const parts = timeLimit.split(':').map(Number);
    return parts[0] * 60 + parts[1] + parts[2] / 60;
  }

  private formatDate(dateString: string): string {
    if (!dateString) return 'Date inconnue';
    const date = new Date(dateString);
    return date.toLocaleDateString('fr-FR', {
      day: '2-digit',
      month: 'short',
      year: 'numeric'
    });
  }


  getQuizById(id: number): Observable<Quiz> {
    const token = this.authService.getToken();

    if (!token) {
      this.router.navigate(['/login']);
      return throwError(() => 'Not authenticated');
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.get<Quiz>(`${this.apiUrlParticipant}/${id}`, {headers}).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          this.authService.logout();
          this.router.navigate(['/login'], {
            queryParams: {reason: 'session_expired'}
          });
        }
        return throwError(() => error.error?.message || 'Error fetching quiz');
      })
    );
  }

/*  submitQuiz(quizId: number, response: any): Observable<any> {
    const token = this.authService.getToken();

    if (!token) {
      console.error('No token available');
      this.authService.logout();
      this.router.navigate(['/login']);
      return throwError(() => 'Not authenticated');
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    console.log('Submitting quiz with token:', token.substring(0, 20) + '...'); // Debug

    return this.http.post(`${this.apiUrlParticipant}/${quizId}/submit`, response, { headers }).pipe(
      catchError((error: HttpErrorResponse) => {
        console.error('Error submitting quiz:', error);
        console.error('Status:', error.status, error.statusText);
        console.error('Error message:', error.error?.message);

        if (error.status === 401) {
          console.log('Session expired, redirecting to login...');
          this.authService.logout();
          this.router.navigate(['/login'], {
            queryParams: { reason: 'session_expired' }
          });
          return throwError(() => 'Session expired');
        }

        return throwError(() => error.error?.message || 'Error submitting quiz');
      })
    );
  }*/
  submitQuiz(quizId: number, response: any): Observable<any> {
    const token = this.authService.getToken();

    if (!token) {
      this.authService.logout();
      this.router.navigate(['/login']);
      return throwError(() => new Error('No token available'));
    }

    // Ajoutez ce log pour vérifier le token
    console.log('Token being sent:', token);

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    return this.http.post(`${this.apiUrlParticipant}/${quizId}/submit`, response, { headers }).pipe(
      catchError((error: HttpErrorResponse) => {
        console.error('Full error response:', error);

        if (error.status === 401) {
          this.authService.logout();
          this.router.navigate(['/login'], {
            queryParams: { reason: 'session_expired' }
          });
        }
        return throwError(() => error.error?.message || 'Error submitting quiz');
      })
    );
  }
}
