package newsugar.Newsugar_Back.domain.quiz.controller;

import newsugar.Newsugar_Back.common.ApiResult;
import newsugar.Newsugar_Back.common.ErrorCode;
import newsugar.Newsugar_Back.domain.quiz.dto.SubmitRequest;
import newsugar.Newsugar_Back.domain.quiz.dto.SubmitResult;
import newsugar.Newsugar_Back.domain.quiz.model.Quiz;
import newsugar.Newsugar_Back.domain.quiz.model.Question;
import newsugar.Newsugar_Back.domain.quiz.dto.CreateQuizRequest;
import newsugar.Newsugar_Back.domain.quiz.dto.QuizResponse;
import newsugar.Newsugar_Back.domain.quiz.service.QuizService;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/quizzes")
@Validated
public class QuizController {
    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping
    public ResponseEntity<ApiResult<QuizResponse>> create(@Valid @RequestBody CreateQuizRequest req) {
        Quiz quiz = new Quiz();
        quiz.setTitle(req.title());
        quiz.setStartAt(req.startAt());
        quiz.setEndAt(req.endAt());
        if (quiz.getStartAt() != null && quiz.getEndAt() != null) {
            if (quiz.getEndAt().isBefore(quiz.getStartAt())) {
                throw new IllegalArgumentException("종료 시간이 시작 시간보다 빠릅니다");
            }
        }
        if (req.questions() != null) {
            for (CreateQuizRequest.QuestionCreate qc : req.questions()) {
                Question q = new Question();
                q.setText(qc.text());
                q.setOptions(qc.options());
                q.setCorrectIndex(qc.correctIndex());
                quiz.getQuestions().add(q);
            }
        }
        Quiz saved = quizService.create(quiz);
        QuizResponse res = toResponse(saved);
        return ResponseEntity.ok(ApiResult.ok(res));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResult<QuizResponse>> get(@PathVariable Long id) {
        Quiz quiz = quizService.get(id);
        QuizResponse res = toResponse(quiz);
        return ResponseEntity.ok(ApiResult.ok(res));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<ApiResult<SubmitResult>> submit(@PathVariable Long id, @RequestBody SubmitRequest req) {
        Quiz quiz = quizService.get(id);
        if (quiz == null) {
            return ResponseEntity.ok(ApiResult.error(ErrorCode.QUIZ_NOT_FOUND.name(), "퀴즈를 찾을 수 없습니다"));
        }
        java.time.Instant now = java.time.Instant.now();
        if ((quiz.getStartAt() != null && now.isBefore(quiz.getStartAt())) ||
            (quiz.getEndAt() != null && now.isAfter(quiz.getEndAt()))) {
            return ResponseEntity.ok(ApiResult.error(ErrorCode.QUIZ_EXPIRED.name(), "퀴즈 제출 기간이 아닙니다"));
        }
        SubmitResult result = quizService.score(id, req != null ? req.answers() : null);
        return ResponseEntity.ok(ApiResult.ok(result));
    }

    private QuizResponse toResponse(Quiz quiz) {
        if (quiz == null) return null;
        java.util.List<QuizResponse.QuestionView> views = new java.util.ArrayList<>();
        if (quiz.getQuestions() != null) {
            for (Question q : quiz.getQuestions()) {
                views.add(new QuizResponse.QuestionView(q.getText(), q.getOptions()));
            }
        }
        return new QuizResponse(quiz.getId(), quiz.getTitle(), views, quiz.getStartAt(), quiz.getEndAt());
    }
}
