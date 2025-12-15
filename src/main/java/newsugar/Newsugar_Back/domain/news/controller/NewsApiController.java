package newsugar.Newsugar_Back.domain.news.controller;

import newsugar.Newsugar_Back.common.ApiResult;
import newsugar.Newsugar_Back.domain.news.dto.deepserviceDTO.DeepSearchResponseDTO;
import newsugar.Newsugar_Back.domain.news.service.NewsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/news")
public class NewsApiController {
    private final NewsService newsService;

    public NewsApiController(NewsService newsApiService, NewsService newsService) {
        this.newsService = newsApiService;
    }

    @GetMapping
    public ResponseEntity<ApiResult<DeepSearchResponseDTO>> getNewsByCategory(
            @RequestParam(required = false) List<String> category,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer page_size
    ) {
            DeepSearchResponseDTO response = newsService.getNewsByCategory(category, page, page_size);
            return ResponseEntity.ok(ApiResult.ok(response));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResult<DeepSearchResponseDTO>> getNewsByKeyword (
            @RequestParam String keyword,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int page_size
    ) {
        DeepSearchResponseDTO response = newsService.getNewsByKeyword(keyword, page, page_size);
        return ResponseEntity.ok(ApiResult.ok(response));
    }
}