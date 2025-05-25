import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';

export interface QuizHistory {
  id: number;
  quizId: number;
  title: string;
  category: string;
  date: string;
  score: number;
  passed: boolean;
}

export interface GlobalStats {
  totalQuizzes: number;
  averageScore: number;
  topCategory: string;
}

@Injectable({
  providedIn: 'root'
})
export class HistoryService {
  private apiUrl = 'http://localhost:8080/api/participant/history';

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

  getQuizHistory(): Observable<QuizHistory[]> {
    try {
      const participantId = this.authService.getUserId();
      if (!participantId) {
        return throwError(() => new Error('User not authenticated'));
      }

      const headers = this.getAuthHeaders();
      return this.http.get<QuizHistory[]>(`${this.apiUrl}/${participantId}`, { headers }).pipe(
        catchError((error: HttpErrorResponse) => {
          this.handleAuthError(error);
          return throwError(() => this.getErrorMessage(error));
        })
      );
    } catch (error) {
      return throwError(() => error);
    }
  }

  getGlobalStats(): Observable<GlobalStats> {
    try {
      const participantId = this.authService.getUserId();
      if (!participantId) {
        return throwError(() => new Error('User not authenticated'));
      }

      const headers = this.getAuthHeaders();
      return this.http.get<GlobalStats>(`${this.apiUrl}/statistics/${participantId}`, { headers }).pipe(
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
