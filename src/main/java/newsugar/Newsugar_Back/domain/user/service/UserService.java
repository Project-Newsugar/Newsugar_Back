package newsugar.Newsugar_Back.domain.user.service;

import lombok.AllArgsConstructor;
import newsugar.Newsugar_Back.common.CustomException;
import newsugar.Newsugar_Back.common.ErrorCode;
import newsugar.Newsugar_Back.domain.user.model.User;
import newsugar.Newsugar_Back.domain.user.repository.UserRepository;
import newsugar.Newsugar_Back.domain.user.utils.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public final String login(String email, String rawPassword){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.AUTH_ACCOUNT_NOT_FOUND));

        if(!passwordEncoder.matches(rawPassword, user.getPassword())){
            throw new CustomException(ErrorCode.AUTH_INVALID_CREDENTIALS);
        }

        return jwtUtil.generateToken(user.getId());
    }

    public User signup (String name, String email, String rawPassword, String nickname, String phone){
        if(userRepository.existsByEmail(email)){
            throw new CustomException(ErrorCode.CONFLICT, "이미 사용중인 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(rawPassword);

        User user = User.builder()
                .name(name)
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .phone(phone)
                .build();

        return userRepository.save(user);
    }
}
