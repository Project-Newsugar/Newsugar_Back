package newsugar.Newsugar_Back.domain.quiz.dto;

import java.util.List;
import java.time.Instant;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record CreateQuizRequest(
        @NotBlank String title,
        @NotEmpty @Valid List<QuestionCreate> questions,
        Instant startAt,
        Instant endAt
) {
    public static record QuestionCreate(
            @NotBlank String text,
            @NotEmpty List<String> options,
            Integer correctIndex
    ) {}
}
