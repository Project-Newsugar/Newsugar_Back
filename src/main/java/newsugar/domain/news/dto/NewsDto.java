package newsugar.domain.news.dto;

import newsugar.domain.news.model.NewsCategory;

public record NewsDto(
        String title,
        String content,
        String link,
        NewsCategory category
) {
}
