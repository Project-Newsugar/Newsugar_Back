package newsugar.Newsugar_Back.domain.ai;

import lombok.RequiredArgsConstructor;
import newsugar.Newsugar_Back.domain.ai.clients.GeminiClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GeminiService {

    private final GeminiClient geminiClient; // 이미 있거나 만들 예정

    public String summarize(String category, List<String> summaries) {
        String joinedSummaries = summaries.stream()
                .map(s -> "- " + s)
                .collect(Collectors.joining("\n"));

        String prompt = """
        다음은 최근 %s 뉴스들의 요약이야.
        이 내용들을 종합해서
        - 중복 없이
        - 객관적으로
        - 마크다운이나 이모티콘 없이
        - 5문장 이상의 하나의 카테고리 요약을
        - 하나의 문단으로 만들어줘.

        뉴스 요약 목록:
        %s
        """.formatted(category, joinedSummaries);

        return geminiClient.summarizeCategory(prompt);
    }

}