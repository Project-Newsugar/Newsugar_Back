package newsugar.domain.news.service;

import lombok.RequiredArgsConstructor;
import newsugar.domain.news.dto.NewsDto;
import newsugar.domain.news.repository.NewsRepository;
import newsugar.domain.news.entity.News;
import newsugar.domain.news.model.NewsCategory;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final RSSService rssService;
    private final NewsRepository newsRepository;

    private static final String API_KEY = "YOUR_NEWS_API_KEY";   // ← 여기만 바꾸면됨
    private static final String NEWS_API_URL =
            "https://newsapi.org/v2/top-headlines?country=kr&category=%s&apiKey=%s";

    // 1) RSS로 뉴스 저장
    public void refreshNews(String rssUrl) {

        List<NewsDto> dtoList = rssService.fetchRss(rssUrl);

        for (NewsDto dto : dtoList) {
            News news = News.builder()
                    .title(dto.title())
                    .content(dto.content())
                    .url(dto.link())
                    .category(dto.category())
                    .build();

            newsRepository.save(news);
        }
    }

    // RSS + Category 적용
    public void refreshNews(NewsCategory category, String rssUrl) {
        List<NewsDto> dtoList = rssService.fetchRss(rssUrl);

        for (NewsDto dto : dtoList) {
            News news = News.builder()
                    .title(dto.title())
                    .content(dto.content())
                    .url(dto.link())
                    .category(category)   // ← 강제 카테고리 지정
                    .build();
            newsRepository.save(news);
        }
    }

    // 2) NEWS API (newsapi.org) 으로 뉴스 저장
    public int refreshNewsFromApi(NewsCategory category) {
