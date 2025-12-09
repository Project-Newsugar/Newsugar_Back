package newsugar.Newsugar_Back;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import newsugar.Newsugar_Back.domain.quiz.repository.QuizRepository;
import newsugar.Newsugar_Back.domain.quiz.repository.QuizSubmissionRepository;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
})
class NewsugarBackEndApplicationTests {

    @MockBean
    private QuizRepository quizRepository;

    @MockBean
    private QuizSubmissionRepository quizSubmissionRepository;

    @Test
    void contextLoads() {
    }
}
