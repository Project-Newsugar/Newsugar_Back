package newsugar.domain.news.model;

import lombok.Getter;

@Getter
public enum NewsCategory {

    POLITICS("politics", "정치"),
    ECONOMY("economy", "경제"),
    SOCIAL("society", "사회"),
    CULTURE("culture", "문화"),
    WORLD("world", "세계"),
    IT("it", "IT/과학"),
    SPORTS("sports", "스포츠"),
    ENTERTAINMENT("entertainment", "연예");

    private final String rssPath;
    private final String displayName;

    NewsCategory(String rssPath, String displayName) {
        this.rssPath = rssPath;
        this.displayName = displayName;
    }
}
