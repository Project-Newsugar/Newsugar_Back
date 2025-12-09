package newsugar.Newsugar_Back.domain.user.dto.Response;

import newsugar.Newsugar_Back.domain.user.model.Score;
import newsugar.Newsugar_Back.domain.user.model.User;

public record UserInfoResponseDTO (
        User user,
        Integer score
){}
