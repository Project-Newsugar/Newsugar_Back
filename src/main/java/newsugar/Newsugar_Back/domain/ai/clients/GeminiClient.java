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
        Dotenv env = Dotenv.load();
        this.apiKey = env.get("QUIZ_API_KEY");
        if (this.apiKey == null || this.apiKey.isBlank()) {
            throw new RuntimeException("Gemini API Key가 설정되지 않았습니다.");
        }
    }

    public String summarizeCategory(String prompt) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;

        GeminiRequestDTO request = GeminiRequestDTO.of(prompt);

        GeminiResponseDTO response = restTemplate.postForObject(url, request, GeminiResponseDTO.class);

        if (response == null || response.getText() == null) {
            throw new RuntimeException("Gemini API 응답 오류");
        }

        return response.getText();
    }
}