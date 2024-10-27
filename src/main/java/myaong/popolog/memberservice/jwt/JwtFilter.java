package myaong.popolog.memberservice.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import myaong.popolog.memberservice.dto.response.TokenDTO;
import myaong.popolog.memberservice.util.AuthPathList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final String ACCESS_HEADER = "AccessToken";
    private static final String REFRESH_HEADER = "RefreshToken";

    private final String secretKey;
    private final TokenProvider tokenProvider;

    public JwtFilter(@Value("${jwt.secret_key}") String secretKey, TokenProvider tokenProvider) {
        this.secretKey = secretKey;
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        if (isRequestPassURI(request, response, filterChain)) { // 권한이 필요없는 요청은 필터를 건너뜀
//            return;
//        }

        String accessToken = getTokenFromHeader(request, ACCESS_HEADER);

        if (validateToken(accessToken) && validateExpire(accessToken)) {
            SecurityContextHolder.getContext().setAuthentication(tokenProvider.getAuthentication(accessToken));
        }

        //
        if (!validateExpire(accessToken) && validateToken(accessToken)) {
            String refreshToken = getTokenFromHeader(request, REFRESH_HEADER);

            //
            if (validateToken(refreshToken) && validateExpire(refreshToken)) {
                // accessToken, refreshToken 재발급
                TokenDTO tokenDTO = tokenProvider.reissueAccessToken(refreshToken);
                SecurityContextHolder.getContext()
                        .setAuthentication(tokenProvider.getAuthentication(tokenDTO.getAccessToken()));

                redirectReissueURI(request, response, tokenDTO);
            }
        }

        filterChain.doFilter(request, response);
    }

    // 토큰 유효성 검증이 필요하지 않은 요청 URI라면 필터를 통과
    private boolean isRequestPassURI(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        String requestURI = request.getRequestURI();
        boolean isPassURI = AuthPathList.getAuthWhitelist().stream()
                .anyMatch(authPath -> authPath.getPath().equals(requestURI));  // 인증을 건너뛰는 URI

        return isPassURI;
    }

    private String getTokenFromHeader(HttpServletRequest request, String headerName) {
        String token = request.getHeader(headerName);
        log.info("Token from header: {}", token);
        if (token != null && !token.isEmpty()) {
            return token;
        }
        return null; // 토큰이 없거나 비어있을 경우 null 반환
    }

    // token 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    // token이 만료되었는지 검사
    public boolean validateExpire(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token); // 주어진 token을 파싱하고, 해당 JWT의 서명이 유효한지를 검증
            return true;
        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    private static void redirectReissueURI(HttpServletRequest request, HttpServletResponse response, TokenDTO tokenDto)
            throws IOException {
        HttpSession session = request.getSession();
        session.setAttribute("accessToken", tokenDto.getAccessToken());
        session.setAttribute("refreshToken", tokenDto.getRefreshToken());
        response.sendRedirect("/auth/reissue");
    }
}
