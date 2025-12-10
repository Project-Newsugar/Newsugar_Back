package newsugar.Newsugar_Back;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import newsugar.Newsugar_Back.domain.user.service.UserService;
import newsugar.Newsugar_Back.domain.user.service.JwtService;
import newsugar.Newsugar_Back.domain.score.Service.ScoreService;
import newsugar.Newsugar_Back.domain.user.utils.JwtUtil;

@SpringBootTest
class NewsugarBackEndApplicationTests {

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private ScoreService scoreService;

    @MockBean
    private JwtUtil jwtUtil;

	@Test
	void contextLoads() {
	}
}
