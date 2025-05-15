export class Question {
  id: number;
  text: string;
  options: string[];
  correctAnswer: string;
  explanation: string;
  [key: string]: any;

  // Constructor to initialize the question object
  constructor(id: number, text: string, options: string[], correctAnswer: string = '', explanation: string = '') {
    this.id = id;
    this.text = text;
    this.options = options;
    this.correctAnswer = correctAnswer;
    this.explanation = explanation;
  }
}
