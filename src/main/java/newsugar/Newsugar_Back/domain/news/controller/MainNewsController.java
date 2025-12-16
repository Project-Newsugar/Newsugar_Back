package newsugar.Newsugar_Back.domain.news.controller;

import newsugar.Newsugar_Back.common.ApiResult;
import newsugar.Newsugar_Back.domain.ai.GeminiService;
import newsugar.Newsugar_Back.domain.news.dto.deepserviceDTO.ArticleDTO;
import newsugar.Newsugar_Back.domain.news.dto.deepserviceDTO.DeepSearchResponseDTO;
import newsugar.Newsugar_Back.domain.news.service.NewsService;
import newsugar.Newsugar_Back.domain.news.service.RssNewsService;
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

    public MainNewsController(RssNewsService rssNewsService, NewsService newsService, GeminiService geminiService) {
        this.rssNewsService = rssNewsService;
        this.newsService = newsService;
        this.geminiService = geminiService;
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
        return ResponseEntity.ok(ApiResult.ok(todaySummary));
    }
}
