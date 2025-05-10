import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { CreatorComponent } from './pages/creator/creator.component';
import { AuthGuard } from './core/auth.guard';
import { AdminComponent } from './pages/admin/admin.component';

import { CreateQuizzeComponent } from './pages/creator/create-quizze/create-quizze.component';
import {MesQuizComponent} from './pages/creator/mes-quiz/mes-quiz.component';
import {QuizComponent} from './pages/participant/quiz/quiz.component';
import {HistoryComponent} from './pages/participant/history/history.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  {
    path: 'creator',
    component: CreatorComponent,
    canActivate: [AuthGuard],
    data: { roles: ['CREATOR'] }
  },
  {
    path: 'creer-quiz',
    component: CreateQuizzeComponent,
    canActivate: [AuthGuard],
    data: { roles: ['CREATOR'] }
  },
  {
    path: 'mes-quiz',
    component: MesQuizComponent,
    canActivate: [AuthGuard],
    data: { roles: ['CREATOR'] }
  },
  {
    path: 'participant',
    component: QuizComponent,
    // component: ParticipantComponent,
    canActivate: [AuthGuard],
    data: { roles: ['PARTICIPANT'] }
  },
  {
    path: 'admin',
    component: AdminComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ADMIN'] }
  },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: '**', redirectTo: '/login' },

  {
    path: 'mes-quiz',
    component: MesQuizComponent,
    canActivate: [AuthGuard],
    data: { roles: ['CREATOR'] }
  },

  { path: 'quiz/:id', component: QuizComponent },
  { path: 'history', component: HistoryComponent },
];
