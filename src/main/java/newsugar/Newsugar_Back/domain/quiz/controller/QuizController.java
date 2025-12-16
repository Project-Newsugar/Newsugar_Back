package newsugar.Newsugar_Back.domain.quiz.controller;

import newsugar.Newsugar_Back.common.ApiResult;
import newsugar.Newsugar_Back.domain.quiz.dto.SubmitRequest;
import newsugar.Newsugar_Back.domain.quiz.dto.SubmitResult;
import newsugar.Newsugar_Back.domain.quiz.model.Quiz;
import newsugar.Newsugar_Back.domain.quiz.model.Question;
import newsugar.Newsugar_Back.domain.quiz.dto.QuizResponse;
import newsugar.Newsugar_Back.domain.quiz.dto.UserQuizStats;
import newsugar.Newsugar_Back.domain.quiz.service.QuizService;
import newsugar.Newsugar_Back.domain.news.service.NewsService;
import newsugar.Newsugar_Back.domain.news.service.RssNewsService;
import newsugar.Newsugar_Back.domain.ai.clients.AiQuizClient;
import newsugar.Newsugar_Back.domain.news.dto.deepserviceDTO.DeepSearchResponseDTO;
import newsugar.Newsugar_Back.domain.news.dto.deepserviceDTO.ArticleDTO;
import newsugar.Newsugar_Back.domain.user.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/quizzes")
@Validated
public class QuizController {
    private final QuizService quizService;
    private final JwtService jwtService;
    private final NewsService newsService;
    private final RssNewsService rssNewsService;
    private final AiQuizClient aiQuizClient;

    public QuizController(QuizService quizService, JwtService jwtService, NewsService newsService, AiQuizClient aiQuizClient, RssNewsService rssNewsService) {
        this.quizService = quizService;
        this.jwtService = jwtService;
        this.newsService = newsService;
        this.aiQuizClient = aiQuizClient;
        this.rssNewsService = rssNewsService;
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
        java.time.Instant now = java.time.Instant.now();
        boolean playable = (quiz != null) && ((quiz.getStartAt() == null || !now.isBefore(quiz.getStartAt()))
                && (quiz.getEndAt() == null || !now.isAfter(quiz.getEndAt())));
        QuizResponse res = toResponse(quiz, !playable);
        return ResponseEntity.ok(ApiResult.ok(res));
    }

    @PostMapping("/summary/{summaryId}/generate")
    public ResponseEntity<ApiResult<QuizResponse>> generateFromSummary(
            @PathVariable Long summaryId,
            @RequestHeader("Authorization") String token
    ) {
        String actualToken = token != null ? token.replace("Bearer ", "") : null;
        jwtService.getUserIdFromToken(actualToken);
        Quiz quiz = quizService.generateFromSummary(summaryId);
        QuizResponse res = toResponse(quiz, false);
        return ResponseEntity.ok(ApiResult.ok(res));
    }


