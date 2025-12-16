package newsugar.Newsugar_Back.domain.news.controller;

import newsugar.Newsugar_Back.common.ApiResult;
import newsugar.Newsugar_Back.domain.ai.GeminiService;
import newsugar.Newsugar_Back.domain.news.dto.deepserviceDTO.ArticleDTO;
import newsugar.Newsugar_Back.domain.news.dto.deepserviceDTO.DeepSearchResponseDTO;
import newsugar.Newsugar_Back.domain.news.service.NewsService;
import newsugar.Newsugar_Back.domain.news.service.RssNewsService;
import newsugar.Newsugar_Back.domain.summary.model.Summary;
import newsugar.Newsugar_Back.domain.summary.repository.CategorySummaryRedis;
import newsugar.Newsugar_Back.domain.summary.repository.SummaryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/news")
public class MainNewsController {

    private final RssNewsService rssNewsService;
    private final NewsService newsService;
    private final GeminiService geminiService;
    private final CategorySummaryRedis categorySummaryRedis;
    private final SummaryRepository summaryRepository;

    public MainNewsController(RssNewsService rssNewsService,
                              NewsService newsService,
                              GeminiService geminiService,
                              CategorySummaryRedis categorySummaryRedis,
                              SummaryRepository summaryRepository) {
        this.rssNewsService = rssNewsService;
        this.newsService = newsService;
        this.geminiService = geminiService;
        this.categorySummaryRedis = categorySummaryRedis;
        this.summaryRepository = summaryRepository;
    }

    @GetMapping("/today-summary")
    public ResponseEntity<ApiResult<DeepSearchResponseDTO>> getTodayMainNewsSummary(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "5") Integer page_size
    ) {
        DeepSearchResponseDTO response;
        try {
            response = newsService.getNewsByCategory(null, page, page_size);
            boolean empty = (response == null) || (response.data() == null) || response.data().isEmpty();
            if (empty) {
                response = rssNewsService.getTopHeadlines(page, page_size);
            }
        } catch (Exception e) {
            response = rssNewsService.getTopHeadlines(page, page_size);
        }
        return ResponseEntity.ok(ApiResult.ok(response));
    }

    @GetMapping("/today-main-summary")
    public ResponseEntity<ApiResult<String>> getTodayMainSummaryText(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "5") Integer page_size
    ) {
        String redisKey = "today_main_summary";
        String cached = categorySummaryRedis.getSummary(redisKey);
        if (cached != null && !cached.isBlank()) {
            return ResponseEntity.ok(ApiResult.ok(cached));
        }
        java.util.Optional<Summary> latest = summaryRepository.findAll().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .findFirst();
        if (latest.isPresent()) {
            String txt = latest.get().getSummaryText();
            if (txt != null && !txt.isBlank()) {
                categorySummaryRedis.saveSummary(redisKey, txt);
                return ResponseEntity.ok(ApiResult.ok(txt));
            }
        }
        DeepSearchResponseDTO response;
        try {
            response = newsService.getNewsByCategory(null, page, page_size);
            boolean empty = (response == null) || (response.data() == null) || response.data().isEmpty();
            if (empty) {
                response = rssNewsService.getTopHeadlines(page, page_size);
            }
        } catch (Exception e) {
            response = rssNewsService.getTopHeadlines(page, page_size);
        }
        List<ArticleDTO> articles = (response != null && response.data() != null) ? response.data() : List.of();
        List<String> summaries = articles.stream()
                .map(ArticleDTO::summary)
                .filter(s -> s != null && !s.isBlank())
                .map(s -> s.replaceAll("<[^>]*>", " ").replaceAll("&[^;]+;", " ").trim())
                .filter(s -> !s.isBlank())
                .toList();
        if (summaries.isEmpty()) {
            return ResponseEntity.ok(ApiResult.ok(""));
        }
        String todaySummary = geminiService.summarize("오늘 주요", summaries);
        categorySummaryRedis.saveSummary(redisKey, todaySummary);
        return ResponseEntity.ok(ApiResult.ok(todaySummary));
    }
}
