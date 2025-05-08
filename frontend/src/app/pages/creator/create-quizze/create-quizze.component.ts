import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { QuizService } from '../../../services/quiz.service';
import { AuthService } from '../../../auth/auth.service';

@Component({
  selector: 'app-create-quizze',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './create-quizze.component.html',
  styleUrls: ['./create-quizze.component.css']
})
export class CreateQuizzeComponent {
  // Contrôle l'affichage des sections
  sections = {
    config: { open: true },
    questions: { open: true },
    customization: { open: false },
    advanced: { open: false },
    sharing: { open: false }
  };

  // État de soumission
  isSubmitting = false;

  // Message d'erreur pour soumission
  submitError: string | null = null;

  // Modèle de données du quiz
  quiz = {
    title: '',
    description: '',
    domaine: 0,
    autoCorrection: true,
    isPublic: false,
    timeLimit: '5',
    primaryColor: '#9333EA',
    theme: 'light',
    completionMessage: '',
    randomizeQuestions: false,
    showResults: true,
    maxAttempts: 0,
    passingGrade: 60,
    publishDate: '',
    expiryDate: '',
    questions: [
      this.createNewQuestion('qcm')
    ]
  };

  constructor(
    private router: Router,
    private quizService: QuizService,
    private authService: AuthService
  ) {}

  // Crée une nouvelle question selon le type
  private createNewQuestion(type: string): any {
    const baseQuestion = {
      text: '',
      type: type,
      score: 1,
      explication: '',
      showExplanation: false
    };

    switch(type) {
      case 'qcm':
        return {
          ...baseQuestion,
          options: [
            { text: '', isCorrect: false },
            { text: '', isCorrect: false }
          ]
        };
      case 'vf':
        return {
          ...baseQuestion,
          reponseCorrecte: false
        };
      case 'text':
        return baseQuestion;
      default:
        return baseQuestion;
    }
  }

  // Réinitialiser le formulaire de quiz
  private resetQuizForm(): void {
    this.quiz = {
      title: '',
      description: '',
      domaine: 0,
      autoCorrection: true,
      isPublic: false,
      timeLimit: '5',
      primaryColor: '#9333EA',
      theme: 'light',
      completionMessage: '',
      randomizeQuestions: false,
      showResults: true,
      maxAttempts: 0,
      passingGrade: 60,
      publishDate: '',
      expiryDate: '',
      questions: [
        this.createNewQuestion('qcm')
      ]
    };

    // Réinitialiser également l'état d'affichage des sections
    this.sections = {
      config: { open: true },
      questions: { open: true },
      customization: { open: false },
      advanced: { open: false },
      sharing: { open: false }
    };
  }

  // Basculer l'état d'une section
  toggleSection(section: string): void {
    // @ts-ignore
    this.sections[section].open = !this.sections[section].open;
  }

  // Ajouter une nouvelle question
  addQuestion(): void {
    this.quiz.questions.push(this.createNewQuestion('qcm'));
  }

  // Supprimer une question
  removeQuestion(index: number): void {
    if (this.quiz.questions.length > 1) {
      this.quiz.questions.splice(index, 1);
    } else {
      alert('Un quiz doit avoir au moins une question');
    }
  }

  // Ajouter une option à une question QCM
  addOption(questionIndex: number): void {
    if (this.quiz.questions[questionIndex].type === 'qcm') {
      this.quiz.questions[questionIndex].options.push({
        text: '',
        isCorrect: false
      });
    }
  }

  // Supprimer une option
  removeOption(questionIndex: number, optionIndex: number): void {
    if (this.quiz.questions[questionIndex].options.length > 2) {
      this.quiz.questions[questionIndex].options.splice(optionIndex, 1);
    } else {
      alert('Une question QCM doit avoir au moins deux options');
    }
  }

  // Basculer l'affichage de l'explication
  toggleExplanation(questionIndex: number): void {
    this.quiz.questions[questionIndex].showExplanation =
      !this.quiz.questions[questionIndex].showExplanation;
  }

  // Gérer le changement de type de question
  onQuestionTypeChange(questionIndex: number): void {
    const newType = this.quiz.questions[questionIndex].type;
    this.quiz.questions[questionIndex] = this.createNewQuestion(newType);
  }

  // Générer un lien de partage
  generateShareLink(): string {
    return `${window.location.origin}/quiz/${this.quiz.title.replace(/\s+/g, '-').toLowerCase()}`;
  }

