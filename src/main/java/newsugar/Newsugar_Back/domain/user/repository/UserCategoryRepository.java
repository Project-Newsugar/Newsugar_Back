package newsugar.Newsugar_Back.domain.user.repository;

import newsugar.Newsugar_Back.domain.user.model.User;
import newsugar.Newsugar_Back.domain.user.model.UserCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCategoryRepository extends JpaRepository<UserCategory, Long> {
    Optional<UserCategory> findById(Long id);
    boolean existsByUserIdAndCategoryId(Long userId, Long categoryId);
}