    @PostMapping("/today-main/generate")
    public ResponseEntity<ApiResult<QuizResponse>> generateTodayMainQuiz(
            @RequestHeader("Authorization") String token
    ) {
        String actualToken = token != null ? token.replace("Bearer ", "") : null;
        jwtService.getUserIdFromToken(actualToken);

        DeepSearchResponseDTO news;
        try {
            news = newsService.getNewsByCategory(null, 1, 5);
            boolean empty = (news == null) || (news.data() == null) || news.data().isEmpty();
            if (empty) {
                news = rssNewsService.getTopHeadlines(1, 5);
            }
        } catch (Exception e) {
            news = rssNewsService.getTopHeadlines(1, 5);
        }
        java.util.List<ArticleDTO> items = (news != null && news.data() != null) ? news.data() : java.util.List.of();
        java.util.List<String> summaries = new java.util.ArrayList<>();
        for (int i = 0; i < Math.min(5, items.size()); i++) {
            String s = items.get(i).summary();
            if (s != null) {
                s = s.replaceAll("<[^>]*>", " ").replaceAll("&[^;]+;", " ").trim();
                if (!s.isBlank()) summaries.add(s);
            }
        }
        String aggregated = String.join("\n- ", summaries);

        java.util.List<newsugar.Newsugar_Back.domain.ai.clients.AiQuizClient.QuestionData> gen;
        try {
            String finalSummary = (aggregated != null && !aggregated.isBlank()) ? aiQuizClient.summarize(aggregated) : null;
            gen = (finalSummary != null && !finalSummary.isBlank()) ? aiQuizClient.generate(finalSummary) : java.util.List.of();
        } catch (Exception ex) {
            gen = java.util.List.of();
        }

        if ((gen == null || gen.isEmpty())) {
            String src = (aggregated != null && !aggregated.isBlank()) ? aggregated : null;
            if (src == null) src = "";
            String base = src.replaceAll("[^가-힣A-Za-z0-9 ]", " ").trim();
            String[] toks = base.split("\\s+");
            java.util.LinkedHashSet<String> uniq = new java.util.LinkedHashSet<>();
            for (String t : toks) {
                if (t != null && t.length() >= 2) {
                    uniq.add(t);
                    if (uniq.size() >= 4) break;
                }
            }
            java.util.List<String> opts = new java.util.ArrayList<>(uniq);
            if (opts.size() < 2) {
                opts = java.util.List.of("예", "아니오");
            }
            newsugar.Newsugar_Back.domain.ai.clients.AiQuizClient.QuestionData fd = new newsugar.Newsugar_Back.domain.ai.clients.AiQuizClient.QuestionData();
            fd.text = "요약의 핵심 키워드로 가장 적합한 것은 무엇인가요?";
            fd.options = opts;
            fd.correctIndex = 0;
            fd.explanation = null;
            gen = java.util.List.of(fd);
        }

        java.util.List<Question> questions = new java.util.ArrayList<>();
        if (gen != null && !gen.isEmpty()) {
            newsugar.Newsugar_Back.domain.ai.clients.AiQuizClient.QuestionData d = gen.get(0);
            Question q = new Question();
            q.setText(d.text);
            q.setOptions(d.options != null ? d.options : java.util.List.of());
            q.setCorrectIndex(d.correctIndex);
            q.setExplanation(d.explanation);
            questions.add(q);
        }

        Quiz quiz = new Quiz();
        quiz.setTitle("오늘의 주요뉴스 퀴즈");
        quiz.setQuestions(questions);
        java.time.Instant now = java.time.Instant.now();
        quiz.setStartAt(now);
        quiz.setEndAt(now.plus(java.time.Duration.ofHours(6)));

        Quiz saved = quizService.create(quiz);
        QuizResponse res = toResponse(saved, false);
        return ResponseEntity.ok(ApiResult.ok(res));
    }


    @PostMapping("/{id}/submit")
    public ResponseEntity<ApiResult<SubmitResult>> submit(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token,
            @RequestBody SubmitRequest req
    ) {
        String actualToken = token != null ? token.replace("Bearer ", "") : null;
        Long userId = jwtService.getUserIdFromToken(actualToken);
        SubmitResult result = quizService.score(
                id,
                userId,
                req != null ? req.answers() : null
        );
        return ResponseEntity.ok(ApiResult.ok(result));
    }

    @GetMapping("/{id}/result")
    public ResponseEntity<ApiResult<SubmitResult>> result(@PathVariable Long id) {
        SubmitResult last = quizService.resultOrThrow(id);
        return ResponseEntity.ok(ApiResult.ok(last));
    }

    @GetMapping("/{id}/answers")
    public ResponseEntity<ApiResult<QuizResponse>> answers(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ) {
        String actualToken = token != null ? token.replace("Bearer ", "") : null;
        Long userId = jwtService.getUserIdFromToken(actualToken);
        boolean has = quizService.hasSubmission(id, userId);
        if (!has) {
            return ResponseEntity.status(403).body(ApiResult.error(newsugar.Newsugar_Back.common.ErrorCode.FORBIDDEN.name(), "제출 이력이 없습니다"));
        }
        Quiz quiz = quizService.get(id);
        QuizResponse res = toResponse(quiz, true);
        return ResponseEntity.ok(ApiResult.ok(res));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResult<UserQuizStats>> stats(
            @RequestHeader("Authorization") String token
    ) {
        String actualToken = token != null ? token.replace("Bearer ", "") : null;
        Long userId = jwtService.getUserIdFromToken(actualToken);
        UserQuizStats stats = quizService.statsForUser(userId);
        return ResponseEntity.ok(ApiResult.ok(stats));
    }

    private QuizResponse toResponse(Quiz quiz, boolean includeAnswers) {
        if (quiz == null) return null;
        java.util.List<QuizResponse.QuestionView> views = new java.util.ArrayList<>();
        if (quiz.getQuestions() != null) {
            for (Question q : quiz.getQuestions()) {
                views.add(new QuizResponse.QuestionView(
                        q.getText(),
                        q.getOptions(),
                        includeAnswers ? q.getCorrectIndex() : null,
                        includeAnswers ? q.getExplanation() : null
                ));
            }
        }
        return new QuizResponse(quiz.getId(), quiz.getTitle(), views, quiz.getStartAt(), quiz.getEndAt());
    }
}
