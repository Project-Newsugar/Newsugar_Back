package newsugar.Newsugar_Back.domain.news.dto;

import java.util.List;

public record NaverNewsResponseDTO(
        String lastBuildDate,
        int total,
        int start,
        int display,
        List<Item> items
) {
    public record Item(
            String title,
            String link,
            String description,
            String pubDate
    ) {}
}