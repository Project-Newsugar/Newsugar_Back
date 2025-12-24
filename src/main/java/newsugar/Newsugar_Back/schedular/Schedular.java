package newsugar.Newsugar_Back.schedular;

import newsugar.Newsugar_Back.domain.summary.repository.CategorySummaryRedis;
import newsugar.Newsugar_Back.domain.summary.service.CategorySummaryService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Schedular {

    private final CategorySummaryService categorySummaryService;
    private final CategorySummaryRedis categorySummaryRedis;
    private final DailyTaskService dailyTaskService;

    private final String[] categories = {"politics", "economy","society", "culture","world", "tech", "entertainment", "opinion"};

    public Schedular(CategorySummaryService categorySummaryService,
                     CategorySummaryRedis categorySummaryRedis,
                     DailyTaskService dailyTaskService) {
        this.categorySummaryService = categorySummaryService;
        this.categorySummaryRedis = categorySummaryRedis;
        this.dailyTaskService = dailyTaskService;
    }


    // 로컬에서만 실행
    @Scheduled(cron = "0 0 * * * *")
    public void runDailyTask() {
        for (String category : categories) {
            try {
                String summary = categorySummaryService.generateCategorySummary(category);
                categorySummaryService.saveInRedis(category, summary);
                System.out.println("Category: " + category + ", Summary: " + summary);
                
                // API 쿼터 제한을 피하기 위해 카테고리 처리 사이에 10초 대기
                // 하나 성공하면 좀 쉬었다가 다음꺼 진행
                Thread.sleep(10000); 
            } catch (Exception e) {
                System.err.println("카테고리 요약 생성 중 오류 발생 (" + category + "): " + e.getMessage());
                // 오류가 나도 다음 카테고리는 계속 진행 시도
            }
        }
    }

    @Scheduled(cron = "0 0 0,6,12,18 * * *")
    public void generateTodayMainContent() {
        dailyTaskService.executeDailyRoutine();
    }
}
