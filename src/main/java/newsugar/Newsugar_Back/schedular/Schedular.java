package newsugar.Newsugar_Back.schedular;

import newsugar.Newsugar_Back.domain.summary.service.CategorySummaryService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Schedular {

    private final CategorySummaryService categorySummaryService;
    private final String[] categories = {"politics", "economy","society", "culture", "tech", "entertainment", "opinion"};

    public Schedular(CategorySummaryService categorySummaryService) {
        this.categorySummaryService = categorySummaryService;
    }

    // 6시간마다 실행 (cron: 초 분 시 일 월 요일)
    @Scheduled(cron = "0 0 0,6,12,18 * * *")
    public void generateAllCategorySummaries() {
        System.out.println("Generating Category Summaries...");
        for (String category : categories) {
            String summary = categorySummaryService.generateCategorySummary(category);
            System.out.println("Category: " + category + ", Summary: " + summary);
        }
    }
}
