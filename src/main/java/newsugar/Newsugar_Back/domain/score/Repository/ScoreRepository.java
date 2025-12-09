package newsugar.Newsugar_Back.domain.score.Repository;

import newsugar.Newsugar_Back.domain.score.Model.Score;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScoreRepository extends JpaRepository<Score, Long> {
    Optional<Score> findByUserId(Long userId);
}
