package myaong.popolog.memberservice.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import myaong.popolog.memberservice.converter.AuthConverter;
import myaong.popolog.memberservice.dto.response.TokenDTO;
import myaong.popolog.memberservice.entity.RefreshToken;
import myaong.popolog.memberservice.oauth2.CustomOAuth2User;
import myaong.popolog.memberservice.oauth2.dto.OAuthUserDTO;
import myaong.popolog.memberservice.repository.RefreshTokenRedisRepository;
import myaong.popolog.memberservice.service.RedisService;
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

@Component
@Slf4j
public class JwtUtil {
    private static final long ACCESS_EXPIRATION_MS = 60 * 10 * 1 * 1000L;
    private static final long REFRESH_EXPIRATION_MS = 60 * 60 * 24 * 1 * 1000L;
    private static final String REISSUE_REDIRECT_URI = "/auth/reissue";

    private SecretKey secretKey;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final RedisService redisService;

    // application.yml에 있는 평문 secret key를 가져와 초기화하였다.
    // 여기서는 HS256으로 진행했다.
    public JwtUtil(@Value("${jwt.secret_key}") String secret,
                   RefreshTokenRedisRepository refreshTokenRedisRepository,
                   RedisService redisService) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
        this.refreshTokenRedisRepository = refreshTokenRedisRepository;
        this.redisService = redisService;
    }

    public String getTokenFromHeader(HttpServletRequest request, String headerName) {
        String token = request.getHeader(headerName);
        log.info("token from header: {}", token);
        if (token != null && !token.isEmpty()) {
            // Bearer 제거 <- oAuth2를 이용했다고 명시적으로 붙여주는 타입인데 JWT를 검증하거나 정보를 추출 시 제거해줘야한다.
            return token.substring(7);
        }
        return null; // 토큰이 없거나 비어있을 경우 null 반환
    }

    // accessToken인지 refreshToken인지 확인
    public String getTokenType(String token) {
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

    public Authentication getAuthentication(String accessToken) {
        // 사용자명과 권한을 accessToken에서 추출
        String providerId = getProviderId(accessToken);
        String permission = getPermission(accessToken);

        OAuthUserDTO oAuthUserDTO = AuthConverter.toOAuthUserDTO(null, null, providerId, permission, null, false);

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(oAuthUserDTO);

        Authentication authentication = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
        return authentication;
    }

    // token 유효확인
    // 현재 날짜와 만료 날짜를 비교하여,
    // 만료 날짜가 현재 날짜보다 이전(만료O)이면 true를 반환하고, 그렇지 않으면 false(만료X)를 반환
    public Boolean isExpired(String token) {
        Date expiration = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
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
            return true;
        }
    }

    public void redirectReissueURI(HttpServletRequest request, HttpServletResponse response, TokenDTO tokenDto)
            throws IOException {
        HttpSession session = request.getSession();
        session.setAttribute("access", tokenDto.getAccessToken());
        session.setAttribute("refresh", tokenDto.getRefreshToken());
        response.sendRedirect(REISSUE_REDIRECT_URI);
    }

    // access, refresh 토큰 동시에 재발급
    @Transactional
    public TokenDTO reissueAccessToken(String refreshToken) {
        RefreshToken findRefreshToken = refreshTokenRedisRepository.findByRefreshToken(refreshToken);

        String providerId = getProviderId(findRefreshToken.getRefreshToken());
        Long memberId = getMemberId(findRefreshToken.getRefreshToken());

        TokenDTO tokenDto = createAccessAndRefreshToken(memberId, providerId, findRefreshToken.getAuthority());
//        refreshTokenRedisRepository.save(RefreshToken.builder()
//                .id(findRefreshToken.getId())
//                .authorities(findRefreshToken.getAuthorities())
//                .refreshToken(tokenDto.getRefreshToken())
//                .build());

        // redis에 있는 refresh token 새로운 refresh token으로 대체
        // update refreshToken to Redis
        redisService.setValues(providerId, tokenDto.getRefreshToken(), Duration.ofMillis(REFRESH_EXPIRATION_MS));

        return tokenDto;
    }

    // access, refresh Token 생성
    public TokenDTO createAccessAndRefreshToken(Long memberId, String providerId, String permission) {
        String accessToken = createJwt("access", memberId, providerId, permission, ACCESS_EXPIRATION_MS);
        String refreshToken = createJwt("refresh", memberId, providerId, permission, REFRESH_EXPIRATION_MS);

        return TokenDTO.of(accessToken, refreshToken);
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
