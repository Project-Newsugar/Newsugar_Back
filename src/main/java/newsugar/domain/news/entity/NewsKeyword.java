package newsugar.domain.news.entity;

import jakarta.persistence.*;
import lombok.*;
import newsugar.domain.news.entity.News;


@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String keyword;

    @ManyToOne
    private News news;
}
