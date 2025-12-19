package newsugar.Newsugar_Back.schedular;

import newsugar.Newsugar_Back.domain.ai.GeminiService;
import newsugar.Newsugar_Back.domain.ai.clients.AiQuizClient;
import newsugar.Newsugar_Back.domain.news.dto.deepserviceDTO.ArticleDTO;
import newsugar.Newsugar_Back.domain.news.dto.deepserviceDTO.DeepSearchResponseDTO;
import newsugar.Newsugar_Back.domain.news.service.NewsService;
import newsugar.Newsugar_Back.domain.news.service.RssNewsService;
import newsugar.Newsugar_Back.domain.quiz.model.Question;
import newsugar.Newsugar_Back.domain.quiz.model.Quiz;
import newsugar.Newsugar_Back.domain.quiz.service.QuizService;
import newsugar.Newsugar_Back.domain.summary.model.Summary;
import newsugar.Newsugar_Back.domain.summary.repository.CategorySummaryRedis;
import newsugar.Newsugar_Back.domain.summary.repository.SummaryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Service
public class DailyTaskService {

    private final NewsService newsService;
    private final RssNewsService rssNewsService;
    private final CategorySummaryRedis categorySummaryRedis;
    private final GeminiService geminiService;
    private final SummaryRepository summaryRepository;
    private final AiQuizClient aiQuizClient;
    private final QuizService quizService;

    private static final String TODAY_MAIN_KEY = "today_main_summary";

    public DailyTaskService(NewsService newsService,
                            RssNewsService rssNewsService,
                            CategorySummaryRedis categorySummaryRedis,
                            GeminiService geminiService,
                            SummaryRepository summaryRepository,
                            AiQuizClient aiQuizClient,
                            QuizService quizService) {
        this.newsService = newsService;
        this.rssNewsService = rssNewsService;
        this.categorySummaryRedis = categorySummaryRedis;
        this.geminiService = geminiService;
        this.summaryRepository = summaryRepository;
        this.aiQuizClient = aiQuizClient;
        this.quizService = quizService;
    }

    @Transactional
    public void executeDailyRoutine() {
        // 뉴스 가져오기 및 요약 생성
        Summary summary = generateSummary();

        // 생성된 요약을 바탕으로 퀴즈 생성
        if (summary != null) {
            generateQuiz(summary);
        }
    }

    private Summary generateSummary() {
        DeepSearchResponseDTO news;
        try {
            // 뉴스 조회 시 정렬 파라미터(sort=date) 추가는 NewsService 내부 로직 개선 필요
            news = newsService.getNewsByCategory(null, 1, 5);
            boolean empty = (news == null) || (news.data() == null) || news.data().isEmpty();
            if (empty) {
                news = rssNewsService.getTopHeadlines(1, 5);
            }
        } catch (Exception e) {
            news = rssNewsService.getTopHeadlines(1, 5);
        }

        List<ArticleDTO> items = (news != null && news.data() != null) ? news.data() : List.of();
        List<String> summaries = new ArrayList<>();
        for (int i = 0; i < Math.min(5, items.size()); i++) {
            String s = items.get(i).summary();
            if (s != null) {
                s = s.replaceAll("<[^>]*>", " ").replaceAll("&[^;]+;", " ").trim();
                if (!s.isBlank()) summaries.add(s);
            }
        }

        if (summaries.isEmpty()) {
            categorySummaryRedis.deleteSummary(TODAY_MAIN_KEY);
            return null;
        }

        String todaySummary = geminiService.summarize("오늘 주요", summaries);
        Summary summary = Summary.builder()
                .summaryText(todaySummary)
                .build();
        
        return summaryRepository.save(summary);
    }

    private void generateQuiz(Summary baseSummary) {
        if (baseSummary == null || baseSummary.getSummaryText() == null || baseSummary.getSummaryText().isBlank()) {
            return;
        }

        String aggregated = baseSummary.getSummaryText();

        List<AiQuizClient.QuestionData> gen;
        try {
            String finalSummary = (aggregated != null && !aggregated.isBlank()) ? aiQuizClient.summarize(aggregated) : null;
            gen = (finalSummary != null && !finalSummary.isBlank()) ? aiQuizClient.generate(finalSummary) : List.of();
        } catch (Exception ex) {
            gen = List.of();
        }

        if (gen == null || gen.isEmpty()) {
            String src = (aggregated != null && !aggregated.isBlank()) ? aggregated : null;
            if (src == null) src = "";
            String base = src.replaceAll("[^가-힣A-Za-z0-9 ]", " ").trim();
            String[] toks = base.split("\\s+");
            LinkedHashSet<String> uniq = new LinkedHashSet<>();
            for (String t : toks) {
                if (t != null && t.length() >= 2) {
                    uniq.add(t);
                    if (uniq.size() >= 4) break;
                }
            }
            List<String> opts = new ArrayList<>(uniq);
            if (opts.size() < 2) {
                opts = List.of("예", "아니오");
            }
            AiQuizClient.QuestionData fd = new AiQuizClient.QuestionData();
            fd.text = "요약의 핵심 키워드로 가장 적합한 것은 무엇인가요?";
            fd.options = opts;
            fd.correctIndex = 0;
            fd.explanation = null;
            gen = List.of(fd);
        }

        List<Question> questions = new ArrayList<>();
        if (gen != null && !gen.isEmpty()) {
            AiQuizClient.QuestionData d = gen.get(0);
            Question q = new Question();
            q.setText(d.text);
            q.setOptions(d.options != null ? d.options : List.of());
            q.setCorrectIndex(d.correctIndex);
            q.setExplanation(d.explanation);
            questions.add(q);
        }

        Quiz quiz = new Quiz();
        quiz.setTitle("오늘의 주요뉴스 퀴즈");
        quiz.setQuestions(questions);
        Instant now = Instant.now();
        quiz.setStartAt(now);
        quiz.setEndAt(now.plus(Duration.ofHours(6)));

        quizService.create(quiz);
    }
}
