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
