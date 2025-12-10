package newsugar.domain.news.controller;

import lombok.RequiredArgsConstructor;
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

    @GetMapping
    public List<NewsResponse> latestNews() {
        return newsService.getLatestNews();
    }

    @GetMapping("/{category}")
    public List<NewsResponse> newsByCategory(@PathVariable NewsCategory category) {
        return newsService.getNewsByCategory(category);
    }

    @PostMapping("/refresh/{category}")
    public String refreshNews(@PathVariable NewsCategory category) {

    // ⭐연합뉴스 RSS URL 매핑
        String rssUrl = switch (category) {
            case POLITICS -> "https://www.yna.co.kr/rss/politics.xml";
            case ECONOMY -> "https://www.yna.co.kr/rss/economy.xml";
            case SOCIAL -> "https://www.yna.co.kr/rss/society.xml";
            case IT -> "https://www.yna.co.kr/rss/it.xml";
            case WORLD -> "https://www.yna.co.kr/rss/world.xml";
            case SPORTS -> "https://www.yna.co.kr/rss/sports.xml";
            default -> "https://www.yna.co.kr/rss/all.xml";
        };

        newsService.refreshNews(category, rssUrl);
        return "OK";
    }
}