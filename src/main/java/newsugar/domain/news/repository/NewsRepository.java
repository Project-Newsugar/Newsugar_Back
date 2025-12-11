package newsugar.domain.news.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import newsugar.domain.news.entity.News;
import newsugar.domain.news.model.NewsCategory;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {

    // 중복 체크용
    boolean existsByUrl(String url);

    // 기존 최신 20개 조회
    List<News> findTop20ByOrderByPublishedAtDesc();

    // 기존 카테고리별 조회
    List<News> findByCategoryOrderByPublishedAtDesc(NewsCategory category);

    // Pageable 기반 API
    Page<News> findAllByOrderByPublishedAtDesc(Pageable pageable);

    Page<News> findByCategoryOrderByPublishedAtDesc(NewsCategory category, Pageable pageable);
}
