import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import {AuthService} from '../../../auth/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {
  constructor(private authService: AuthService, protected router: Router) {}

  // Méthode pour vérifier le rôle de l'utilisateur
  isCreator(): boolean {
    return this.authService.hasRole('CREATOR');
  }

  isAdmin(): boolean {
    return this.authService.hasRole('ADMIN');
  }

  isParticipant(): boolean {
    return this.authService.hasRole('PARTICIPANT');
  }

  // Méthode pour vérifier si l'utilisateur est connecté
  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  // Méthode pour se déconnecter
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
