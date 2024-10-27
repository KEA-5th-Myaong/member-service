package myaong.popolog.memberservice.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myaong.popolog.memberservice.common.exception.ApiCode;
import myaong.popolog.memberservice.common.exception.ApiException;
import myaong.popolog.memberservice.dto.response.TokenDTO;
import myaong.popolog.memberservice.entity.Member;
import myaong.popolog.memberservice.entity.RefreshToken;
import myaong.popolog.memberservice.jwt.TokenProvider;
import myaong.popolog.memberservice.repository.MemberRepository;
import myaong.popolog.memberservice.repository.RefreshTokenRedisRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final String REDIRECT_URI = "http://localhost:8083/login/kakao?accessToken=%s&refreshToken=%s";

    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    @Transactional
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // KakaoMemberDetailsService에서 시큐리티 컨텍스트에 Authentication 객체를 저장한 덕분에 사용자 정보를 꺼낼 수 있음
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        KakaoUserInfo kakaoUserInfo = new KakaoUserInfo(oAuth2User.getAttributes());

        Member member = memberRepository.findByEmail(kakaoUserInfo.getEmail())
                .orElseThrow(() -> new ApiException(ApiCode.MEMBER_NOT_FOUND));

        TokenDTO tokenDTO = tokenProvider.createToken(member.getId(), member.getEmail(), member.getPermission().name());

        saveRefreshTokenOnRedis(member, tokenDTO);

        log.info("memberId = {}", member.getId());
        log.info("accessToken = {}", tokenDTO.getAccessToken());
        log.info("refreshToken = {}", tokenDTO.getRefreshToken());

        String redirectURI = String.format(REDIRECT_URI, tokenDTO.getAccessToken(), tokenDTO.getRefreshToken());
        getRedirectStrategy().sendRedirect(request, response, redirectURI);
    }

    private void saveRefreshTokenOnRedis(Member member, TokenDTO tokenDTO) {
        List<SimpleGrantedAuthority> simpleGrantedAuthorities = new ArrayList<>();
        simpleGrantedAuthorities.add(new SimpleGrantedAuthority(member.getPermission().name()));

        refreshTokenRedisRepository.save(RefreshToken.builder()
                .id(member.getId())
                .email(member.getEmail())
                .authorities(simpleGrantedAuthorities)
                .refreshToken(tokenDTO.getRefreshToken())
                .build());
    }
}
