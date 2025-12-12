package newsugar.Newsugar_Back.domain.news.service;

import io.github.cdimascio.dotenv.Dotenv;
import newsugar.Newsugar_Back.domain.news.dto.DeepSearchResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class NewsApiService {

    private final RestTemplate restTemplate;
    private final String apiKey;

    public NewsApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

        Dotenv dotenv = Dotenv.load();
        this.apiKey = dotenv.get("NEWS_API_KEY");
    }

    public DeepSearchResponseDTO getNewsByCategory(String category, Integer page, Integer page_size) {

        // 기본값 설정
        int currentPage = (page != null) ? page : 1;
        int currentPageSize = (page_size != null) ? page_size : 10;

        // URL 생성
        String url = UriComponentsBuilder
                .fromHttpUrl("https://api-v2.deepsearch.com/v1/articles/" + URLEncoder.encode(category, StandardCharsets.UTF_8))
                .queryParam("api_key", apiKey)
                .queryParam("page", currentPage)
                .queryParam("page_size", currentPageSize)
                .toUriString();

        // API 호출
        return restTemplate.getForObject(url, DeepSearchResponseDTO.class);
    }
}