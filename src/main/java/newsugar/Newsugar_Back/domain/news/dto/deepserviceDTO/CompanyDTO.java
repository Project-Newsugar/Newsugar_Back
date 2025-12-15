package newsugar.Newsugar_Back.domain.news.dto.deepserviceDTO;

public record CompanyDTO(
        String name,
        String symbol,
        String exchange,
        Object sentiment
) {}