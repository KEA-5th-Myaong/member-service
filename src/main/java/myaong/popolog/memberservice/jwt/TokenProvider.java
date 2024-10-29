package myaong.popolog.memberservice.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import myaong.popolog.memberservice.dto.response.TokenDTO;
import myaong.popolog.memberservice.entity.RefreshToken;
import myaong.popolog.memberservice.oauth2.KakaoMemberDetails;
import myaong.popolog.memberservice.repository.RefreshTokenRedisRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TokenProvider {
    private static final String AUTH_KEY = "permission";
    private static final String AUTH_PROVIDER_ID = "providerId";
    private static final String AUTH_PK = "memberId";

    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final JwtUtil jwtUtil;
    private final String secretKey;
    private final long accessTokenValidityMilliSeconds;
    private final long refreshTokenValidityMilliSeconds;
 
    private Key secretkey;
 
    public TokenProvider(RefreshTokenRedisRepository refreshTokenRedisRepository,
                         JwtUtil jwtUtil,
                         @Value("${jwt.secret_key}") String secretKey,
                         @Value("${jwt.access-token-validity-in-seconds}") long accessTokenValiditySeconds,
                         @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValiditySeconds) {
        this.refreshTokenRedisRepository = refreshTokenRedisRepository;
        this.jwtUtil = jwtUtil;
        this.secretKey = secretKey;
        this.accessTokenValidityMilliSeconds = accessTokenValiditySeconds * 1000;
        this.refreshTokenValidityMilliSeconds = refreshTokenValiditySeconds * 1000;
    }
 
    @PostConstruct
    public void initKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.secretkey = Keys.hmacShaKeyFor(keyBytes);
    }

    // access, refresh Token 생성
    public TokenDTO createToken(Long memberId, String providerId, String role) {
        long now = (new Date()).getTime();

        Date accessValidity = new Date(now + this.accessTokenValidityMilliSeconds);
        Date refreshValidity = new Date(now + this.refreshTokenValidityMilliSeconds);

        String accessToken = getJwtToken(memberId, providerId, role, accessValidity);

        String refreshToken = getJwtToken(memberId, providerId, role, refreshValidity);

        return TokenDTO.of(accessToken, refreshToken);
    }

    private String getJwtToken(Long memberId, String providerId, String role, Date accessValidity) {
        String accessToken = Jwts.builder()
                .addClaims(Map.of(AUTH_PK, memberId))
                .addClaims(Map.of(AUTH_PROVIDER_ID, providerId))
                .addClaims(Map.of(AUTH_KEY, role))
                .signWith(secretkey, SignatureAlgorithm.HS256)
                .setExpiration(accessValidity)
                .compact();
        return accessToken;
    }

    @Transactional
    public TokenDTO reissueAccessToken(String refreshToken) {
        RefreshToken findToken = refreshTokenRedisRepository.findByRefreshToken(refreshToken);

        String providerId = jwtUtil.getProviderId(findToken.getRefreshToken());
        Long memberId = jwtUtil.getMemberId(findToken.getRefreshToken());

        TokenDTO tokenDto = createToken(memberId, providerId, findToken.getAuthority());
        refreshTokenRedisRepository.save(RefreshToken.builder()
                .id(findToken.getId())
                .authorities(findToken.getAuthorities())
                .refreshToken(tokenDto.getRefreshToken())
                .build());

        return tokenDto;
    }

    // token으로부터 Authentication 객체를 만들어 리턴하는 메소드
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        List<String> authorities = Arrays.asList(claims.get(AUTH_KEY)
                .toString()
                .split(","));

        List<? extends GrantedAuthority> simpleGrantedAuthorities = authorities.stream()
                .map(auth -> new SimpleGrantedAuthority(auth))
                .collect(Collectors.toList());

        KakaoMemberDetails principal = new KakaoMemberDetails(
                (String) claims.get(AUTH_PROVIDER_ID), // TODO: EMAIL이 없는 경우도 있을 수 있으니 AUTH_ID로 바꾸는거 고민
                simpleGrantedAuthorities, Map.of());

//        CustomOAuth2User principal2 = new CustomOAuth2User()

        return new UsernamePasswordAuthenticationToken(principal, token, simpleGrantedAuthorities);
    }
}