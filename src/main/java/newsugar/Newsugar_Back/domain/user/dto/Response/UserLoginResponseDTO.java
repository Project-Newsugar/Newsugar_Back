package newsugar.Newsugar_Back.domain.user.dto.Response;

public record UserLoginResponseDTO (
    Long userId,
    String accessToken,
    String refreshToken

){}
