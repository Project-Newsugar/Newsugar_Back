package newsugar.Newsugar_Back.domain.quiz.service;

import newsugar.Newsugar_Back.domain.quiz.dto.SubmitResult;
import newsugar.Newsugar_Back.domain.quiz.model.Quiz;
import java.util.List;
import java.time.Instant;

public interface QuizService {
    Quiz create(Quiz quiz);
    Quiz get(Long id);
    SubmitResult score(Long id, Long userId, List<Integer> answers);
    List<Quiz> listToday();
    List<Quiz> listByPeriod(Instant from, Instant to);
    SubmitResult lastResult(Long quizId);
    void ensurePlayable(Long id);
    SubmitResult resultOrThrow(Long quizId);
    Quiz generateFromSummary(Long summaryId);
}
