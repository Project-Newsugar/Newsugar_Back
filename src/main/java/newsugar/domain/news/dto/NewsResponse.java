package newsugar.domain.news.dto;

import newsugar.domain.news.model.News;

public record NewsResponse(
        Long id,
        String title,
        String content,
        String url,
        String category
) {

    public static NewsResponse from(News news) {
        return new NewsResponse(
                news.getId(),
                news.getTitle(),
                news.getContent(),
                news.getUrl(),
                news.getCategory().name()
        );
    }
}
