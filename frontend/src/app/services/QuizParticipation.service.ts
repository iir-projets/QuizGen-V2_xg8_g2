// QuizParticipation.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Quiz, QuizResponse, QuizResult } from '../shared/model/Quiz.model';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class QuizParticipationService {
  private apiUrl = 'http://localhost:8080/api/participant/quizzes';

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private router: Router
  ) {}

  private getAuthHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    if (!token) {
      this.authService.logout();
      this.router.navigate(['/login'], {
        queryParams: { reason: 'session_expired' }
      });
      throw new Error('No authentication token available');
    }
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  getQuizById(id: number): Observable<Quiz> {
    try {
      const headers = this.getAuthHeaders();
      return this.http.get<Quiz>(`${this.apiUrl}/${id}`, { headers }).pipe(
        catchError((error: HttpErrorResponse) => {
          this.handleAuthError(error);
          return throwError(() => this.getErrorMessage(error));
        })
      );
    } catch (error) {
      return throwError(() => error);
    }
  }

  submitQuiz(quizId: number, responses: QuizResponse): Observable<QuizResult> {
    try {
      const headers = this.getAuthHeaders();
      return this.http.post<QuizResult>(
        `${this.apiUrl}/${quizId}/submit`,
        responses,
        { headers }
      ).pipe(
        catchError((error: HttpErrorResponse) => {
          this.handleAuthError(error);
          return throwError(() => this.getErrorMessage(error));
        })
      );
    } catch (error) {
      return throwError(() => error);
    }
  }

  private handleAuthError(error: HttpErrorResponse): void {
    if (error.status === 401) {
      this.authService.logout();
      this.router.navigate(['/login'], {
        queryParams: { reason: 'session_expired' }
      });
    }
  }

  private getErrorMessage(error: HttpErrorResponse): string {
    if (error.status === 401) {
      return 'Your session has expired. Please login again.';
    }
    return error.error?.message || 'An unexpected error occurred';
  }
}
