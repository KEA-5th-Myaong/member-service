package myaong.popolog.memberservice.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import myaong.popolog.memberservice.converter.AuthConverter;
import myaong.popolog.memberservice.dto.response.AuthResponse;
import myaong.popolog.memberservice.oauth2.CustomOAuth2User;
import myaong.popolog.memberservice.oauth2.dto.OAuthUserDTO;
import myaong.popolog.memberservice.repository.RefreshTokenRedisRepository;
import myaong.popolog.memberservice.service.RedisService;
import myaong.popolog.memberservice.util.CookieUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

import static myaong.popolog.memberservice.common.Constants.*;

@Component
@Slf4j
public class JwtUtil {
    private final CookieUtil cookieUtil;
    private SecretKey secretKey;
    private String reissueUrl;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final RedisService redisService;

    // application.yml에 있는 평문 secret key를 가져와 초기화하였다.
    // 여기서는 HS256으로 진행했다.
    public JwtUtil(@Value("${jwt.secret-key}") String secret,
                   @Value("${redirect-url.reissue}") String reissueUrl,
                   RefreshTokenRedisRepository refreshTokenRedisRepository,
                   RedisService redisService, CookieUtil cookieUtil) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
        this.reissueUrl = reissueUrl;
        this.refreshTokenRedisRepository = refreshTokenRedisRepository;
        this.redisService = redisService;
        this.cookieUtil = cookieUtil;
    }

    public String getTokenFromHeader(HttpServletRequest request, String name) {
        String token = request.getHeader(name);
        log.info("token from header: {}", token);

        // 토큰에 값이 존재하는 경우
        if (token != null && !token.isEmpty()) {
            return token.substring(7); // access token은 Bearer 제거
        }

        return null; // 토큰이 없거나 비어있을 경우 null 반환
    }

    public String getTokenFromCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null; // 쿠키가 없거나 값이 없을 경우 null 반환
    }

    // accessToken인지 refreshToken인지 확인
    public String getTokenType(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .get("tokenType", String.class);
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

    public Authentication getAuthentication(String accessToken) {
        // 사용자명과 권한을 accessToken에서 추출
        String providerId = getProviderId(accessToken);
        String permission = getPermission(accessToken);

        OAuthUserDTO oAuthUserDTO = AuthConverter.toOAuthUserDTO(null, null, providerId, permission);

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(oAuthUserDTO);

        Authentication authentication = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
        return authentication;
    }

    // token 유효확인
    // 현재 날짜와 만료 날짜를 비교하여,
    // 만료 날짜가 현재 날짜보다 이전(만료O)이면 true를 반환하고, 그렇지 않으면 false(만료X)를 반환
    public Boolean isExpired(String token) {
        try {
            Date expiration = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();

            expiration.before(new Date());
            return false;
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
            return false;
        } catch (IllegalArgumentException e) {
            log.info("JWT is null, 토큰이 존재하지 않습니다.");
            return false;
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token, 만료된 JWT token 입니다.");
            return true;
        }
    }

    // token 유효성 검사
    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
            return false;
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
            return false;
        } catch (IllegalArgumentException e) {
            log.info("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
            return false;
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token, 만료된 JWT token 입니다.");
            return true; // 만료된 토큰은 유효하다고 판단한 후 isExpired 메서드로 만료여부 재확인
        }
    }

    // access, refresh 토큰 동시에 재발급
    @Transactional
    public AuthResponse.TokenDTO reissueToken(String refreshToken) {
        String providerId = getProviderId(refreshToken);
        Long memberId = getMemberId(refreshToken);
        String permission = getPermission(refreshToken);

        // 기존의 refresh token 삭제
        redisService.deleteValues(String.valueOf(memberId));

        AuthResponse.TokenDTO tokenDto = createAccessAndRefreshToken(memberId, providerId, permission);

        // redis에 있는 refresh token 새로운 refresh token으로 대체
        // update refreshToken to Redis
        redisService.setValues(String.valueOf(memberId), tokenDto.getRefreshToken(), Duration.ofMillis(REFRESH_DURATION_MILLIS));

        return tokenDto;
    }

    public void redirectReissueURI(HttpServletResponse response, String refreshToken)
            throws IOException {
        Cookie cookie = cookieUtil.createCookie(REFRESH_KEY_NAME, refreshToken);

        response.addCookie(cookie);
        response.sendRedirect(reissueUrl);
    }

    // access, refresh Token 생성
    public AuthResponse.TokenDTO createAccessAndRefreshToken(Long memberId, String providerId, String permission) {
        String accessToken = createJwt(ACCESS_KEY_NAME, memberId, providerId, permission, ACCESS_DURATION_MILLIS);
        String refreshToken = createJwt(REFRESH_KEY_NAME, memberId, providerId, permission, REFRESH_DURATION_MILLIS);

        return AuthConverter.toTokenDTO(accessToken, refreshToken);
    }

    // JWT 발급
    public String createJwt(String tokenType, Long memberId, String providerId, String permission, Long expiredMs) {
        return Jwts.builder()
                .claim("tokenType", tokenType)
                .claim("memberId", memberId)
                .claim("providerId", providerId)
                .claim("permission", permission)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
