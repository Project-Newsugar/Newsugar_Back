package newsugar.domain.news.service;

import lombok.RequiredArgsConstructor;
import newsugar.domain.news.dto.NewsDto;
import newsugar.domain.news.model.News;  // ★ 여기도 중요
import newsugar.domain.news.repository.NewsRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final RSSService rssService;
    private final NewsRepository newsRepository;

    public void refreshNews(String rssUrl) {

        List<NewsDto> dtoList = rssService.fetchRss(rssUrl);

        for (NewsDto dto : dtoList) {
            News news = News.builder()
                    .title(dto.title())
                    .content(dto.content())
                    .url(dto.url())
                    .category(dto.category())
                    .build();

            newsRepository.save(news);
        }
    }
}
