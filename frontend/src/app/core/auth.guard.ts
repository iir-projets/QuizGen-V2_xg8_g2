import { Injectable } from '@angular/core';
import {
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  Router
} from '@angular/router';
import { AuthService } from './../auth/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    console.log('AuthGuard - Vérification de l\'accès à la route:', state.url); // Log de la route

    if (!this.authService.isLoggedIn()) {
      console.log('AuthGuard - Utilisateur non connecté, redirection vers login');
      this.router.navigate(['/login']);
      return false;
    }

    const userRole = this.authService.getUserRole();
    console.log(`AuthGuard - Utilisateur connecté avec le rôle: ${userRole}`); // Log du rôle

    if (route.data && route.data['roles']) {
      const allowedRoles = route.data['roles'];
      console.log(`AuthGuard - Rôles autorisés pour cette route: ${allowedRoles}`); // Log des rôles requis

      if (!this.authService.checkUserAuthorization(allowedRoles)) {
        console.log(`AuthGuard - Accès refusé: l'utilisateur n'a pas les rôles requis`); // Log refus

        if (userRole?.includes('ADMIN')) {
          console.log('Redirection vers /admin');
          this.router.navigate(['/admin']);
        } else if (userRole?.includes('CREATOR')) {
          console.log('Redirection vers /creator');
          this.router.navigate(['/creator']);
        } else if (userRole?.includes('PARTICIPANT')) {
          console.log('Redirection vers /participant');
          this.router.navigate(['/participant']);
        } else {
          console.log('Redirection vers /login (rôle inconnu)');
          this.router.navigate(['/login']);
        }
        return false;
      }
    }

    console.log('AuthGuard - Accès autorisé');
    return true;
  }
}
