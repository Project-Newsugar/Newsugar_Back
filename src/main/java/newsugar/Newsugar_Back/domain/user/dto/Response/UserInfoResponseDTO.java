package newsugar.Newsugar_Back.domain.user.dto.Response;

public record UserInfoResponseDTO (
        Long id,
        String name,
        String email,
        String nickname,
        String phone,
        Integer score
){}
