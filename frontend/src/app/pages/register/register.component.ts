import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../auth/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink]
})
export class RegisterComponent {
  user = {
    name: '',
    email: '',
    password: ''
  };

  isCreator = false;
  errorMessage = '';
  isLoading = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  toggleUserType(): void {
    this.isCreator = !this.isCreator;
  }

  onSubmit(): void {
    this.isLoading = true;
    this.errorMessage = '';

    const registerEndpoint = this.isCreator ? 'creator' : 'participant';

    this.authService.register(this.user, registerEndpoint)
      .subscribe({
        next: () => {
          this.isLoading = false;
          // Après l'inscription réussie, connecter l'utilisateur
          this.authService.login({
            email: this.user.email,
            password: this.user.password
          }).subscribe({
            next: () => {
              // Rediriger vers la page correspondante
              const redirectPath = this.isCreator ? '/creator' : '/participant';
              this.router.navigate([redirectPath]);
            },
            error: (err: any) => {
              console.error('Erreur lors de la connexion après inscription', err);
              // Rediriger vers la page de connexion en cas d'erreur
              this.router.navigate(['/login']);
            }
          });
        },
        error: (err: { status: number; }) => {
          this.isLoading = false;
          if (err.status === 400) {
            this.errorMessage = 'Cet email est déjà utilisé';
          } else {
            this.errorMessage = 'Une erreur est survenue. Veuillez réessayer plus tard.';
          }
          console.error('Erreur d\'inscription', err);
        }
      });
  }
}
