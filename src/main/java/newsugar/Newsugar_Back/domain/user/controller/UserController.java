package newsugar.Newsugar_Back.domain.user.controller;

import lombok.RequiredArgsConstructor;
import newsugar.Newsugar_Back.common.ApiResult;
import newsugar.Newsugar_Back.domain.user.dto.UserSignupRequestDTO;
import newsugar.Newsugar_Back.domain.user.model.User;
import newsugar.Newsugar_Back.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private  final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResult<User>> signup (@RequestBody UserSignupRequestDTO request){
        User user=userService.signup(
                request.name(),
                request.email(),
                request.password(),
                request.nickname(),
                request.phone()
        );

        return ResponseEntity.ok(ApiResult.ok(user));
    }
}
