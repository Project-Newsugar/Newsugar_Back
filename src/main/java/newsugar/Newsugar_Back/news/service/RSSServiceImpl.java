package newsugar.domain.news.service;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.RequiredArgsConstructor;
import newsugar.domain.news.dto.NewsDto;
import newsugar.domain.news.model.NewsCategory;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RSSServiceImpl {

    // RFC1123 포맷 파싱, KST로 변환 후 LocalDateTime 반환
    public LocalDateTime parseDate(String pubDate) {
        if (pubDate == null || pubDate.isBlank()) return LocalDateTime.now();
        try {
            ZonedDateTime zdt = ZonedDateTime.parse(pubDate, DateTimeFormatter.RFC_1123_DATE_TIME);
            return zdt.withZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        } catch (Exception e) {
            // 어떤 RSS는 다른 형식이므로 fallback 시도
            try {
                Instant instant = Instant.parse(pubDate);
                return LocalDateTime.ofInstant(instant, ZoneId.of("Asia/Seoul"));
            } catch (Exception ex) {
                return LocalDateTime.now();
            }
        }
    }

    // RSS 읽어서 NewsDto 리스트 반환 (예시, rometools 사용)
    public List<NewsDto> fetchRss(String rssUrl, NewsCategory category) {
        List<NewsDto> result = new ArrayList<>();
        try (XmlReader reader = new XmlReader(new URL(rssUrl))) {
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(reader);

            for (SyndEntry entry : feed.getEntries()) {
                String title = entry.getTitle();
                String desc = entry.getDescription() != null ? entry.getDescription().getValue() : "";
                String link = entry.getLink();
                // publishedDate는 Date 타입일 수 있음
                String pubDateStr = (entry.getPublishedDate() != null) ?
                        DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneOffset.UTC)
                                .format(entry.getPublishedDate().toInstant().atZone(ZoneOffset.UTC))
                        : null;
                LocalDateTime publishedAt = entry.getPublishedDate() != null ?
                        LocalDateTime.ofInstant(entry.getPublishedDate().toInstant(), ZoneId.of("Asia/Seoul"))
                        : LocalDateTime.now();

                // 또는 parseDate(pubDateStr)

                result.add(new NewsDto(title, desc, link, category, publishedAt));
            }
        } catch (Exception e) {
            // 로깅
            e.printStackTrace();
        }
        return result;
    }
}
