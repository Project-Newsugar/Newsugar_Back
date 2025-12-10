package newsugar.domain.news.service;

import lombok.RequiredArgsConstructor;
import newsugar.domain.news.dto.NewsDto;
import newsugar.domain.news.dto.NewsResponse;
import newsugar.domain.news.entity.News;
import newsugar.domain.news.entity.NewsKeyword;
import newsugar.domain.news.entity.NewsRepository;
import newsugar.domain.news.entity.NewsSummary;
import newsugar.domain.news.model.NewsCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;

    /**
     * 최신 뉴스 조회 (전체)
     */
    public Page<NewsResponse> getLatestNews(Pageable pageable) {
        return newsRepository.findAllByOrderByPublishedAtDesc(pageable)
                .map(NewsResponse::fromEntity);
    }

    /**
     * 카테고리별 조회
     */
    public Page<NewsResponse> getNewsByCategory(NewsCategory category, Pageable pageable) {
        return newsRepository.findByCategoryOrderByPublishedAtDesc(category, pageable)
                .map(NewsResponse::fromEntity);
    }

    /**
     * RSSService 에서 전달받은 NewsDto 리스트 저장
     */
    public void saveNews(List<NewsDto> dtoList) {
        List<News> newsEntities = dtoList.stream().map(dto ->
                News.builder()
                        .title(dto.getTitle())
                        .link(dto.getLink())
                        .category(dto.getCategory())
                        .publishedAt(dto.getPublishedAt())
                        .summary(NewsSummary.builder()
                                .summaryText(dto.getSummary())
                                .build())
                        .keywords(dto.getKeywords().stream()
                                .map(k -> NewsKeyword.builder().keyword(k).build())
                                .collect(Collectors.toList()))
                        .build()
        ).collect(Collectors.toList());

        newsRepository.saveAll(newsEntities);
    }
}
