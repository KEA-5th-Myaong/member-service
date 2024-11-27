package myaong.popolog.memberservice.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myaong.popolog.memberservice.entity.Member;
import myaong.popolog.memberservice.entity.RefreshToken;
import myaong.popolog.memberservice.enums.RequiredInfo;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static myaong.popolog.memberservice.common.Constants.*;


@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private static final long REFRESH_DURATION_MILLIS = 60 * 60 * 24 * 1000 * 1L;

	private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final RedisService redisService;
    private final MemberQueryService memberQueryService;

    @Value("${redirect-url.main}")
    private String mainPageUrl;

    @Value("${redirect-url.profile-form}")
    private String profileFormUrl;
    
    @Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
    	// SecurityContext에서 Authentication 객체 꺼내기
        CustomOAuth2User customUserDetail = (CustomOAuth2User) authentication.getPrincipal();
        
        // 토큰 생성시에 category, memberId, providerId 권한이 필요하니 준비
        Long memberId = customUserDetail.getMemberId();
        String providerId = customUserDetail.getProviderId();
        
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String permission = auth.getAuthority();
        
        // accessToken과 refreshToken 생성
        String accessToken = jwtUtil.createJwt(ACCESS_KEY_NAME, memberId, providerId, permission, 60*60*12*1000L); // 초 * 분 * 시 * msec
        // TODO: refresh 토큰에는 사용자 정보 안담아도 됨!
        String refreshToken = jwtUtil.createJwt(REFRESH_KEY_NAME, memberId, providerId, permission, 60*60*24*1*1000L);

        // redis에 insert (key = memberId / value = refreshToken)
        redisService.setValues(String.valueOf(memberId), refreshToken, Duration.ofMillis(REFRESH_DURATION_MILLIS));
//        saveRefreshTokenOnRedis(String.valueOf(memberId), refreshToken, permission);

        // 로그인 시도 횟수 초기화
        Member findMember = memberQueryService.findMemberByMemberId(memberId);
        findMember.initiateCountAttempt();

        log.info("accessToken: {}", accessToken);
        log.info("refreshToken: {}", refreshToken);

        // 신규 회원인지 아닌지에 따라 redirect할 url이 달라짐.
        String redirectionUrl = getRedirectionUrl(findMember.getRequiredInfo());

        // 리다이렉션 URL 생성
        String finalRedirectionUrl = UriComponentsBuilder.fromUriString(redirectionUrl)
                .queryParam(ACCESS_KEY_NAME, accessToken)
                .queryParam(REFRESH_KEY_NAME, refreshToken)
                .build().toUriString();

        response.setStatus(HttpStatus.OK.value());
        response.sendRedirect(finalRedirectionUrl);     // 로그인 성공시 프론트에 알려줄 redirect 경로
    }


    private void saveRefreshTokenOnRedis(String memberId, String refreshToken, String permission) {
        List<SimpleGrantedAuthority> simpleGrantedAuthorities = new ArrayList<>();
        simpleGrantedAuthorities.add(new SimpleGrantedAuthority(permission));

        refreshTokenRedisRepository.save(RefreshToken.builder()
                .id(memberId)
                .authorities(simpleGrantedAuthorities)
                .refreshToken(refreshToken)
                .build());

        redisService.setValues(memberId, refreshToken, Duration.ofDays(REFRESH_DURATION_MILLIS));
    }

    public String getRedirectionUrl(RequiredInfo requiredInfo) {
        return switch (requiredInfo) {
            case BOTH -> profileFormUrl; // 프로필과 관심직군 둘 다 입력 필요
            case PREJOBS -> mainPageUrl; // 관심직군 입력 필요
            case COMPLETED -> mainPageUrl; // 둘 다 입력 완료
            default -> mainPageUrl; // 기본 URL
        };
    }
    
}