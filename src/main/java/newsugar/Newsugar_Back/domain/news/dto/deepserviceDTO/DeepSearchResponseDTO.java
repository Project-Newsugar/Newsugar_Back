package newsugar.Newsugar_Back.domain.news.dto.deepserviceDTO;

import java.util.List;

public record DeepSearchResponseDTO(
        Detail detail,
        long total_items,
        long total_pages,
        int page,
        int page_size,
        List<ArticleDTO> data
) {}

record Detail(
        String message,
        String code,
        boolean ok
) {}