package myaong.popolog.memberservice.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private SecretKey secretKey;

    // application.yml에 있는 평문 secret key를 가져와 초기화하였다.
    // 여기서는 HS256으로 진행했다.
    public JwtUtil(@Value("${jwt.secret_key}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
    }

    // accessToken인지 refreshToken인지 확인
    public String getCategory(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .get("category", String.class);
    }

    // memberId 추출
    public Long getMemberId(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .get("memberId", Long.class);
    }

    // providerId 추출
    public String getProviderId(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .get("providerId", String.class);
    }

    // 권한 추출
    public String getPermission(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .get("permission", String.class);
    }

    // token 유효확인
    public Boolean isExpired(String token) {
        Date expiration = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }

    // JWT 발급
    public String createJwt(String category, Long memberId, String providerId, String permission, Long expiredMs) {
        return Jwts.builder()
                .claim("category", category)
                .claim("memberId", memberId)
                .claim("providerId", providerId)
                .claim("permission", permission)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
