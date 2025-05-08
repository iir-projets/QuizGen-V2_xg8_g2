import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Clipboard } from '@angular/cdk/clipboard';
import { QuizService } from '../../../services/quiz.service';

@Component({
  selector: 'app-question-editor',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './question-editor.component.html',
  styleUrl: './question-editor.component.css'
})
export class QuestionEditorComponent {
  sections = {
    questions: { open: true },
    sidebar: { open: true }
  };

  quiz = {
    title: '',
    description: '',
    isPublic: false,
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

  participants: any[] = [];
  selectedParticipants: number[] = [];
  isPublished = false;
  shareableLink = '';
  isLinkCopied = false;
  isLoading = false;

  constructor(
    private quizService: QuizService,
    private clipboard: Clipboard
  ) {}


  publishQuiz() {
    this.isLoading = true;
    this.quizService.createQuiz(this.quiz).subscribe({
      next: (response: { id: string }) => {
        this.isPublished = true;
        this.shareableLink = `${window.location.origin}/quiz/${response.id}`;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Erreur lors de la publication:', err);
        this.isLoading = false;
      }
    });
  }

  copyLink() {
    this.clipboard.copy(this.shareableLink);
    this.isLinkCopied = true;
    setTimeout(() => this.isLinkCopied = false, 2000);
  }

  loadParticipants() {
    this.isLoading = true;
    this.quizService.getQuizzesByCreator(1).subscribe({
      next: (data: any) => {
        this.participants = data.participants || [];
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Erreur:', err);
        this.isLoading = false;
      }
    });
  }

  togglePublic() {
    this.quiz.isPublic = !this.quiz.isPublic;
  }
  toggleSection(section: 'questions' | 'sidebar') {
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

  toggleParticipantSelection(participantId: number) {
    const index = this.selectedParticipants.indexOf(participantId);
    if (index === -1) {
      this.selectedParticipants.push(participantId);
    } else {
      this.selectedParticipants.splice(index, 1);
    }
  }
}
