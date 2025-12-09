package newsugar.Newsugar_Back.domain.quiz.service;

import newsugar.Newsugar_Back.domain.quiz.dto.SubmitResult;
import newsugar.Newsugar_Back.domain.quiz.model.Quiz;
import java.util.List;

public interface QuizService {
    Quiz create(Quiz quiz);
    Quiz get(Long id);
    SubmitResult score(Long id, List<Integer> answers);
}

