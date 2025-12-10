package newsugar.Newsugar_Back.domain.quiz.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.AccessLevel;

@Entity
@Table(name = "quiz_answers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "questionIndex")
    private Integer questionIndex;
    @Column(name = "user_answer")
    private Integer chosenIndex;
    @Column(name = "is_correct")
    private Boolean correct;
    @Column(name = "answered_at")
    private java.time.Instant answeredAt;
<<<<<<< HEAD
=======

>>>>>>> 0b6ed08 (feat(quiz): 요약 기반 AI 퀴즈 생성 서비스엔드포인트 추가\n- Summary 연동 및 AI 호출로 질문 생성\n- QuizService에 generateFromSummary 추가 및 구현\n- 컨트롤러에 /quizzes/summary/{summaryId}/generate 추가\n- 기존 구조 유지, 예외는 팀 공통 코드 사용)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "quiz_id")
    private Long quizId;
}
