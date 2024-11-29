package myaong.popolog.memberservice.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myaong.popolog.memberservice.common.exception.ApiCode;
import myaong.popolog.memberservice.common.exception.ApiResponse;
import myaong.popolog.memberservice.converter.AuthConverter;
import myaong.popolog.memberservice.dto.response.AuthResponse;
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
	private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final RedisService redisService;
    private final MemberQueryService memberQueryService;

    
    @Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
    	// SecurityContext에서 Authentication 객체 꺼내기
        CustomOAuth2User customUserDetail = (CustomOAuth2User) authentication.getPrincipal();
        
        Long memberId = customUserDetail.getMemberId();
        String providerId = customUserDetail.getProviderId();
        
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String permission = auth.getAuthority();
        
        String accessToken = jwtUtil.createJwt(ACCESS_KEY_NAME, memberId, providerId, permission, ACCESS_DURATION_MILLIS); // 초 * 분 * 시 * msec
        String refreshToken = jwtUtil.createJwt(REFRESH_KEY_NAME, memberId, providerId, permission, REFRESH_DURATION_MILLIS);

        redisService.setValues(String.valueOf(memberId), refreshToken, Duration.ofMillis(REFRESH_DURATION_MILLIS));
//        saveRefreshTokenOnRedis(String.valueOf(memberId), refreshToken, permission);

        // 로그인 시도 횟수 초기화
        Member findMember = memberQueryService.findMemberByMemberId(memberId);
        findMember.initiateCountAttempt();

        log.info("accessToken: {}", accessToken);
        log.info("refreshToken: {}", refreshToken);

        AuthResponse.LoginDTO loginDTO = AuthConverter.toLoginDTO(AUTH_TYPE + accessToken, findMember.getRequiredInfo());
        response.addCookie(cookieUtil.createCookie(REFRESH_KEY_NAME, refreshToken));

        ApiResponse.responseSuccessOnFilter(response, ApiCode.OK.getCode(), ApiCode.OK.getMessage(), loginDTO);
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