package myaong.popolog.memberservice.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myaong.popolog.memberservice.entity.Member;
import myaong.popolog.memberservice.entity.RefreshToken;
import myaong.popolog.memberservice.jwt.JwtUtil;
import myaong.popolog.memberservice.repository.RefreshTokenRedisRepository;
import myaong.popolog.memberservice.service.MemberQueryService;
import myaong.popolog.memberservice.service.RedisService;
import myaong.popolog.memberservice.util.CookieUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private static final long REFRESH_DURATION_MILLIS = 60 * 60 * 24 * 1000 * 1L;
    private static final String REFRESH_KEY_NAME = "refresh";
    private static final String AUTH_TYPE = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";

	private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final RedisService redisService;
    private final MemberQueryService memberQueryService;
//    private final RedisService redisService;
    @Value("${redirect-url.new-user}")
    private String mainPageUrl;
    @Value("${redirect-url.main}")
    private String newUserFormUrl;
    
    @Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
    	CustomOAuth2User customUserDetail = (CustomOAuth2User) authentication.getPrincipal();
        
        // 토큰 생성시에 category, memberId, providerId 권한이 필요하니 준비하자
        Long memberId = customUserDetail.getMemberId();
        String providerId = customUserDetail.getProviderId();
        
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String permission = auth.getAuthority();
        
        // accessToken과 refreshToken 생성
        String accessToken = jwtUtil.createJwt("access", memberId, providerId, permission, 60*10*1000L);
        // TODO: refresh 토큰에는 사용자 정보 안담아도 됨!
        String refreshToken = jwtUtil.createJwt("refresh", memberId, providerId, permission, 60*60*24*1*1000L);

        // redis에 insert (key = providerId / value = refreshToken)
        redisService.setValues(providerId, refreshToken, Duration.ofMillis(REFRESH_DURATION_MILLIS));
//        saveRefreshTokenOnRedis(providerId, refreshToken, permission);

        // 로그인 시도 횟수 초기화
        Member findMember = memberQueryService.findMemberByMemberId(memberId);
        findMember.initiateCountAttempt();

        // 응답
        response.setHeader(AUTHORIZATION_HEADER, AUTH_TYPE + accessToken);
        response.addCookie(cookieUtil.createCookie(REFRESH_KEY_NAME, refreshToken));
        response.setStatus(HttpStatus.OK.value());

        log.info("accessToken: {}", accessToken);
        log.info("refreshToken: {}", refreshToken);

        String finalRedirectionUrl = customUserDetail.isNewUser() ? newUserFormUrl : mainPageUrl;

        response.sendRedirect(finalRedirectionUrl);     // 로그인 성공시 프론트에 알려줄 redirect 경로
    }


    private void saveRefreshTokenOnRedis(String providerId, String refreshToken, String permission) {
        List<SimpleGrantedAuthority> simpleGrantedAuthorities = new ArrayList<>();
        simpleGrantedAuthorities.add(new SimpleGrantedAuthority(permission));

        refreshTokenRedisRepository.save(RefreshToken.builder()
                .id(providerId)
                .authorities(simpleGrantedAuthorities)
                .refreshToken(refreshToken)
                .build());

//        redisService.setValues(providerId, refreshToken, Duration.ofDays(86400000L));
    }
    
}