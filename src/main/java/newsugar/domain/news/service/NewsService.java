package newsugar.domain.news.service;

import lombok.RequiredArgsConstructor;
import newsugar.domain.news.dto.NewsResponse;
import newsugar.domain.news.model.NewsCategory;
import newsugar.domain.news.repository.NewsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;

    public List<NewsResponse> getLatestNews() {
        return newsRepository.findTop20ByOrderByPublishedAtDesc()
                .stream()
                .map(NewsResponse::from)
                .collect(Collectors.toList());
    }

    public List<NewsResponse> getNewsByCategory(NewsCategory category) {
        return newsRepository.findByCategoryOrderByPublishedAtDesc(category)
                .stream()
                .map(NewsResponse::from)
                .collect(Collectors.toList());
    }
}