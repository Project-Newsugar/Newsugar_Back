package newsugar.Newsugar_Back.domain.score.Service;

import jakarta.transaction.Transactional;
import newsugar.Newsugar_Back.common.CustomException;
import newsugar.Newsugar_Back.common.ErrorCode;
import newsugar.Newsugar_Back.domain.score.Model.Score;
import newsugar.Newsugar_Back.domain.user.model.User;
import newsugar.Newsugar_Back.domain.score.Repository.ScoreRepository;
import newsugar.Newsugar_Back.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ScoreService {
    private final ScoreRepository scoreRepository;
    private final UserRepository userRepository;

    public ScoreService(ScoreRepository scoreRepository, UserRepository userRepository) {

        this.scoreRepository = scoreRepository;
        this.userRepository = userRepository;
    }

    public Integer getScore (Long userId){
        Score score = scoreRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUTH_ACCOUNT_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        return score.getScore();
    }

    public Score createScore (Long userId){
         User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUTH_ACCOUNT_NOT_FOUND, "사용자를 찾을 수 없습니다."));

         Score score = Score.builder()
                 .user(user)
                 .score(0)
                 .build();

         return scoreRepository.save(score);

    }
}
