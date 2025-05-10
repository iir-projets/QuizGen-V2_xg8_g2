export interface Quiz {
  id: number;
  title: string;
  description: string;
  domaine: number;
  autoCorrection: boolean;
  isPublic: boolean;
  timeLimit: string;
  primaryColor: string;
  theme: string;
  completionMessage: string;
  randomizeQuestions: boolean;
  showResults: boolean;
  maxAttempts: number;
  passingGrade: number;
  publishDate: string;
  expiryDate: string;
  shareLink: string;
  embedCode: string;
  questions: string; // JSON string
  creatorId: number;
}



export interface QuizResponse {
  quizId: number;
  participantId: number;
  answers: { [questionId: number]: string };
}

export interface QuizResult {
  quizId: number;
  quizTitle: string;
  participantId: number;
  score: number;
  passed: boolean;
  submissionDate: string;
  correctAnswers?: { [questionId: number]: string }; // Optionnel
}
