package myaong.popolog.memberservice.oauth2;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myaong.popolog.memberservice.jwt.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandlerV2 extends SimpleUrlAuthenticationSuccessHandler {
	private final JwtUtil jwtUtil;
//    private final RedisService redisService;
    
    @Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
    	CustomOAuth2User customUserDetail = (CustomOAuth2User) authentication.getPrincipal();
        
        // 토큰 생성시에 category, memberId, providerId 권한이 필요하니 준비하자
        Long memberId = customUserDetail.getMemberId();
        String providerId = customUserDetail.getProviderId();
        
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();
        
        // accessToken과 refreshToken 생성
        String accessToken = jwtUtil.createJwt("access", memberId, providerId, role, 60000L);
        String refreshToken = jwtUtil.createJwt("refresh", memberId, providerId, role, 86400000L);

        // redis에 insert (key = providerId / value = refreshToken)
//        redisService.setValues(providerId, refreshToken, Duration.ofMills(86400000L));
        
        // 응답
        response.setHeader("access", "Bearer " + accessToken);
        response.addCookie(createCookie("refresh", refreshToken));
        response.setStatus(HttpStatus.OK.value());

        log.info("accessToken: {}", accessToken);
        log.info("refreshToken: {}", refreshToken);

        response.sendRedirect("http://localhost:8083/");     // 로그인 성공시 프론트에 알려줄 redirect 경로
    }
    
    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);     // 쿠키가 살아있을 시간
        /*cookie.setSecure();*/         // https에서만 동작할것인지 (로컬은 http 환경이라 안먹음)
        /*cookie.setPath("/");*/        // 쿠키가 전역에서 동작
        cookie.setHttpOnly(true);       // http에서만 쿠키가 동작할 수 있도록 (js와 같은곳에서 가져갈 수 없도록)

        return cookie;
    }
    
}