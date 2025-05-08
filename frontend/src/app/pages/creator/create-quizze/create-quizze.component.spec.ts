import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateQuizzeComponent } from './create-quizze.component';

describe('CreateQuizzeComponent', () => {
  let component: CreateQuizzeComponent;
  let fixture: ComponentFixture<CreateQuizzeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateQuizzeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateQuizzeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
