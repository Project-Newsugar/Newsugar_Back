package newsugar.Newsugar_Back.domain.user.dto;

public record UserLoginResponseDTO (
    String accessToken,
    Long userId
){}
