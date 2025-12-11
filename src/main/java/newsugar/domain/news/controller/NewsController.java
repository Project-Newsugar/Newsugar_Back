package newsugar.domain.news.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import newsugar.domain.news.dto.NewsResponse;
import newsugar.domain.news.model.NewsCategory;
import newsugar.domain.news.service.NewsService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    // 최신 뉴스 20개 조회
    @GetMapping
    public List<NewsResponse> latestNews() {
        return newsService.getLatestNews();
    }

    // 카테고리별 조회
    @GetMapping("/{category}")
    public List<NewsResponse> newsByCategory(@PathVariable NewsCategory category) {
        return newsService.getNewsByCategory(category);
    }

    // 특정 카테고리 새로고침
    @PostMapping("/refresh/{category}")
    public String refreshCategory(@PathVariable NewsCategory category) {

        String rssUrl = switch (category) {
            case POLITICS -> "https://www.yna.co.kr/rss/politics.xml";
            case ECONOMY -> "https://www.yna.co.kr/rss/economy.xml";
            case SOCIETY -> "https://www.yna.co.kr/rss/society.xml";
            case IT -> "https://www.yna.co.kr/rss/it.xml";
            case WORLD -> "https://www.yna.co.kr/rss/world.xml";
            case SPORTS -> "https://www.yna.co.kr/rss/sports.xml";
        };

        newsService.refreshNews(category, rssUrl);
        return "OK";
    }

    // 전체 카테고리 뉴스 새로고침
    @PostMapping("/refresh")
    public String refreshAll() {

        newsService.refreshNews(NewsCategory.POLITICS, "https://www.yna.co.kr/rss/politics.xml");
        newsService.refreshNews(NewsCategory.ECONOMY, "https://www.yna.co.kr/rss/economy.xml");
        newsService.refreshNews(NewsCategory.SOCIETY, "https://www.yna.co.kr/rss/society.xml");
        newsService.refreshNews(NewsCategory.IT, "https://www.yna.co.kr/rss/it.xml");
        newsService.refreshNews(NewsCategory.WORLD, "https://www.yna.co.kr/rss/world.xml");
        newsService.refreshNews(NewsCategory.SPORTS, "https://www.yna.co.kr/rss/sports.xml");

        return "ALL REFRESHED";
    }

    // NewsAPI 외부 테스트 (옵션)
    @GetMapping("/external")
    public ResponseEntity<String> fetchExternalNews() {
        return ResponseEntity.ok(newsService.fetchExternalNews());
    }
}
