package myaong.popolog.memberservice.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myaong.popolog.memberservice.common.exception.ApiCode;
import myaong.popolog.memberservice.common.exception.ApiResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static myaong.popolog.memberservice.common.Constants.*;

// JwtFilter는 스프링 빈으로 관리하지 않을 것이므로 @Component 사용 X
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final RequestMatcherHolder requestMatcherHolder;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        log.info("Request URI = {}", request.getRequestURI());
        return requestMatcherHolder.getRequestMatchersByMinPermission(null).matches(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = jwtUtil.getTokenFromHeader(request, AUTHORIZATION_HEADER);
        String refreshToken = jwtUtil.getTokenFromCookie(request, REFRESH_KEY_NAME);

        // 유효한 토큰(유효성 검사 통과, 만료되지 않은 토큰)이면 SecurityContext에 인증 정보 저장
        if (jwtUtil.validateToken(accessToken) && !jwtUtil.isExpired(accessToken)) {
            SecurityContextHolder.getContext().setAuthentication(jwtUtil.getAuthentication(accessToken));
        }

        // 만료됐으면 재발급
        if (jwtUtil.isExpired(accessToken) && jwtUtil.validateToken(accessToken)) {
            if (jwtUtil.validateToken(refreshToken) && !jwtUtil.isExpired(refreshToken)) {
                jwtUtil.redirectReissueURI(response, refreshToken);
            }
        }

        if (!jwtUtil.validateToken(accessToken) || !jwtUtil.validateToken(refreshToken)) {
            ApiResponse.responseErrorOnFilter(response, HttpServletResponse.SC_UNAUTHORIZED, ApiCode.INVALID_TOKEN);
        }

        filterChain.doFilter(request, response);
    }
}