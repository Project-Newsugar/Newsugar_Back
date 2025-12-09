package newsugar.Newsugar_Back.domain.user.utils;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import newsugar.Newsugar_Back.common.CustomException;
import newsugar.Newsugar_Back.common.ErrorCode;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Objects;

@Component
@Getter
public class JwtUtil {
    private final String JWT_SECRET;
    private final long JWT_EXPIRATION;
    private final Key key;

    public JwtUtil() {
        Dotenv dotenv = Dotenv.load();
        this.JWT_SECRET = dotenv.get("JWT_SECRET");
        this.JWT_EXPIRATION = Long.parseLong(Objects.requireNonNull(dotenv.get("JWT_EXPIRATION")));
        this.key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
    }

    public String generateToken (Long userId){
        Date now = new Date();
        Date expiry = new Date(now.getTime() + JWT_EXPIRATION);

        byte[] keyBytes = JWT_SECRET.getBytes(StandardCharsets.UTF_8);
        Key hmacKey = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(hmacKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Long validateToken(String token){
        return Long.parseLong(
                Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject()
        );
    }
}