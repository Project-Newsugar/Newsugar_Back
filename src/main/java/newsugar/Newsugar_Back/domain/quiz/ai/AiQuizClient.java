package newsugar.Newsugar_Back.domain.quiz.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import newsugar.Newsugar_Back.common.CustomException;
import newsugar.Newsugar_Back.common.ErrorCode;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class AiQuizClient {
    private final String baseUrl;
    private final String apiKey;
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newHttpClient();

    public static class QuestionData {
        public String text;
        public List<String> options;
        public Integer correctIndex;
        public String explanation;
    }

    public AiQuizClient() {
        Dotenv env = Dotenv.load();
        this.baseUrl = env.get("QUIZ_AI_BASE_URL");
        this.apiKey = env.get("QUIZ_AI_API_KEY");
    }

    public List<QuestionData> generate(String summaryText) {
        if (baseUrl == null || apiKey == null) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR, "AI 설정이 없습니다");
        }
        try {
            String body = mapper.createObjectNode()
                    .put("summary", summaryText)
                    .toString();
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/generate-quiz"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (res.statusCode() >= 300) {
                throw new CustomException(ErrorCode.INTERNAL_ERROR, "AI 호출 실패");
            }
            JsonNode root = mapper.readTree(res.body());
            JsonNode qs = root.get("questions");
            List<QuestionData> out = new ArrayList<>();
            if (qs != null && qs.isArray()) {
                for (JsonNode q : qs) {
                    QuestionData d = new QuestionData();
                    d.text = q.get("text").asText();
                    List<String> opts = new ArrayList<>();
                    JsonNode arr = q.get("options");
                    if (arr != null && arr.isArray()) {
                        for (JsonNode o : arr) opts.add(o.asText());
                    }
                    d.options = opts;
                    d.correctIndex = q.has("correctIndex") ? q.get("correctIndex").asInt() : null;
                    d.explanation = q.has("explanation") && !q.get("explanation").isNull() ? q.get("explanation").asText() : null;
                    out.add(d);
                }
            }
            return out;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR, "AI 처리 오류");
        }
    }
}
