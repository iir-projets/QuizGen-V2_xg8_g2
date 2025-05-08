import { Component } from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-question-editor',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './question-editor.component.html',
  styleUrl: './question-editor.component.css'
})
export class QuestionEditorComponent {
  sections = {
    questions: { open: true }
  };

  quiz = {
    questions: [
      {
        text: '',
        type: 'qcm',
        options: [
          { text: '', isCorrect: false },
          { text: '', isCorrect: false },
          { text: '', isCorrect: false },
          { text: '', isCorrect: false }
        ]
      }
    ]
  };

  toggleSection(section: 'questions') {
    this.sections[section].open = !this.sections[section].open;
  }

  addQuestion() {
    this.quiz.questions.push({
      text: '',
      type: 'qcm',
      options: [
        { text: '', isCorrect: false },
        { text: '', isCorrect: false },
        { text: '', isCorrect: false },
        { text: '', isCorrect: false }
      ]
    });
  }

  addOption(questionIndex: number) {
    this.quiz.questions[questionIndex].options.push({
      text: '',
      isCorrect: false
    });
  }
}
