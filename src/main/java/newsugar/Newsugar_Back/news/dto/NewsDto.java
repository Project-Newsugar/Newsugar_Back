package newsugar.domain.news.dto;

import newsugar.domain.news.model.NewsCategory;

import java.time.LocalDateTime;

public record NewsDto(
        String title,
        String content,
        String link,
        NewsCategory category,
        LocalDateTime publishedAt
) {}
