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
import {ListQuizzesComponent} from './pages/participant/list-quizzes/list-quizzes.component';

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
 /* {
    path: 'participant',
    // component: HistoryComponent,
    component: ListQuizzesComponent,
    // component: QuizComponent,
    canActivate: [AuthGuard],
    data: { roles: ['PARTICIPANT'] }
  },*/

  {
    path: 'participant',
    canActivate: [AuthGuard],
    data: { roles: ['PARTICIPANT'] },
    children: [
      {
        path: 'history', // accès via /participant/history
        component: HistoryComponent
      },
      {
        path: 'quizzes', // accès via /participant/quizzes
        component: ListQuizzesComponent
      },
      {
        path: 'quiz/:id', // accès via /participant/quiz/123
        component: QuizComponent
      },
      {
        path: '', // redirection par défaut
        redirectTo: 'quizzes',
        pathMatch: 'full'
      }
    ]
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
