import {ApplicationConfig, importProvidersFrom} from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import {
  provideHttpClient,
  withInterceptors,
  HTTP_INTERCEPTORS,
  HttpClientModule,
  withInterceptorsFromDi
} from '@angular/common/http';
import { AuthInterceptor } from './../app/auth/Auth.Interceptor';
import {provideAnimations} from '@angular/platform-browser/animations';
import {AuthService} from './auth/auth.service';
import {AuthGuard} from './core/auth.guard';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideAnimations(),
    importProvidersFrom(HttpClientModule),
    provideHttpClient(withInterceptorsFromDi()),
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    },
    AuthService,
    AuthGuard
  ]
};
