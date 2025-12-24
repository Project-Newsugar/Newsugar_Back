package newsugar.Newsugar_Back.domain.quiz.controller;

import newsugar.Newsugar_Back.common.ApiResult;
import newsugar.Newsugar_Back.domain.quiz.model.QuizSubmission;
import newsugar.Newsugar_Back.domain.quiz.repository.QuizRepository;
import newsugar.Newsugar_Back.domain.quiz.repository.QuizSubmissionRepository;
import newsugar.Newsugar_Back.schedular.Schedular;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dev/quizzes")
// @Profile("dev")
public class DevQuizController {

    private final Schedular schedular;
    private final QuizRepository quizRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;

    public DevQuizController(Schedular schedular, QuizRepository quizRepository, QuizSubmissionRepository quizSubmissionRepository) {
        this.schedular = schedular;
        this.quizRepository = quizRepository;
        this.quizSubmissionRepository = quizSubmissionRepository;
    }

    @PostMapping("/today-main/generate")
    public ResponseEntity<ApiResult<Void>> generateTodayMainQuizDev() {
        schedular.generateTodayMainContent();
        return ResponseEntity.ok(ApiResult.ok(null));
    }

    @PostMapping("/category-summary/generate")
    public ResponseEntity<ApiResult<Void>> generateCategorySummaryDev() {
        // 비동기로 실행하여 API 타임아웃 방지
        java.util.concurrent.CompletableFuture.runAsync(() -> {
            try {
                schedular.runDailyTask();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return ResponseEntity.ok(ApiResult.ok(null));
    }

    @DeleteMapping("/{quizId}")
    public ResponseEntity<ApiResult<Void>> deleteQuizWithSubmissions(@PathVariable Long quizId) {
        List<QuizSubmission> submissions = quizSubmissionRepository.findByQuiz_Id(quizId);
        if (submissions != null && !submissions.isEmpty()) {
            quizSubmissionRepository.deleteAll(submissions);
        }
        if (quizRepository.existsById(quizId)) {
            quizRepository.deleteById(quizId);
        }
        return ResponseEntity.ok(ApiResult.ok(null));
    }
}