  // Copier le lien de partage
  copyShareLink(): void {
    navigator.clipboard.writeText(this.generateShareLink());
    alert('Lien copié dans le presse-papiers!');
  }

  // Générer le code d'intégration
  generateEmbedCode(): string {
    return `<iframe src="${this.generateShareLink()}" width="600" height="400" frameborder="0"></iframe>`;
  }

  // Soumettre le quiz
// Soumettre le quiz
  submitQuiz(): void {
    this.submitError = null;
    this.isSubmitting = true;

    // Vérification de la connexion
    if (!this.authService.isLoggedIn()) {
      this.submitError = 'Vous n\'êtes pas connecté. Veuillez vous connecter pour créer un quiz.';
      this.isSubmitting = false;
      window.scrollTo(0, 0);
      this.router.navigate(['/login']);
      return;
    }

    // Récupération de l'ID du créateur
    const creatorId = this.authService.getUserId();
    if (!creatorId) {
      this.submitError = 'Impossible de déterminer votre identifiant utilisateur. Veuillez vous reconnecter.';
      this.isSubmitting = false;
      window.scrollTo(0, 0);
      return;
    }

    // Vérification du token
    const token = this.authService.getToken();
    console.log('Token actuel:', token ? `${token.substring(0, 20)}...` : 'Aucun token');
    console.log('ID du créateur:', creatorId);

    // Vérification explicite du rôle avec logging
    const userRole = this.authService.getUserRole();
    console.log('Rôle utilisateur actuel:', userRole);

    if (!this.authService.hasRole('CREATOR')) {
      this.submitError = 'Vous n\'avez pas les permissions nécessaires pour créer un quiz !';
      this.isSubmitting = false;
      window.scrollTo(0, 0);
      return;
    }

    // Validation
    if (!this.isQuizValid()) {
      this.submitError = 'Veuillez remplir tous les champs requis correctement !';
      this.isSubmitting = false;
      window.scrollTo(0, 0);
      return;
    }

    // Préparation des données avec creatorId
    const quizToSend = {
      title: this.quiz.title,
      description: this.quiz.description,
      creatorId: creatorId, // Ajout de l'ID du créateur
      domaine: this.quiz.domaine,
      autoCorrection: this.quiz.autoCorrection,
      isPublic: this.quiz.isPublic,
      timeLimit: this.quiz.timeLimit,
      primaryColor: this.quiz.primaryColor,
      theme: this.quiz.theme,
      completionMessage: this.quiz.completionMessage,
      randomizeQuestions: this.quiz.randomizeQuestions,
      showResults: this.quiz.showResults,
      maxAttempts: this.quiz.maxAttempts,
      passingGrade: this.quiz.passingGrade,
      publishDate: this.quiz.publishDate || null,
      expiryDate: this.quiz.expiryDate || null,
      shareLink: this.generateShareLink(),
      embedCode: this.generateEmbedCode(),
      questions: JSON.stringify(this.quiz.questions)
    };

    // Envoi des données avec gestion des erreurs améliorée
    this.quizService.createQuiz(quizToSend).subscribe({
      next: (response) => {
        this.isSubmitting = false;
        alert('Quiz enregistré avec succès !');
        this.resetQuizForm();
        window.scrollTo(0, 0);
      },
      error: (err) => {
        this.isSubmitting = false;
        console.error('Erreur lors de la création du quiz:', err);

        if (err === 'Session expirée') {
          this.submitError = 'Votre session a expiré. Veuillez vous reconnecter.';
        } else if (err === 'Vous n\'avez pas les permissions nécessaires') {
          this.submitError = 'Accès refusé - Vous n\'avez pas le rôle requis pour cette action.';
        } else if (typeof err === 'string') {
          this.submitError = err;
        } else {
          this.submitError = `Erreur technique - Veuillez réessayer. (${err.status || 'inconnu'})`;
        }

        window.scrollTo(0, 0);
      }
    });
  }
  // Fonction utilitaire pour la validation
  isQuizValid(): boolean {
    if (!this.quiz.title?.trim()) return false;

    for (const question of this.quiz.questions) {
      if (!question.text?.trim()) return false;

      if (question.type === 'qcm') {
        const hasCorrectOption = question.options.some((opt: { isCorrect: any; }) => opt.isCorrect);
        const allOptionsValid = question.options.every((opt: { text: string; }) => opt.text?.trim());
        if (!hasCorrectOption || !allOptionsValid) return false;
      }
    }

    return true;
  }
}
