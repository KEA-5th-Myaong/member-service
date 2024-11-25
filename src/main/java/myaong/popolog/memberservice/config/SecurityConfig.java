package myaong.popolog.memberservice.config;

import lombok.RequiredArgsConstructor;
import myaong.popolog.memberservice.enums.Permission;
import myaong.popolog.memberservice.jwt.*;
import myaong.popolog.memberservice.oauth2.OAuth2SuccessHandler;
import myaong.popolog.memberservice.oauth2.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final JwtFilter jwtFilter;
//    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
//    private final JwtAuthenticationFailEntryPoint jwtAuthenticationFailEntryPoint;
    private final RequestMatcherHolder requestMatcherHolder;

    //AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfigurationSource()))
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
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // 기존 시큐리티의 UsernamePasswordAuthenticationFilter를 커스텀한 JwtFilter로 대체
//                .exceptionHandling(exceptionHandling -> {
//                    exceptionHandling.authenticationEntryPoint(jwtAuthenticationFailEntryPoint);
//                    exceptionHandling.accessDeniedHandler(jwtAccessDeniedHandler);
//                });

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*")); // 해당 출처에서 요청 허용 (http://localhost:3000와 같이 주소로 허용가능)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*")); // 모든 HTTP 헤더의 요청을 허용합니다.
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Set-Cookie")); // 브라우저가 접근할 수 있도록 특정 응답 헤더를 노출합니다. 여기서는 "Set-Cookie"와 "Authorization"
        configuration.setAllowCredentials(true); // 인증 정보(쿠키, 인증 토큰 등)의 전송을 허용합니다.
        configuration.setMaxAge(3600L); // 최대 유효시간 설정

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

}
