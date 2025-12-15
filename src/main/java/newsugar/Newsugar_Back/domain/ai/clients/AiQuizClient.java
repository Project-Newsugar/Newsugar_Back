package newsugar.Newsugar_Back.domain.ai.clients;

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
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
public class AiQuizClient {
    private final String baseUrl;
    private final String apiKey;
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient client;
    private final long timeoutMs;

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
        long tm = 0L;
        try {
            String v = env.get("QUIZ_AI_TIMEOUT_MS");
            if (v != null && !v.isBlank()) tm = Long.parseLong(v.trim());
        } catch (Exception ignored) {}
        this.timeoutMs = tm > 0 ? tm : 0L;
        HttpClient.Builder builder = HttpClient.newBuilder();
        if (timeoutMs > 0) builder = builder.connectTimeout(Duration.ofMillis(timeoutMs));
        this.client = builder.build();
    }

    public List<QuestionData> generate(String summaryText) {
        if (baseUrl == null || apiKey == null) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR, "AI 설정이 없습니다");
        }
        try {
            String body = mapper.createObjectNode()
                    .put("summary", summaryText)
                    .toString();
            HttpRequest.Builder rb = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/generate-quiz"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8));
            if (timeoutMs > 0) rb = rb.timeout(Duration.ofMillis(timeoutMs));
            HttpRequest req = rb.build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            System.out.println("[AI-CLIENT] request url=" + (baseUrl + "/generate-quiz"));
            System.out.println("[AI-CLIENT] status=" + res.statusCode());
            String preview = res.body();
            System.out.println("[AI-CLIENT] body-preview=" + (preview != null ? preview.substring(0, Math.min(preview.length(), 500)) : "null"));
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
            System.out.println("[AI-CLIENT] exception=" + e.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_ERROR, "AI 처리 오류");
        }
    }

    public String summarize(String content) {
        if (baseUrl == null || apiKey == null) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR, "AI 설정이 없습니다");
        }
        try {
            String body = mapper.createObjectNode()
                    .put("content", content)
                    .toString();
            HttpRequest.Builder rb = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/summarize"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8));
            if (timeoutMs > 0) rb = rb.timeout(Duration.ofMillis(timeoutMs));
            HttpRequest req = rb.build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            System.out.println("[AI-CLIENT] request url=" + (baseUrl + "/summarize"));
            System.out.println("[AI-CLIENT] status=" + res.statusCode());
            String preview = res.body();
            System.out.println("[AI-CLIENT] body-preview=" + (preview != null ? preview.substring(0, Math.min(preview.length(), 500)) : "null"));
            if (res.statusCode() >= 300) {
                throw new CustomException(ErrorCode.INTERNAL_ERROR, "AI 호출 실패");
            }
            JsonNode root = mapper.readTree(res.body());
            JsonNode summaryNode = root.get("summary");
            if (summaryNode == null || !summaryNode.isTextual()) {
                throw new CustomException(ErrorCode.INTERNAL_ERROR, "AI 처리 오류");
            }
            return summaryNode.asText();
        } catch (Exception e) {
            System.out.println("[AI-CLIENT] exception=" + e.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_ERROR, "AI 처리 오류");
        }
    }
}
