package newsugar.Newsugar_Back;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

<<<<<<< HEAD
@SpringBootTest
class NewsugarBackEndApplicationTests {

=======
@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
})

class NewsugarBackEndApplicationTests {
>>>>>>> origin/main
	@Test
	void contextLoads() {
	}

}
