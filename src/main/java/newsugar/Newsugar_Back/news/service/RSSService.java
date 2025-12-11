package newsugar.domain.news.service;

import java.util.List;
import newsugar.domain.news.dto.NewsDto;

public interface RSSService {
    List<NewsDto> fetchRss(String rssUrl);
}