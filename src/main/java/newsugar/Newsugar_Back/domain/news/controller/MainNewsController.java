package newsugar.Newsugar_Back.domain.news.controller;

import newsugar.Newsugar_Back.common.ApiResult;
import newsugar.Newsugar_Back.domain.news.dto.deepserviceDTO.DeepSearchResponseDTO;
import newsugar.Newsugar_Back.domain.news.service.NewsService;
import newsugar.Newsugar_Back.domain.news.service.RssNewsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/news")
public class MainNewsController {

    private final RssNewsService rssNewsService;
    private final NewsService newsService;

    public MainNewsController(RssNewsService rssNewsService, NewsService newsService) {
        this.rssNewsService = rssNewsService;
        this.newsService = newsService;
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
}
