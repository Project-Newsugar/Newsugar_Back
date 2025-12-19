package newsugar.Newsugar_Back.schedular;

import newsugar.Newsugar_Back.domain.summary.repository.CategorySummaryRedis;
import newsugar.Newsugar_Back.domain.summary.service.CategorySummaryService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Schedular {

    private final CategorySummaryService categorySummaryService;
    private final CategorySummaryRedis categorySummaryRedis;
    private final DailyTaskService dailyTaskService;

    private final String[] categories = {"politics", "economy","society", "culture", "tech", "entertainment", "opinion"};

    public Schedular(CategorySummaryService categorySummaryService,
                     CategorySummaryRedis categorySummaryRedis,
                     DailyTaskService dailyTaskService) {
        this.categorySummaryService = categorySummaryService;
        this.categorySummaryRedis = categorySummaryRedis;
        this.dailyTaskService = dailyTaskService;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void generateAllCategorySummaries() {
        for (String category : categories) {
            String summary = categorySummaryService.generateCategorySummary(category);
            categorySummaryService.saveInRedis(category, summary);
            System.out.println("Category: " + category + ", Summary: " + summary);
        }
    }

    @Scheduled(cron = "0 0 0,6,12,18 * * *")
    public void generateTodayMainContent() {
        dailyTaskService.executeDailyRoutine();
    }
}
