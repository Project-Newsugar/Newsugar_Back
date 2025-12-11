package newsugar.domain.news.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "summary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2000)
    private String summaryText;

    @OneToOne
    @JoinColumn(name = "news_id")
    private News news;
}
