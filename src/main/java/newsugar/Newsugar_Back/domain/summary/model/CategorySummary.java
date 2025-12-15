package newsugar.Newsugar_Back.domain.summary.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash(value = "CategorySummary", timeToLive = 21600)
public class CategorySummary implements Serializable {

    @Id
    private String category; // Redis key 역할
    private String summary;

    public CategorySummary() {}

    public CategorySummary(String category, String summary) {
        this.category = category;
        this.summary = summary;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}