import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { jwtDecode } from 'jwt-decode';

export interface User {
  id: number;
  name?: string;
  email: string;
  role: string;
}

export interface LoginResponse {
  token: string;
  type: string;
  id: number;
  email: string;
  roles: string[];
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = 'http://localhost:8080/api/auth';
  private tokenKey = 'auth_token';
  private currentUserSubject = new BehaviorSubject<User | null>(null);

  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    this.loadStoredUser();
  }

  private loadStoredUser(): void {
    const token = localStorage.getItem(this.tokenKey);
    if (token) {
      try {
        const decodedToken: any = jwtDecode(token);
        console.log('Full decoded token:', decodedToken);

        if (decodedToken) {
          const now = Date.now() / 1000;
          const isTokenValid = decodedToken.exp > now;

          if (isTokenValid) {
            const user: User = {
              id: Number(decodedToken.id),
              email: decodedToken.email,
              role: this.extractRoleFromToken(token)
            };

            // Ajout du log pour l'utilisateur connecté
            console.log('Utilisateur connecté - ID:', user.id, 'Email:', user.email);

            this.currentUserSubject.next(user);
          } else {
            this.logout();
          }
        }
      } catch (error) {
        console.error('Token decoding error:', error);
        this.logout();
      }
    }
  }

  login(credentials: { email: string; password: string }): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.API_URL}/login`, credentials)
      .pipe(
        tap(response => {
          localStorage.setItem(this.tokenKey, response.token);

          const user: User = {
            id: response.id,
            email: response.email,
            role: response.roles[0] || ''
          };

          // Ajout du log lors de la connexion
          console.log('Connexion réussie - ID:', user.id, 'Email:', user.email);

          this.currentUserSubject.next(user);
        }),
        catchError(error => {
          console.error('Login error:', error);
          return throwError(() => error);
        })
      );
  }

  getUserId(): number | null {
    const token = this.getToken();
    if (!token) return null;

    try {
      const decodedToken: any = jwtDecode(token);
      return Number(decodedToken.id); // Garanti comme number
    } catch (error) {
      console.error('Error getting user ID:', error);
      return null;
    }
  }


  register(user: any, type: string): Observable<any> {
    return this.http.post(`${this.API_URL}/register/${type}`, user);
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    this.currentUserSubject.next(null);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  isLoggedIn(): boolean {
    const token = this.getToken();
    if (!token) return false;

    try {
      const decodedToken: any = jwtDecode(token);
      return decodedToken.exp > Date.now() / 1000;
    } catch {
      return false;
    }
  }

  getUserRole(): string | null {
    const token = this.getToken();
    if (!token) return null;
    return this.extractRoleFromToken(token);
  }

  hasRole(role: string): boolean {
    const userRole = this.getUserRole();
    if (!userRole) return false;

    // Compare sans tenir compte de la casse
    return userRole.toUpperCase() === role.toUpperCase();
  }

  checkUserAuthorization(allowedRoles: string[]): boolean {
    if (!this.isLoggedIn()) return false;

    const userRole = this.getUserRole();
    if (!userRole) return false;

    return allowedRoles.some(role => this.hasRole(role));
  }

  private extractRoleFromToken(token: string): string {
    try {
      const decodedToken: any = jwtDecode(token);
      if (decodedToken?.roles) {
        return typeof decodedToken.roles === 'string'
          ? decodedToken.roles
          : decodedToken.roles[0] || '';
      }
      return '';
    } catch (error) {
      console.error('Role extraction error:', error);
      return '';
    }
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }
}
