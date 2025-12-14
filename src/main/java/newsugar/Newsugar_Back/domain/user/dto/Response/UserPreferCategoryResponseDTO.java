package newsugar.Newsugar_Back.domain.user.dto.Response;

import java.util.List;

public record UserPreferCategoryResponseDTO(
        Long id,
        Long userId,
        List<String> categoryIdList
) {}
