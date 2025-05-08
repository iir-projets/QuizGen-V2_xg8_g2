import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import {AuthService, LoginResponse} from '../../auth/auth.service';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink]
})
export class LoginComponent {
  credentials = {
    email: '',
    password: ''
  };
  rememberMe = false;
  isLoading = false;
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.authService.login(this.credentials)
      .subscribe({
        next: () => {
          this.isLoading = false;
          // Naviguer en fonction du rôle de l'utilisateur
          const userRole = this.authService.getUserRole();

          switch (userRole) {
            case 'ADMIN':
              this.router.navigate(['/admin']);
              break;
            case 'CREATOR':
              this.router.navigate(['/creator']);
              break;
            case 'PARTICIPANT':
              this.router.navigate(['/participant']);
              break;
            default:
              this.router.navigate(['/login']);
          }
        },
        error: (err: { status: number; }) => {
          this.isLoading = false;
          if (err.status === 401) {
            this.errorMessage = 'Email ou mot de passe incorrect';
          } else {
            this.errorMessage = 'Une erreur est survenue. Veuillez réessayer plus tard.';
          }
          console.error('Erreur de connexion', err);
        }
      });
  }


}
