package newsugar.Newsugar_Back.domain.user.controller;

import lombok.RequiredArgsConstructor;
import newsugar.Newsugar_Back.common.ApiResult;
import newsugar.Newsugar_Back.domain.user.dto.Request.UserLoginRequestDTO;
import newsugar.Newsugar_Back.domain.user.dto.Request.UserModifyRequestDTO;
import newsugar.Newsugar_Back.domain.user.dto.Response.UserInfoResponseDTO;
import newsugar.Newsugar_Back.domain.user.dto.Response.UserLoginResponseDTO;
import newsugar.Newsugar_Back.domain.user.dto.Request.UserSignupRequestDTO;
import newsugar.Newsugar_Back.domain.user.model.Score;
import newsugar.Newsugar_Back.domain.user.model.User;
import newsugar.Newsugar_Back.domain.user.service.JwtService;
import newsugar.Newsugar_Back.domain.user.service.ScoreService;
import newsugar.Newsugar_Back.domain.user.service.UserService;
import newsugar.Newsugar_Back.domain.user.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final ScoreService scoreService;
    private final JwtUtil jwtUtil;


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

    @PostMapping("/login")
    public ResponseEntity<ApiResult<UserLoginResponseDTO>> login (@RequestBody UserLoginRequestDTO request){
        UserLoginResponseDTO user = userService.login(
                request.email(),
                request.password()
        );

        return ResponseEntity.ok(ApiResult.ok(user));
    }

    @PatchMapping("/modify")
    public ResponseEntity<ApiResult<User>> modify (
            @RequestHeader("Authorization") String token,
            @RequestBody UserModifyRequestDTO request
            ){
        String actualToken = token != null ? token.replace("Bearer ", "") : null;

        Long userId = jwtService.getUserIdFromToken(actualToken);

        User updatedUser = userService.modify(
                userId,
                request.name(),
                request.password(),
                request.nickname(),
                request.phone()
        );



        return ResponseEntity.ok(ApiResult.ok(updatedUser));
    }

    @GetMapping("/getInfo")
    public ResponseEntity<ApiResult<UserInfoResponseDTO>> getInfo (
            @RequestHeader("Authorization") String token
    ){
        String actualToken = token != null ? token.replace("Bearer ", "") : null;

        Long userId = jwtService.getUserIdFromToken(actualToken);
        User user = userService.getInfo(userId);
        Integer score = scoreService.getScore(userId);

        UserInfoResponseDTO response = new UserInfoResponseDTO(user,score);

        return  ResponseEntity.ok(ApiResult.ok(response));
    }
}
