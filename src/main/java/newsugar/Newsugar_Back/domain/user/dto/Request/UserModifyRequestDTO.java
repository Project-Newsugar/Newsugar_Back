package newsugar.Newsugar_Back.domain.user.dto.Request;

public record UserModifyRequestDTO(
        String name,
        String password,
        String nickname,
        String phone
) { }
