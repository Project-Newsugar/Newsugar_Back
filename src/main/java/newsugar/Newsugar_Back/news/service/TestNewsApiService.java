package newsugar.domain.news.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TestNewsApiService {

    public String fetchExternalNews() {
        RestTemplate rest = new RestTemplate();
        String url = "https://newsapi.org/v2/top-headlines?country=us&apiKey=YOUR_KEY";
        return rest.getForObject(url, String.class);
    }
}
