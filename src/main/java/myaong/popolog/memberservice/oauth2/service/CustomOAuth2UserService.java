package myaong.popolog.memberservice.oauth2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myaong.popolog.memberservice.client.BlogServiceClient;
import myaong.popolog.memberservice.converter.AuthConverter;
import myaong.popolog.memberservice.converter.MemberConverter;
import myaong.popolog.memberservice.dto.request.MemberProfileRequest;
import myaong.popolog.memberservice.entity.Member;
import myaong.popolog.memberservice.enums.Permission;
import myaong.popolog.memberservice.enums.RequiredInfo;
import myaong.popolog.memberservice.oauth2.CustomOAuth2User;
import myaong.popolog.memberservice.oauth2.dto.GoogleResponse;
import myaong.popolog.memberservice.oauth2.dto.KakaoResponse;
import myaong.popolog.memberservice.oauth2.dto.OAuth2Response;
import myaong.popolog.memberservice.oauth2.dto.OAuthUserDTO;
import myaong.popolog.memberservice.service.MemberCommandService;
import myaong.popolog.memberservice.service.MemberQueryService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberQueryService memberQueryService;
    private final MemberCommandService memberCommandService;
    private final BlogServiceClient blogServiceClient;

    // 여기서 리턴된 OAuth2User 객체는 사용자 인증 정보를 나타내기 위해 Authentication 객체에 담겨지고,
    // 이 Authentication 객체는 사용자의 인증 상태를 나타내며, SecurityContext에 저장된다고 보면됨
    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = null;

        if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
            log.info("Kakao OAuth2User: {}", oAuth2User.getAttributes());
            log.info("Kakao OAuth2Response Email: {}", oAuth2Response.getEmail());
        } else if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
            log.info("Google OAuth2User: {}", oAuth2User.getAttributes());
            log.info("Google OAuth2Response Email: {}", oAuth2Response.getEmail());
        } else {
            return null;
        }

        //리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만듬
        String providerId = oAuth2Response.getProviderId();
        Member findMember = memberQueryService.findByProviderId(providerId);

        // 존재하지 않는 member면 회원정보를 저장하고 CustomOAuth2User 반환
        if (findMember == null) {
            // member 저장
            Member newMember = AuthConverter.toMember(oAuth2Response, RequiredInfo.BOTH);
            Member savedMember = memberCommandService.saveMember(newMember);

            OAuthUserDTO oAuthUserDTO = AuthConverter.toOAuthUserDTO(oAuth2Response.getName(), savedMember.getId(), providerId, Permission.MEMBER.name());

            return new CustomOAuth2User(oAuthUserDTO);
        } else { // 회원정보가 존재한다면 조회된 데이터로 반환
            OAuthUserDTO oAuthUserDTO = AuthConverter.toOAuthUserDTO(oAuth2Response.getName(), findMember.getId(), findMember.getProviderId(), findMember.getPermission().name().toLowerCase());

            return new CustomOAuth2User(oAuthUserDTO);
        }
    }
}