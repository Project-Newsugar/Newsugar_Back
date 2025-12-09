package newsugar.Newsugar_Back.domain.user.service;

import jakarta.transaction.Transactional;
import newsugar.Newsugar_Back.common.CustomException;
import newsugar.Newsugar_Back.common.ErrorCode;
import newsugar.Newsugar_Back.domain.user.model.Score;
import newsugar.Newsugar_Back.domain.user.repository.ScoreRepository;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ScoreService {
    public final ScoreRepository scoreRepository;

    public ScoreService(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    public Integer getScore (Long userId){
        Score score = scoreRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUTH_ACCOUNT_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        return score.getScore();
    }
}
