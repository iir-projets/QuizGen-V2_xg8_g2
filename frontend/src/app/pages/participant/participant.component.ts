import { Component } from '@angular/core';
import {CommonModule} from '@angular/common';
import {CreateQuizzeComponent} from '../creator/create-quizze/create-quizze.component';
import {FooterComponent} from '../creator/footer/footer.component';
import {HeaderComponent} from '../creator/header/header.component';

@Component({
  selector: 'app-participant',
  standalone: true, // <-- Ajoutez ceci
  imports: [
    CommonModule, // <-- Ajoutez ceci
  ],
  templateUrl: './participant.component.html',
  styleUrl: './participant.component.css'
})
export class ParticipantComponent {

}
