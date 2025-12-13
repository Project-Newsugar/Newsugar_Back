package newsugar.Newsugar_Back.domain.news.service;

import io.github.cdimascio.dotenv.Dotenv;
import newsugar.Newsugar_Back.domain.news.dto.DeepSearchResponseDTO;
import newsugar.Newsugar_Back.domain.news.dto.NaverNewsResponseDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class NewsApiService {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String clientId;
    private final String clientSecret;
    private final String newsUrl;

    public NewsApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

        Dotenv dotenv = Dotenv.load();
        this.apiKey = dotenv.get("NEWS_API_KEY");
        this.clientId = dotenv.get("NAVER_CLIENT_ID");
        this.clientSecret = dotenv.get("NAVER_CLIENT_SECRET");
        this.newsUrl = dotenv.get("NAVER_NEWS_URL");
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

    public List<NaverNewsResponseDTO.Item> getNewsByKeyword(String keyword) {

        String url = UriComponentsBuilder
                .fromHttpUrl(newsUrl)
                .queryParam("query", keyword)
                .queryParam("display", 20)
                .queryParam("start", 1)
                .queryParam("sort", "sim")
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<NaverNewsResponseDTO> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        NaverNewsResponseDTO.class
                );

        // items만 추출해서 반환
        return response.getBody().items();
    }
}