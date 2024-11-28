package myaong.popolog.memberservice.config;

import lombok.RequiredArgsConstructor;
import myaong.popolog.memberservice.jwt.*;
import myaong.popolog.memberservice.oauth2.OAuth2SuccessHandler;
import myaong.popolog.memberservice.oauth2.service.CustomOAuth2UserService;
import myaong.popolog.memberservice.service.RedisService;
import myaong.popolog.memberservice.util.CookieUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static myaong.popolog.memberservice.enums.Permission.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final RedisService redisService;
    private final RequestMatcherHolder requestMatcherHolder;

    //AuthenticationManager가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입
    private final AuthenticationConfiguration authenticationConfiguration;

//    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
//    private final JwtAuthenticationFailEntryPoint jwtAuthenticationFailEntryPoint;

    @Value("${redirect-url.main}")
    private String mainPageUrl;

    //AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers(requestMatcherHolder.getRequestMatchersByMinPermission(null)).permitAll()
                                .requestMatchers(requestMatcherHolder.getRequestMatchersByMinPermission(MEMBER))
                                .hasAnyAuthority(MEMBER.name(), ADMIN.name(), SUPER.name())
                                .requestMatchers(requestMatcherHolder.getRequestMatchersByMinPermission(ADMIN))
                                .hasAnyAuthority(ADMIN.name(), SUPER.name())
                                .requestMatchers(requestMatcherHolder.getRequestMatchersByMinPermission(SUPER))
                                .hasAnyAuthority(SUPER.name())
                                .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                )
                .addFilterAt(new JwtFilter(jwtUtil, requestMatcherHolder), NormalLoginFilter.class) // JwtFilter를 NormalLoginFilter 앞에 추가하여 JWT 검증을 수행
                .addFilterAt(new NormalLoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, cookieUtil, redisService, mainPageUrl), UsernamePasswordAuthenticationFilter.class); // NormalLoginFilter를 UsernamePasswordAuthenticationFilter 앞에 추가하여 로그인 요청을 처리
//                .exceptionHandling(exceptionHandling -> {
//                    exceptionHandling.authenticationEntryPoint(jwtAuthenticationFailEntryPoint);
//                    exceptionHandling.accessDeniedHandler(jwtAccessDeniedHandler);
//                });

        return http.build();
    }
}
