package newsugar.domain.news.service;

import newsugar.domain.news.dto.NewsDto;
import newsugar.domain.news.model.NewsCategory;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.rometools.rome.feed.synd.SyndEntry;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class RSSServiceImpl implements RSSService {

    @Override
    public List<NewsDto> fetchRss(String rssUrl) {
        List<NewsDto> list = new ArrayList<>();

        try {
            URL feedSource = new URL(rssUrl);
            SyndFeedInput input = new SyndFeedInput();
            var feed = input.build(new XmlReader(feedSource));

            for (SyndEntry entry : feed.getEntries()) {
                list.add(new NewsDto(
                        entry.getTitle(),
                        entry.getDescription() != null ? entry.getDescription().getValue() : "",
                        entry.getLink(),
                        NewsCategory.GENERAL // 기본값 (원하면 카테고리 지정)
                ));
            }

        } catch (Exception e) {
            throw new RuntimeException("RSS parsing failed: " + e.getMessage());
        }

        return list;
    }
}
