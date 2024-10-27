package myaong.popolog.memberservice.config;

import lombok.RequiredArgsConstructor;
import myaong.popolog.memberservice.jwt.JwtAccessDeniedHandler;
import myaong.popolog.memberservice.jwt.JwtAuthenticationFailEntryPoint;
import myaong.popolog.memberservice.jwt.JwtFilter;
import myaong.popolog.memberservice.jwt.RequestMatcherHolder;
import myaong.popolog.memberservice.oauth2.service.KakaoMemberDetailsService;
import myaong.popolog.memberservice.oauth2.OAuth2SuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static myaong.popolog.memberservice.enums.Permission.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final KakaoMemberDetailsService kakaoMemberDetailsService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final JwtFilter jwtFilter;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationFailEntryPoint jwtAuthenticationFailEntryPoint;
    private final RequestMatcherHolder requestMatcherHolder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
//                .cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/members/v3/api-docs/**").permitAll()  // 이 요청은 허용
                        .requestMatchers(requestMatcherHolder.getRequestMatchersByMinPermission(null)).permitAll()
                        .requestMatchers(requestMatcherHolder.getRequestMatchersByMinPermission(ADMIN))
                        .hasAnyAuthority(ADMIN.name(), SUPER.name())
                        .requestMatchers(requestMatcherHolder.getRequestMatchersByMinPermission(SUPER))
                        .hasAnyAuthority(SUPER.name())
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(kakaoMemberDetailsService))
                        .successHandler(oAuth2SuccessHandler))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // 기존 시큐리티의 UsernamePasswordAuthenticationFilter를 커스텀한 JwtFilter로 대체
                .exceptionHandling(exceptionHandling -> {
                    exceptionHandling.authenticationEntryPoint(jwtAuthenticationFailEntryPoint);
                    exceptionHandling.accessDeniedHandler(jwtAccessDeniedHandler);
                });

        return http.build();
    }
}
