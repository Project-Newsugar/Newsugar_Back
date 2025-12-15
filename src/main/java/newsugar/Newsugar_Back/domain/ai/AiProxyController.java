package newsugar.Newsugar_Back.domain.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai-proxy")
public class AiProxyController {
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newHttpClient();
    private final String apiKey;

    public AiProxyController() {
        Dotenv env = Dotenv.load();
        this.apiKey = env.get("QUIZ_AI_API_KEY");
    }

    public static class SummaryReq {
        public String summary;
    }

    @PostMapping("/generate-quiz")
    public ResponseEntity<String> generate(@RequestHeader(value = "Authorization", required = false) String auth,
                                           @RequestBody SummaryReq req) {
        if (apiKey == null || apiKey.isBlank()) {
            return ResponseEntity.status(500).body(jsonError("AI 설정이 없습니다"));
        }
        if (auth == null || !auth.startsWith("Bearer ") || !apiKey.equals(auth.substring(7))) {
            return ResponseEntity.status(401).body(jsonError("unauthorized"));
        }
        try {
            String prompt = "다음 요약문을 기반으로 뉴스기사 관련련 4지선다 객관식 문제를 2개 생성하고 JSON으로만 반환하세요. " +
                    "출력형식은 {\"questions\":[{\"text\":문장,\"options\":[옵션4],\"correctIndex\":정답인덱스,\"explanation\":해설}...]} 로 정확히 맞추세요. 요약문:" +
                    (req != null ? req.summary : "");

            Map<String, Object> content = new HashMap<>();
            Map<String, Object> textPart = new HashMap<>();
            textPart.put("text", prompt);
            Map<String, Object> parts = new HashMap<>();
            parts.put("parts", new Object[]{textPart});
            Map<String, Object> contents = new HashMap<>();
            contents.put("contents", new Object[]{parts});
            String body = mapper.writeValueAsString(contents);

            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;
            HttpRequest httpReq = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> res = client.send(httpReq, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (res.statusCode() >= 300) {
                return ResponseEntity.status(500).body(jsonError("AI 호출 실패"));
            }
            JsonNode root = mapper.readTree(res.body());
            JsonNode candidates = root.path("candidates");
            if (!candidates.isArray() || candidates.size() == 0) {
                return ResponseEntity.status(500).body(jsonError("AI 응답 없음"));
            }
            JsonNode partsNode = candidates.get(0).path("content").path("parts");
            String text = null;
            if (partsNode.isArray() && partsNode.size() > 0) {
                JsonNode t = partsNode.get(0).path("text");
                if (t.isTextual()) text = t.asText();
            }
            if (text == null) {
                return ResponseEntity.status(500).body(jsonError("AI 텍스트 없음"));
            }
            String trimmed = text.trim();
            if (!trimmed.startsWith("{")) {
                return ResponseEntity.status(500).body(jsonError("JSON 형식 아님"));
            }
            JsonNode json = mapper.readTree(trimmed);
            if (!json.has("questions") || !json.get("questions").isArray()) {
                return ResponseEntity.status(500).body(jsonError("필드 누락"));
            }
            return ResponseEntity.ok(mapper.writeValueAsString(json));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(jsonError("AI 처리 오류"));
        }
    }

    private String jsonError(String msg) {
        try {
            return mapper.writeValueAsString(Map.of("error", msg));
        } catch (Exception e) {
            return "{\"error\":\"" + msg + "\"}";
        }
    }
}

