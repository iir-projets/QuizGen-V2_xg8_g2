import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CreateQuizzeComponent } from './create-quizze/create-quizze.component';
import { FooterComponent } from './footer/footer.component';
import { HeaderComponent } from './header/header.component';

@Component({
  selector: 'app-creator',
  standalone: true, // <-- Ajoutez ceci
  imports: [
    CommonModule, // <-- Ajoutez ceci
    CreateQuizzeComponent,
    FooterComponent,
    HeaderComponent,
  ],
  templateUrl: './creator.component.html',
  styleUrls: ['./creator.component.css']
})
export class CreatorComponent {}
