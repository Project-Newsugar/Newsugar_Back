package newsugar.Newsugar_Back.domain.quiz.repository;

import newsugar.Newsugar_Back.domain.quiz.model.QuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {
}

