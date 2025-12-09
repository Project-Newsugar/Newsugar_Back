package newsugar.Newsugar_Back.domain.user.dto.Request;

public record UserSignupRequestDTO(
        String name,
        String email,
        String password,
        String nickname,
        String phone
){}