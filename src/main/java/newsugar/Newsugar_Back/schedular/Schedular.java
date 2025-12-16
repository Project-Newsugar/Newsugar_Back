package newsugar.Newsugar_Back.schedular;

import newsugar.Newsugar_Back.domain.ai.clients.AiQuizClient;
import newsugar.Newsugar_Back.domain.news.dto.deepserviceDTO.ArticleDTO;
import newsugar.Newsugar_Back.domain.news.dto.deepserviceDTO.DeepSearchResponseDTO;
import newsugar.Newsugar_Back.domain.news.service.NewsService;
import newsugar.Newsugar_Back.domain.news.service.RssNewsService;
import newsugar.Newsugar_Back.domain.quiz.model.Question;
import newsugar.Newsugar_Back.domain.quiz.model.Quiz;
import newsugar.Newsugar_Back.domain.quiz.service.QuizService;
import newsugar.Newsugar_Back.domain.summary.service.CategorySummaryService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Component
public class Schedular {

    private final CategorySummaryService categorySummaryService;
    private final QuizService quizService;
    private final NewsService newsService;
    private final RssNewsService rssNewsService;
    private final AiQuizClient aiQuizClient;
    private final String[] categories = {"politics", "economy","society", "culture", "tech", "entertainment", "opinion"};

    public Schedular(CategorySummaryService categorySummaryService,
                     QuizService quizService,
                     NewsService newsService,
                     RssNewsService rssNewsService,
                     AiQuizClient aiQuizClient) {
        this.categorySummaryService = categorySummaryService;
        this.quizService = quizService;
        this.newsService = newsService;
        this.rssNewsService = rssNewsService;
        this.aiQuizClient = aiQuizClient;
    }

    @Scheduled(cron = "0 0 0,6,12,18 * * *")
    public void generateAllCategorySummaries() {
        for (String category : categories) {
            categorySummaryService.generateCategorySummary(category);
        }
    }

    @Scheduled(cron = "0 0 0,6,12,18 * * *")
    public void generateTodayMainQuiz() {
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
        List<ArticleDTO> items = (news != null && news.data() != null) ? news.data() : List.of();
        List<String> summaries = new ArrayList<>();
        for (int i = 0; i < Math.min(5, items.size()); i++) {
            String s = items.get(i).summary();
            if (s != null) {
                s = s.replaceAll("<[^>]*>", " ").replaceAll("&[^;]+;", " ").trim();
                if (!s.isBlank()) summaries.add(s);
            }
        }
        String aggregated = String.join("\n- ", summaries);

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
