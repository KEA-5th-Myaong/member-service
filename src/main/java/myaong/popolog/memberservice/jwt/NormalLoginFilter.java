package myaong.popolog.memberservice.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myaong.popolog.memberservice.entity.Member;
import myaong.popolog.memberservice.service.RedisService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static myaong.popolog.memberservice.common.Constants.*;

// NormalLoginFilter는 스프링 빈으로 관리하지 않을 것이므로 @Component 사용 X
@Slf4j
public class NormalLoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RedisService redisService;

    @Value("${redirect-url.main}")
    private String mainPageUrl;

    public NormalLoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, RedisService redisService, String mainPageUrl) {
//        super(); // UsernamePasswordAuthenticationFilter의 생성자 호출
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.redisService = redisService;
        this.mainPageUrl = mainPageUrl;

        setFilterProcessesUrl("/auth/login"); // 일반 로그인 경로 /auth/login으로 변경
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 클라이언트 요청에서 username, password 추출
        //클라이언트 요청에서 username, password 추출
        String username = "";
        String password = "";

        try {
            BufferedReader reader = request.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> jsonRequest = mapper.readValue(sb.toString(), HashMap.class);
            username = jsonRequest.get("username");
            password = jsonRequest.get("password");

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 스프링 시큐리티에서 username과 password를 검증하기 위해서는 token에 담아야 함.
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

        // token에 담은 검증을 위한 AuthenticationManager로 전달 -> AuthenticationManager는 AuthenticationProvider에게 인증을 위임
        // DaoAuthenticationProvider -> UserDetailsService를 사용하여 사용자를 로드하고 인증
        // CustomUserDetailService가 UserDetatilsService를 구현하여 loadUserByUsername을 로드
        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        Member member = customUserDetails.getMember();
        Long memberId = customUserDetails.getMemberId();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String permission = auth.getAuthority();

        // accessToken과 refreshToken 생성
        String accessToken = jwtUtil.createJwt(ACCESS_KEY_NAME, memberId, null, permission, 60*60*12*1000L); // 초 * 분 * 시 * msec
        String refreshToken = jwtUtil.createJwt(REFRESH_KEY_NAME, memberId, null, permission, 60*60*24*1*1000L);

        // redis에 insert (key = memberId / value = refreshToken)
        redisService.setValues(String.valueOf(memberId), refreshToken, Duration.ofMillis(REFRESH_DURATION_MILLIS));

        // 로그인 시도 횟수 초기화
        member.initiateCountAttempt();

        log.info("accessToken: {}", accessToken);
        log.info("refreshToken: {}", refreshToken);

        String finalRedirectionUrl = UriComponentsBuilder.fromUriString(mainPageUrl)
                .queryParam(ACCESS_KEY_NAME, accessToken)
                .queryParam(REFRESH_KEY_NAME, refreshToken)
                .build().toUriString();

        response.setStatus(HttpStatus.OK.value());
        response.sendRedirect(finalRedirectionUrl);
    }

    // 로그인 실패
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(401);
    }
}
