package newsugar.Newsugar_Back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class NewsugarBackEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewsugarBackEndApplication.class, args);
    }

}
