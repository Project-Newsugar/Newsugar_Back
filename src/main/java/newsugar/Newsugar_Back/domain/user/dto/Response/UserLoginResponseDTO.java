package newsugar.Newsugar_Back.domain.user.dto.Response;

public record UserLoginResponseDTO (
    String accessToken,
    Long userId
){}
