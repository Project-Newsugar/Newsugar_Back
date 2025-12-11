package newsugar.domain.news.entity;

import jakarta.persistence.*;
import lombok.*;
import newsugar.domain.news.model.NewsCategory;
import newsugar.domain.news.dto.NewsDto;

import java.time.LocalDateTime;

@Entity
@Table(name = "news")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String url;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Enumerated(EnumType.STRING)
    private NewsCategory category;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public static News fromDto(NewsDto dto) {
        return News.builder()
                .title(dto.title())
                .content(dto.content())
                .url(dto.link())
                .category(dto.category())
                .build();
    }
}
