package myaong.popolog.memberservice.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myaong.popolog.memberservice.dto.response.TokenDTO;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
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
        // 요청 헤더에 있는 access라는 값을 가져오자 이게 accessToken이다.
        String accessToken = jwtUtil.getTokenFromHeader(request, AUTHORIZATION_HEADER);

        // 유효한 토큰(유효성 검사 통과, 만료되지 않은 토큰)이면 SecurityContext에 인증 정보 저장
        if (jwtUtil.validateToken(accessToken) && !jwtUtil.isExpired(accessToken)) {
            SecurityContextHolder.getContext().setAuthentication(jwtUtil.getAuthentication(accessToken));
        }

        // refreshToken이 유효하지 않거나 만료된 경우,
        // 또는 accessToken이 유효하지 않거나 만료된 경우에는 doFilter로 타고 들어가 JwtAccessDeined 핸들러에서 에러 메시지로 응답하도록 동작

        // accessToken 검증(유효하며 만료됐을 때 if문 안으로 들어감)
        if (jwtUtil.isExpired(accessToken) && jwtUtil.validateToken(accessToken)) {
            String refreshToken = jwtUtil.getTokenFromHeader(request, REFRESH_KEY_NAME);

            // refresh token이 유효하고, 만료되지 않았을 때 access, refresh 재발급
            if (jwtUtil.validateToken(refreshToken) && !jwtUtil.isExpired(refreshToken)) {
                // accessToken, refreshToken 재발급
                TokenDTO tokenDTO = jwtUtil.reissueAccessToken(refreshToken);
                SecurityContextHolder.getContext()
                        .setAuthentication(jwtUtil.getAuthentication(tokenDTO.getAccessToken()));

                jwtUtil.redirectReissueURI(request, response, tokenDTO);
            }
        }

        filterChain.doFilter(request, response);
    }
}
