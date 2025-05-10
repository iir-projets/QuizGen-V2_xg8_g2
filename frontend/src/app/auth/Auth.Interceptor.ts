/*
import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { AuthService } from './auth.service';
import { Router } from '@angular/router';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  // In Auth.Interceptor.ts
  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const token = this.authService.getToken();

    // Add more debug logging
    console.log('Token being used:', token);

    if (token) {
      request = request.clone({
        setHeaders: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json' // Ensure content type is set
        }
      });
    }

    // Envoyer la requête modifiée et intercepter les erreurs
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        // Amélioration du logging d'erreur
        if (error.status === 401) {
          console.error('Erreur 401:', error.message);

          // Si ce n'est pas une requête d'authentification, déconnecter l'utilisateur
          if (!request.url.includes('/api/auth/login')) {
            console.warn('Token non valide ou expiré, déconnexion...');
            this.authService.logout();
            this.router.navigate(['/login'], {
              queryParams: {
                redirectUrl: this.router.url,
                reason: 'session_expired'
              }
            });
          }
        } else if (error.status === 403) {
          console.error('Erreur 403: Accès refusé');
        }

        return throwError(() => error);
      })
    );
  }
}
*/

// Auth.Interceptor.ts
import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { catchError, switchMap, filter, take } from 'rxjs/operators';
import { AuthService } from './auth.service';
import { Router } from '@angular/router';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    // Skip adding token for auth requests
    if (request.url.includes('/api/auth/')) {
      return next.handle(request);
    }

    const token = this.authService.getToken();
    if (token) {
      request = this.addToken(request, token);
    }

    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401 && !request.url.includes('/api/auth/')) {
          return this.handle401Error(request, next);
        }
        return throwError(() => error);
      })
    );
  }

  private addToken(request: HttpRequest<any>, token: string): HttpRequest<any> {
    return request.clone({
      setHeaders: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
  }

  private handle401Error(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // If token is expired, try to refresh it
    if (this.authService.isTokenExpired()) {
      return this.authService.refreshToken().pipe(
        switchMap((token: string) => {
          return next.handle(this.addToken(request, token));
        }),
        catchError((err) => {
          this.authService.logout();
          this.router.navigate(['/login'], {
            queryParams: {
              redirectUrl: this.router.url,
              reason: 'session_expired'
            }
          });
          return throwError(() => err);
        })
      );
    } else {
      // If token is not expired but still getting 401, logout
      this.authService.logout();
      this.router.navigate(['/login'], {
        queryParams: {
          redirectUrl: this.router.url,
          reason: 'session_expired'
        }
      });
      return throwError(() => new Error('Session expired'));
    }
  }
}
