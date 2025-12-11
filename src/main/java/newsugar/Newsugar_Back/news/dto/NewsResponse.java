package newsugar.domain.news.dto;

import newsugar.domain.news.entity.News;
import newsugar.domain.news.model.NewsCategory;

import java.time.LocalDateTime;

public record NewsResponse(
        Long id,
        String title,
        String content,
        String url,
        NewsCategory category,
        LocalDateTime publishedAt
) {

    public static NewsResponse from(News news) {
        return new NewsResponse(
                news.getId(),
                news.getTitle(),
                news.getContent(),
                news.getUrl(),
                news.getCategory(),
                news.getPublishedAt()
        );
    }
}
