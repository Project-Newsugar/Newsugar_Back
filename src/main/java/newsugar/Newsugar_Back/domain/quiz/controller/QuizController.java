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
        QuizResponse res = toResponse(saved, false);
        return ResponseEntity.ok(ApiResult.ok(res));
    }

    @GetMapping
    public ResponseEntity<ApiResult<java.util.List<QuizResponse>>> list(
            @RequestParam(name = "scope", required = false) String scope,
            @RequestParam(name = "from", required = false) java.time.Instant from,
            @RequestParam(name = "to", required = false) java.time.Instant to
    ) {
        java.util.List<Quiz> quizzes;
        if ("period".equalsIgnoreCase(scope) && from != null && to != null) {
            quizzes = quizService.listByPeriod(from, to);
        } else {
            quizzes = quizService.listToday();
        }
        java.time.Instant now = java.time.Instant.now();
        java.util.List<QuizResponse> res = new java.util.ArrayList<>();
        for (Quiz q : quizzes) {
            boolean playable = (q.getStartAt() == null || !now.isBefore(q.getStartAt()))
                    && (q.getEndAt() == null || !now.isAfter(q.getEndAt()));
            res.add(toResponse(q, !playable));
        }
        return ResponseEntity.ok(ApiResult.ok(res));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResult<QuizResponse>> get(@PathVariable Long id) {
        Quiz quiz = quizService.get(id);
        QuizResponse res = toResponse(quiz, false);
        return ResponseEntity.ok(ApiResult.ok(res));
    }

    @PostMapping("/summary/{summaryId}/generate")
    public ResponseEntity<ApiResult<QuizResponse>> generateFromSummary(@PathVariable Long summaryId) {
        Quiz quiz = quizService.generateFromSummary(summaryId);
        QuizResponse res = toResponse(quiz, false);
        return ResponseEntity.ok(ApiResult.ok(res));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<ApiResult<String>> start(@PathVariable Long id) {
        quizService.ensurePlayable(id);
        return ResponseEntity.ok(ApiResult.ok("OK"));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<ApiResult<SubmitResult>> submit(@PathVariable Long id, @RequestBody SubmitRequest req) {
        SubmitResult result = quizService.score(
                id,
                req != null ? req.userId() : null,
                req != null ? req.answers() : null
        );
        return ResponseEntity.ok(ApiResult.ok(result));
    }

    @GetMapping("/{id}/result")
    public ResponseEntity<ApiResult<SubmitResult>> result(@PathVariable Long id) {
        SubmitResult last = quizService.resultOrThrow(id);
        return ResponseEntity.ok(ApiResult.ok(last));
    }

    private QuizResponse toResponse(Quiz quiz, boolean includeAnswers) {
        if (quiz == null) return null;
        java.util.List<QuizResponse.QuestionView> views = new java.util.ArrayList<>();
        if (quiz.getQuestions() != null) {
            for (Question q : quiz.getQuestions()) {
                views.add(new QuizResponse.QuestionView(q.getText(), q.getOptions(), includeAnswers ? q.getCorrectIndex() : null));
            }
        }
        return new QuizResponse(quiz.getId(), quiz.getTitle(), views, quiz.getStartAt(), quiz.getEndAt());
    }
}
