package newsugar.Newsugar_Back.domain.category.Repository;

import newsugar.Newsugar_Back.domain.category.Model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findById(Long id);
}