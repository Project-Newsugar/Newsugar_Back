package newsugar.Newsugar_Back.domain.quiz.model;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.AccessLevel;
import lombok.Builder.Default;

@Entity
@Table(name = "user_quiz")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "score_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "submission_id")
    @Default
    private List<SubmissionAnswer> answers = new ArrayList<>();

    @Column(name = "total")
    private int total;
    @Column(name = "quiz_score")
    private int correct;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
<<<<<<< HEAD
=======

>>>>>>> 0b6ed08 (feat(quiz): 요약 기반 AI 퀴즈 생성 서비스엔드포인트 추가\n- Summary 연동 및 AI 호출로 질문 생성\n- QuizService에 generateFromSummary 추가 및 구현\n- 컨트롤러에 /quizzes/summary/{summaryId}/generate 추가\n- 기존 구조 유지, 예외는 팀 공통 코드 사용)
    @Column(name = "user_id")
    private Long userId;
}
