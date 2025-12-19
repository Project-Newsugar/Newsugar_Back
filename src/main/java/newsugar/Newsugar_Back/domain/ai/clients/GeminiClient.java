package newsugar.Newsugar_Back.domain.ai.clients;

import io.github.cdimascio.dotenv.Dotenv;
import newsugar.Newsugar_Back.domain.ai.dto.GeminiRequestDTO;
import newsugar.Newsugar_Back.domain.ai.dto.GeminiResponseDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GeminiClient {

    private final String apiKey;
    private final RestTemplate restTemplate = new RestTemplate();

    public GeminiClient() {
        Dotenv env = Dotenv.configure().ignoreIfMissing().load();
        String k = System.getenv("QUIZ_AI_API_KEY");
        if (k == null) k = env.get("QUIZ_AI_API_KEY");
        this.apiKey = k;
        if (this.apiKey == null || this.apiKey.isBlank()) {
            throw new RuntimeException("Gemini API Key가 설정되지 않았습니다.");
        }
    }

    public String summarizeCategory(String prompt) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-pro:generateContent?key=" + apiKey;
        GeminiRequestDTO request = GeminiRequestDTO.of(prompt);

        int maxRetries = 5;
        int retryCount = 0;
        long waitTime = 10000; // 10초로 시작

        while (retryCount < maxRetries) {
            try {
                GeminiResponseDTO response = restTemplate.postForObject(url, request, GeminiResponseDTO.class);
                if (response != null && response.getText() != null) {
                    return response.getText();
                }
            } catch (Exception e) {
                if (e.getMessage().contains("429")) {
                    System.out.println("Gemini API Quota Exceeded. Retrying in " + (waitTime / 1000) + "s...");
                    try {
                        Thread.sleep(waitTime);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Interrupted during retry wait", ie);
                    }
                    retryCount++;
                    waitTime *= 2; // 지수 백오프
                    continue;
                }
                throw e; // 다른 에러는 바로 던짐
            }
        }
        throw new RuntimeException("Gemini API 호출 실패: 재시도 횟수 초과");
    }
}
