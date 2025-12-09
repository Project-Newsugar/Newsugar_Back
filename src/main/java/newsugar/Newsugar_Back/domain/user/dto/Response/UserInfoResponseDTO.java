package newsugar.Newsugar_Back.domain.user.dto.Response;

import newsugar.Newsugar_Back.domain.user.model.Score;
import newsugar.Newsugar_Back.domain.user.model.User;

public record UserInfoResponseDTO (
        Long id,
        String name,
        String email,
        String nickname,
        String phone,
        Integer score
){}
