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
        다음은 최근 %s 카테고리 뉴스들의 요약이다.
          
          이 뉴스들을 종합하여 아래 조건을 반드시 지켜서 요약을 생성하라.
          
          [요약 조건]
          - 중복되는 내용은 제거할 것
          - 객관적인 서술체를 사용할 것
          - 이모티콘, 감정 표현, 의견은 포함하지 말 것
          - 하나의 문단으로 작성할 것 (줄바꿈 금지)
          - 최소 5문장, 최대 7문장으로 작성할 것
          - 중요하다고 판단되는 핵심 단어 또는 문장은 Markdown 문법의 ==이렇게== 표시할 것
          - 리스트, 번호, 제목, 코드블록을 사용하지 말 것
          - HTML 태그를 절대 사용하지 말 것
          - React에서 바로 렌더링 가능한 순수 Markdown 텍스트만 출력할 것
          
          [출력 규칙]
          - 요약 내용만 출력하고 그 외 설명은 포함하지 말 것
        """.formatted(category, joinedSummaries);

        return geminiClient.summarizeCategory(prompt);
    }

}