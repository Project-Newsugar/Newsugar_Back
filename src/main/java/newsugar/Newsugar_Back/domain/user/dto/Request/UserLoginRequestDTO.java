package newsugar.Newsugar_Back.domain.user.dto.Request;

public record UserLoginRequestDTO(
        String email,
        String password
){}