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
import org.springframework.security.authentication.InternalAuthenticationServiceException;
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

import static myaong.popolog.memberservice.common.Constants.*;


@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final RedisService redisService;
    private final MemberQueryService memberQueryService;

    @Value("${redirect-url.main}")
    private String mainPageUrl;

    @Value("${redirect-url.profile-form}")
    private String profileFormUrl;

    @Value("${redirect-url.login}")
    private String loginPageUrl;

    
    @Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
    	// SecurityContext에서 Authentication 객체 꺼내기
        CustomOAuth2User customUserDetail = (CustomOAuth2User) authentication.getPrincipal();

        if (customUserDetail.getDuplicateEmail()) {
            log.error("Email Duplicate!!");
            throw new InternalAuthenticationServiceException("duplicate email");
        }

        Long memberId = customUserDetail.getMemberId();
        String providerId = customUserDetail.getProviderId();
        
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String permission = auth.getAuthority();
        
        String accessToken = jwtUtil.createJwt(ACCESS_KEY_NAME, memberId, providerId, permission, ACCESS_DURATION_MILLIS);
        String refreshToken = jwtUtil.createJwt(REFRESH_KEY_NAME, memberId, providerId, permission, REFRESH_DURATION_MILLIS);

        redisService.setValues(String.valueOf(memberId), refreshToken, Duration.ofMillis(REFRESH_DURATION_MILLIS));
//        saveRefreshTokenOnRedis(String.valueOf(memberId), refreshToken, permission);

        // 로그인 시도 횟수 초기화
        Member findMember = memberQueryService.findMemberByMemberId(memberId);
        findMember.initiateCountAttempt();

        log.info("accessToken: {}", accessToken);
        log.info("refreshToken: {}", refreshToken);

        String redirectionUrl = getRedirectionUrl(findMember.getRequiredInfo());

        response.addCookie(cookieUtil.createCookie(ACCESS_KEY_NAME, accessToken, false)); // 소셜 로그인에서는 access를 쿠키에 담아줘야하므로 프론트에서 꺼내서 쓸 수 있도록 httpOnly False로 설정
        response.addCookie(cookieUtil.createCookie(REFRESH_KEY_NAME, refreshToken, true));

        response.sendRedirect(redirectionUrl);
    }

    public String getRedirectionUrl(RequiredInfo requiredInfo) {
        return switch (requiredInfo) {
            case PROFILE -> profileFormUrl; // 프로필과 관심직군 둘 다 입력 필요
            case COMPLETED -> mainPageUrl; // 둘 다 입력 완료
            default -> mainPageUrl; // 기본 URL
        };
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
}