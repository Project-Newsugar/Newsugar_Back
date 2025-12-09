package newsugar.Newsugar_Back.domain.user.dto;

public record UserLoginRequestDTO(
        String email,
        String password
){}