package newsugar.Newsugar_Back.domain.news.controller;

import newsugar.Newsugar_Back.common.ApiResult;
import newsugar.Newsugar_Back.domain.news.dto.DeepSearchResponseDTO;
import newsugar.Newsugar_Back.domain.news.service.NewsApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/news")
public class NewsApiController {

    private final NewsApiService newsApiService;

    public NewsApiController(NewsApiService newsApiService) {
        this.newsApiService = newsApiService;
    }

    @GetMapping
    public ResponseEntity<ApiResult<DeepSearchResponseDTO>> getNewsByCategory(
            @RequestParam String category,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int page_size
    ) {
        DeepSearchResponseDTO response = newsApiService.getNewsByCategory(category, page, page_size);
        return ResponseEntity.ok(ApiResult.ok(response));
    }
}